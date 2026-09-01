package com.nicico.internal.sales.extrabill.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.EOperator;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
import com.nicico.internal.sales.broker.model.BrokerModel;
import com.nicico.internal.sales.broker.repository.BrokerRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.dto.*;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.extrabill.repository.ProformaBankBillAuditRepository;
import com.nicico.internal.sales.extrabill.repository.ProformaBankBillReportRepository;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import com.nicico.internal.sales.lc.dto.request.LcBrokerEmailRequest;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.lc.service.LcServiceHelper;
import com.nicico.internal.sales.notification.service.NotificationService;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import com.nicico.internal.sales.wf.service.ProcessStatusDeterminerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExtraBillServiceImpl implements ExtraBillService {

	// ==================== CONSTANTS ====================
	private static final String MSG_TRADE_NOT_FOUND = "آگهی عرضه وجود ندارد";
	private static final String MSG_BANK_NOT_FOUND = "بانک یافت نشد";
	private static final String MSG_PROFORMA_DETAIL_NOT_FOUND = "جزئیات پیش فاکتور یافت نشد";
	private static final String MSG_PROFORMA_MASTER_NOT_FOUND = "قرارداد فروش وجود ندارد";
	private static final String MSG_BROKER_EMAIL_MISSING = "اطلاعات تماس ایمیل کارگزار  موجود نمی باشد.";
	private static final String DEFAULT_PLACEHOLDER = "-";


	// Validation error messages
	private static final String MSG_ISSUER_BANK_ID_REQUIRED = "شناسه بانک صادرکننده نمی‌تواند خالی باشد";
	private static final String MSG_NOSA_CODE_REQUIRED = "کد تفصیلی نمی‌تواند خالی باشد";
	private static final String MSG_SEPAM_CODE_REQUIRED = "کد سپام نمی‌تواند خالی باشد";
	private static final String MSG_TREASURY_ID_REQUIRED = "شناسه خزانه‌داری نمی‌تواند خالی باشد";
	private static final String MSG_ISSUE_DATE_REQUIRED = "تاریخ صدور برات نمی‌تواند خالی باشد";
	private static final String MSG_ISSUE_DATE_AFTER_DUE_DATE = "تاریخ صدور برات نمی‌تواند بعد از تاریخ سررسید باشد";
	private static final String MSG_DUE_DATE_REQUIRED = "تاریخ سررسید نمی‌تواند خالی باشد";
	private static final String MSG_PROFORMA_DETAIL_ID_REQUIRED = "شناسه جزئیات پیش‌فاکتور نمی‌تواند خالی باشد";
	private static final String MSG_SALES_CONTRACT_NOT_FOUND = "قرارداد فروش وجود ندارد";
	private static final String MSG_DUPLICATE_PROFORMA_BILL = "برات برای این جزئیات پیش‌فاکتور قبلاً ثبت شده است";
	// ==================== DEPENDENCIES ====================
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaBankBillMapper mapper;
	private final ExtraBillRepository extraBillRepository;
	private final IssuingBankRepository issuingBankRepository;
	private final ProformaBankBillReportRepository proformaBankBillReportRepository;
	private final ProformaBankBillReportMapper proformaBankBillReportMapper;
	private final ProformaMasterRepository proformaMasterRepository;
	private final BrokerRepository brokerRepository;
	private final IMETradeRepository imeTradeRepository;
	private final NotificationService notificationService;
	private final ProformaBankBillAuditRepository auditRepository;

	private final ProcessStatusDeterminerService processStatusDeterminerService;

	private final LcServiceHelper lcServiceHelper;

	// ==================== PROFORMA CREATION ====================

	// ==================== BANK BILL CRUD ====================

	@Override
	public SearchDTO.SearchRs<ProformaBankBillDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(extraBillRepository, request, mapper::toDTO);
	}

	@Override
	public SearchDTO.SearchRs<ProformaBankBillReportDto.Info> searchReport(SearchDTO.SearchRq request) {
		return SearchUtil.search(proformaBankBillReportRepository, request, proformaBankBillReportMapper::toDTO);
	}


	@Override
	@Transactional
	public ProformaBankBillDto.Info save(ProformaBankBillRequest request) {
		log.debug("Saving extra bill for detailId: {}", request.getProformaDetailId());

		// Validate mandatory fields
		validateProformaBankBillRequest(request);

		// اعتبارسنجی و یافتن موجودیت‌ها
		var issuerBank = issuingBankRepository.findById(request.getIssuerBankId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));

		// ساخت و ذخیره مدل

		ExtraBankBillModel model = extraBillRepository.findLastInProgressByProformaDetailId(request.getProformaDetailId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_NOT_FOUND));

		model.setIssueDate(request.getIssueDate());
		model.setDueDate(request.getDueDate());
		model.setNosaCode(request.getNosaCode());
		model.setSepamCode(request.getSepamCode());
		model.setTreasuryId(request.getTreasuryId());
		model.setAgentBankId(issuerBank.getId());
		model.setAgentBankName(issuerBank.getBankName());
		model.setIssuerBankId(request.getIssuerBankId());
		model.setIssuerBankName(issuerBank.getBankName());
		model.setBranchCode(issuerBank.getBranchCode());
		model.setBranchName(issuerBank.getBranchName());
		model.setPaymentCity(issuerBank.getCity());
		model.setAcknowledgment(Acknowledgment.RECKONING);
		model.setExtraBillFileId(request.getExtraBillFileId());

		ExtraBankBillModel savedModel = extraBillRepository.save(model);

		log.info("Extra bill saved successfully with id: {}", savedModel.getId());
		return mapper.toDTO(savedModel);
	}

	@Override
	public List<ProformaBankBillDto.Info> getByMasterId(Long proformaMasterId) {
		log.debug("Getting extra bills by masterId: {}", proformaMasterId);

		return extraBillRepository.findAllByProformaMasterId(proformaMasterId).stream()
				.map(mapper::toDTO)
				.toList();
	}


	// ==================== PRIVATE HELPER METHODS ====================


	/**
	 * Validates the ProformaBankBillRequest for mandatory fields
	 */
	private void validateProformaBankBillRequest(ProformaBankBillRequest request) {
		processStatusDeterminerService.updateAllExtraBillAcknowledgments();
		if (request.getIssuerBankId() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_ISSUER_BANK_ID_REQUIRED);
		}
		if (!StringUtils.hasText(request.getNosaCode())) {
			throw new InternalSaleCustomException.ValidationException(MSG_NOSA_CODE_REQUIRED);
		}
		if (!StringUtils.hasText(request.getSepamCode())) {
			throw new InternalSaleCustomException.ValidationException(MSG_SEPAM_CODE_REQUIRED);
		}
		if (!StringUtils.hasText(request.getTreasuryId())) {
			throw new InternalSaleCustomException.ValidationException(MSG_TREASURY_ID_REQUIRED);
		}
		if (request.getIssueDate() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_ISSUE_DATE_REQUIRED);
		}
		if (request.getDueDate() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_DUE_DATE_REQUIRED);
		}
		if (request.getProformaDetailId() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_ID_REQUIRED);
		}
		if (!request.getIssueDate().before(request.getDueDate())) {
			throw new InternalSaleCustomException.ValidationException(MSG_ISSUE_DATE_AFTER_DUE_DATE);
		}
	}

	/**
	 * بروزرسانی فایل‌های پیوست برات
	 * این متد فقط فیلدهای extraBillFileId و dispatchAttachmentId را بروزرسانی می‌کند
	 */
	@Override
	@Transactional
	public ProformaBankBillDto.Info updateBillFiles(ProformaBankBillFileUpdateDto updateDto) {
		// یافتن برات بر اساس شناسه
		ExtraBankBillModel bill = extraBillRepository.findById(updateDto.getId())
				.orElseThrow(() -> new RuntimeException("برات با شناسه " + updateDto.getId() + " یافت نشد"));

		// بروزرسانی فیلدهای مورد نظر

		if (updateDto.getDispatchAttachmentId() != null) {
			bill.setDispatchAttachmentId(updateDto.getDispatchAttachmentId());
			extraBillRepository.save(bill);
		}

		// ذخیره تغییرات

		// تبدیل به DTO و بازگشت
		return mapper.toDTO(bill);
	}

	@Override
	@Transactional
	public void sendReckoningEmail(Long extraBillId) {

		ExtraBankBillModel billModel = extraBillRepository.findById(extraBillId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));

		var masterModel = proformaMasterRepository.findById(billModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_MASTER_NOT_FOUND));

		markAllBillsAsReckoning(billModel.getProformaMasterId());
		ProformaDetailModel detail = proformaDetailRepository.findById(billModel.getProformaDetailId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_NOT_FOUND));
		var broker = fetchBrokerForTrade(masterModel.getTradeId());
		LcBrokerEmailRequest emailRequest = buildExtraBillBrokerEmailRequest(detail, broker);
		String emailContent = generateExtraBillBrokerEmailContent(emailRequest);
		sendExtraBillBrokerReckoningEmail(emailRequest, emailContent);
	}

	/**
	 * علامت‌گذاری تمام برات‌های مرتبط با یک قرارداد به عنوان تسویه شده
	 */
	private void markAllBillsAsReckoning(Long proformaMasterId) {
		List<ExtraBankBillModel> billItems = extraBillRepository.findAllByProformaMasterId(proformaMasterId);

		if (billItems == null || billItems.isEmpty()) {
			log.warn("No extra bill items found for proformaMasterId: {}", proformaMasterId);
			return;
		}

		for (ExtraBankBillModel billItem : billItems) {
			if (!billItem.isReckoningSend()) {
				Date newReckoningSendDate = new Date();
				billItem.setReckoningSend(true);
				billItem.setReckoningSendDate(newReckoningSendDate);
				billItem.setAcknowledgment(Acknowledgment.RECKONING);
				extraBillRepository.save(billItem);
			}
		}
	}

	/**
	 * دریافت کارگزار مربوط به معامله
	 */
	private BrokerModel fetchBrokerForTrade(Long tradeId) {
		var sellerBrokerCode = imeTradeRepository.findSellerBrokerCodeById(tradeId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_TRADE_NOT_FOUND));
		return brokerRepository.findById(sellerBrokerCode)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BROKER_EMAIL_MISSING));
	}

	/**
	 * ساخت درخواست ایمیل برای کارگزار
	 */
	private LcBrokerEmailRequest buildExtraBillBrokerEmailRequest(ProformaDetailModel detail, BrokerModel broker) {
		var proformaMaster = proformaMasterRepository.findById(detail.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						MSG_PROFORMA_MASTER_NOT_FOUND));

		this.markAllLcsAsReckoning(proformaMaster.getId());

		LcBrokerEmailRequest request = new LcBrokerEmailRequest();
		request.setContractNo(proformaMaster.getContractNo());
		request.setContractDate(detail.getContractDate());
		request.setQuantity(proformaMaster.getTotalQuantity().longValue());
		request.setCustomerName(proformaMaster.getCustomerName());
		request.setGoodName(proformaMaster.getGoodName());
		request.setBrokerName(broker.getName());
		request.setBrokerEmail(broker.getEmail());

		return request;
	}

	/**
	 * تولید محتوای ایمیل برای کارگزار
	 */
	private String generateExtraBillBrokerEmailContent(LcBrokerEmailRequest dto) {
		return "کارگزاری محترم " + dto.getBrokerName() + " : قرارداد شماره " + dto.getContractNo() +
				"  مورخ  " + dto.getContractDate() + " جهت خرید " + dto.getQuantity() +
				" کیلوگرم محصول " + dto.getGoodName() + " توسط شرکت:  " + dto.getCustomerName() +
				" جهت تسویه مورد تایید می باشد";
	}

	@Override
	public String generateExtraBillBrokerEmailContent(long extraBillId) {
		ExtraBankBillModel billModel = extraBillRepository.findById(extraBillId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));

		var masterModel = proformaMasterRepository.findById(billModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_SALES_CONTRACT_NOT_FOUND));

		ProformaDetailModel detail = extraBillRepository.getDetailByBillId(extraBillId).orElseThrow(
				() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_NOT_FOUND));
		var broker = lcServiceHelper.fetchBrokerForTrade(masterModel.getTradeId());
		LcBrokerEmailRequest emailRequest = buildExtraBillBrokerEmailRequest(detail, broker);
		return generateExtraBillBrokerEmailContent(emailRequest);
	}

	@Override
	public Map<String, List<UserTaskReportDTO>> getUserTasksReport(Long extraBillId) {

		return processStatusDeterminerService.getProformaBankBillSummaryReport(extraBillId);

	}

	@Override
	public ProcessInstanceHistory getHistoryDetail(Long extraBillId) {
		return processStatusDeterminerService.getProformaBankBillHistoryDetail(extraBillId);
	}

	/**
	 * ارسال ایمیل تسویه به کارگزار
	 */
	private void sendExtraBillBrokerReckoningEmail(LcBrokerEmailRequest emailRequest, String emailContent) {
		log.info("Generated Extra Bill broker reckoning email content for broker: {} - Content: {}",
				emailRequest.getBrokerName(), emailContent);
		notificationService.sendEmailForLcBroker(emailRequest, emailContent);
	}

	@Override
	@Transactional
	public ProformaBankBillDto.Info updateExtraBill(UpdateExtraBillRequest updateExtraBillRequest) {
		ExtraBankBillModel bill = extraBillRepository.findById(updateExtraBillRequest.getId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));

		bill.setIssuerBankId(updateExtraBillRequest.getIssuerBankId());
		bill.setAgentBankId(updateExtraBillRequest.getAgentBankId());
		bill.setNosaCode(updateExtraBillRequest.getNosaCode());
		bill.setSepamCode(updateExtraBillRequest.getSepamCode());
		bill.setTreasuryId(updateExtraBillRequest.getTreasuryId());
		bill.setIssueDate(updateExtraBillRequest.getIssueDate());
		bill.setDueDate(updateExtraBillRequest.getDueDate());

		ExtraBankBillModel savedBill = extraBillRepository.save(bill);
		log.info("Extra bill updated successfully with id: {}", savedBill.getId());
		return mapper.toDTO(savedBill);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProformaBankBillAuditDto> getAuditHistory(Long extraBillId) {
		boolean existBill = extraBillRepository.existsById(extraBillId);
		if (!existBill) {
			throw new InternalSaleCustomException.ValidationException(
					"برات با شناسه " + extraBillId + " یافت نشد");
		}
		log.info("Fetching audit history for Extra Bill ID: {}", extraBillId);
		return auditRepository.getAuditHistory(extraBillId);
	}

	@Override
	public SearchDTO.SearchRs<ProformaBankBillDto.Info> findReadyReckoning(SearchDTO.SearchRq request) {
		processStatusDeterminerService.updateAllExtraBillAcknowledgments();
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
				.setFieldName("workflowApproveStatus")
				.setOperator(EOperator.equals)
				.setValue(WorkflowApproveStatus.IN_PROGRESS));

		return SearchUtil.search(extraBillRepository, searchRq, mapper::toDTO);
	}

	@Override
	public void markAllLcsAsReckoning(Long proformaMasterId) {

		List<ExtraBankBillModel> billModels = extraBillRepository.findAllByProformaMasterId(proformaMasterId);

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

				extraBillRepository.saveAndFlush(item);
			}

		}

	}


	@Override
	public void cancel(ExtraBillCancelRequest request) {

		ExtraBankBillModel bill = extraBillRepository.findById(request.getId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));

		List<ExtraBankBillModel> lcModelList = extraBillRepository.findAllByProformaMasterId(bill.getProformaMasterId());

		lcModelList.forEach(model -> cancelExtraBillModel(model, request));
	}


	public void cancelExtraBillModel(ExtraBankBillModel model, ExtraBillCancelRequest request) {
		model.setCancelDate(new Date());
		model.setCancellationReason(LcCancellationReason.BUYER_WITHDRAWAL);
		model.setWorkflowApproveStatus(WorkflowApproveStatus.REVERSAL);

		String cancellationRecord = buildCancellationRecord(request);
		appendCancellationRecord(model, cancellationRecord);

		extraBillRepository.save(model);
	}

	private String buildCancellationRecord(ExtraBillCancelRequest request) {
		String timestamp = DateUtility.getJalaliDate(new Date());
		String userFullName = com.nicico.copper.core.SecurityUtil.getFullName();
		String notes = request.getDescription() != null ? request.getDescription() : "ندارد";

		return String.format(
				"""
						سابقه ابطال برات الکترونیک
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
	private void appendCancellationRecord(ExtraBankBillModel model, String cancellationRecord) {
		String existingDesc = model.getDescription() != null ? model.getDescription() : "";
		if (!existingDesc.isEmpty()) {
			model.setDescription(existingDesc + "\n\n" + cancellationRecord);
		} else {
			model.setDescription(cancellationRecord);
		}
	}



}