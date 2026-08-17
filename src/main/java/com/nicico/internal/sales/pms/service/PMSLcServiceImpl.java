package com.nicico.internal.sales.pms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.bank.model.IssuingBankModel;
import com.nicico.internal.sales.bank.model.TradingBankModel;
import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
import com.nicico.internal.sales.common.properties.PMSProperties;
import com.nicico.internal.sales.common.properties.RabbitConfigPMSProperties;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.goods.service.GoodsService;
import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.lc.dto.LcMapper;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.pms.dto.PMSLcDTO;
import com.nicico.internal.sales.pms.enums.PMSLcMarkEnum;
import com.nicico.internal.sales.pms.model.PMSBankLcModel;
import com.nicico.internal.sales.pms.model.PmsLcModel;
import com.nicico.internal.sales.pms.repository.PMSBankLcRepository;
import com.nicico.internal.sales.pms.repository.PMSCustomerRepository;
import com.nicico.internal.sales.pms.repository.PmsLcRepository;
import com.nicico.internal.sales.proforma.enums.SettlementType;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaGoodItemRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PMSLcServiceImpl implements PMSLcService {
	private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";
	private final LcRepository lcRepository;
	private final LcMapper lcMapper;
	private final GoodsService goodsService;
	private final PMSCustomerRepository pmsCustomerRepository;
	private final PMSBankLcRepository bankLcRepository;
	private final PmsLcRepository pmsLcRepository;
	private final IssuingBankRepository issuingBankRepository;
	private final ProformaDetailRepository proformaDetailRepository;
	private final PMSProperties pmsProperties;
	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;
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
	public void createPMSLc(Long proformaMasterId, String username, boolean resend) {

//		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.LETTER_OF_CREDIT)
//				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
//		log.info(exportConfig.toString());
//		if (exportConfig.getSendPms() == false) return;


		List<LcModel> lcList = lcRepository.findByMasterId(proformaMasterId);
		if (lcList.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException("LC of Master ID not found " + proformaMasterId);
		}

		lcList.forEach(lc -> {
			if (!resend && Objects.nonNull(lc.getPmsLcId())) {
				log.info("LC id {} already sent to PMS with pms lc id {}, skipping resend",
						lc.getId(), lc.getPmsLcId());
				return;
			}
			try {
				sendLcModelToPMS(lc, username);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new InternalSaleCustomException.ApplicationServerException(e.getMessage());
			}
		});
	}

	@Override
	public void createPMSLc(Long lcID, boolean resend) throws IOException {
		var lcModel = lcRepository.findById(lcID)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		if (!resend && Objects.nonNull(lcModel.getPmsLcId())) {
			log.info("LC id {} already sent to PMS with pms lc id {}, skipping resend",
					lcModel.getId(), lcModel.getPmsLcId());
			return;
		}
		sendLcModelToPMS(lcModel, SecurityUtil.getUsername());

	}

	@Override
	public void sendLcModelToPMS(LcModel model, String username) {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.LETTER_OF_CREDIT)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;


		PMSLcDTO.Create o = buildPmsLcCreateDto(model, username);
		ProformaGoodItemModel proformaGoodItemModel = getPerformaGoodItemModel(model);
		log.info("User {} -  {} - {} asked for send lc to pms for lc id  : {}  ind proforma good item id: {}",
				SecurityUtil.getUsername(),
				SecurityUtil.getNationalCode(),
				SecurityUtil.getFullName(),
				model.getId(),
				proformaGoodItemModel.getId()
		);
		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
				rabbitConfigPMSProperties.getQueues().getLc().getRoutingKey(),
				new PMSLcDTO.RabbitListenerRequestDTO(pmsProperties.getLc().getUrl(),
						model.getId(), o, rabbitConfigPMSProperties.getQueues().getLc().getResponseRoutingKey()
				)
		);
//        PMSLcDTO.Info info = httpClient.post(pmsProperties.getLc().getUrl(), o, PMSLcDTO.Info.class);
//        model.setPmsLcId(info.getId());
//        repository.save(model);
		log.info("User {} -  {} - {} asked for send lc to pms for lc id  : {}  ind proforma good item id: {} ",
				SecurityUtil.getUsername(),
				SecurityUtil.getNationalCode(),
				SecurityUtil.getFullName(),
				model.getId(),
				proformaGoodItemModel.getId()

		);


	}

	@Override
	public void updatePmsLc(String pmsId, String username) {
		LcModel model = lcRepository.findFirstByPmsLcId(pmsId).orElseThrow(
				() -> new InternalSaleCustomException.ValidationException("LC of PMS LC ID not found " + pmsId)
		);
		PmsLcModel pmsLcModel = pmsLcRepository.findById(model.getPmsLcId()).orElseThrow(() -> new InternalSaleCustomException.ValidationException(
				String.format("no lc found in pms for pmsId %s", model.getPmsLcId())
		));
		log.info("updatePmsLc pmslc before update was {}", pmsLcModel);
		ProformaGoodItemModel proformaGoodItemModel = getPerformaGoodItemModel(model);

		PMSLcDTO.Update o = PMSLcDTO.Update
				.lBuilder()
				.id(model.getPmsLcId())
				.user(pmsProperties.getPreFactor().getUser())
				.pass(pmsProperties.getPreFactor().getPass())
				.username(resolveUsername(username))
				.customerId(extractCustomerId(proformaGoodItemModel))
				.bankLCId(extractIssuingPMSBankLcId(model.getIssuingBankModel()))
				.prefactorId(proformaDetailRepository.findById(model.getProformaDetailId()).get().getPmsId())
				.goodsId(goodsService.findPmsIdByGoodName(proformaGoodItemModel.getGoodName()))
				.issueDate(DateUtility.getJalaliDate(model.getLcDate()))
				.expiryDate(DateUtility.getJalaliDate(model.getLcExpiryDate()))
				.amount(getAmount(proformaGoodItemModel))
				.price(model.getTotalFinalAmount())
				.lcNumber(model.getLcNo())
				.type(523)
				.state(281)
				.mark(PMSLcMarkEnum.LC)
				.bankLCMoamelehId(extractTradingPMSBankLcId(model.getTradingBankModel()))
				.lBuild();
		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
				rabbitConfigPMSProperties.getQueues().getLc().getUpdateRoutingKey(),
				new PMSLcDTO.RabbitListenerRequestDTO(pmsProperties.getLc().getUrl(),
						model.getId(), o, rabbitConfigPMSProperties.getQueues().getLc().getUpdateResponseRoutingKey()
				)
		);
		log.info("User {} -  {} - {} asked for update lc in pms for pms lc id: {} lc id: {}",
				SecurityUtil.getUsername(),
				SecurityUtil.getNationalCode(),
				SecurityUtil.getFullName(),
				model.getPmsLcId(),
				model.getId()
		);
	}

	private PMSLcDTO.Create buildPmsLcCreateDto(LcModel model, String username) {
		ProformaGoodItemModel proformaGoodItemModel = getPerformaGoodItemModel(model);
		return PMSLcDTO.Create
				.lBuilder()
				.user(pmsProperties.getPreFactor().getUser())
				.pass(pmsProperties.getPreFactor().getPass())
				.customerId(extractCustomerId(proformaGoodItemModel))
				.bankLCId(extractIssuingPMSBankLcId(model.getIssuingBankModel()))
				.username(resolveUsername(username))
				.prefactorId(proformaDetailRepository.findById(model.getProformaDetailId()).get().getPmsId())
				.goodsId(goodsService.findPmsIdByGoodName(proformaGoodItemModel.getGoodName()))
				.issueDate(DateUtility.getJalaliDate(model.getLcDate()))
				.expiryDate(DateUtility.getJalaliDate(model.getLcExpiryDate()))
				.amount(getAmount(proformaGoodItemModel))
				.price(model.getTotalFinalAmount())
				.lcNumber(model.getLcNo())
				.type(523)
				.state(281)
				.mark(PMSLcMarkEnum.LC)
				.bankLCMoamelehId(extractTradingPMSBankLcId(model.getTradingBankModel())).lBuild();
	}

	private ProformaGoodItemModel getPerformaGoodItemModel(LcModel model) {
		return proformaGoodItemRepository.findLatestActiveItemWithProformaMasterId(model.getProformaMasterId());
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

//	private static String normalizePersian(String text) {
//		return text == null ? null
//				: text.replace('ي', 'ی')
//				.replace('ك', 'ک')
//				.trim();
//	}

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

	private String extractTradingPMSBankLcId(TradingBankModel tradingBankModel) {


		log.error(tradingBankModel.toString());

		String tradingBankTitle = Optional.ofNullable(tradingBankModel.getBankTitle())
				.orElse("")
				.toLowerCase();

		Optional<IssuingBankModel> issuingBankModel = issuingBankRepository.findAll()
				.stream()
				.filter(ib -> tradingBankModel.getFixedBranchCode().equals(ib.getFixedBranchCode()))
				.filter(ib -> {
					var bank = ib.getBaseBankModel();
					if (bank == null || bank.getBankTitle() == null) {
						return false;
					}

					String issuingBankTitle = bank.getBankTitle().toLowerCase();

					// LIKE behavior
					return issuingBankTitle.contains(tradingBankTitle)
							|| tradingBankTitle.contains(issuingBankTitle);
				})
				.findFirst();

		log.error(issuingBankModel.toString());

		IssuingBankModel bank = issuingBankModel.orElseThrow(() ->
				new InternalSaleCustomException.ValidationException(
						String.format(
								"Failed to find issuing bank. Bank='%s', Branch='%s'",
								tradingBankModel.getBankTitle(),
								tradingBankModel.getFixedBranchCode()
						)
				));

		return extractIssuingPMSBankLcId(bank);
	}

	@RabbitListener(queues = "${rabbitmq.config.pms.queues.lc.response-queue}")
	void savePmsLC(String message) throws JsonProcessingException {


		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.LETTER_OF_CREDIT)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;


		log.info("Saving PMS LC ID: {}", message);
		PMSLcDTO.RabbitListenerResponseDTO response = objectMapper.readValue(message,
				PMSLcDTO.RabbitListenerResponseDTO.class);
		Optional<LcModel> detail = lcRepository.findById(response.getRequest().getId());
		if (detail.isPresent()) {
			LcModel lc = detail.get();
			lc.setPmsLcId(response.getResponse().getId());
			lcRepository.save(lc);
		} else {
			log.error("Could not find detail for  PMS lc ID: {}", message);
		}
	}


	@Override
	public List<LcDto.Info> findRemittanceLcWithoutPmsId() {
		List<LcModel> lcModels = lcRepository.findRemittanceLcWithoutPmsId();
		String username = SecurityUtil.getUsername();
		for (LcModel lc : lcModels) {
			Long proformaMasterId = lc.getProformaMasterId();
			createPMSLc(proformaMasterId, username, false);
		}
		return lcModels.stream()
				.map(lcMapper::toDTO)
				.toList();
	}

	@RabbitListener(queues = "${rabbitmq.config.pms.queues.lc.update-response-queue}")
	void updatePmsLCFromPms(String message) throws JsonProcessingException {


		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.LETTER_OF_CREDIT)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;


		log.info("Updating PMS LC from response queue: {}", message);
		PMSLcDTO.RabbitListenerResponseDTO response = objectMapper.readValue(message,
				PMSLcDTO.RabbitListenerResponseDTO.class);
		if (response.getRequest() == null || response.getRequest().getId() == null) {
			log.warn("Ignoring malformed PMS LC update response without request id: {}", message);
			return;
		}
		Optional<LcModel> detail = lcRepository.findById(response.getRequest().getId());
		if (detail.isPresent()) {
			LcModel lc = detail.get();
			if (response.getResponse() == null || response.getResponse().getId() == null) {
				log.warn("PMS LC update response had empty PMS id for LC id {}", response.getRequest().getId());
				return;
			}
			lc.setPmsLcId(response.getResponse().getId());
			lcRepository.save(lc);
			log.info("PMS LC id updated from response for LC id {}", lc.getId());
		} else {
			log.error("Could not find detail for update response PMS LC: {}", message);
		}
	}

}
