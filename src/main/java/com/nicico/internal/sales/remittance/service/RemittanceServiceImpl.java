package com.nicico.internal.sales.remittance.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.enums.SettlementType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.dto.*;
import com.nicico.internal.sales.remittance.mapper.RemittanceGoodItemMapper;
import com.nicico.internal.sales.remittance.mapper.RemittanceProformaDataProviderMapper;
import com.nicico.internal.sales.remittance.mapper.RemittanceTradeDataProviderMapper;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceProformaDataProviderRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceTradeDataProviderRepository;
import com.nicico.internal.sales.wf.dto.RemittanceVariablesInput;
import com.nicico.internal.sales.wf.service.RemittanceProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RemittanceServiceImpl implements RemittanceService {
	private static final String DEFAULT_PLACEHOLDER = "-";
	private static final Date DEFAULT_DATE = new Date(0);
	private final RemittanceMasterMapper mapper;
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
	private final ProformaMasterRepository proformaMasterRepository;


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
		LcModel lcModel = findLcById(remittance.getLcId());
		RemittanceVariablesInput workflowInput = buildProformaWorkflowInput(remittance, lcModel);
		var process = remittanceProcessService.startProcess(workflowInput);
		remittance.setProcessId(process.getId());
		remittance.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
		remittanceMasterRepository.save(remittance);
		return mapper.toDTO(remittance);
	}

	@Override
	public SearchDTO.SearchRs<RemittanceTradeDataProviderDto.Info> searchFromTrade(SearchDTO.SearchRq request) {
		return SearchUtil.search(remittanceTradeDataProviderRepository, request, remittanceTradeDataProviderMapper::toDTO);
	}

	@Override
	public SearchDTO.SearchRs<RemittanceProformaDataProviderDto.Info> searchFromProforma(SearchDTO.SearchRq request) {
		return SearchUtil.search(remittanceProformaDataProviderRepository, request, remittanceProformaDataProviderMapper::toDTO);
	}

	@Override
	public RemittanceMasterDto.Info getDetailById(Long masterId) {
		remittanceProcessService.refreshRemittanceStatus();
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
		ProformaMasterModel performaMaster = findLatestProformaMasterByContractNo(contractNo);
		List<RemittanceMasterModel> remittances = remittanceMasterRepository
				.findAllByContractNoAndPaymentCodeAndCustomerId(
						String.valueOf(performaMaster.getContractNo()),
						performaMaster.getPaymentCode(),
						performaMaster.getCustomerId());
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

	private RemittanceVariablesInput buildBaseWorkflowInput(RemittanceMasterModel remittance) {
		RemittanceVariablesInput input = new RemittanceVariablesInput();
		input.setRemittanceNumber(remittance.getRemittanceNumber());
		input.setCustomerName(remittance.getCustomerName());
		input.setGoodId(remittance.getGoodId());
		input.setGoodName(remittance.getGoodName());
		input.setContractNo(String.valueOf(remittance.getContractNo()));
		input.setContractDate(String.valueOf(remittance.getContractDate()));
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

	private RemittanceVariablesInput buildProformaWorkflowInput(RemittanceMasterModel remittance, LcModel lcModel) {
		RemittanceVariablesInput input = buildBaseWorkflowInput(remittance);
		input.setProformaIssueDate(remittance.getProformaDate());
		input.setTradingBank(formatBankInfo(lcModel.getTradingBankTitle(), lcModel.getTradingBankBranchTitle()));
		input.setLcNo(remittance.getLcNo());
		input.setIssuerBank(formatBankInfo(lcModel.getIssuerBankName(), lcModel.getIssuerBankBranchName()));
		input.setLcDate(lcModel.getLcDate());
		return input;
	}

	private RemittanceMasterModel findRemittanceById(Long remittanceId) {
		return remittanceMasterRepository.findById(remittanceId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("حواله وجود ندارد"));
	}

	private LcModel findLcById(Long lcId) {
		return lcRepository.findById(lcId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("اعتبار اسنادی وجود ندارد"));
	}

	private ProformaMasterModel findLatestProformaMasterByContractNo(Long contractNo) {
		return proformaMasterRepository.findAllByContractNoOrderByIdDesc(contractNo)
				.stream()
				.findFirst()
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("پیش فاکتور وجود ندارد"));
	}

	private String formatBankInfo(String bankName, String branchName) {
		return bankName + " - " + branchName;
	}
}
