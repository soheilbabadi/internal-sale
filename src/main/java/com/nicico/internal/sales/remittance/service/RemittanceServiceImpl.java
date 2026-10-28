package com.nicico.internal.sales.remittance.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.deposit.model.CashDepositModel;
import com.nicico.internal.sales.deposit.repository.CashDepositRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.gaam.repository.GaamRepository;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.SettlementType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.dto.*;
import com.nicico.internal.sales.remittance.mapper.RemittanceGoodItemMapper;
import com.nicico.internal.sales.remittance.mapper.RemittanceProformaDataProviderMapper;
import com.nicico.internal.sales.remittance.mapper.RemittanceSulfuricAcidDataProviderMapper;
import com.nicico.internal.sales.remittance.mapper.RemittanceTradeDataProviderMapper;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceProformaDataProviderRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceSulfuricAcidDataProviderRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceTradeDataProviderRepository;
import com.nicico.internal.sales.wf.detection.ProcessHistoryService;
import com.nicico.internal.sales.wf.dto.RemittanceVariablesInput;
import com.nicico.internal.sales.wf.service.RemittanceProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RemittanceServiceImpl implements RemittanceService {
	private static final String DEFAULT_PLACEHOLDER = "-";
	private static final String MSG_REMITTANCE_NOT_FOUND = "حواله وجود ندارد";
	private static final String MSG_LC_NOT_FOUND = "اعتبار اسنادی وجود ندارد";
	private static final String MSG_PROFORMA_NOT_FOUND = "پیش فاکتور وجود ندارد";
	private static final String MSG_GAAM_NOT_FOUND = "اوراق گام وجود ندارد";
	private static final String MSG_EXTRA_BILL_NOT_FOUND = "برات الکترونیک وجود ندارد";
	private static final Date DEFAULT_DATE = new Date(0);
	private final RemittanceMasterMapper mapper;
	private final RemittanceSulfuricAcidDataProviderMapper sulfuricAcidMapper;
	private final RemittanceMasterRepository remittanceMasterRepository;
	private final RemittanceDataProvider remittanceDataProvider;
	private final RemittanceValidation remittanceValidation;
	private final RemittanceGoodItemMapper remittanceGoodItemMapper;
	private final RemittanceTradeDataProviderRepository remittanceTradeDataProviderRepository;
	private final RemittanceProformaDataProviderRepository remittanceProformaDataProviderRepository;
	private final RemittanceTradeDataProviderMapper remittanceTradeDataProviderMapper;
	private final RemittanceProformaDataProviderMapper remittanceProformaDataProviderMapper;
	private final RemittanceProcessService remittanceProcessService;
	private final LcRepository lcRepository;
	private final GaamRepository gaamRepository;
	private final ExtraBillRepository extraBillRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProcessHistoryService processHistoryService;
	private final CashDepositRepository cashDepositRepository;
	private final RemittanceSulfuricAcidDataProviderRepository sulfuricAcidRepository;

	@Override
	public SearchDTO.SearchRs<RemittanceMasterDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(remittanceMasterRepository, request, mapper::toDTO);
	}

	@Override
	public RemittanceMasterDto.Info createFromTrade(RemittanceCreateDto request) {
		remittanceValidation.validateCreateTradeRemittance(request);
		RemittanceMasterModel remittance = remittanceDataProvider.getRemittanceFromTrade(request);
		remittance = remittanceMasterRepository.saveAndFlush(remittance);
		RemittanceVariablesInput workflowInput = buildTradeWorkflowInput(remittance);
		var process = remittanceProcessService.startProcess(workflowInput);
		remittance.setProcessId(process.getId());
		remittance.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
		var savedRemittance = remittanceMasterRepository.save(remittance);
		return mapper.toDTO(savedRemittance);
	}

	@Override
	public RemittanceMasterDto.Info createFromProforma(RemittanceCreateDto request) {
		remittanceValidation.validateCreateProformaRemittance(request);
		RemittanceMasterModel remittance = remittanceDataProvider.getRemittanceFromProforma(request);
		remittance = remittanceMasterRepository.save(remittance);

		RemittanceVariablesInput workflowInput = buildProformaWorkflowInput(remittance);
		var process = remittanceProcessService.startProcess(workflowInput);
		remittance.setProcessId(process.getId());
		remittance.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
		remittanceMasterRepository.save(remittance);
		return mapper.toDTO(remittance);
	}

	@Override
	public RemittanceMasterDto.Info createFromSulfuricAcid(RemittanceCreateDto request) {
		remittanceValidation.validateCreateTradeRemittance(request);
		RemittanceMasterModel remittance = remittanceDataProvider.getRemittanceFromSulfuricAcid(request);
		remittance = remittanceMasterRepository.saveAndFlush(remittance);
		RemittanceVariablesInput workflowInput = buildTradeWorkflowInput(remittance);
		var process = remittanceProcessService.startProcess(workflowInput);
		remittance.setProcessId(process.getId());
		remittance.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
		var savedRemittance = remittanceMasterRepository.save(remittance);
		return mapper.toDTO(savedRemittance);
	}

	@Override
	public SearchDTO.SearchRs<RemittanceTradeDataProviderDto.Info> searchFromTrade(SearchDTO.SearchRq request) {
		return SearchUtil.search(remittanceTradeDataProviderRepository, request, remittanceTradeDataProviderMapper::toDTO);
	}

	@Override
	public SearchDTO.SearchRs<RemittanceTradeDataProviderDto.Info> searchFromSulfuricAcid(SearchDTO.SearchRq request) {
		return SearchUtil.search(sulfuricAcidRepository, request, sulfuricAcidMapper::toDTO);
	}

	@Override
	public SearchDTO.SearchRs<RemittanceProformaDataProviderDto.Info> searchFromProforma(SearchDTO.SearchRq request) {
		return SearchUtil.search(remittanceProformaDataProviderRepository, request, remittanceProformaDataProviderMapper::toDTO);
	}

	@Override
	public RemittanceMasterDto.Info getDetailById(Long masterId) {
		RemittanceMasterModel remittance = findRemittanceById(masterId);
		List<RemittanceGoodItemDto.Info> remittanceDetails = remittance.getRemittanceGoodItemModels()
				.stream()
				.map(remittanceGoodItemMapper::toDTO)
				.toList();
		RemittanceMasterDto.Info remittanceDto = mapper.toDTO(remittance);
		remittanceDto.setRemittanceGoodItemDtos(remittanceDetails);
		return remittanceDto;
	}

	@Override
	public List<RemittanceMasterDto.Info> getByContractNo(Long contractNo) {
		ProformaMasterModel masterModel = findLatestProformaMasterByContractNo(contractNo);
		List<RemittanceMasterModel> remittances = remittanceMasterRepository
				.findAllByContractNoAndPaymentCodeAndCustomerId(
						String.valueOf(masterModel.getContractNo()),
						masterModel.getPaymentCode(),
						masterModel.getCustomerId());
		if (remittances == null || remittances.isEmpty()) {
			return Collections.emptyList();
		}
		return remittances.stream()
				.map(mapper::toDTO)
				.toList();
	}

	@Override
	@Transactional
	public RemittanceMasterDto updateDescription(RemittanceUpdateRequest request) {
		RemittanceMasterModel remittance = findRemittanceById(request.getRemittanceId());
		remittance.setDescription(request.getDescription());
		remittanceMasterRepository.save(remittance);
		return mapper.toDTO(remittance);
	}
	@Override
	public Map<String, List<UserTaskReportDTO>> getUserTasksReport(Long id) {
		return processHistoryService.getRemittanceSummaryReport(id);
	}

	@Override
	public ProcessInstanceHistory getHistoryDetail(Long id) {
		return processHistoryService.getRemittanceHistoryDetail(id);
	}
	private RemittanceVariablesInput buildBaseWorkflowInput(RemittanceMasterModel remittance) {
		RemittanceVariablesInput input = new RemittanceVariablesInput();

		input.setCustomerName(remittance.getCustomerName());
		input.setGoodId(remittance.getGoodId());
		input.setGoodName(remittance.getGoodName());
		input.setContractNo(String.valueOf(remittance.getContractNo()));
		input.setContractDate(String.valueOf(remittance.getContractDate()));
		input.setRemittanceNumber(remittance.getRemittanceNumber());
		input.setRemittanceDate(remittance.getRemittanceDate());
		input.setIssuerId(SecurityUtil.getUserId());
		input.setIssuerName(SecurityUtil.getFirstName() + " " + SecurityUtil.getLastName());
		input.setSettlementType(SettlementType.CASH.name());
		input.setRemittanceMasterId(remittance.getId());
		input.setProformaType(remittance.getProformaIssueType().name());
		input.setProformaNo(remittance.getProformaNo());
		return input;
	}

	private RemittanceVariablesInput buildTradeWorkflowInput(RemittanceMasterModel remittance) {
		RemittanceVariablesInput input = buildBaseWorkflowInput(remittance);
		input.setProformaIssueDate(DEFAULT_DATE);
		input.setTradingBank(remittance.getTradingBankTitle());
		input.setLcNo(DEFAULT_PLACEHOLDER);
		input.setIssuerBank(remittance.getIssuerBankName());
		input.setLcDate(DEFAULT_DATE);
		return input;
	}

	private RemittanceVariablesInput buildProformaWorkflowInput(RemittanceMasterModel remittance) {
		RemittanceVariablesInput input = buildBaseWorkflowInput(remittance);
		input.setProformaIssueDate(remittance.getProformaDate());

		if (remittance.getProformaIssueType() == ProformaIssueType.LETTER_OF_CREDIT_OPENING) {
			LcModel lc = findRemittanceLc(remittance);
			remittance.setLcId(lc.getId());
			remittance.setLcNo(lc.getLcNo());
			remittance.setIssuerBankId(lc.getIssuerBankId());
			remittance.setIssuerBankName(lc.getIssuerBankName());
			remittance.setIssuerBankBranchName(lc.getIssuerBankBranchName());
			remittance.setIssuerBankBranchCode(lc.getIssuerBankBranchCode());
			remittance.setLcExpiryDate(lc.getLcExpiryDate());
			remittance.setSettlementDueDate(lc.getSettlementDueDate());
			input.setLcNo(lc.getLcNo());
			input.setIssuerBank(lc.getIssuerBankName());
			input.setLcDate(lc.getLcDate());
		} else if (remittance.getProformaIssueType() == ProformaIssueType.GAM_BONDS) {
			GaamModel gaam = findRemittanceGaam(remittance);
			remittance.setLcNo(gaam.getSepamCode());
			remittance.setIssuerBankId(gaam.getIssuerBankId());
			remittance.setIssuerBankName(gaam.getIssuerBankName());
			remittance.setIssuerBankBranchName(gaam.getBranchName());
			remittance.setIssuerBankBranchCode(gaam.getBranchCode());
			remittance.setLcExpiryDate(gaam.getDueDate());
			remittance.setSettlementDueDate(gaam.getDueDate());
			input.setLcNo(gaam.getSepamCode());
			input.setIssuerBank(gaam.getIssuerBankName());
			input.setLcDate(gaam.getIssueDate());
		} else if (remittance.getProformaIssueType() == ProformaIssueType.EXTRA_BILL_OF_EXCHANGE) {
			ExtraBankBillModel extraBill = findRemittanceExtraBill(remittance);
			remittance.setLcNo(extraBill.getSepamCode());
			remittance.setIssuerBankId(extraBill.getIssuerBankId());
			remittance.setIssuerBankName(extraBill.getIssuerBankName());
			remittance.setIssuerBankBranchName(extraBill.getBranchName());
			remittance.setIssuerBankBranchCode(extraBill.getBranchCode());
			remittance.setLcExpiryDate(extraBill.getDueDate());
			remittance.setSettlementDueDate(extraBill.getDueDate());
			input.setLcNo(extraBill.getSepamCode());
			input.setIssuerBank(extraBill.getIssuerBankName());
			input.setLcDate(extraBill.getIssueDate());
		} else if (remittance.getProformaIssueType() == ProformaIssueType.CASH_DEPOSIT_RECEIPT){
			CashDepositModel cashDeposit =findRemittanceCashDeposit(remittance);
			remittance.setLcNo(DEFAULT_PLACEHOLDER);
			input.setLcDate(cashDeposit.getIssueDate());
			input.setIssuerBank(remittance.getIssuerBankName());
		}

		else {
			input.setLcNo(DEFAULT_PLACEHOLDER);
			input.setIssuerBank(remittance.getIssuerBankName());
			input.setLcDate(DEFAULT_DATE);
		}
		return input;
	}

	private LcModel findRemittanceLc(RemittanceMasterModel remittance) {
		if (remittance.getLcId() != null) {
			return findLcById(remittance.getLcId());
		}
		return lcRepository.findByProformaNo(remittance.getProformaNo())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_LC_NOT_FOUND));
	}

	private GaamModel findRemittanceGaam(RemittanceMasterModel remittance) {
		return gaamRepository.findByMasterId(remittance.getProformaMasterId())
				.stream()
				.findFirst()
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_GAAM_NOT_FOUND));
	}

	private ExtraBankBillModel findRemittanceExtraBill(RemittanceMasterModel remittance) {
		return extraBillRepository.findByMasterId(remittance.getProformaMasterId())
				.stream()
				.findFirst()
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_EXTRA_BILL_NOT_FOUND));
	}

	private CashDepositModel findRemittanceCashDeposit(RemittanceMasterModel remittance) {
		return cashDepositRepository.findAllByProformaMasterId(remittance.getProformaMasterId())
				.stream()
				.findFirst()
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_EXTRA_BILL_NOT_FOUND));

	}
	private RemittanceMasterModel findRemittanceById(Long remittanceId) {
		return remittanceMasterRepository.findById(remittanceId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_REMITTANCE_NOT_FOUND));
	}

	private LcModel findLcById(Long lcId) {
		return lcRepository.findById(lcId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_LC_NOT_FOUND));
	}

	private ProformaMasterModel findLatestProformaMasterByContractNo(Long contractNo) {
		return proformaMasterRepository.findAllByContractNoOrderByIdDesc(contractNo)
				.stream()
				.findFirst()
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_NOT_FOUND));
	}


}
