package com.nicico.internal.sales.extrabill.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.model.BaseBankModel;
import com.nicico.internal.sales.bank.model.IssuingBankModel;
import com.nicico.internal.sales.bank.repository.BaseBankRepository;
import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.dto.*;
import com.nicico.internal.sales.extrabill.model.ProformaBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.extrabill.repository.ProformaBankBillReportRepository;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExtraBillServiceImpl implements ExtraBillService {

	// ==================== CONSTANTS ====================
	private static final String MSG_TRADE_NOT_FOUND = "آگهی عرضه وجود ندارد";
	private static final String MSG_BANK_NOT_FOUND = "بانک یافت نشد";
	private static final String MSG_PROFORMA_DETAIL_NOT_FOUND = "جزئیات پیش فاکتور یافت نشد";
	private static final String DEFAULT_PLACEHOLDER = "-";

	// ==================== DEPENDENCIES ====================
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaBankBillMapper mapper;
	private final ExtraBillRepository repository;
	private final IssuingBankRepository issuingBankRepository;
	private final BaseBankRepository baseBankRepository;
	private final ProformaBankBillReportRepository proformaBankBillReportRepository;
	private final ProformaBankBillReportMapper proformaBankBillReportMapper;

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
		ProformaDetailModel detailModel = findProformaDetail(request.getProformaDetailId());
		var issuerBank = findIssuingBank(request.getIssuerBankId());
		var agentBank = findBaseBank(request.getAgentBankId());

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
	 * یافتن جزئیات پیش فاکتور
	 */
	private ProformaDetailModel findProformaDetail(Long detailId) {
		return proformaDetailRepository.findById(detailId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_DETAIL_NOT_FOUND));
	}

	/**
	 * یافتن بانک صادرکننده
	 */
	private IssuingBankModel findIssuingBank(Long bankId) {
		return issuingBankRepository.findById(bankId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));
	}

	/**
	 * یافتن بانک عامل
	 */
	private BaseBankModel findBaseBank(Long bankId) {
		return baseBankRepository.findById(bankId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_BANK_NOT_FOUND));
	}

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
				// اطلاعات تاریخ
				.issueDate(request.getIssueDate())
				.dueDate(request.getDueDate())
				// کدها
				.nosaCode(request.getNosaCode())
				.sepamCode(request.getSepamCode())
				.treasuryId(request.getTreasuryId())
				// بانک عامل
				.agentBankId(request.getAgentBankId())
				.agentBankName(agentBank.getBankTitle())
				// بانک صادرکننده
				.issuerBankName(issuerBank.getBankName())
				.branchCode(issuerBank.getBranchCode())
				.branchName(issuerBank.getBranchName())
				.paymentCity(issuerBank.getCity())
				// ارتباط با پیش فاکتور
				.proformaDetailId(detailModel.getId())
				.proformaMasterId(detailModel.getProformaMasterId())
				.contractNo(masterModel.getContractNo())
				.tradeId(masterModel.getTradeId())
				// وضعیت
				.processId(DEFAULT_PLACEHOLDER)
				.reversalProcessId(DEFAULT_PLACEHOLDER)
				.workflowApproveStatus(WorkflowApproveStatus.DRAFT)
				.acknowledgment(Acknowledgment.UNKNOWN)
				.extraBillFileId(request.getExtraBillFileId())
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

}