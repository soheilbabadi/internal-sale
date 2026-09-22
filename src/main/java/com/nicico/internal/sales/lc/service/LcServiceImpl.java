package com.nicico.internal.sales.lc.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.EOperator;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.accounting.dto.FinancialInstrumentType;
import com.nicico.internal.sales.accounting.service.AccountingDetailService;
import com.nicico.internal.sales.bank.model.IssuingBankModel;
import com.nicico.internal.sales.bank.model.TradingBankModel;
import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
import com.nicico.internal.sales.bank.repository.TradingBankRepository;
import com.nicico.internal.sales.broker.model.BrokerModel;
import com.nicico.internal.sales.broker.repository.BrokerRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.gaam.repository.GaamRepository;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import com.nicico.internal.sales.lc.dto.LcAuditDto;
import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.lc.dto.LcFilesDto;
import com.nicico.internal.sales.lc.dto.LcMapper;
import com.nicico.internal.sales.lc.dto.request.BrokerEmailRequest;
import com.nicico.internal.sales.lc.dto.request.LcCancelRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateAcceptedLcRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateStartedLcRequest;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcAuditRepository;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.nosa.LcNosaCodeService;
import com.nicico.internal.sales.notification.service.NotificationService;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import com.nicico.internal.sales.wf.service.ProcessStatusDeterminerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class LcServiceImpl implements LcService {
	private static final String MSG_LC_NOT_FOUND = "اعتبار اسنادی وجود ندارد";
	private static final String MSG_INVALID_DATA = "اطلاعات اعتبار اسنادی نادرست است";
	private static final String MSG_SALES_CONTRACT_NOT_FOUND = "قرارداد فروش وجود ندارد";
	private static final String MSG_PROFORMA_NOT_FOUND = "پیش فاکتور وجود ندارد";
	private static final String MSG_TRADING_BANK_NOT_FOUND = "شعبه بانک وجود ندارد";
	private static final String ERROR_TRADE_NOT_FOUND = "کالای مورد نظر وجود ندارد";
	private static final String MSG_ISSUING_BANK_NOT_FOUND = "بانک گشایش کننده وجود ندارد";
	private static final String MSG_ISSUING_BANK_NOT_FOUND_FOR_LC = "برای این اعتبار اسنادی بانک گشایش کننده وجود ندارد";
	private static final String MSG_LC_DATE_EMPTY = "تاریخ گشایش اعتبار اسنادی نمی تواند خالی باشد";
	private static final String MSG_LC_DATE_BEFORE_PROFORMA = "تاریخ گشایش اعتبار اسنادی نمی تواند قبل از تاریخ پیش فاکتور باشد";
	private static final String MSG_LC_DISPATCH_FILE_REQUIRED = "برای این اعتبار اسنادی فایل ابلاغیه فروش الزامی است";
	private static final String MSG_BROKER_EMAIL_MISSING = "اطلاعات تماس ایمیل کارگزار  موجود نمی باشد.";
	private static final int PAYMENT_DEFERRAL_NONE = 0;

	private final LcRepository lcRepository;
	private final LcAuditRepository lcAuditRepository;
	private final LcMapper lcMapper;
	private final LcValidationService lcValidationService;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProcessStatusDeterminerService processStatusDeterminerService;
	private final IssuingBankRepository issuingBankRepository;
	private final TradingBankRepository tradingBankRepository;
	private final NotificationService notificationService;
	private final BrokerRepository brokerRepository;
	private final IMETradeRepository imeTradeRepository;
	private final LcNosaCodeService lcNosaCodeService;
	private final GaamRepository gaamRepository;
	private final ExtraBillRepository extraBillRepository;
//	private final AccountingDetailService accountingDetailService;


//	public void appendCancellationRecord(LcModel model, String cancellationRecord) {
//		String existingDesc = model.getDescription() != null ? model.getDescription() : "";
//		if (!existingDesc.isEmpty()) {
//			model.setDescription(existingDesc + "\n\n" + cancellationRecord);
//		} else {
//			model.setDescription(cancellationRecord);
//		}
//	}

	public void markAllAsReckoning(Long proformaMasterId) {
		List<com.nicico.internal.sales.extrabill.model.ExtraBankBillModel> billModels =
				extraBillRepository.findAllByProformaMasterId(proformaMasterId);

		if (billModels == null || billModels.isEmpty()) {
			log.warn("No ExtraBill items found for proformaMasterId: {}", proformaMasterId);
			return;
		}


		for (ExtraBankBillModel item : billModels) {
			boolean oldReckoningSend = item.isReckoningSend();
			if (!oldReckoningSend) {
				Date newReckoningSendDate = new Date();
				item.setReckoningSend(true);
				item.setReckoningSendDate(newReckoningSendDate);
				item.setAcknowledgment(Acknowledgment.RECKONING);

			}
		}
	}

	public BrokerEmailRequest buildExtraBillBrokerEmailRequest(ProformaMasterModel proformaMaster, BrokerModel broker) {
		markAllAsReckoning(proformaMaster.getId());

		BrokerEmailRequest request = new BrokerEmailRequest();
		request.setContractNo(proformaMaster.getContractNo());
		request.setContractDate(proformaMaster.getContractDate());
		request.setQuantity(proformaMaster.getTotalQuantity().longValue());
		request.setCustomerName(proformaMaster.getCustomerName());
		request.setGoodName(proformaMaster.getGoodName());
		request.setBrokerName(broker.getName());
		request.setBrokerEmail(broker.getEmail());

		return request;
	}


	public void updateLcDetailsIfPresent(LcModel lc, UpdateAcceptedLcRequest request) {
		if (request.getLcNo() != null) {
			lc.setLcNo(request.getLcNo());
		}
		if (request.getLcDate() != null) {
			lc.setLcDate(request.getLcDate());
		}
		if (request.getLcExpiryDate() != null) {
			lc.setLcExpiryDate(request.getLcExpiryDate());
		}
		if (request.getNosaCode() != null) {
			lc.setNosaCode(request.getNosaCode());
		}
		if (request.getSettlementDueDate() != null) {
			lc.setSettlementDueDate(request.getSettlementDueDate());
		}
		if (request.getLcAttachmentId() != null) {
			lc.setLcAttachmentId(request.getLcAttachmentId());
		}
		if (request.getDispatchAttachmentId() != null) {
			lc.setDispatchAttachmentId(request.getDispatchAttachmentId());
		}
		if (request.getNotificationDocumentId() != null) {
			lc.setNotificationDocumentId(request.getNotificationDocumentId());
		}
	}

	public BrokerEmailRequest buildLcBrokerEmailRequest(ProformaDetailModel detail, BrokerModel broker) {

		var proformaMaster = proformaMasterRepository.findById(detail.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						"قرارداد فروش وجود ندارد "));

		BrokerEmailRequest request = new BrokerEmailRequest();
		request.setContractNo(proformaMaster.getContractNo());
		request.setContractDate(detail.getContractDate());
		request.setQuantity(proformaMaster.getTotalQuantity().longValue());
		request.setCustomerName(proformaMaster.getCustomerName());
		request.setGoodName(proformaMaster.getGoodName());
		request.setBrokerName(broker.getName());
		request.setBrokerEmail(broker.getEmail());
		return request;
	}


	public BrokerModel fetchBrokerForTrade(Long tradeId) {
		var sellerBrokerCode = imeTradeRepository.findSellerBrokerCodeById(tradeId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						ERROR_TRADE_NOT_FOUND));
		return brokerRepository.findById(sellerBrokerCode)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_BROKER_EMAIL_MISSING));
	}

	public void markAllLcsAsReckoning(Long proformaMasterId) {
		List<LcModel> lcItems = lcRepository.findByMasterId(proformaMasterId);

		if (lcItems == null || lcItems.isEmpty()) {
			log.warn("No LC items found for proformaMasterId: {}", proformaMasterId);
			return;
		}


		for (LcModel lcItem : lcItems) {
			boolean oldReckoningSend = lcItem.isReckoningSend();
			if (!oldReckoningSend) {
				Date newReckoningSendDate = new Date();
				lcItem.setReckoningSend(true);
				lcItem.setReckoningSendDate(newReckoningSendDate);
				lcItem.setAcknowledgment(Acknowledgment.RECKONING);

			}
		}
		lcRepository.saveAllAndFlush(lcItems);
	}

	@Override
	public LcDto.Info updateStartedLc(UpdateStartedLcRequest lcRequest) {


		LcModel lcModel = findLcModel(lcRequest.getProformaId());
		ProformaDetailModel detailModel = findProformaDetail(lcRequest.getProformaId());

		var masterModel = proformaMasterRepository.findById(lcModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_SALES_CONTRACT_NOT_FOUND));

		validateAndAdjustLcDate(lcRequest, detailModel);
		var tradingBank = findBankBranch(lcRequest.getTradingBankId());
		var issuingBank = findIssuingBank(lcRequest.getIssuerBankId());

		Date expireDate = calculateExpireDate(lcRequest);
		populateLcModel(lcModel, lcRequest, detailModel, masterModel, tradingBank, issuingBank, expireDate);
		LcModel savedLc = lcRepository.saveAndFlush(lcModel);
		return lcMapper.toDTO(savedLc);
	}


	@Override
	public Date calculateExpireDate(UpdateStartedLcRequest lcRequest) {
		return DateUtility.addJalaliMonthsToGregorianDate(lcRequest.getLcDate(), 3);

	}

	@Override
	public SearchDTO.SearchRs<LcDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(lcRepository, request, lcMapper::toDTO);
	}

	@Override
	public LcDto.Info getLcData(Long id) {
		var entity = lcRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_LC_NOT_FOUND));
		return lcMapper.toDTO(entity);
	}

	@Override
	public LcDto.Info updateCurrentAcceptedLc(UpdateAcceptedLcRequest updateAcceptedLcRequest) {

		LcModel lc = lcRepository.findByProformaNo(updateAcceptedLcRequest.getProformaNo())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_LC_NOT_FOUND));

		ProformaDetailModel proformaDetail = findProformaDetail(lc.getProformaDetailId());

		validateAndAdjustLcDate(updateAcceptedLcRequest, proformaDetail);

		updateTradingBankIfPresent(lc, updateAcceptedLcRequest);
		updateIssuingBankIfPresent(lc, updateAcceptedLcRequest);
		updateLcDetailsIfPresent(lc, updateAcceptedLcRequest);

		LcModel savedLc = lcRepository.save(lc);
		return lcMapper.toDTO(savedLc);
	}


	@Override
	public LcFilesDto updateLcFiles(LcFilesDto lcFilesDto) {

		LcModel lcModel = lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(lcFilesDto.getProformaId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_LC_NOT_FOUND));

		validateDispatchFileRequirement(lcModel, lcFilesDto.getDispatchFileId());
		lcModel.setNotificationDocumentId(lcFilesDto.getNotificationFileId());
		lcModel.setDispatchAttachmentId(lcFilesDto.getDispatchFileId());
//		lcModel.setNosaCode(lcFilesDto.getNosaCode());
		if (lcFilesDto.getDispatchFileId() != null) {
			lcModel.setDispatchAttachmentId(lcFilesDto.getDispatchFileId());
		}
		lcRepository.saveAndFlush(lcModel);
		return lcFilesDto;
	}


	@Override
	public List<LcDto.Info> getAllLcDataByProformaMasterId(Long proformaMasterId) {
		var lcModels = lcRepository.findAllByProformaMasterId(proformaMasterId);
		if (lcModels.isEmpty()) {
			return List.of();
		}

		return lcModels.stream().map(lcMapper::toDTO).toList();
	}

	@Override
	public LcDto.Info getByProformaDetailId(Long detailId) {
		return lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(detailId)
				.map(lcMapper::toDTO)
				.orElse(null);
	}

	@Override
	public List<LcDto.Info> getAllLcDataByProcessInstanceId(String processInstanceId) {
		var lcModels = lcRepository.findAllByProcessId(processInstanceId);
		if (lcModels.isEmpty()) {
			return List.of();
		}
		return lcModels.stream().map(lcMapper::toDTO).toList();
	}

	@Override
	public List<LcDto.Info> getFailedLc(Pageable pageable, Sort sort) {
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
		List<WorkflowApproveStatus> failureStatuses = List.of(
				WorkflowApproveStatus.EXCEPTION,
				WorkflowApproveStatus.DRAFT
		);
		return lcRepository.findAllByWorkflowApproveStatusIn(failureStatuses, sortedPageable)
				.stream()
				.map(lcMapper::toDTO)
				.toList();
	}

	@Override
	public List<LcAuditDto> getAuditHistory(Long lcId) {
		return lcAuditRepository.getAuditHistory(lcId);
	}

	@Override
	public void sendReckoningEmail(Long lcId) {
		LcModel lcModel = lcRepository.findById(lcId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_LC_NOT_FOUND));

		var masterModel = proformaMasterRepository.findById(lcModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_SALES_CONTRACT_NOT_FOUND));

		markAllLcsAsReckoning(lcModel.getProformaMasterId());
		ProformaDetailModel detail = findProformaDetail(lcModel.getProformaDetailId());
		var broker = fetchBrokerForTrade(masterModel.getTradeId());
		BrokerEmailRequest emailRequest = buildLcBrokerEmailRequest(detail, broker);
		String emailContent = generateLcBrokerEmailContent(emailRequest);
		sendLcBrokerReckoningEmail(emailRequest, emailContent);
	}

	@Override
	public List<LcDto.Info> findUnsentReckoning() {
		return lcRepository.findUnsentReckoning().stream()
				.map(lcMapper::toDTO)
				.toList();
	}

	@Override
	public SearchDTO.SearchRs<LcDto.Info> findReadyReckoning(SearchDTO.SearchRq request) {
		SearchDTO.SearchRq searchRq = request == null ? new SearchDTO.SearchRq() : request;
		SearchDTO.CriteriaRq rootCriteria = searchRq.getCriteria();

		if (rootCriteria == null) {
			rootCriteria = new SearchDTO.CriteriaRq()
					.setOperator(EOperator.and)
					.setCriteria(new ArrayList<>());
			searchRq.setCriteria(rootCriteria);
		} else if (rootCriteria.getCriteria() == null && rootCriteria.getFieldName() != null) {
			rootCriteria = new SearchDTO.CriteriaRq()
					.setOperator(EOperator.and)
					.setCriteria(new ArrayList<>(List.of(searchRq.getCriteria())));
			searchRq.setCriteria(rootCriteria);
		}

		if (rootCriteria.getOperator() == null) {
			rootCriteria.setOperator(EOperator.and);
		}

		if (rootCriteria.getCriteria() == null) {
			rootCriteria.setCriteria(new ArrayList<>());
		}

		rootCriteria.getCriteria().add(new SearchDTO.CriteriaRq()
				.setFieldName("acknowledgment")
				.setOperator(EOperator.notEqual)
				.setValue(Acknowledgment.REMITTANCE));
		rootCriteria.getCriteria().add(new SearchDTO.CriteriaRq()
				.setFieldName("workflowApproveStatus")
				.setOperator(EOperator.equals)
				.setValue(WorkflowApproveStatus.IN_PROGRESS));
		rootCriteria.getCriteria().add(new SearchDTO.CriteriaRq()
				.setFieldName("lcNo")
				.setOperator(EOperator.notNull));


		return SearchUtil.search(lcRepository, searchRq, lcMapper::toDTO);
	}

	@Override
	public String generateLcBrokerEmailContent(BrokerEmailRequest dto) {
		return "کارگزاری محترم " + dto.getBrokerName() + " : قرارداد شماره " + dto.getContractNo() +
				"  مورخ  " + dto.getContractDate() + " جهت خرید " + dto.getQuantity() +
				" کیلوگرم محصول " + dto.getGoodName() + " توسط شرکت:  " + dto.getCustomerName() +
				" جهت تسویه مورد تایید می باشد";
	}

	@Override
	public ProcessInstanceHistory getLcHistoryDetail(Long lcId) {
		return processStatusDeterminerService.getLcHistoryDetail(lcId);
	}

	@Override
	public void updateLcAcknowledgment(Long lcId) {
		processStatusDeterminerService.updateLcAcknowledgment(lcId);
	}


	@Override
	public String generateLcBrokerEmailContent(long lcId) {

		LcModel lcModel = lcRepository.findById(lcId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_LC_NOT_FOUND));


		var masterModel = proformaMasterRepository.findById(lcModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_SALES_CONTRACT_NOT_FOUND));

		markAllLcsAsReckoning(lcModel.getProformaMasterId());
		ProformaDetailModel detail = lcRepository.getDetailByLcId(lcId).get();
		var broker = fetchBrokerForTrade(masterModel.getTradeId());
		BrokerEmailRequest emailRequest = buildLcBrokerEmailRequest(detail, broker);
		return generateLcBrokerEmailContent(emailRequest);
	}


	@Override
	public Map<String, List<UserTaskReportDTO>> getUserTasksReport(Long lcId) {
		updateLcAcknowledgment(lcId);
		return processStatusDeterminerService.getLcSummaryReport(lcId);
	}


	@Override
	public void cancel(LcCancelRequest request) {
		validateCancelRequest(request);
		LcModel lcModel = lcRepository.findById(request.getLcId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_LC_NOT_FOUND));
		List<LcModel> lcModelList = lcRepository.findByMasterId(lcModel.getProformaMasterId());


		for (LcModel model : lcModelList) {
			model.setCancelDate(new Date());
			model.setLcCancellationReason(LcCancellationReason.BUYER_WITHDRAWAL);
			model.setWorkflowApproveStatus(WorkflowApproveStatus.REVERSAL);

			String cancellationRecord = buildCancellationRecord(request);
			appendCancellationRecord(model, cancellationRecord);

			lcRepository.save(model);
		}
	}

	private void appendCancellationRecord(LcModel model, String cancellationRecord) {
		String existingDesc = model.getDescription() != null ? model.getDescription() : "";
		if (!existingDesc.isEmpty()) {
			model.setDescription(existingDesc + "\n\n" + cancellationRecord);
		} else {
			model.setDescription(cancellationRecord);
		}
	}

	private String buildCancellationRecord(LcCancelRequest request) {
		String timestamp = DateUtility.getJalaliDate(new Date());
		String userFullName = com.nicico.copper.core.SecurityUtil.getFullName();
		String notes = request.getDescription() != null ? request.getDescription() : "ندارد";

		return String.format(
				"""
						سابقه ابطال اعتبار اسنادی
						**************************
						تاریخ و زمان ابطال: %s
						نام کاربری اقدام کننده: %s
						دلیل ابطال: %s
						توضیحات تکمیلی: %s
						وضعیت: ابطال شده
						**************************""",
				timestamp, userFullName, LcCancellationReason.BUYER_WITHDRAWAL, notes
		);
	}

	/**
	 * Validates the cancel request for errors
	 */
	private void validateCancelRequest(LcCancelRequest request) {
		List<String> errors = lcValidationService.validateCancel(request.getLcId());
		if (!errors.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(MSG_INVALID_DATA, errors);
		}
	}

	@Override
	public void updateAllAcknowledgments() {
		processStatusDeterminerService.updateAllLcAcknowledgments();
	}


	public LcModel findLcModel(Long proformaId) {
		return lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(proformaId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_LC_NOT_FOUND));
	}

	public ProformaDetailModel findProformaDetail(Long proformaId) {
		return proformaDetailRepository.findById(proformaId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_PROFORMA_NOT_FOUND));
	}


	public void validateAndAdjustLcDate(UpdateStartedLcRequest lcRequest, ProformaDetailModel proformaDetail) {
		if (lcRequest.getLcDate() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_LC_DATE_EMPTY);
		}

		Date lcDate = lcRequest.getLcDate();
		Date performaDate = proformaDetail.getPerformaDate();
		LocalDateTime lcDateTime = lcDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime performDateTime = performaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDate lcDatePart = lcDateTime.toLocalDate();
		LocalDate performDatePart = performDateTime.toLocalDate();

		if (lcDatePart.equals(performDatePart)) {
			lcRequest.setLcDate(DateUtility.toDate(performDateTime.plusMinutes(1).toLocalDate()));
		} else if (lcDate.before(performaDate)) {
			throw new InternalSaleCustomException.ValidationException(MSG_LC_DATE_BEFORE_PROFORMA);
		}
	}


	public void validateAndAdjustLcDate(UpdateAcceptedLcRequest lcRequest, ProformaDetailModel proformaDetail) {
		if (lcRequest.getLcDate() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_LC_DATE_EMPTY);
		}

		Date lcDate = lcRequest.getLcDate();
		Date performaDate = proformaDetail.getPerformaDate();
		LocalDateTime lcDateTime = lcDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime performDateTime = performaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDate lcDatePart = lcDateTime.toLocalDate();
		LocalDate performDatePart = performDateTime.toLocalDate();

		if (lcDatePart.equals(performDatePart)) {
			lcRequest.setLcDate(DateUtility.toDate(performDateTime.plusMinutes(1).toLocalDate()));
		} else if (lcDate.before(performaDate)) {
			throw new InternalSaleCustomException.ValidationException(MSG_LC_DATE_BEFORE_PROFORMA);
		}
	}

	public TradingBankModel findBankBranch(Long requestId) {

		return tradingBankRepository.findById(requestId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_TRADING_BANK_NOT_FOUND));
	}

	private IssuingBankModel findIssuingBank(long requestId) {

		return issuingBankRepository.findById(requestId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_ISSUING_BANK_NOT_FOUND));
	}

	private void populateLcModel(LcModel lcModel, UpdateStartedLcRequest lcRequest,
	                             ProformaDetailModel proformaDetail, ProformaMasterModel proformaMaster,
	                             TradingBankModel tradingBank, IssuingBankModel issuingBank, Date expireDate) {

		lcModel.setPerformaNo(proformaDetail.getPerformaNo());
		lcModel.setPerformaDate(DateUtility.getJalaliDate(proformaDetail.getPerformaDate()));
		lcModel.setProformaMasterId(proformaMaster.getId());
		lcModel.setProformaDetailId(proformaDetail.getId());

		// LC basic information
		lcModel.setContractNo(proformaMaster.getContractNo());
		lcModel.setLcNo(lcRequest.getLcNo());
		lcModel.setLcDate(lcRequest.getLcDate());
		lcModel.setLcExpiryDate(expireDate);

		// Trading bank information
		lcModel.setTradingBankId(tradingBank.getId());
		lcModel.setTradingBankTitle(tradingBank.getBankTitle());
		lcModel.setTradingBankBranchTitle(tradingBank.getBankBranchTitle());

		// Issuing bank information
		lcModel.setIssuerBankId(issuingBank.getId());
		lcModel.setIssuerBankName(issuingBank.getBankName());
		lcModel.setIssuerBankBranchName(issuingBank.getBranchName());
		lcModel.setIssuerBankBranchCode(issuingBank.getBranchCode());
		lcModel.setNosaCode(lcNosaCodeService.getNosaCode(issuingBank.getId()));

		// Additional configuration
		lcModel.setCreditExpirePeriod(proformaDetail.getCreditExpirePeriod());
		lcModel.setDeadlineDays(proformaDetail.getDeadlineDays());
		lcModel.setPaymentDeferral(PAYMENT_DEFERRAL_NONE);
		lcModel.setRequireDispatchFile(lcRequest.getRequireDispatchFile());
		lcModel.setLcAttachmentId(lcRequest.getLcAttachmentId());
		lcModel.setAcknowledgment(Acknowledgment.RECKONING);

	}

	private void validateDispatchFileRequirement(LcModel lcModel, String dispatchFileId) {
		if (Boolean.TRUE.equals(lcModel.getRequireDispatchFile()) && dispatchFileId == null) {
			throw new InternalSaleCustomException.ValidationException(
					MSG_LC_DISPATCH_FILE_REQUIRED);
		}
	}


	private void updateTradingBankIfPresent(LcModel lc, UpdateAcceptedLcRequest request) {
		if (request.getTradingBankId() != null) {
			TradingBankModel tradingBank = tradingBankRepository.findById(request.getTradingBankId())
					.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
							MSG_TRADING_BANK_NOT_FOUND));
			lc.setTradingBankTitle(tradingBank.getBankTitle());
			lc.setTradingBankBranchTitle(tradingBank.getBankBranchTitle());
			lc.setTradingBankId(tradingBank.getId());
		}
	}


	private void updateIssuingBankIfPresent(LcModel lc, UpdateAcceptedLcRequest request) {
		if (request.getIssuerBankId() != null) {
			IssuingBankModel issuingBank = issuingBankRepository.findById(request.getIssuerBankId())
					.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
							MSG_ISSUING_BANK_NOT_FOUND));
			lc.setIssuerBankName(issuingBank.getBankName());
			lc.setIssuerBankBranchName(issuingBank.getBranchName());
			lc.setIssuerBankBranchCode(issuingBank.getBranchCode());
			lc.setIssuerBankId(issuingBank.getId());
		}
	}

	private void sendLcBrokerReckoningEmail(BrokerEmailRequest brokerEmailRequest, String emailContent) {
		log.info("Generated LC broker reckoning email content for broker: {} - Content: {}",
				brokerEmailRequest.getBrokerName(), emailContent);
		notificationService.sendEmailForLcBroker(brokerEmailRequest, emailContent);
	}

	@Transactional
	@Override
	public String createDetail(Long id) {
//		LcModel lc = lcRepository.findById(id)
//				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_LC_NOT_FOUND));
//
//		Long issuerBankId = lc.getIssuerBankId();
//		if (issuerBankId == null) {
//			throw new InternalSaleCustomException.ValidationException(MSG_ISSUING_BANK_NOT_FOUND_FOR_LC);
//		}
//
//		var bank = issuingBankRepository.findById(issuerBankId)
//				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
//						MSG_ISSUING_BANK_NOT_FOUND));
//
//		String bankCode = bank.getBankCode();
//		String yearSuffix = DateUtility.currentYearLast2();
//		String detailName = buildLcDetailName(lc, bank);
//
//		var response = accountingDetailService.generateAndCreateFinancialInstrumentDetail(
//				FinancialInstrumentType.LETTER_OF_CREDIT,
//				bankCode,
//				yearSuffix,
//				detailName,
//				null
//		);
//
//		if (response != null && response.getCode() != null) {
//			lc.setNosaCode(response.getCode());
//			lcRepository.save(lc);
//			return response.getCode();
//		}

		return null;
	}

	private String buildLcDetailName(LcModel lc, IssuingBankModel bank) {
		String bankName = bank.getBankName() != null ? bank.getBankName() : (lc.getIssuerBankName() != null ? lc.getIssuerBankName() : "");
		String customerName = lc.getCustomerName() != null ? lc.getCustomerName() : "";
		String lcNo = lc.getLcNo() != null ? lc.getLcNo() : "";

		StringBuilder detailNameBuilder = new StringBuilder("اعتبار");
		if (!lcNo.isBlank()) {
			detailNameBuilder.append(" ").append(lcNo.trim());
		}
		if (!bankName.isBlank()) {
			detailNameBuilder.append(" ").append(bankName.trim());
		}
		if (!customerName.isBlank()) {
			detailNameBuilder.append(" - ").append(customerName.trim());
		}
		return detailNameBuilder.toString().trim();
	}

}
