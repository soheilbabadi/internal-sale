//package com.nicico.internal.sales.pms.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.nicico.copper.core.SecurityUtil;
//import com.nicico.internal.sales.bank.model.IssuingBankModel;
//import com.nicico.internal.sales.bank.model.TradingBankModel;
//import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
//import com.nicico.internal.sales.common.properties.PMSProperties;
//import com.nicico.internal.sales.common.properties.RabbitConfigPMSProperties;
//import com.nicico.internal.sales.exception.InternalSaleCustomException;
//import com.nicico.internal.sales.export.enums.EntityTypeEnum;
//import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
//import com.nicico.internal.sales.goods.service.GoodsService;
//import com.nicico.internal.sales.extrabill.dto.ProformaBankBillDto;
//import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
//import com.nicico.internal.sales.pms.repository.PMSCustomerRepository;
//import com.nicico.internal.sales.proforma.enums.SettlementType;
//import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
//import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
//import com.nicico.internal.sales.proforma.repository.ProformaGoodItemRepository;
//import com.nicico.internal.sales.util.date.DateUtility;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Objects;
//
//@Service
//@AllArgsConstructor
//@Slf4j
//public class PMExtraBillServiceImpl implements PMExtraBillService {
//private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";
//private static final String EXTRA_BILL_NOT_FOUND_BY_MASTER_ID_MESSAGE = "ال‌سی با شناسه پروفرما یافت نشد ";
//private static final String EXTRA_BILL_NOT_FOUND_BY_PMS_ID_MESSAGE = "ال‌سی با شناسه ال‌سی PMS یافت نشد ";
//private static final String CUSTOMER_NOT_FOUND_MESSAGE = "مشتری با شناسه ";
//private static final String CONTRACT_NOT_FOUND_MESSAGE = "شماره قرارداد وجود ندارد";
//private static final String INVALID_PROFORMA_GOOD_ITEM_MESSAGE = "کالای پروفرما نامعتبر است: ";
//private static final String INVALID_BANK_MODEL_MESSAGE = "مدل بانک نمی‌تواند null باشد";
//private static final String INVALID_TRADING_BANK_MESSAGE = "مدل بانک عامل نمی‌تواند null باشد";
//private static final String EXTRA_BILL_MODEL_NULL_MESSAGE = "مدل ال‌سی نمی‌تواند null باشد";
//private static final String EXTRA_BILL_ID_NULL_MESSAGE = "شناسه ال‌سی نمی‌تواند null باشد";
//private static final String EXTRA_BILL_NUMBER_EMPTY_MESSAGE = "شماره ال‌سی نمی‌تواند خالی باشد";
//private static final String PROFORMA_MASTER_ID_NULL_MESSAGE = "شناسه پروفرما نمی‌تواند null باشد";
//private static final String PROFORMA_DETAIL_ID_NULL_MESSAGE = "شناسه جزئیات پروفرما نمی‌تواند null باشد";
//private static final String PMS_ID_NULL_OR_EMPTY_MESSAGE = "شناسه PMS نمی‌تواند null یا خالی باشد";
//private static final String BASE_BANK_MODEL_NULL_MESSAGE = "مدل پایه بانک نمی‌تواند null باشد";
//private static final String BANK_TITLE_EMPTY_MESSAGE = "عنوان بانک نمی‌تواند خالی باشد";
//private static final String TRADING_BANK_BRANCH_CODE_EMPTY_MESSAGE = "کد شعبه ثابت بانک عامل نمی‌تواند خالی باشد";
//private static final String PROFORMA_DETAIL_NOT_FOUND_MESSAGE = "جزئیات پروفرما برای شناسه یافت نشد: ";
//private static final String NO_EXTRA_BILL_FOUND_IN_PMS_MESSAGE = "هیچ ال‌سی در PMS برای شناسه یافت نشد ";
//private static final String ISSUING_BANK_NOT_FOUND_MESSAGE = "شناسه بانک صادرکننده یافت نشد: بانک=";
//private static final String EXTRA_BILL_SEND_FAILURE_MESSAGE = "خطا در ارسال به PMS: ";
//private static final String JSON_PROCESSING_ERROR_MESSAGE = "خطا در پردازش JSON: ";
//private static final int EXTRA_BILL_TYPE_CODE = 523;
//private static final int EXTRA_BILL_STATE_CODE = 281;
//private static final BigDecimal ZERO = BigDecimal.ZERO;
//
//	private final ExtraBillRepository extraBillRepository;
//	private final GoodsService goodsService;
//	private final PMSCustomerRepository pmsCustomerRepository;
//	private final IssuingBankRepository issuingBankRepository;
//	private final ProformaDetailRepository proformaDetailRepository;
//	private final PMSProperties pmsProperties;
//	private final RabbitTemplate rabbitTemplate;
//	private final ObjectMapper objectMapper;
//	private final RabbitConfigPMSProperties rabbitConfigPMSProperties;
//	private final ExportNotificationConfigRepository exportNotificationConfigRepository;
//	private final ProformaGoodItemRepository proformaGoodItemRepository;
//
//
//	private static BigDecimal getAmount(ProformaGoodItemModel goodItem) {
//		if (Objects.nonNull(goodItem.getNetQuantity()) && goodItem.getNetQuantity().compareTo(ZERO) > 0) {
//			return goodItem.getNetQuantity();
//		}
//		if (goodItem.getProformaDetailModel().getSettlementType() != null && !goodItem.getProformaDetailModel().getSettlementType().equals(SettlementType.CASH) &&
//				Objects.nonNull(goodItem.getCreditQuantity()) && goodItem.getCreditQuantity().compareTo(ZERO) > 0) {
//			return goodItem.getCreditQuantity();
//		}
//		return goodItem.getQuantity();
//	}
//
//	/**
//	 * Checks if PMS export is enabled for LETTER_OF_CREDIT entity type.
//	 * @return true if export is enabled, false otherwise
//	 */
//	private boolean isPmsExportEnabled() {
//		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.LETTER_OF_CREDIT)
//				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
//		log.info(exportConfig.toString());
//		return exportConfig.getSendPms() != null && exportConfig.getSendPms();
//	}
//
//	/**
//	 * Sends a message to RabbitMQ PMS queue.
//	 */
//	private void sendToPmsQueue(String routingKey, String responseRoutingKey, Long extraBillId, Object payload) {
//		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
//				routingKey,
//				new PMSExtraBillDTO.RabbitListenerRequestDTO(pmsProperties.getExtraBill().getUrl(),
//						extraBillId, payload, responseRoutingKey));
//	}
//
//	/**
//	 * Logs security information for EXTRA_BILL operations.
//	 */
//	private void logExtraBillOperation(String operation, Long extraBillId, Long proformaGoodItemId) {
//		log.info("User {} - {} - {} asked for {} extraBill to pms for extraBill id : {} and proforma good item id: {}",
//				SecurityUtil.getUsername(),
//				SecurityUtil.getNationalCode(),
//				SecurityUtil.getFullName(),
//				operation,
//				extraBillId,
//				proformaGoodItemId);
//	}
//
//	/**
//	 * Retrieves EXTRA_BILL model by ID with proper error handling.
//	 */
//	private ExtraBillModel getExtraBillModelById(Long extraBillId) {
//		return extraBillRepository.findById(extraBillId)
//				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(EXTRA_BILL_NOT_FOUND_BY_MASTER_ID_MESSAGE + extraBillId));
//	}
//
//	/**
//	 * Retrieves EXTRA_BILL model by PMS EXTRA_BILL ID with proper error handling.
//	 */
//	private ExtraBillModel getExtraBillModelByPmsId(String pmsId) {
//		return extraBillRepository.findFirstByPmsExtraBillId(pmsId).orElseThrow(
//				() -> new InternalSaleCustomException.ValidationException(EXTRA_BILL_NOT_FOUND_BY_PMS_ID_MESSAGE + pmsId));
//	}
//
//	/**
//	 * Validates that the EXTRA_BILL model is not null and has required fields.
//	 */
//	private void validateExtraBillModel(ExtraBillModel model) {
//		if (model == null) {
//			throw new InternalSaleCustomException.ValidationException(EXTRA_BILL_MODEL_NULL_MESSAGE);
//		}
//		if (model.getId() == null) {
//			throw new InternalSaleCustomException.ValidationException(EXTRA_BILL_ID_NULL_MESSAGE);
//		}
//		if (!StringUtils.hasText(model.getExtraBillNo())) {
//			throw new InternalSaleCustomException.ValidationException(EXTRA_BILL_NUMBER_EMPTY_MESSAGE);
//		}
//		if (model.getProformaMasterId() == null) {
//			throw new InternalSaleCustomException.ValidationException(PROFORMA_MASTER_ID_NULL_MESSAGE);
//		}
//		if (model.getProformaDetailId() == null) {
//			throw new InternalSaleCustomException.ValidationException(PROFORMA_DETAIL_ID_NULL_MESSAGE);
//		}
//	}
//
//	/**
//	 * Validates that the proforma good item model is not null.
//	 */
//	private void validateProformaGoodItem(ProformaGoodItemModel proformaGoodItemModel) {
//		if (proformaGoodItemModel == null) {
//			throw new InternalSaleCustomException.ValidationException(INVALID_PROFORMA_GOOD_ITEM_MESSAGE + "null");
//		}
//		if (proformaGoodItemModel.getId() == null) {
//			throw new InternalSaleCustomException.ValidationException(INVALID_PROFORMA_GOOD_ITEM_MESSAGE + "شناسه null است");
//		}
//		if (!StringUtils.hasText(proformaGoodItemModel.getGoodName())) {
//			throw new InternalSaleCustomException.ValidationException(INVALID_PROFORMA_GOOD_ITEM_MESSAGE + "نام کالا خالی است");
//		}
//	}
//
//	/**
//	 * Validates that the PMS ID is not null or empty.
//	 */
//	private void validatePmsId(String pmsId) {
//		if (!StringUtils.hasText(pmsId)) {
//			throw new InternalSaleCustomException.ValidationException(PMS_ID_NULL_OR_EMPTY_MESSAGE);
//		}
//	}
//
//	/**
//	 * Validates that the proforma master ID is not null.
//	 */
//	private void validateProformaMasterId(Long proformaMasterId) {
//		if (proformaMasterId == null) {
//			throw new InternalSaleCustomException.ValidationException(PROFORMA_MASTER_ID_NULL_MESSAGE);
//		}
//	}
//
//	/**
//	 * Validates that the issuing bank model is not null.
//	 */
//	private void validateIssuingBank(IssuingBankModel issuingBankModel) {
//		if (issuingBankModel == null) {
//			throw new InternalSaleCustomException.ValidationException(INVALID_BANK_MODEL_MESSAGE);
//		}
//		if (issuingBankModel.getBaseBankModel() == null) {
//			throw new InternalSaleCustomException.ValidationException(BASE_BANK_MODEL_NULL_MESSAGE);
//		}
//		if (!StringUtils.hasText(issuingBankModel.getBaseBankModel().getBankTitle())) {
//			throw new InternalSaleCustomException.ValidationException(BANK_TITLE_EMPTY_MESSAGE);
//		}
//	}
//
//	/**
//	 * Validates that the trading bank model is not null.
//	 */
//	private void validateTradingBank(TradingBankModel tradingBankModel) {
//		if (tradingBankModel == null) {
//			throw new InternalSaleCustomException.ValidationException(INVALID_TRADING_BANK_MESSAGE);
//		}
//		if (!StringUtils.hasText(tradingBankModel.getFixedBranchCode())) {
//			throw new InternalSaleCustomException.ValidationException(TRADING_BANK_BRANCH_CODE_EMPTY_MESSAGE);
//		}
//	}
//
//	@Override
//	public void createPMSExtraBill(Long proformaMasterId, String username, boolean resend) {
//		if (!isPmsExportEnabled()) {
//			return;
//		}
//
//		validateProformaMasterId(proformaMasterId);
//		List<ExtraBillModel> extraBillList = extraBillRepository.findByMasterId(proformaMasterId);
//		if (extraBillList.isEmpty()) {
//			throw new InternalSaleCustomException.ValidationException(EXTRA_BILL_NOT_FOUND_BY_MASTER_ID_MESSAGE + proformaMasterId);
//		}
//
//		extraBillList.forEach(extraBill -> {
//			if (!resend && Objects.nonNull(extraBill.getPmsExtraBillId())) {
//				log.info("EXTRA_BILL id {} already sent to PMS with pms extraBill id {}, skipping resend",
//						extraBill.getId(), extraBill.getPmsExtraBillId());
//				return;
//			}
//			try {
//				sendExtraBillModelToPMS(extraBill, username);
//			} catch (Exception e) {
//				log.error("Failed to send EXTRA_BILL model to PMS for EXTRA_BILL id: {}", extraBill.getId(), e);
//				throw new InternalSaleCustomException.ApplicationServerException(e.getMessage());
//			}
//		});
//	}
//
//	@Override
//	public void createPMSExtraBill(Long extraBillID, boolean resend) throws IOException {
//		ExtraBillModel extraBillModel = getExtraBillModelById(extraBillID);
//		if (!resend && Objects.nonNull(extraBillModel.getPmsExtraBillId())) {
//			log.info("EXTRA_BILL id {} already sent to PMS with pms extraBill id {}, skipping resend",
//					extraBillModel.getId(), extraBillModel.getPmsExtraBillId());
//			return;
//		}
//		sendExtraBillModelToPMS(extraBillModel, SecurityUtil.getUsername());
//	}
//
//	@Override
//	public void sendExtraBillModelToPMS(ExtraBillModel model, String username) {
//		if (!isPmsExportEnabled()) {
//			return;
//		}
//
//		validateExtraBillModel(model);
//		PMSExtraBillDTO.Create o = buildPmsExtraBillCreateDto(model, username);
//		ProformaGoodItemModel proformaGoodItemModel = getPerformaGoodItemModel(model);
//		validateProformaGoodItem(proformaGoodItemModel);
//		logExtraBillOperation("send", model.getId(), proformaGoodItemModel.getId());
//
//		sendToPmsQueue(rabbitConfigPMSProperties.getQueues().getExtraBill().getRoutingKey(),
//				rabbitConfigPMSProperties.getQueues().getExtraBill().getResponseRoutingKey(),
//				model.getId(), o);
//	}
//
//	@Override
//	public void updatePmsExtraBill(String pmsId, String username) {
//		if (!isPmsExportEnabled()) {
//			return;
//		}
//
//		validatePmsId(pmsId);
//		ExtraBillModel model = getExtraBillModelByPmsId(pmsId);
//		PmsExtraBillModel pmsExtraBillModel = pmsExtraBillRepository.findById(model.getPmsExtraBillId()).orElseThrow(() -> new InternalSaleCustomException.ValidationException(
//				NO_EXTRA_BILL_FOUND_IN_PMS_MESSAGE + model.getPmsExtraBillId()
//		));
//		log.info("updatePmsExtraBill pmsextraBill before update was {}", pmsExtraBillModel);
//
//		ProformaGoodItemModel proformaGoodItemModel = getPerformaGoodItemModel(model);
//		validateProformaGoodItem(proformaGoodItemModel);
//		PMSExtraBillDTO.Update o = buildPmsExtraBillUpdateDto(model, proformaGoodItemModel, username);
//
//		sendToPmsQueue(rabbitConfigPMSProperties.getQueues().getExtraBill().getUpdateRoutingKey(),
//				rabbitConfigPMSProperties.getQueues().getExtraBill().getUpdateResponseRoutingKey(),
//				model.getId(), o);
//
//		log.info("User {} - {} - {} asked for update extraBill in pms for pms extraBill id: {} extraBill id: {}",
//				SecurityUtil.getUsername(),
//				SecurityUtil.getNationalCode(),
//				SecurityUtil.getFullName(),
//				model.getPmsExtraBillId(),
//				model.getId()
//		);
//	}
//
//	/**
//	 * Retrieves ProformaDetail PMS ID with proper error handling.
//	 */
//	private Long getProformaDetailPmsId(Long proformaDetailId) {
//		return proformaDetailRepository.findById(proformaDetailId)
//				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(PROFORMA_DETAIL_NOT_FOUND_MESSAGE + proformaDetailId))
//				.getPmsId();
//	}
//
//	private PMSExtraBillDTO.Create buildPmsExtraBillCreateDto(ExtraBillModel model, String username) {
//		ProformaGoodItemModel proformaGoodItemModel = getPerformaGoodItemModel(model);
//		return PMSExtraBillDTO.Create
//				.lBuilder()
//				.user(pmsProperties.getPreFactor().getUser())
//				.pass(pmsProperties.getPreFactor().getPass())
//				.customerId(extractCustomerId(proformaGoodItemModel))
//				.bankEXTRA_BILLId(extractIssuingPMSBankExtraBillId(model.getIssuingBankModel()))
//				.username(resolveUsername(username))
//				.prefactorId(getProformaDetailPmsId(model.getProformaDetailId()))
//				.goodsId(goodsService.findPmsIdByGoodName(proformaGoodItemModel.getGoodName()))
//				.issueDate(DateUtility.getJalaliDate(model.getExtraBillDate()))
//				.expiryDate(DateUtility.getJalaliDate(model.getExtraBillExpiryDate()))
//				.amount(getAmount(proformaGoodItemModel))
//				.price(model.getTotalFinalAmount())
//				.extraBillNumber(model.getExtraBillNo())
//				.type(EXTRA_BILL_TYPE_CODE)
//				.state(EXTRA_BILL_STATE_CODE)
//				.mark(PMSExtraBillMarkEnum.EXTRA_BILL)
//				.bankEXTRA_BILLMoamelehId(extractTradingPMSBankExtraBillId(model.getTradingBankModel())).lBuild();
//	}
//
//	/**
//	 * Builds PMS EXTRA_BILL Update DTO from EXTRA_BILL model.
//	 */
//	private PMSExtraBillDTO.Update buildPmsExtraBillUpdateDto(ExtraBillModel model, ProformaGoodItemModel proformaGoodItemModel, String username) {
//		return PMSExtraBillDTO.Update
//				.lBuilder()
//				.id(model.getPmsExtraBillId())
//				.user(pmsProperties.getPreFactor().getUser())
//				.pass(pmsProperties.getPreFactor().getPass())
//				.username(resolveUsername(username))
//				.customerId(extractCustomerId(proformaGoodItemModel))
//				.bankEXTRA_BILLId(extractIssuingPMSBankExtraBillId(model.getIssuingBankModel()))
//				.prefactorId(getProformaDetailPmsId(model.getProformaDetailId()))
//				.goodsId(goodsService.findPmsIdByGoodName(proformaGoodItemModel.getGoodName()))
//				.issueDate(DateUtility.getJalaliDate(model.getExtraBillDate()))
//				.expiryDate(DateUtility.getJalaliDate(model.getExtraBillExpiryDate()))
//				.amount(getAmount(proformaGoodItemModel))
//				.price(model.getTotalFinalAmount())
//				.extraBillNumber(model.getExtraBillNo())
//				.type(EXTRA_BILL_TYPE_CODE)
//				.state(EXTRA_BILL_STATE_CODE)
//				.mark(PMSExtraBillMarkEnum.EXTRA_BILL)
//				.bankEXTRA_BILLMoamelehId(extractTradingPMSBankExtraBillId(model.getTradingBankModel()))
//				.lBuild();
//	}
//
//	/**
//	 * Handles PMS EXTRA_BILL response from RabbitMQ for both save and update operations.
//	 */
//	private void handlePmsExtraBillResponse(String message, String operation) throws JsonProcessingException {
//		PMSExtraBillDTO.RabbitListenerResponseDTO response = objectMapper.readValue(message,
//				PMSExtraBillDTO.RabbitListenerResponseDTO.class);
//
//		if (response.getRequest() == null || response.getRequest().getId() == null) {
//			log.warn("Ignoring malformed PMS EXTRA_BILL {} response without request id: {}", operation, message);
//			return;
//		}
//
//		extraBillRepository.findById(response.getRequest().getId()).ifPresent(extraBill -> {
//			if (response.getResponse() == null || response.getResponse().getId() == null) {
//				log.warn("PMS EXTRA_BILL {} response had empty PMS id for EXTRA_BILL id {}", operation, response.getRequest().getId());
//				return;
//			}
//			extraBill.setPmsExtraBillId(response.getResponse().getId());
//			extraBillRepository.save(extraBill);
//			log.info("PMS EXTRA_BILL id {}d from response for EXTRA_BILL id {}", operation, extraBill.getId());
//		});
//
//		if (!extraBillRepository.existsById(response.getRequest().getId())) {
//			log.error("Could not find detail for {} response PMS EXTRA_BILL: {}", operation, message);
//		}
//	}
//
//	private ProformaGoodItemModel getPerformaGoodItemModel(ExtraBillModel model) {
//		return proformaGoodItemRepository.findLatestActiveItemWithProformaMasterId(model.getProformaMasterId());
//	}
//
//	private String resolveUsername(String username) {
//		return username == null ? pmsProperties.getExtraBill().getDefaultPmsUser() : username;
//	}
//
//	private String extractCustomerId(ProformaGoodItemModel goodItem) {
//		if (goodItem == null) {
//			throw new InternalSaleCustomException.ValidationException(INVALID_PROFORMA_GOOD_ITEM_MESSAGE + "null");
//		}
//
//		if (goodItem.getProformaDetailModel() != null && goodItem.getProformaDetailModel().getProformaMasterModel() != null) {
//			String nationalCode = goodItem.getProformaDetailModel().getProformaMasterModel().getNationalCode();
//			String economicCode = goodItem.getProformaDetailModel().getProformaMasterModel().getEconomicCode();
//			return pmsCustomerRepository.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(economicCode, nationalCode)
//					.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CUSTOMER_NOT_FOUND_MESSAGE + nationalCode + " وجود ندارد")).getId();
//		}
//		throw new InternalSaleCustomException.ValidationException(CONTRACT_NOT_FOUND_MESSAGE);
//	}
//
//	private String extractIssuingPMSBankExtraBillId(IssuingBankModel issuingBankModel) {
//		validateIssuingBank(issuingBankModel);
//
//		PMSBankExtraBillModel extraBillBank = bankExtraBillRepository
//				.findFirstByBankAndFixedBranchCode(
//						issuingBankModel.getBaseBankModel().getBankTitle(),
//						issuingBankModel.getFixedBranchCode()).orElseThrow(() -> new InternalSaleCustomException.ValidationException
//						("بانک صادرکننده در PMS یافت نشد: " + issuingBankModel.getId() + " " +
//								issuingBankModel.getBaseBankModel().getBankTitle(),
//								issuingBankModel.getBranchCode())));
//		return extraBillBank.getId();
//	}
//
//	private String extractTradingPMSBankExtraBillId(TradingBankModel tradingBankModel) {
//		validateTradingBank(tradingBankModel);
//
//		log.error(tradingBankModel.toString());
//
//		String tradingBankTitle = tradingBankModel.getBankTitle() != null
//				? tradingBankModel.getBankTitle().toLowerCase()
//				: "";
//
//		IssuingBankModel bank = issuingBankRepository.findAll()
//				.stream()
//				.filter(ib -> {
//					var baseBank = ib.getBaseBankModel();
//					if (baseBank == null || baseBank.getBankTitle() == null) {
//						return false;
//					}
//
//					String issuingBankTitle = baseBank.getBankTitle().toLowerCase();
//
//					// LIKE behavior
//					return issuingBankTitle.contains(tradingBankTitle)
//							|| tradingBankTitle.contains(issuingBankTitle);
//				})
//				.findFirst()
//				.orElseThrow(() ->
//					new InternalSaleCustomException.ValidationException(ISSUING_BANK_NOT_FOUND_MESSAGE + tradingBankModel.getBankTitle() + ", شعبه=" + tradingBankModel.getFixedBranchCode()
//						)
//					));
//
//		log.error(bank.toString());
//
//		return extractIssuingPMSBankExtraBillId(bank);
//	}
//
//	@RabbitListener(queues = "${rabbitmq.config.pms.queues.extraBill.response-queue}")
//	void savePmsEXTRA_BILL(String message) throws JsonProcessingException {
//		if (!isPmsExportEnabled()) {
//			return;
//		}
//
//		log.info("Saving PMS EXTRA_BILL ID: {}", message);
//		handlePmsExtraBillResponse(message, "save");
//	}
//
//
//	@Override
//	public List<ProformaBankBillDto.Info> findRemittanceExtraBillWithoutPmsId() {
//		List<ExtraBillModel> extraBillModels = extraBillRepository.findRemittanceExtraBillWithoutPmsId();
//		String username = SecurityUtil.getUsername();
//		for (ExtraBillModel extraBill : extraBillModels) {
//			Long proformaMasterId = extraBill.getProformaMasterId();
//			createPMSExtraBill(proformaMasterId, username, false);
//		}
//		return extraBillModels.stream()
//				.map(extraBillMapper::toDTO)
//				.map(dto -> new ProformaBankBillDto.Info(
//						dto.getId(),
//						dto.getProformaDetailId(),
//						dto.getProformaMasterId(),
//						dto.getTradeId(),
//						dto.getContractNo(),
//						dto.getIssuerBankId(),
//						dto.getIssuerBankName(),
//						dto.getBranchCode(),
//						dto.getBranchName(),
//						dto.getPaymentCity(),
//						dto.getAgentBankName(),
//						dto.getAgentBankId(),
//						dto.getNosaCode(),
//						dto.getSepamCode(),
//						dto.getTreasuryId(),
//						dto.getIssueDate(),
//						dto.getDueDate(),
//						dto.getExtraBillFileId(),
//						dto.getDispatchAttachmentId(),
//						dto.getWorkflowApproveStatus(),
//						dto.getProcessId(),
//						dto.getReversalProcessId(),
//						dto.getPmsExtraBillId(),
//						dto.getAcknowledgment(),
//						dto.isReckoningSend(),
//						dto.getReckoningSendDate(),
//						dto.getCancelDate(),
//						dto.getCancellationReason(),
//						dto.getCreatedDate(),
//						dto.getLastModifiedDate(),
//						dto.getCreatedBy(),
//						dto.getLastModifiedBy(),
//						dto.getComment()
//				))
//				.toList();
//	}
//
//	@RabbitListener(queues = "${rabbitmq.config.pms.queues.extraBill.update-response-queue}")
//	void updatePmsEXTRA_BILLFromPms(String message) throws JsonProcessingException {
//		if (!isPmsExportEnabled()) {
//			return;
//		}
//
//		log.info("Updating PMS EXTRA_BILL from response queue: {}", message);
//		handlePmsExtraBillResponse(message, "update");
//	}
//
//}
