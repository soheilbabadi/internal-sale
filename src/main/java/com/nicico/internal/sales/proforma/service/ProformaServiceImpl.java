package com.nicico.internal.sales.proforma.service;

import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.special.repository.PreciousMetalRepository;
import com.nicico.internal.sales.goods.special.service.OfferTextProcess;
import com.nicico.internal.sales.proforma.dto.*;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaGoodItemRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.trade.repository.TradeExtractRepository;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.service.ProformaProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.nicico.internal.sales.proforma.service.ProformaModelHelper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProformaServiceImpl implements ProformaService {

	// ==================== CONSTANTS ====================
	private static final String ERR_PROFORMA_ACCESS_DENIED = "شما دسترسی به این پیش فاکتور ندارید";
	private static final String ERR_PROFORMA_NOT_FOUND = "پیش فاکتور پیدا نشد";

	// ==================== DEPENDENCIES ====================
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaGoodItemRepository proformaGoodItemRepository;
	private final ProformaMasterMapper proformaMasterMapper;
	private final ProformaDetailMapper proformaDetailMapper;
	private final ProformaContractService proformaContractService;
	private final PreciousMetalRepository preciousMetalRepository;
	private final ProformaProcessService proformaProcessService;
	private final TradeExtractRepository tradeExtractRepository;
	private final ProformaValidationService proformaValidationService;
	private final OfferTextProcess offerTextProcess;
	private final ExtraBillProformaIssueService extraBillProformaIssueService;
	private final BpmsClientService bpmsClientService;

	// ==================== CREATE ====================

	@Override
	@Transactional
	public String create(PerfomaCreateRequest requestDto) {
		log.debug("Creating proforma for tradeId: {}", requestDto.getTradeId());

		// اعتبارسنجی دسترسی
		if (!proformaProcessService.canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(ERR_PROFORMA_ACCESS_DENIED);
		}

		// اعتبارسنجی داده ها
		proformaValidationService.validateProformaData(requestDto);
		proformaValidationService.validateDate(requestDto);

		// پردازش نوع خاص
		if (requestDto.getProformaIssueType() == ProformaIssueType.EXTRA_BILL_OF_EXCHANGE) {
			return extraBillProformaIssueService.create(requestDto);
		}

		if (requestDto.getProformaIssueType() == ProformaIssueType.GAM_BONDS) {
			return extraBillProformaIssueService.create(requestDto);
		}


		// ایجاد پیش فاکتور
		ProformaMasterModel model = createProformaMaster(requestDto);

		// شروع فرآیند
		startWorkflowProcess(model);

		// ذخیره نهایی
		proformaMasterRepository.saveAndFlush(model);

		log.info("Proforma created successfully with contractNo: {}", model.getContractNo());
		return model.getContractNo().toString();
	}

	@Override
	@Transactional
	public ProformaMasterModel createProformaMaster(PerfomaCreateRequest requestDto) {
		log.debug("Creating proforma master for tradeId: {}", requestDto.getTradeId());

		requestDto.setProformaIssueType(requestDto.getProformaIssueType());

		// دریافت جزئیات قرارداد
		ProformaModelResponse contractDetail = proformaContractService.getContractDetail(requestDto);
		ProformaMasterModel ProformaMasterModel = contractDetail.getMasterModel();

		// تنظیم روابط با Helper
		setupFullRelationships(ProformaMasterModel, contractDetail.getDetailModels());

		// ذخیره Master
		proformaMasterRepository.saveAndFlush(ProformaMasterModel);

		Long masterId = ProformaMasterModel.getId();
		List<ProformaDetailModel> detailModels = distinctDetails(contractDetail.getDetailModels());

		// ذخیره Detail و GoodItem ها
		saveDetailAndGoodItems(detailModels, masterId);

		ProformaMasterModel.setProformaDetailModelLists(detailModels);
		return ProformaMasterModel;
	}

	// ==================== SEARCH ====================

	@Override
	public SearchDTO.SearchRs<ProformaMasterDTO.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(proformaMasterRepository, request, proformaMasterMapper::toDTO);
	}

	@Override
	public SearchDTO.SearchRs<ProformaMasterDTO.Info> getByNationalCode(List<String> nationalCodes) {
		List<String> filteredNationalCodes = filterNationalCodes(nationalCodes);

		if (filteredNationalCodes.isEmpty()) {
			return createEmptySearchResponse();
		}

		List<ProformaMasterModel> proformas = proformaMasterRepository
				.findAllByNationalCodeInOrderByIdDesc(filteredNationalCodes);

		List<ProformaMasterDTO.Info> dtoList = proformas.stream()
				.map(proformaMasterMapper::toDTO)
				.toList();

		return createSearchResponse(dtoList);
	}


	@Override
	public List<ProformaMasterDTO.Info> getFailedProforma(Pageable pageable, Sort sort) {
		List<WorkflowApproveStatus> filter = List.of(
				WorkflowApproveStatus.EXCEPTION,
				WorkflowApproveStatus.DRAFT
		);

		return proformaMasterRepository.findAllByWorkflowApproveStatusIn(filter).stream()
				.map(proformaMasterMapper::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<ProformaMasterDTO.Info> getCancelable() {
		return proformaMasterRepository.findCancellable().stream()
				.map(proformaMasterMapper::toDTO)
				.collect(Collectors.toList());
	}

	// ==================== UTILITY ====================

	@Override
	public String getLotNumberByTradeId(Long tradeId) {
		return tradeExtractRepository.findById(tradeId)
				.map(tradeExtract -> offerTextProcess.extractLotNumber(tradeExtract.getOfferDescription()))
				.orElse("");
	}

	@Override
	@Transactional
	public void delete(long performaId) {
		ProformaMasterModel masterModel = findProformaMaster(performaId);
		List<ProformaDetailModel> detailList = masterModel.getProformaDetailModelLists();

		List<ProformaGoodItemModel> goodItemList = detailList.stream()
				.flatMap(detail -> proformaGoodItemRepository.findAllByProformaDetailModel(detail).stream())
				.toList();

		if (!goodItemList.isEmpty()) {
			proformaGoodItemRepository.deleteAll(goodItemList);
		}
		if (!detailList.isEmpty()) {
			proformaDetailRepository.deleteAll(detailList);
		}
		try {
			bpmsClientService.cancelProcessInstance(masterModel.getProcessId());
			bpmsClientService.cancelProcessInstance(masterModel.getReversalProcessId());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		proformaMasterRepository.delete(masterModel);

		log.info("Proforma with id {} deleted successfully", performaId);
	}

	@Override
	public boolean isPreciousMetal(String paymentCode) {
		try {
			GoodsModel good = proformaContractService.getGoodsModel(paymentCode);
			return preciousMetalRepository.existsById(good.getId());
		} catch (Exception e) {
			log.warn("Error checking precious metal for paymentCode: {}", paymentCode, e);
			return false;
		}
	}

	// ==================== GET DETAILS ====================

	@Override
	public ProformaResponseDto getDetailById(long id) {
		log.debug("Getting proforma detail by id: {}", id);

		proformaProcessService.refreshProformaStatus();
		ProformaMasterModel master = findProformaMaster(id);

		return buildProformaResponseFromMaster(master, proformaMasterMapper);
	}

	@Override
	public ProformaResponseDto getActiveProformaById(long id) {
		ProformaResponseDto response = getDetailById(id);

		// فیلتر کردن Detail های فعال با Helper
		response.setDetailDtoList(getActiveDetailDTOs(response.getDetailDtoList()));

		return response;
	}

	@Override
	public ProformaResponseDto getDetailByInstanceId(String instanceId) {
		log.debug("Getting proforma detail by instanceId: {}", instanceId);

		ProformaMasterModel performaMaster = proformaMasterRepository
				.findByProcessId(instanceId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(ERR_PROFORMA_NOT_FOUND));

		return buildProformaResponseFromMaster(performaMaster, proformaMasterMapper);
	}

	// ==================== REVERSAL ====================

	@Override
	@Transactional
	public String createReversal(PerformerCreateRevealRequest requestDto) {
		log.debug("Creating reversal for masterId: {}", requestDto.getMasterId());

		proformaProcessService.refreshProformaStatus();

		// اعتبارسنجی شروع برگشت
		proformaValidationService.canStartReversal(requestDto.getMasterId());

		// دریافت جزئیات برگشت
		ProformaModelResponse responseModel = proformaContractService.getContractDetailReversal(requestDto);
		ProformaMasterModel model = proformaMasterRepository.saveAndFlush(responseModel.getMasterModel());

		Long id = model.getId();

		// ذخیره جزئیات برگشت
		saveReversalDetails(responseModel, id);

		// شروع فرآیند برگشت
		startReversalWorkflow(model, responseModel, id);

		proformaMasterRepository.saveAndFlush(model);

		log.info("Reversal created successfully with contractNo: {}", model.getContractNo());
		return model.getContractNo().toString();
	}

	@Override
	public List<String> getCanceledProformaNo(Long masterId) {
		ProformaMasterModel masterModel = findProformaMaster(masterId);

		return extractProformaNumbersByStatus(
				masterModel.getProformaDetailModelLists(),
				ProformaReversalStatus.CANCELED
		);
	}

	@Override
	public List<ProformaDetailDto.Info> getCanceledByContractNo(Long contractNo) {
		return proformaMasterRepository.findAllByContractNoOrderByIdDesc(contractNo).stream()
				.filter(ProformaMasterModel::getIsReversalProcessFinal)
				.flatMap(proforma -> proforma.getProformaDetailModelLists().stream())
				.map(proformaDetailMapper::toDTO)
				.toList();
	}

	@Override
	public List<String> getEditedProformaNo(Long masterId) {
		ProformaMasterModel masterModel = findProformaMaster(masterId);

		return extractProformaNumbersByStatus(
				masterModel.getProformaDetailModelLists(),
				ProformaReversalStatus.EDITED
		);
	}

	@Override
	public boolean canStartReversal(Long masterId) {
		return proformaValidationService.canStartReversal(masterId);
	}

	// ==================== PRIVATE HELPER METHODS ====================

	/**
	 * شروع فرآیند کاری برای پیش فاکتور
	 */
	private void startWorkflowProcess(ProformaMasterModel model) {
		ProformaVariablesInput input = buildProformaVariablesInput(model);
		var process = proformaProcessService.startProformaProcess(input);

		model.setProcessId(process.getId());
		model.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
	}

	/**
	 * شروع فرآیند کاری برای برگشت
	 */
	private void startReversalWorkflow(
			ProformaMasterModel model,
			ProformaModelResponse responseModel,
			Long id) {

		ProformaVariablesInput input = ProformaVariablesInput.builder()
				.contractDate(responseModel.getDetailModels().get(0).getContractDate())
				.proformaMasterId(id)
				.goodId(model.getGoodId())
				.contractNo(String.valueOf(model.getContractNo()))
				.customerName(model.getCustomerName())
				.goodName(model.getGoodName())
				.commission(model.getCommissionPercentage())
				.build();

		var process = proformaProcessService.startProformaProcess(input);
		model.setProcessId(process.getId());
		model.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
	}

	/**
	 * ساخت ورودی فرآیند
	 */
	private ProformaVariablesInput buildProformaVariablesInput(ProformaMasterModel model) {
		return ProformaVariablesInput.builder()
				.contractDate(model.getProformaDetailModelLists().get(0).getContractDate())
				.proformaMasterId(model.getId())
				.goodId(model.getGoodId())
				.contractNo(String.valueOf(model.getContractNo()))
				.customerName(model.getCustomerName())
				.goodName(model.getGoodName())
				.commission(model.getCommissionPercentage())
				.build();
	}

	/**
	 * ذخیره Detail و GoodItem ها
	 */
	private void saveDetailAndGoodItems(List<ProformaDetailModel> detailModels, Long masterId) {
		detailModels.forEach(detailModel -> {
			detailModel.setProformaMasterId(masterId);
			proformaDetailRepository.saveAndFlush(detailModel);

			detailModel.getProformaGoodItemModels().stream()
					.distinct()
					.forEach(goodItem -> {
						goodItem.setProformaDetailId(detailModel.getId());
						proformaGoodItemRepository.saveAndFlush(goodItem);
					});
		});
	}

	/**
	 * ذخیره جزئیات برگشت
	 */
	private void saveReversalDetails(ProformaModelResponse responseModel, Long masterId) {
		responseModel.getDetailModels().forEach(item -> {
			item.setProformaMasterId(masterId);
			proformaDetailRepository.saveAndFlush(item);

			item.getProformaGoodItemModels().forEach(goodItem -> {
				goodItem.setProformaDetailId(item.getId());
				proformaGoodItemRepository.saveAndFlush(goodItem);
			});
		});
	}

	/**
	 * یافتن پیش فاکتور
	 */
	private ProformaMasterModel findProformaMaster(Long id) {
		return proformaMasterRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(ERR_PROFORMA_NOT_FOUND));
	}

	/**
	 * فیلتر کردن کدهای ملی
	 */
	private List<String> filterNationalCodes(List<String> nationalCodes) {
		if (nationalCodes == null) {
			return List.of();
		}
		return nationalCodes.stream()
				.filter(code -> code != null && !code.isBlank())
				.distinct()
				.toList();
	}

	/**
	 * ایجاد پاسخ جستجوی خالی
	 */
	private SearchDTO.SearchRs<ProformaMasterDTO.Info> createEmptySearchResponse() {
		SearchDTO.SearchRs<ProformaMasterDTO.Info> response = new SearchDTO.SearchRs<>();
		response.setList(List.of());
		response.setTotalCount(0L);
		return response;
	}

	/**
	 * ایجاد پاسخ جستجو
	 */
	private SearchDTO.SearchRs<ProformaMasterDTO.Info> createSearchResponse(
			List<ProformaMasterDTO.Info> dtoList) {

		SearchDTO.SearchRs<ProformaMasterDTO.Info> response = new SearchDTO.SearchRs<>();
		response.setList(dtoList);
		response.setTotalCount((long) dtoList.size());
		return response;
	}


}