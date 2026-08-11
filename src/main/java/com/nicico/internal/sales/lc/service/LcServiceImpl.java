package com.nicico.internal.sales.lc.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.EOperator;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.lc.dto.LcAuditDto;
import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.lc.dto.LcFilesDto;
import com.nicico.internal.sales.lc.dto.LcMapper;
import com.nicico.internal.sales.lc.dto.request.LcBrokerEmailRequest;
import com.nicico.internal.sales.lc.dto.request.LcCancelRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateAcceptedLcRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateStartedLcRequest;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcAuditRepository;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import com.nicico.internal.sales.wf.service.ProcessStatusDeterminerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
	private static final String READY_RECKONING_CONTRACT_DATE_FROM = "1405/03/01";


	private final LcRepository lcRepository;
	private final LcAuditRepository lcAuditRepository;
	private final LcMapper lcMapper;
	private final LcValidationService lcValidationService;
	private final LcServiceHelper lcServiceHelper;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProcessStatusDeterminerService processStatusDeterminerService;


	@Override
	public LcDto.Info updateStartedLc(UpdateStartedLcRequest lcRequest) {


		LcModel lcModel = lcServiceHelper.findLcModel(lcRequest.getProformaId());
		ProformaDetailModel detailModel = lcServiceHelper.findProformaDetail(lcRequest.getProformaId());

		var masterModel = proformaMasterRepository.findById(lcModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_SALES_CONTRACT_NOT_FOUND));

		lcServiceHelper.validateAndAdjustLcDate(lcRequest, detailModel);
		var tradingBank = lcServiceHelper.findBankBranch(lcRequest.getTradingBankId(), lcModel.getTradingBankId());
		var issuingBank = lcServiceHelper.findIssuingBank(lcRequest.getIssuerBankId(), lcModel.getIssuerBankId());


		Date expireDate = calculateExpireDate(lcRequest);
		lcServiceHelper.populateLcModel(lcModel, lcRequest, detailModel, masterModel, tradingBank, issuingBank, expireDate);
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

		ProformaDetailModel proformaDetail = lcServiceHelper.findProformaDetail(lc.getProformaDetailId());

		lcServiceHelper.validateAndAdjustLcDate(updateAcceptedLcRequest, proformaDetail);

		lcServiceHelper.updateTradingBankIfPresent(lc, updateAcceptedLcRequest);
		lcServiceHelper.updateIssuingBankIfPresent(lc, updateAcceptedLcRequest);
		lcServiceHelper.updateLcDetailsIfPresent(lc, updateAcceptedLcRequest);

		LcModel savedLc = lcRepository.save(lc);
		return lcMapper.toDTO(savedLc);
	}


	@Override
	public LcFilesDto updateLcFiles(LcFilesDto lcFilesDto) {

		LcModel lcModel = lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(lcFilesDto.getProformaId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_LC_NOT_FOUND));

		lcServiceHelper.validateDispatchFileRequirement(lcModel, lcFilesDto.getDispatchFileId());
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

		lcServiceHelper.markAllLcsAsReckoning(lcModel.getProformaMasterId());
		ProformaDetailModel detail = lcServiceHelper.findProformaDetail(lcModel.getProformaDetailId());
		var broker = lcServiceHelper.fetchBrokerForTrade(masterModel.getTradeId());
		LcBrokerEmailRequest emailRequest = lcServiceHelper.buildLcBrokerEmailRequest(detail, broker);
		String emailContent = generateLcBrokerEmailContent(emailRequest);
		lcServiceHelper.sendLcBrokerReckoningEmail(emailRequest, emailContent);
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
		rootCriteria.getCriteria().add(new SearchDTO.CriteriaRq()
				.setFieldName("contractDate")
				.setOperator(EOperator.greaterThan)
				.setValue(READY_RECKONING_CONTRACT_DATE_FROM));

		return SearchUtil.search(lcRepository, searchRq, lcMapper::toDTO);
	}

	@Override
	public String generateLcBrokerEmailContent(LcBrokerEmailRequest dto) {
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
	public Acknowledgment determineAcknowledgment(Long lcId) {
		return processStatusDeterminerService.determineAcknowledgment(lcId);
	}


	@Override
	public String generateLcBrokerEmailContent(long lcId) {

		LcModel lcModel = lcRepository.findById(lcId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_LC_NOT_FOUND));


		var masterModel = proformaMasterRepository.findById(lcModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_SALES_CONTRACT_NOT_FOUND));

		lcServiceHelper.markAllLcsAsReckoning(lcModel.getProformaMasterId());
		ProformaDetailModel detail = lcRepository.getDetailByLcId(lcId).get();
		var broker = lcServiceHelper.fetchBrokerForTrade(masterModel.getTradeId());
		LcBrokerEmailRequest emailRequest = lcServiceHelper.buildLcBrokerEmailRequest(detail, broker);
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
		LcModel lcModel = lcServiceHelper.findLcModel(request.getLcId());
		List<LcModel> lcModelList = lcRepository.findByMasterId(lcModel.getProformaMasterId());

		lcModelList.forEach(model -> lcServiceHelper.cancelLcModel(model, request));
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

}
