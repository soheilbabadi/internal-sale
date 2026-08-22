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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class PMSLcServiceImpl implements PMSLcService {
private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";
private static final String LC_NOT_FOUND_BY_MASTER_ID_MESSAGE = "ال‌سی با شناسه پروفرما یافت نشد ";
private static final String LC_NOT_FOUND_BY_PMS_ID_MESSAGE = "ال‌سی با شناسه ال‌سی PMS یافت نشد ";
private static final String CUSTOMER_NOT_FOUND_MESSAGE = "مشتری با شناسه ";
private static final String CONTRACT_NOT_FOUND_MESSAGE = "شماره قرارداد وجود ندارد";
private static final String INVALID_PROFORMA_GOOD_ITEM_MESSAGE = "کالای پروفرما نامعتبر است: ";
private static final String INVALID_BANK_MODEL_MESSAGE = "مدل بانک نمی‌تواند null باشد";
private static final String INVALID_TRADING_BANK_MESSAGE = "مدل بانک عامل نمی‌تواند null باشد";
private static final String LC_MODEL_NULL_MESSAGE = "مدل ال‌سی نمی‌تواند null باشد";
private static final String LC_ID_NULL_MESSAGE = "شناسه ال‌سی نمی‌تواند null باشد";
private static final String LC_NUMBER_EMPTY_MESSAGE = "شماره ال‌سی نمی‌تواند خالی باشد";
private static final String PROFORMA_MASTER_ID_NULL_MESSAGE = "شناسه پروفرما نمی‌تواند null باشد";
private static final String PROFORMA_DETAIL_ID_NULL_MESSAGE = "شناسه جزئیات پروفرما نمی‌تواند null باشد";
private static final String PMS_ID_NULL_OR_EMPTY_MESSAGE = "شناسه PMS نمی‌تواند null یا خالی باشد";
private static final String BASE_BANK_MODEL_NULL_MESSAGE = "مدل پایه بانک نمی‌تواند null باشد";
private static final String BANK_TITLE_EMPTY_MESSAGE = "عنوان بانک نمی‌تواند خالی باشد";
private static final String TRADING_BANK_BRANCH_CODE_EMPTY_MESSAGE = "کد شعبه ثابت بانک عامل نمی‌تواند خالی باشد";
private static final String PROFORMA_DETAIL_NOT_FOUND_MESSAGE = "جزئیات پروفرما برای شناسه یافت نشد: ";
private static final String NO_LC_FOUND_IN_PMS_MESSAGE = "هیچ ال‌سی در PMS برای شناسه یافت نشد ";
private static final int LC_TYPE_CODE = 523;
private static final int LC_STATE_CODE = 281;
private static final BigDecimal ZERO = BigDecimal.ZERO;
	
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
		if (Objects.nonNull(goodItem.getNetQuantity()) && goodItem.getNetQuantity().compareTo(ZERO) > 0) {
			return goodItem.getNetQuantity();
		}
		if (goodItem.getProformaDetailModel().getSettlementType() != null && !goodItem.getProformaDetailModel().getSettlementType().equals(SettlementType.CASH) &&
				Objects.nonNull(goodItem.getCreditQuantity()) && goodItem.getCreditQuantity().compareTo(ZERO) > 0) {
			return goodItem.getCreditQuantity();
		}
		return goodItem.getQuantity();
	}

	/**
	 * Checks if PMS export is enabled for LETTER_OF_CREDIT entity type.
	 * @return true if export is enabled, false otherwise
	 */
	private boolean isPmsExportEnabled() {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.LETTER_OF_CREDIT)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		return exportConfig.getSendPms() != null && exportConfig.getSendPms();
	}

	/**
	 * Sends a message to RabbitMQ PMS queue.
	 */
	private void sendToPmsQueue(String routingKey, String responseRoutingKey, Long lcId, Object payload) {
		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
				routingKey,
				new PMSLcDTO.RabbitListenerRequestDTO(pmsProperties.getLc().getUrl(),
						lcId, payload, responseRoutingKey));
	}

	/**
	 * Logs security information for LC operations.
	 */
	private void logLcOperation(String operation, Long lcId, Long proformaGoodItemId) {
		log.info("User {} - {} - {} asked for {} lc to pms for lc id : {} and proforma good item id: {}",
				SecurityUtil.getUsername(),
				SecurityUtil.getNationalCode(),
				SecurityUtil.getFullName(),
				operation,
				lcId,
				proformaGoodItemId);
	}

	/**
	 * Retrieves LC model by ID with proper error handling.
	 */
	private LcModel getLcModelById(Long lcId) {
		return lcRepository.findById(lcId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()CONFIG_NOT_FOUND_MESSAGE));
	}

	/**
	 * Retrieves LC model by PMS LC ID with proper error handling.
	 */
	private LcModel getLcModelByPmsId(String pmsId) {
		return lcRepository.findFirstByPmsLcId(pmsId).orElseThrow(
				() -> new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()LC_NOT_FOUND_BY_PMS_ID_MESSAGE + pmsId));
	}

	/**
	 * Validates that the LC model is not null and has required fields.
	 */
	private void validateLcModel(LcModel model) {
		if (model == null) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()LC_MODEL_NULL_MESSAGE);
		}
		if (model.getId() == null) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()LC_ID_NULL_MESSAGE);
		}
		if (!StringUtils.hasText(model.getLcNo())) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()LC_NUMBER_EMPTY_MESSAGE);
		}
		if (model.getProformaMasterId() == null) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()PROFORMA_MASTER_ID_NULL_MESSAGE);
		}
		if (model.getProformaDetailId() == null) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()PROFORMA_DETAIL_ID_NULL_MESSAGE);
		}
	}

	/**
	 * Validates that the proforma good item model is not null.
	 */
	private void validateProformaGoodItem(ProformaGoodItemModel proformaGoodItemModel) {
		if (proformaGoodItemModel == null) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()INVALID_PROFORMA_GOOD_ITEM_MESSAGE + "null");
		}
		if (proformaGoodItemModel.getId() == null) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()INVALID_PROFORMA_GOOD_ITEM_MESSAGE + "شناسه null است");
		}
		if (!StringUtils.hasText(proformaGoodItemModel.getGoodName())) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()INVALID_PROFORMA_GOOD_ITEM_MESSAGE + "نام کالا خالی است");
		}
	}

	/**
	 * Validates that the PMS ID is not null or empty.
	 */
	private void validatePmsId(String pmsId) {
		if (!StringUtils.hasText(pmsId)) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()PMS_ID_NULL_OR_EMPTY_MESSAGE);
		}
	}

	/**
	 * Validates that the proforma master ID is not null.
	 */
	private void validateProformaMasterId(Long proformaMasterId) {
		if (proformaMasterId == null) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()PROFORMA_MASTER_ID_NULL_MESSAGE);
		}
	}

	/**
	 * Validates that the issuing bank model is not null.
	 */
	private void validateIssuingBank(IssuingBankModel issuingBankModel) {
		if (issuingBankModel == null) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()INVALID_BANK_MODEL_MESSAGE);
		}
		if (issuingBankModel.getBaseBankModel() == null) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()BASE_BANK_MODEL_NULL_MESSAGE);
		}
		if (!StringUtils.hasText(issuingBankModel.getBaseBankModel().getBankTitle())) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()BANK_TITLE_EMPTY_MESSAGE);
		}
	}

	/**
	 * Validates that the trading bank model is not null.
	 */
	private void validateTradingBank(TradingBankModel tradingBankModel) {
		if (tradingBankModel == null) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()INVALID_TRADING_BANK_MESSAGE);
		}
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()TRADING_BANK_BRANCH_CODE_EMPTY_MESSAGE);
		}
	}

	@Override
	public void createPMSLc(Long proformaMasterId, String username, boolean resend) {
		if (!isPmsExportEnabled()) {
			return;
		}

		validateProformaMasterId(proformaMasterId);
		List<LcModel> lcList = lcRepository.findByMasterId(proformaMasterId);
		if (lcList.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()LC_NOT_FOUND_BY_MASTER_ID_MESSAGE + proformaMasterId);
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
				log.error("Failed to send LC model to PMS for LC id: {}", lc.getId(), e);
				throw new InternalSaleCustomException.ApplicationServerException(e.getMessage());
			}
		});
	}

	@Override
	public void createPMSLc(Long lcID, boolean resend) throws IOException {
		LcModel lcModel = getLcModelById(lcID);
		if (!resend && Objects.nonNull(lcModel.getPmsLcId())) {
			log.info("LC id {} already sent to PMS with pms lc id {}, skipping resend",
					lcModel.getId(), lcModel.getPmsLcId());
			return;
		}
		sendLcModelToPMS(lcModel, SecurityUtil.getUsername());
	}

	@Override
	public void sendLcModelToPMS(LcModel model, String username) {
		if (!isPmsExportEnabled()) {
			return;
		}

		validateLcModel(model);
		PMSLcDTO.Create o = buildPmsLcCreateDto(model, username);
		ProformaGoodItemModel proformaGoodItemModel = getPerformaGoodItemModel(model);
		validateProformaGoodItem(proformaGoodItemModel);
		logLcOperation("send", model.getId(), proformaGoodItemModel.getId());
		
		sendToPmsQueue(rabbitConfigPMSProperties.getQueues().getLc().getRoutingKey(),
				rabbitConfigPMSProperties.getQueues().getLc().getResponseRoutingKey(),
				model.getId(), o);
	}

	@Override
	public void updatePmsLc(String pmsId, String username) {
		if (!isPmsExportEnabled()) {
			return;
		}

		validatePmsId(pmsId);
		LcModel model = getLcModelByPmsId(pmsId);
		PmsLcModel pmsLcModel = pmsLcRepository.findById(model.getPmsLcId()).orElseThrow(() -> new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()
				NO_LC_FOUND_IN_PMS_MESSAGE + model.getPmsLcId()
		));
		log.info("updatePmsLc pmslc before update was {}", pmsLcModel);
		
		ProformaGoodItemModel proformaGoodItemModel = getPerformaGoodItemModel(model);
		validateProformaGoodItem(proformaGoodItemModel);
		PMSLcDTO.Update o = buildPmsLcUpdateDto(model, proformaGoodItemModel, username);
		
		sendToPmsQueue(rabbitConfigPMSProperties.getQueues().getLc().getUpdateRoutingKey(),
				rabbitConfigPMSProperties.getQueues().getLc().getUpdateResponseRoutingKey(),
				model.getId(), o);
		
		log.info("User {} - {} - {} asked for update lc in pms for pms lc id: {} lc id: {}",
				SecurityUtil.getUsername(),
				SecurityUtil.getNationalCode(),
				SecurityUtil.getFullName(),
				model.getPmsLcId(),
				model.getId()
		);
	}

	/**
	 * Retrieves ProformaDetail PMS ID with proper error handling.
	 */
	private Long getProformaDetailPmsId(Long proformaDetailId) {
		return proformaDetailRepository.findById(proformaDetailId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()
						"Proforma detail not found for id: " + proformaDetailId))
				.getPmsId();
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
				.prefactorId(getProformaDetailPmsId(model.getProformaDetailId()))
				.goodsId(goodsService.findPmsIdByGoodName(proformaGoodItemModel.getGoodName()))
				.issueDate(DateUtility.getJalaliDate(model.getLcDate()))
				.expiryDate(DateUtility.getJalaliDate(model.getLcExpiryDate()))
				.amount(getAmount(proformaGoodItemModel))
				.price(model.getTotalFinalAmount())
				.lcNumber(model.getLcNo())
				.type(LC_TYPE_CODE)
				.state(LC_STATE_CODE)
				.mark(PMSLcMarkEnum.LC)
				.bankLCMoamelehId(extractTradingPMSBankLcId(model.getTradingBankModel())).lBuild();
	}

	/**
	 * Builds PMS LC Update DTO from LC model.
	 */
	private PMSLcDTO.Update buildPmsLcUpdateDto(LcModel model, ProformaGoodItemModel proformaGoodItemModel, String username) {
		return PMSLcDTO.Update
				.lBuilder()
				.id(model.getPmsLcId())
				.user(pmsProperties.getPreFactor().getUser())
				.pass(pmsProperties.getPreFactor().getPass())
				.username(resolveUsername(username))
				.customerId(extractCustomerId(proformaGoodItemModel))
				.bankLCId(extractIssuingPMSBankLcId(model.getIssuingBankModel()))
				.prefactorId(getProformaDetailPmsId(model.getProformaDetailId()))
				.goodsId(goodsService.findPmsIdByGoodName(proformaGoodItemModel.getGoodName()))
				.issueDate(DateUtility.getJalaliDate(model.getLcDate()))
				.expiryDate(DateUtility.getJalaliDate(model.getLcExpiryDate()))
				.amount(getAmount(proformaGoodItemModel))
				.price(model.getTotalFinalAmount())
				.lcNumber(model.getLcNo())
				.type(LC_TYPE_CODE)
				.state(LC_STATE_CODE)
				.mark(PMSLcMarkEnum.LC)
				.bankLCMoamelehId(extractTradingPMSBankLcId(model.getTradingBankModel()))
				.lBuild();
	}

	/**
	 * Handles PMS LC response from RabbitMQ for both save and update operations.
	 */
	private void handlePmsLcResponse(String message, String operation) throws JsonProcessingException {
		PMSLcDTO.RabbitListenerResponseDTO response = objectMapper.readValue(message,
				PMSLcDTO.RabbitListenerResponseDTO.class);
		
		if (response.getRequest() == null || response.getRequest().getId() == null) {
			log.warn("Ignoring malformed PMS LC {} response without request id: {}", operation, message);
			return;
		}
		
		lcRepository.findById(response.getRequest().getId()).ifPresent(lc -> {
			if (response.getResponse() == null || response.getResponse().getId() == null) {
				log.warn("PMS LC {} response had empty PMS id for LC id {}", operation, response.getRequest().getId());
				return;
			}
			lc.setPmsLcId(response.getResponse().getId());
			lcRepository.save(lc);
			log.info("PMS LC id {}d from response for LC id {}", operation, lc.getId());
		});
		
		if (!lcRepository.existsById(response.getRequest().getId())) {
			log.error("Could not find detail for {} response PMS LC: {}", operation, message);
		}
	}

	private ProformaGoodItemModel getPerformaGoodItemModel(LcModel model) {
		return proformaGoodItemRepository.findLatestActiveItemWithProformaMasterId(model.getProformaMasterId());
	}

	private String resolveUsername(String username) {
		return username == null ? pmsProperties.getLc().getDefaultPmsUser() : username;
	}

	private String extractCustomerId(ProformaGoodItemModel goodItem) {
		if (goodItem == null) {
			throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()INVALID_PROFORMA_GOOD_ITEM_MESSAGE + "null");
		}

		if (goodItem.getProformaDetailModel() != null && goodItem.getProformaDetailModel().getProformaMasterModel() != null) {
			String nationalCode = goodItem.getProformaDetailModel().getProformaMasterModel().getNationalCode();
			String economicCode = goodItem.getProformaDetailModel().getProformaMasterModel().getEconomicCode();
			return pmsCustomerRepository.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(economicCode, nationalCode)
					.orElseThrow(() -> new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()"مشتری با شناسه " + goodItem.getProformaDetailModel()
							.getProformaMasterModel().getNationalCode() + " وجود ندارد")).getId();
		}
		throw new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()CONTRACT_NOT_FOUND_MESSAGE);
	}

	private String extractIssuingPMSBankLcId(IssuingBankModel issuingBankModel) {
		validateIssuingBank(issuingBankModel);

		PMSBankLcModel lcBank = bankLcRepository
				.findFirstByBankAndFixedBranchCode(
						issuingBankModel.getBaseBankModel().getBankTitle(),
						issuingBankModel.getFixedBranchCode()).orElseThrow(() -> new InternalSaleCustomException.ValidationException
						("بانک صادرکننده در PMS یافت نشد: " + issuingBankModel.getId() + " " +
								issuingBankModel.getBaseBankModel().getBankTitle(),
								issuingBankModel.getBranchCode())));
		return lcBank.getId();
	}

	private String extractTradingPMSBankLcId(TradingBankModel tradingBankModel) {
		validateTradingBank(tradingBankModel);

		log.error(tradingBankModel.toString());

		String tradingBankTitle = tradingBankModel.getBankTitle() != null
				? tradingBankModel.getBankTitle().toLowerCase()
				: "";

		IssuingBankModel bank = issuingBankRepository.findAll()
				.stream()
				.filter(ib -> {
					var baseBank = ib.getBaseBankModel();
					if (baseBank == null || baseBank.getBankTitle() == null) {
						return false;
					}

					String issuingBankTitle = baseBank.getBankTitle().toLowerCase();

					// LIKE behavior
					return issuingBankTitle.contains(tradingBankTitle)
							|| tradingBankTitle.contains(issuingBankTitle);
				})
				.findFirst()
				.orElseThrow(() ->
						new InternalSaleCustomException.ValidationException("شناسه بانک صادرکننده یافت نشد: بانک=" + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()
								)
						));

		log.error(bank.toString());

		return extractIssuingPMSBankLcId(bank);
	}

	@RabbitListener(queues = "${rabbitmq.config.pms.queues.lc.response-queue}")
	void savePmsLC(String message) throws JsonProcessingException {
		if (!isPmsExportEnabled()) {
			return;
		}

		log.info("Saving PMS LC ID: {}", message);
		handlePmsLcResponse(message, "save");
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
		if (!isPmsExportEnabled()) {
			return;
		}

		log.info("Updating PMS LC from response queue: {}", message);
		handlePmsLcResponse(message, "update");
	}

}
