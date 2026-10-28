package com.nicico.internal.sales.pms.service;

import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.bank.model.IssuingBankModel;
import com.nicico.internal.sales.bank.repository.BaseBankRepository;
import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
import com.nicico.internal.sales.common.properties.PMSProperties;
import com.nicico.internal.sales.common.properties.RabbitConfigPMSProperties;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.gaam.repository.GaamRepository;
import com.nicico.internal.sales.goods.service.GoodsService;
import com.nicico.internal.sales.pms.dto.PMSLcDTO;
import com.nicico.internal.sales.pms.enums.PMSLcMarkEnum;
import com.nicico.internal.sales.pms.model.PMSBankLcModel;
import com.nicico.internal.sales.pms.repository.PMSBankLcRepository;
import com.nicico.internal.sales.pms.repository.PMSCustomerRepository;
import com.nicico.internal.sales.proforma.enums.SettlementType;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaGoodItemRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PMSGaamServiceImpl implements PMSGaamService {
	private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";

	private final GaamRepository gaamRepository;
	private final GoodsService goodsService;
	private final PMSCustomerRepository pmsCustomerRepository;
	private final PMSBankLcRepository bankLcRepository;
	private final IssuingBankRepository issuingBankRepository;
	private final BaseBankRepository baseBankRepository;
	private final ProformaDetailRepository proformaDetailRepository;
	private final PMSProperties pmsProperties;
	private final RabbitTemplate rabbitTemplate;
	private final RabbitConfigPMSProperties rabbitConfigPMSProperties;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;
	private final ProformaGoodItemRepository proformaGoodItemRepository;

	private static BigDecimal getAmount(ProformaGoodItemModel goodItem) {
		if (Objects.nonNull(goodItem.getNetQuantity()) && goodItem.getNetQuantity().longValue() > 0) {
			return goodItem.getNetQuantity();
		}
		if (goodItem.getProformaDetailModel().getSettlementType() != null && !goodItem.getProformaDetailModel().getSettlementType().equals(SettlementType.CASH) &&
				Objects.nonNull(goodItem.getCreditQuantity()) && goodItem.getCreditQuantity().longValue() > 0) {
			return goodItem.getCreditQuantity();
		}
		return goodItem.getQuantity();
	}

	@Override
	public void createPMSGaam(Long proformaMasterId, String username, boolean resend) {
		List<GaamModel> gaamList = gaamRepository.findByMasterId(proformaMasterId);
		if (gaamList.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException("GAAM of Master ID not found " + proformaMasterId);
		}

		gaamList.forEach(gaam -> {
			if (!resend && Objects.nonNull(gaam.getPmsBillId())) {
				log.info("GAAM id {} already sent to PMS with pms bill id {}, skipping resend",
						gaam.getId(), gaam.getPmsBillId());
				return;
			}
			try {
				sendGaamModelToPMS(gaam, username);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new InternalSaleCustomException.ApplicationServerException(e.getMessage());
			}
		});
	}

	@Override
	public void createPMSGaam(Long gaamId, boolean resend) throws IOException {
		var gaamModel = gaamRepository.findById(gaamId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		if (!resend && Objects.nonNull(gaamModel.getPmsBillId())) {
			log.info("GAAM id {} already sent to PMS with pms bill id {}, skipping resend",
					gaamModel.getId(), gaamModel.getPmsBillId());
			return;
		}
		sendGaamModelToPMS(gaamModel, SecurityUtil.getUsername());
	}

	@Override
	public void sendGaamModelToPMS(GaamModel model, String username) {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.LETTER_OF_CREDIT)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;

		PMSLcDTO.Create o = buildPmsGaamCreateDto(model, username);
		ProformaGoodItemModel proformaGoodItemModel = proformaGoodItemRepository.findLatestActiveItemWithProformaMasterId(model.getProformaMasterId());
		log.info("User {} - {} - {} asked for send gaam to pms for gaam id: {} proforma good item id: {}",
				SecurityUtil.getUsername(),
				SecurityUtil.getNationalCode(),
				SecurityUtil.getFullName(),
				model.getId(),
				proformaGoodItemModel != null ? proformaGoodItemModel.getId() : null
		);
		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
				rabbitConfigPMSProperties.getQueues().getLc().getRoutingKey(),
				new PMSLcDTO.RabbitListenerRequestDTO(pmsProperties.getLc().getUrl(),
						model.getId(), o, rabbitConfigPMSProperties.getQueues().getLc().getResponseRoutingKey()
				)
		);
	}

	@Override
	public void updatePmsGaam(String pmsId, String username) {
		GaamModel model = gaamRepository.findFirstByPmsBillId(pmsId).orElseThrow(
				() -> new InternalSaleCustomException.ValidationException("GAAM of PMS Bill ID not found " + pmsId)
		);
		ProformaGoodItemModel proformaGoodItemModel = proformaGoodItemRepository.findLatestActiveItemWithProformaMasterId(model.getProformaMasterId());

		String issuingBankLcId = extractIssuingPMSBankId(model.getIssuerBankId());
		String tradingBankLcId = extractTradingPMSBankId(model.getAgentBankId(), model.getIssuerBankId());
		String pmsPrefactorId = proformaDetailRepository.findById(model.getProformaDetailId())
				.map(ProformaDetailModel::getPmsId)
				.orElse(null);
		String docNumber = StringUtils.hasText(model.getSepamCode()) ? model.getSepamCode() : model.getTreasuryId();

		PMSLcDTO.Update o = PMSLcDTO.Update
				.lBuilder()
				.id(model.getPmsBillId())
				.user(pmsProperties.getPreFactor().getUser())
				.pass(pmsProperties.getPreFactor().getPass())
				.username(resolveUsername(username))
				.customerId(extractCustomerId(proformaGoodItemModel))
				.bankLCId(issuingBankLcId)
				.prefactorId(pmsPrefactorId)
				.goodsId(goodsService.findPmsIdByGoodName(proformaGoodItemModel.getGoodName()))
				.issueDate(model.getIssueDate() != null ? DateUtility.getJalaliDate(model.getIssueDate()) : null)
				.expiryDate(model.getDueDate() != null ? DateUtility.getJalaliDate(model.getDueDate()) : null)
				.amount(getAmount(proformaGoodItemModel))
				.price(proformaGoodItemModel.getFinalAmount() != null ? proformaGoodItemModel.getFinalAmount() : BigDecimal.ZERO)
				.lcNumber(docNumber)
				.type(523)
				.state(281)
				.mark(PMSLcMarkEnum.oragh)
				.bankLCMoamelehId(tradingBankLcId)
				.lBuild();

		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
				rabbitConfigPMSProperties.getQueues().getLc().getUpdateRoutingKey(),
				new PMSLcDTO.RabbitListenerRequestDTO(pmsProperties.getLc().getUrl(),
						model.getId(), o, rabbitConfigPMSProperties.getQueues().getLc().getUpdateResponseRoutingKey()
				)
		);
	}

	@Override
	public void savePmsId(Long gaamId, String pmsBillId) {
		Optional<GaamModel> gaamOpt = gaamRepository.findById(gaamId);
		if (gaamOpt.isPresent()) {
			GaamModel gaam = gaamOpt.get();
			gaam.setPmsBillId(pmsBillId);
			gaamRepository.save(gaam);
			log.info("PMS ID {} saved for GAAM id {}", pmsBillId, gaamId);
		} else {
			log.error("Could not find GAAM detail for id {}", gaamId);
		}
	}

	private PMSLcDTO.Create buildPmsGaamCreateDto(GaamModel model, String username) {
		ProformaGoodItemModel proformaGoodItemModel = proformaGoodItemRepository.findLatestActiveItemWithProformaMasterId(model.getProformaMasterId());
		if (proformaGoodItemModel == null) {
			throw new InternalSaleCustomException.ValidationException("کالای پیش‌فاکتور یافت نشد برای شناسه مستر: " + model.getProformaMasterId());
		}

		String issuingBankLcId = extractIssuingPMSBankId(model.getIssuerBankId());
		String tradingBankLcId = extractTradingPMSBankId(model.getAgentBankId(), model.getIssuerBankId());
		String pmsPrefactorId = proformaDetailRepository.findById(model.getProformaDetailId())
				.map(ProformaDetailModel::getPmsId)
				.orElse(null);
		String docNumber = StringUtils.hasText(model.getSepamCode()) ? model.getSepamCode() : model.getTreasuryId();

		return PMSLcDTO.Create
				.lBuilder()
				.user(pmsProperties.getPreFactor().getUser())
				.pass(pmsProperties.getPreFactor().getPass())
				.customerId(extractCustomerId(proformaGoodItemModel))
				.bankLCId(issuingBankLcId)
				.username(resolveUsername(username))
				.prefactorId(pmsPrefactorId)
				.goodsId(goodsService.findPmsIdByGoodName(proformaGoodItemModel.getGoodName()))
				.issueDate(model.getIssueDate() != null ? DateUtility.getJalaliDate(model.getIssueDate()) : null)
				.expiryDate(model.getDueDate() != null ? DateUtility.getJalaliDate(model.getDueDate()) : null)
				.amount(getAmount(proformaGoodItemModel))
				.price(proformaGoodItemModel.getFinalAmount() != null ? proformaGoodItemModel.getFinalAmount() : BigDecimal.ZERO)
				.lcNumber(docNumber)
				.type(523)
				.state(281)
				.mark(PMSLcMarkEnum.oragh)
				.bankLCMoamelehId(tradingBankLcId)
				.lBuild();
	}

	private String resolveUsername(String username) {
		return username == null ? pmsProperties.getLc().getDefaultPmsUser() : username;
	}

	private String extractCustomerId(ProformaGoodItemModel goodItem) {
		if (goodItem.getProformaDetailModel() != null && goodItem.getProformaDetailModel().getProformaMasterModel() != null) {
			String nationalCode = goodItem.getProformaDetailModel().getProformaMasterModel().getNationalCode();
			String economicCode = goodItem.getProformaDetailModel().getProformaMasterModel().getEconomicCode();
			return pmsCustomerRepository.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(economicCode, nationalCode)
					.orElseThrow(() -> new InternalSaleCustomException.ValidationException("مشتری با شناسه " + goodItem.getProformaDetailModel()
							.getProformaMasterModel().getNationalCode() + " وجود ندارد")).getId();
		}
		throw new InternalSaleCustomException.ValidationException("شماره قرارداد وجود ندارد");
	}

	private String extractIssuingPMSBankId(Long issuerBankId) {
		if (issuerBankId == null) {
			throw new InternalSaleCustomException.ValidationException("بانک صادرکننده تعیین نشده است");
		}
		IssuingBankModel issuingBankModel = issuingBankRepository.findById(issuerBankId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("بانک صادرکننده با شناسه " + issuerBankId + " یافت نشد"));
		return extractIssuingPMSBankLcId(issuingBankModel);
	}

	private String extractTradingPMSBankId(Long agentBankId, Long fallbackIssuerBankId) {
		if (agentBankId != null) {
			var baseBankOpt = baseBankRepository.findById(agentBankId);
			if (baseBankOpt.isPresent()) {
				String agentBankTitle = baseBankOpt.get().getBankTitle().toLowerCase();
				Optional<IssuingBankModel> matchingIssuingBank = issuingBankRepository.findAll().stream()
						.filter(ib -> {
							if (ib.getBaseBankModel() == null || ib.getBaseBankModel().getBankTitle() == null) return false;
							String title = ib.getBaseBankModel().getBankTitle().toLowerCase();
							return title.contains(agentBankTitle) || agentBankTitle.contains(title);
						})
						.findFirst();
				if (matchingIssuingBank.isPresent()) {
					return extractIssuingPMSBankLcId(matchingIssuingBank.get());
				}
			}
		}
		if (fallbackIssuerBankId != null) {
			return extractIssuingPMSBankId(fallbackIssuerBankId);
		}
		throw new InternalSaleCustomException.ValidationException("بانک عامل یا صادرکننده یافت نشد");
	}

	private String extractIssuingPMSBankLcId(IssuingBankModel issuingBankModel) {
		PMSBankLcModel lcBank = bankLcRepository
				.findFirstByBankAndFixedBranchCode(
						issuingBankModel.getBaseBankModel().getBankTitle(),
						issuingBankModel.getFixedBranchCode()).orElseThrow(() -> new InternalSaleCustomException.ValidationException
						(String.format("No issue Bank found in PMS for %s %s %s", issuingBankModel.getId(),
								issuingBankModel.getBaseBankModel().getBankTitle(),
								issuingBankModel.getBranchCode())));
		return lcBank.getId();
	}
}
