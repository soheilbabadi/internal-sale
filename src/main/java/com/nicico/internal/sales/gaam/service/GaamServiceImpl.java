package com.nicico.internal.sales.gaam.service;


import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.bank.model.IssuingBankModel;
import com.nicico.internal.sales.bank.repository.BaseBankRepository;
import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
import com.nicico.internal.sales.broker.model.BrokerModel;
import com.nicico.internal.sales.broker.repository.BrokerRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.gaam.dto.*;
import com.nicico.internal.sales.gaam.mapper.GaamMapper;
import com.nicico.internal.sales.gaam.mapper.GaamReportMapper;
import com.nicico.internal.sales.gaam.mapper.GaamRevokingMapper;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.gaam.repository.GaamAuditRepository;
import com.nicico.internal.sales.gaam.repository.GaamReadyRevokingRepository;
import com.nicico.internal.sales.gaam.repository.GaamReportRepository;
import com.nicico.internal.sales.gaam.repository.GaamRepository;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import com.nicico.internal.sales.lc.dto.request.BrokerEmailRequest;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.notification.service.NotificationService;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import com.nicico.internal.sales.wf.service.ProcessStatusDeterminerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GaamServiceImpl implements GaamService {

	// ==================== CONSTANTS ====================
	private static final String MSG_BANK_NOT_FOUND = "بانک یافت نشد";
	private static final String MSG_ISSUER_BANK_ID_REQUIRED = "شناسه بانک صادرکننده نمی تواند خالی باشد";
	private static final String MSG_AGENT_BANK_ID_REQUIRED = "شناسه بانک عامل نمی تواند خالی باشد";
	private static final String MSG_AGENT_BANK_NOT_FOUND = "بانک عامل یافت نشد";
	private static final String MSG_PROFORMA_DETAIL_NOT_FOUND = "جزئیات پیش فاکتور یافت نشد";
	private static final String MSG_PROFORMA_MASTER_NOT_FOUND = "قرارداد فروش وجود ندارد";
	private static final String MSG_BROKER_EMAIL_MISSING = "اطلاعات تماس ایمیل کارگزار  موجود نمی باشد.";
	private static final String MSG_CONCURRENT_EXTRA_BILL_UPDATE = "اطلاعات برات همزمان توسط کاربر دیگری تغییر کرده است. لطفا مجدد تلاش کنید";
	private static final String ERROR_TRADE_NOT_FOUND = "کالای مورد نظر وجود ندارد";
	private static final String MSG_SEPAM_CODE_REQUIRED = "کد سپام نمی تواند خالی باشد";
	private static final String MSG_TREASURY_ID_REQUIRED = "شناسه خزانه داری نمی تواند خالی باشد";
	private static final String MSG_ISSUE_DATE_REQUIRED = "تاریخ صدور برات نمی تواند خالی باشد";
	private static final String MSG_ISSUE_DATE_AFTER_DUE_DATE = "تاریخ صدور برات نمی تواند بعد از تاریخ سررسید باشد";
	private static final String MSG_DUE_DATE_REQUIRED = "تاریخ سررسید نمی تواند خالی باشد";
	private static final String MSG_PROFORMA_DETAIL_ID_REQUIRED = "شناسه جزئیات پیش فاکتور نمی تواند خالی باشد";
	private static final String MSG_PROFORMA_DETAIL_CANCELED = " پیش فاکتور ابطال شده است";
	private static final String MSG_PROFORMA_NOT_ACCEPTED = "پیش فاکتور در وضعیت تایید شده نیست";
	private static final String LC_ALREADY_EXISTS = "برای این پیش فاکتور اعتبار اسنادی فعال وجود دارد";
	private static final String PROFORMA_DUPLICATE_START = "برای این پیش فاکتور قبلا اعتبار صادر شده است";
	private static final String MSG_ITEM_CANCELED_BEFORE = "این آیتم قبلا باطل شده است و امکان ابطال مجدد وجود ندارد";
	// ==================== DEPENDENCIES ====================
	private final ProformaDetailRepository proformaDetailRepository;
	private final GaamMapper gaamMapper;
	private final GaamRepository gaamRepository;
	private final IssuingBankRepository issuingBankRepository;
	private final BaseBankRepository baseBankRepository;
	private final GaamReportRepository gaamReportRepository;
	private final GaamReportMapper gaamReportMapper;
	private final ProformaMasterRepository proformaMasterRepository;
	private final GaamAuditRepository auditRepository;
	private final GaamReadyRevokingRepository gaamReadyRevokingRepository;
	private final GaamRevokingMapper gaamRevokingMapper;
	private final NotificationService notificationService;
	private final ProcessStatusDeterminerService processStatusDeterminerService;
	private final LcRepository lcRepository;
	private final BrokerRepository brokerRepository;
	private final IMETradeRepository imeTradeRepository;
	private final ExtraBillRepository extraBillRepository;
//	private final AccountingDetailService accountingDetailService;
	// ==================== PROFORMA CREATION ====================

	// ==================== BANK BILL CRUD ====================


	@Override
	public SearchDTO.SearchRs<GaamDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(gaamRepository, request, gaamMapper::toDTO);
	}


	@Override
	public SearchDTO.SearchRs<GaamReportDto.Info> searchIssueHistory(SearchDTO.SearchRq request) {
		processStatusDeterminerService.updateAllGaamAcknowledgments();
		return SearchUtil.search(gaamReportRepository, request, gaamReportMapper::toDTO);
	}


	@Transactional
	@Override
	public List<GaamDto.Info> saveAll(List<GaamRequest> requests) {
		log.debug("Saving {} extra bills", requests.size());
		if (requests.isEmpty()) return Collections.emptyList();
		List<GaamModel> models = requests.stream().map(this::prepareGaamBound).toList();
		List<GaamModel> savedModels = gaamRepository.saveAllAndFlush(models);
		return savedModels.stream().map(gaamMapper::toDTO).toList();
	}

	private GaamModel prepareGaamBound(GaamRequest request) {
		ValidateGaamRequest(request);
		var issuerBank = issuingBankRepository.findById(request.getIssuerBankId()).orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));
		var agentBank = baseBankRepository.findById(request.getAgentBankId()).orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_AGENT_BANK_NOT_FOUND));

		try {
			GaamModel model = gaamRepository.findById(request.getId())
					.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_NOT_FOUND));
			model.setIssueDate(request.getIssueDate());
			model.setDueDate(request.getDueDate());
			model.setSepamCode(request.getSepamCode());
			model.setTreasuryId(request.getTreasuryId());
			model.setAgentBankId(agentBank.getId());
			model.setAgentBankName(agentBank.getBankTitle());
			model.setIssuerBankId(request.getIssuerBankId());
			model.setIssuerBankName(issuerBank.getBankName());
			model.setBranchCode(issuerBank.getBranchCode());
			model.setBranchName(issuerBank.getBranchName());
			model.setPaymentCity(issuerBank.getCity());
			model.setAcknowledgment(Acknowledgment.RECKONING);
			model.setExtraBillFileId(request.getExtraBillFileId());
			return model;

		} catch (ObjectOptimisticLockingFailureException ex) {
			log.warn("Concurrent extra bill update detected for detailId={}", request.getProformaDetailId(), ex);

			throw new InternalSaleCustomException.ValidationException(MSG_CONCURRENT_EXTRA_BILL_UPDATE);
		}
	}


	@Transactional
	@Override
	public GaamDto.Info save(GaamRequest request) {
		log.debug("Saving extra bill for detailId: {}", request.getProformaDetailId());

		// Validate mandatory fields
		ValidateGaamRequest(request);

		// اعتبارسنجی و یافتن موجودیت ها
		var issuerBank = issuingBankRepository.findById(request.getIssuerBankId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));
		var agentBank = baseBankRepository.findById(request.getAgentBankId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_AGENT_BANK_NOT_FOUND));

		// ساخت و ذخیره مدل

		try {
			GaamModel model = gaamRepository.findById(request.getId())
					.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_NOT_FOUND));

			model.setIssueDate(request.getIssueDate());
			model.setDueDate(request.getDueDate());
//			model.setNosaCode(request.getNosaCode());
			model.setSepamCode(request.getSepamCode());
			model.setTreasuryId(request.getTreasuryId());
			model.setAgentBankId(agentBank.getId());
			model.setAgentBankName(agentBank.getBankTitle());
			model.setIssuerBankId(request.getIssuerBankId());
			model.setIssuerBankName(issuerBank.getBankName());
			model.setBranchCode(issuerBank.getBranchCode());
			model.setBranchName(issuerBank.getBranchName());
			model.setPaymentCity(issuerBank.getCity());
			model.setAcknowledgment(Acknowledgment.RECKONING);
			model.setExtraBillFileId(request.getExtraBillFileId());
			GaamModel savedModel = gaamRepository.saveAndFlush(model);

			log.info("Extra bill saved successfully with id: {}", savedModel.getId());
			return gaamMapper.toDTO(savedModel);
		} catch (Exception ex) {
			log.warn("Concurrent extra bill update detected for id={}", request.getId(), ex);
			throw new InternalSaleCustomException.ValidationException(MSG_CONCURRENT_EXTRA_BILL_UPDATE);
		}
	}


	@Override
	public List<GaamDto.Info> getByMasterId(Long proformaMasterId) {
//		processStatusDeterminerService.updateExtraBillAcknowledgment(proformaMasterId);

		return gaamRepository.findAllByProformaMasterId(proformaMasterId).stream()
				.map(gaamMapper::toDTO)
				.toList();
	}


	// ==================== PRIVATE HELPER METHODS ====================


	/**
	 * Validates the ProformaBankBillRequest for mandatory fields
	 */
	private void ValidateGaamRequest(GaamRequest request) {

		ProformaDetailModel detail = proformaDetailRepository.findById(request.getProformaDetailId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_NOT_FOUND));

		if (detail.getProformaReversalStatus() == ProformaReversalStatus.CANCELED) {
			throw new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_CANCELED);
		}


		List<ExtraBankBillModel> bills = extraBillRepository.findAllByProformaMasterId(request.getProformaDetailId());

		long nonCanceledCount = bills.stream().filter(bill -> bill.getWorkflowApproveStatus() != WorkflowApproveStatus.CANCELED).count();
		if (nonCanceledCount > 0) {
			throw new InternalSaleCustomException.ValidationException(PROFORMA_DUPLICATE_START);
		}

		ProformaMasterModel master = proformaMasterRepository.findById(detail.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_MASTER_NOT_FOUND));

		if (master.getWorkflowApproveStatus() != WorkflowApproveStatus.ACCEPTED) {
			throw new InternalSaleCustomException.ValidationException(MSG_PROFORMA_NOT_ACCEPTED);
		}


		List<LcModel> lcs = lcRepository.findAllByProformaMasterId(master.getId());

		long activeLcCount = lcs.stream()
				.filter(lc -> lc.getWorkflowApproveStatus() != WorkflowApproveStatus.CANCELED)
				.count();

		if (activeLcCount > 0) {
			throw new InternalSaleCustomException.ValidationException(LC_ALREADY_EXISTS);
		}

		if (request.getIssuerBankId() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_ISSUER_BANK_ID_REQUIRED);
		}
		if (request.getAgentBankId() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_AGENT_BANK_ID_REQUIRED);
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
	 * بروزرسانی فایل های پیوست برات
	 * این متد فقط فیلدهای extraBillFileId و dispatchAttachmentId را بروزرسانی می کند
	 */

	@Transactional
	@Override
	public GaamDto.Info updateGaamFiles(GaamFileUpdateDto updateDto) {
		// یافتن برات بر اساس شناسه
		GaamModel bill = gaamRepository.findById(updateDto.getId())
				.orElseThrow(() -> new RuntimeException("برات با شناسه " + updateDto.getId() + " یافت نشد"));

		// بروزرسانی فیلدهای مورد نظر

		if (updateDto.getDispatchAttachmentId() != null) {
			bill.setDispatchAttachmentId(updateDto.getDispatchAttachmentId());
			gaamRepository.save(bill);
		}

		// ذخیره تغییرات

		// تبدیل به DTO و بازگشت
		return gaamMapper.toDTO(bill);
	}


	@Transactional
	@Override
	public void sendReckoningEmail(Long gaamId) {

		GaamModel gaamModel = gaamRepository.findById(gaamId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));

		markAllGaamAsReckoning(gaamModel.getProformaMasterId());

		ProformaDetailModel detail = proformaDetailRepository.findById(gaamModel.getProformaDetailId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_NOT_FOUND));

		BrokerEmailRequest emailRequest = buildGaamBrokerEmailRequest(detail);
		String emailContent = generateGaamBrokerEmailContent(gaamId);
		sendGaamBrokerReckoningEmail(emailRequest, emailContent);
	}


	public void sendGaamBrokerReckoningEmail(BrokerEmailRequest emailRequest, String emailContent) {
		log.info("Generated GAAM broker reckoning email content for broker: {} - Content: {}",
				emailRequest.getBrokerName(), emailContent);
		notificationService.sendEmailForLcBroker(emailRequest, emailContent);
	}


	public BrokerEmailRequest buildGaamBrokerEmailRequest(ProformaDetailModel detail) {
		markAllGaamAsReckoning(detail.getProformaMasterId());
		var masterModel = proformaMasterRepository.findById(detail.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_MASTER_NOT_FOUND));
		var broker = fetchBrokerForTrade(masterModel.getTradeId());

		BrokerEmailRequest request = new BrokerEmailRequest();

		request.setContractNo(proformaMasterRepository.findById(detail.getProformaMasterId())
				.map(ProformaMasterModel::getContractNo).get());
		request.setContractDate(detail.getContractDate());
		request.setQuantity(proformaMasterRepository.findById(detail.getProformaMasterId())
				.map(m -> m.getTotalQuantity().longValue()).get());
		request.setCustomerName(proformaMasterRepository.findById(detail.getProformaMasterId())
				.map(ProformaMasterModel::getCustomerName).get());
		request.setGoodName(proformaMasterRepository.findById(detail.getProformaMasterId())
				.map(ProformaMasterModel::getGoodName).get());
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


	public void markAllGaamAsReckoning(Long proformaMasterId) {
		List<GaamModel> billModels =
				gaamRepository.findAllByProformaMasterId(proformaMasterId);

		if (billModels == null || billModels.isEmpty()) {
			log.warn("No GAAM items found for proformaMasterId: {}", proformaMasterId);
			return;
		}


		for (GaamModel item : billModels) {
			boolean oldReckoningSend = item.isReckoningSend();
			if (!oldReckoningSend) {
				Date newReckoningSendDate = new Date();
				item.setReckoningSend(true);
				item.setReckoningSendDate(newReckoningSendDate);
				item.setAcknowledgment(Acknowledgment.RECKONING);

			}
		}

		gaamRepository.saveAll(billModels);

	}


	@Override
	public String generateGaamBrokerEmailContent(long gaamId) {

		GaamModel billModel = gaamRepository.findById(gaamId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));


		ProformaDetailModel detail = proformaDetailRepository.findById(billModel.getProformaDetailId()).orElseThrow(
				() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_NOT_FOUND));
		BrokerEmailRequest dto = buildGaamBrokerEmailRequest(detail);
		return "کارگزاری محترم " + dto.getBrokerName() + " : قرارداد شماره " + dto.getContractNo() +
				"  مورخ  " + dto.getContractDate() + " جهت خرید " + dto.getQuantity() +
				" کیلوگرم محصول " + dto.getGoodName() + " توسط شرکت:  " + dto.getCustomerName() +
				" جهت تسویه مورد تایید می باشد";
	}


	@Override
	public Map<String, List<UserTaskReportDTO>> getUserTasksReport(Long gaamId) {

		processStatusDeterminerService.updateAllExtraBillAcknowledgments();
		return processStatusDeterminerService.getGaamSummaryReport(gaamId);

	}


	@Override
	public ProcessInstanceHistory getHistoryDetail(Long gaamId) {
		processStatusDeterminerService.updateAllExtraBillAcknowledgments();
		return processStatusDeterminerService.getGaamHistoryDetail(gaamId);
	}


	@Transactional
	@Override
	public GaamDto.Info update(UpdateGaamRequest updateExtraBillRequest) {
		GaamModel bill = gaamRepository.findById(updateExtraBillRequest.getId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));

		var agentBank = baseBankRepository.findById(updateExtraBillRequest.getAgentBankId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_AGENT_BANK_NOT_FOUND));

		var issuerBank = issuingBankRepository.findById(updateExtraBillRequest.getIssuerBankId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));

		bill.setIssuerBankId(issuerBank.getId());
		bill.setIssuerBankName(issuerBank.getBankName());
		bill.setBranchCode(issuerBank.getBranchCode());
		bill.setBranchName(issuerBank.getBranchName());
		bill.setPaymentCity(issuerBank.getCity());
		bill.setAgentBankId(agentBank.getId());
		bill.setAgentBankName(agentBank.getBankTitle());

		bill.setNosaCode(updateExtraBillRequest.getNosaCode());
		bill.setSepamCode(updateExtraBillRequest.getSepamCode());
		bill.setTreasuryId(updateExtraBillRequest.getTreasuryId());
		bill.setIssueDate(updateExtraBillRequest.getIssueDate());
		bill.setDueDate(updateExtraBillRequest.getDueDate());

		GaamModel savedBill = gaamRepository.save(bill);
		log.info("Extra bill updated successfully with id: {}", savedBill.getId());
		return gaamMapper.toDTO(savedBill);
	}


	@Transactional(readOnly = true)
	@Override
	public List<GaamAuditDto> getAuditHistory(Long gaamId) {
		boolean existBill = gaamRepository.existsById(gaamId);
		if (!existBill) {
			throw new InternalSaleCustomException.ValidationException(
					"برات با شناسه " + gaamId + " یافت نشد");
		}
		log.info("Fetching audit history for Extra Bill ID: {}", gaamId);
		return auditRepository.getAuditHistory(gaamId);
	}


	@Override
	public SearchDTO.SearchRs<GaamReportDto.Info> findReadyReckoning(SearchDTO.SearchRq request) {
		SearchDTO.SearchRq searchRq = request == null ? new SearchDTO.SearchRq() : request;
		return SearchUtil.search(gaamReadyRevokingRepository, searchRq, gaamRevokingMapper::toDTO);
	}


	@Override
	public void cancel(GaamCancelRequest request) {

		GaamModel gaamModel = gaamRepository.findById(request.getId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));

		if (gaamModel.getWorkflowApproveStatus() == WorkflowApproveStatus.REVERSAL) {
			throw new InternalSaleCustomException.ValidationException(MSG_ITEM_CANCELED_BEFORE);
		}
		List<GaamModel> all = gaamRepository.findAllByProformaMasterId(gaamModel.getProformaMasterId());

		all.forEach(model -> {
			model.setCancelDate(new Date());
			model.setCancellationReason(request.getCancellationReason());
			model.setWorkflowApproveStatus(WorkflowApproveStatus.REVERSAL);
			model.setDescription(buildCancellationRecord(request));

		});
		gaamRepository.saveAll(all);
	}


	private String buildCancellationRecord(GaamCancelRequest request) {
		String timestamp = DateUtility.getJalaliDate(new Date());
		String userFullName = SecurityUtil.getFullName();
		String notes = request.getDescription() != null ? request.getDescription() : "ندارد";

		return String.format(
				"""
						سابقه ابطال اوراق گام
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

	@Transactional
	@Override
	public String createDetail(Long id) {
//		GaamModel gaam = gaamRepository.findById(id)
//				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_NOT_FOUND));
//
//		Long issuerBankId = gaam.getIssuerBankId();
//		if (issuerBankId == null) {
//			throw new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND);
//		}
//
//		var bank = issuingBankRepository.findById(issuerBankId)
//				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));
//
//		String bankCode = bank.getBankCode();
//		String yearSuffix = DateUtility.currentYearLast2();
//		String detailName = buildGaamDetailName(gaam, bank);
//
//		var response = accountingDetailService.generateAndCreateFinancialInstrumentDetail(
//				FinancialInstrumentType.GAM,
//				bankCode,
//				yearSuffix,
//				detailName,
//				null
//		);
//
//		if (response != null && response.getCode() != null) {
//			gaam.setNosaCode(response.getCode());
//			gaamRepository.save(gaam);
//			return response.getCode();
//		}

		return null;
	}

	private String buildGaamDetailName(GaamModel gaam, IssuingBankModel bank) {
		String bankName = bank.getBankName() != null ? bank.getBankName() : (gaam.getIssuerBankName() != null ? gaam.getIssuerBankName() : "");
		String sepamCode = gaam.getSepamCode() != null ? gaam.getSepamCode() : "";

		String customerName = "";
		if (gaam.getProformaMasterId() != null) {
			var masterOpt = proformaMasterRepository.findById(gaam.getProformaMasterId());
			if (masterOpt.isPresent() && masterOpt.get().getCustomerName() != null) {
				customerName = masterOpt.get().getCustomerName();
			}
		}

		StringBuilder detailNameBuilder = new StringBuilder("اوراق گام");
		if (!sepamCode.isBlank()) {
			detailNameBuilder.append(" ").append(sepamCode.trim());
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