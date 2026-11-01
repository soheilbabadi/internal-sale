package com.nicico.internal.sales.pms.service;

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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
			return;
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
				return;
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

	/**
	 * Simple single-shot write - NO retry loop (per review decision).
	 * <p>
	 * Why this is safe against the recurring ObjectOptimisticLockingFailureExceptions:
	 *  - Every workflow writer on the SAME row (LcProcessServiceImpl.reviewTask / refreshStatus) now
	 *    goes through saveLcRetryOnOptimisticLock -> persistLc, i.e. it re-reads the row inside its own
	 *    short REQUIRES_NEW transaction right before committing, and retries up to
	 *    LcProcessServiceImpl.LC_SAVE_MAX_ATTEMPTS times. So when this listener writes, the counterpart
	 *    side absorbs any conflict instead of failing.
	 *  - The window between our findById and the commit-time UPDATE contains no remote call, so it is
	 *    microseconds wide.
	 *  - If a conflict does slip through anyway, we log it and return WITHOUT rethrowing: rethrowing
	 *    would make the Rabbit listener container requeue and redeliver the message endlessly
	 *    (poison-message behavior seen previously). Instead the scheduler's sendToPms job re-detects
	 *    LCs that never received their pmsLcId (findRemittanceLcWithoutPmsId: ACCEPTED + REMITTANCE
	 *    rows with pmsLcId IS NULL) and resends them, so a lost write converges on the next run.
	 * <p>
	 * Kept guards:
	 *  - REQUIRES_NEW: the write commits independently and becomes visible immediately.
	 *  - Idempotency: a duplicate delivery carrying the exact same pmsLcId skips the write instead of
	 *    bumping the version pointlessly.
	 *  - save() only (no explicit flush): the versioned UPDATE runs exactly once at commit - same rule
	 *    as LcProcessServiceImpl.persistLc.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void savePmsId(Long lcId, String pmsLcId) {
		Optional<LcModel> detail = lcRepository.findById(lcId);
		if (detail.isEmpty()) {
			log.error("Could not find LC detail for id {}", lcId);
			return;
		}
		LcModel lc = detail.get();
		if (Objects.equals(lc.getPmsLcId(), pmsLcId)) {
			log.info("PMS LC id {} already saved for LC id {}, skipping", pmsLcId, lcId);
			return;
		}
		lc.setPmsLcId(pmsLcId);
		try {
			lcRepository.save(lc);
			log.info("PMS LC id {} saved for LC id {}", pmsLcId, lcId);
		} catch (ObjectOptimisticLockingFailureException e) {
			// Do NOT rethrow: the message must not be redelivered forever. The scheduled
			// sendToPms job will resend this LC because its pmsLcId is still null.
			log.error("تعارض قفل خوش‌بینانه در ذخیره PMS LC id {} برای LC {}؛ پیام دوباره ارسال نخواهد شد "
					+ "و اسکجول sendToPms این LC را مجدداً به PMS می‌فرستد", pmsLcId, lcId, e);
		}
	}

	@Override
	public List<LcDto.Info> findRemittanceLcWithoutPmsId() {
		List<LcModel> lcModels = lcRepository.findRemittanceLcWithoutPmsId();
		// Scheduler/system context has no authenticated user; SecurityUtil getters return null there,
		// so resolve the username ourselves (same fallback as resolveUsername in buildPmsLcCreateDto).
		String username = SecurityUtil.getUsername();
		if (username == null) {
			username = pmsProperties.getLc().getDefaultPmsUser();
		}
		for (LcModel lc : lcModels) {
			Long proformaMasterId = lc.getProformaMasterId();
			createPMSLc(proformaMasterId, username, false);
		}
		return lcModels.stream()
				.map(lcMapper::toDTO)
				.toList();
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
}
