package com.nicico.internal.sales.extrabill.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.model.BaseBankModel;
import com.nicico.internal.sales.bank.model.IssuingBankModel;
import com.nicico.internal.sales.bank.repository.BaseBankRepository;
import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
import com.nicico.internal.sales.broker.model.BrokerModel;
import com.nicico.internal.sales.broker.repository.BrokerRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.dto.*;
import com.nicico.internal.sales.extrabill.model.ProformaBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.extrabill.repository.ProformaBankBillAuditRepository;
import com.nicico.internal.sales.extrabill.repository.ProformaBankBillReportRepository;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import com.nicico.internal.sales.lc.dto.request.LcBrokerEmailRequest;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.notification.service.NotificationService;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

	// ==================== DEPENDENCIES ====================
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaBankBillMapper mapper;
	private final ExtraBillRepository repository;
	private final IssuingBankRepository issuingBankRepository;
	private final BaseBankRepository baseBankRepository;
	private final ProformaBankBillReportRepository proformaBankBillReportRepository;
	private final ProformaBankBillReportMapper proformaBankBillReportMapper;
	private final ProformaMasterRepository proformaMasterRepository;
	private final BrokerRepository brokerRepository;
	private final IMETradeRepository imeTradeRepository;
	private final NotificationService notificationService;
	private final ProformaBankBillAuditRepository auditRepository;

	// ==================== PROFORMA CREATION ====================

	// ==================== BANK BILL CRUD ====================

	@Override
	public SearchDTO.SearchRs<ProformaBankBillDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}

	@Override
	public SearchDTO.SearchRs<ProformaBankBillReportDto.Info> searchReport(SearchDTO.SearchRq request) {
		return SearchUtil.search(proformaBankBillReportRepository, request, proformaBankBillReportMapper::toDTO);
	}


	@Override
	@Transactional
	public ProformaBankBillDto.Info save(ProformaBankBillRequest request) {
		log.debug("Saving extra bill for detailId: {}", request.getProformaDetailId());

		// اعتبارسنجی و یافتن موجودیت‌ها
		ProformaDetailModel detailModel = proformaDetailRepository.findById(request.getProformaDetailId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));
		var issuerBank = issuingBankRepository.findById(request.getIssuerBankId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));
		var agentBank = baseBankRepository.findById(request.getAgentBankId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));

		// ساخت و ذخیره مدل
		ProformaBankBillModel model = buildBankBillModel(request, detailModel, issuerBank, agentBank);
		ProformaBankBillModel savedModel = repository.save(model);

		log.info("Extra bill saved successfully with id: {}", savedModel.getId());
		return mapper.toDTO(savedModel);
	}

	@Override
	public List<ProformaBankBillDto.Info> getByMasterId(Long proformaMasterId) {
		log.debug("Getting extra bills by masterId: {}", proformaMasterId);

		return repository.findAllByProformaMasterId(proformaMasterId).stream()
				.map(mapper::toDTO)
				.toList();
	}

	// ==================== PRIVATE HELPER METHODS ====================

	/**
	 * ساخت مدل بانک‌بیل
	 */
	private ProformaBankBillModel buildBankBillModel(
			ProformaBankBillRequest request,
			ProformaDetailModel detailModel,
			IssuingBankModel issuerBank,
			BaseBankModel agentBank) {

		ProformaMasterModel masterModel = detailModel.getProformaMasterModel();

		return ProformaBankBillModel.builder()
				.issueDate(request.getIssueDate())
				.dueDate(request.getDueDate())
				.nosaCode(request.getNosaCode())
				.sepamCode(request.getSepamCode())
				.treasuryId(request.getTreasuryId())
				.agentBankId(request.getAgentBankId())
				.agentBankName(agentBank.getBankTitle())
				.issuerBankName(issuerBank.getBankName())
				.branchCode(issuerBank.getBranchCode())
				.branchName(issuerBank.getBranchName())
				.paymentCity(issuerBank.getCity())
				.proformaDetailId(detailModel.getId())
				.proformaMasterId(detailModel.getProformaMasterId())
				.contractNo(masterModel.getContractNo())
				.tradeId(masterModel.getTradeId())
				.processId(DEFAULT_PLACEHOLDER)
				.reversalProcessId(DEFAULT_PLACEHOLDER)
				.workflowApproveStatus(WorkflowApproveStatus.DRAFT)
				.acknowledgment(Acknowledgment.UNKNOWN)
				.extraBillFileId(request.getExtraBillFileId())
				.issuerBankId(request.getIssuerBankId())
				.dispatchAttachmentId(null)
				.isReckoningSend(false)
				.reckoningSendDate(null)
				.pmsBillId(null)
				.cancelDate(null)
				.cancellationReason(null)
				.build();
	}

	/**
	 * بروزرسانی فایل‌های پیوست برات
	 * این متد فقط فیلدهای extraBillFileId و dispatchAttachmentId را بروزرسانی می‌کند
	 */
	@Override
	@Transactional
	public ProformaBankBillDto.Info updateBillFiles(ProformaBankBillFileUpdateDto updateDto) {
		// یافتن برات بر اساس شناسه
		ProformaBankBillModel bill = repository.findById(updateDto.getId())
				.orElseThrow(() -> new RuntimeException("برات با شناسه " + updateDto.getId() + " یافت نشد"));

		// بروزرسانی فیلدهای مورد نظر

		if (updateDto.getDispatchAttachmentId() != null) {
			bill.setDispatchAttachmentId(updateDto.getDispatchAttachmentId());
			repository.save(bill);
		}

		// ذخیره تغییرات

		// تبدیل به DTO و بازگشت
		return mapper.toDTO(bill);
	}

	@Override
	@Transactional
	public void sendReckoningEmail(Long extraBillId) {
		ProformaBankBillModel billModel = repository.findById(extraBillId)
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
		List<ProformaBankBillModel> billItems = repository.findAllByProformaMasterId(proformaMasterId);

		if (billItems == null || billItems.isEmpty()) {
			log.warn("No extra bill items found for proformaMasterId: {}", proformaMasterId);
			return;
		}

		for (ProformaBankBillModel billItem : billItems) {
			if (!billItem.isReckoningSend()) {
				Date newReckoningSendDate = new Date();
				billItem.setReckoningSend(true);
				billItem.setReckoningSendDate(newReckoningSendDate);
				billItem.setAcknowledgment(Acknowledgment.RECKONING);
				repository.save(billItem);
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
		ProformaBankBillModel bill = repository.findById(updateExtraBillRequest.getId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));

		// بروزرسانی فیلدهای بانکی در صورت وجود مقدار
		if (updateExtraBillRequest.getIssuerBankId() != null) {
			bill.setIssuerBankId(updateExtraBillRequest.getIssuerBankId());
		}

		if (updateExtraBillRequest.getAgentBankId() != null) {
			bill.setAgentBankId(updateExtraBillRequest.getAgentBankId());
			// بروزرسانی نام بانک عامل
			baseBankRepository.findById(updateExtraBillRequest.getAgentBankId())
					.ifPresent(agentBank -> bill.setAgentBankName(agentBank.getBankTitle()));
		}

		// بروزرسانی فیلدهای برات الکترونیک در صورت وجود مقدار
		if (updateExtraBillRequest.getNosaCode() != null) {
			bill.setNosaCode(updateExtraBillRequest.getNosaCode());
		}

		if (updateExtraBillRequest.getSepamCode() != null) {
			bill.setSepamCode(updateExtraBillRequest.getSepamCode());
		}

		if (updateExtraBillRequest.getTreasuryId() != null) {
			bill.setTreasuryId(updateExtraBillRequest.getTreasuryId());
		}

		if (updateExtraBillRequest.getIssueDate() != null) {
			bill.setIssueDate(updateExtraBillRequest.getIssueDate());
		}

		if (updateExtraBillRequest.getDueDate() != null) {
			bill.setDueDate(updateExtraBillRequest.getDueDate());
		}

		ProformaBankBillModel savedBill = repository.save(bill);
		log.info("Extra bill updated successfully with id: {}", savedBill.getId());
		return mapper.toDTO(savedBill);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProformaBankBillAuditDto> getAuditHistory(Long extraBillId) {
		boolean existBill = repository.existsById(extraBillId);
		if (!existBill) {
			throw new InternalSaleCustomException.ValidationException(
					"برات با شناسه " + extraBillId + " یافت نشد");
		}
		log.info("Fetching audit history for Extra Bill ID: {}", extraBillId);
		return auditRepository.getAuditHistory(extraBillId);
	}

}