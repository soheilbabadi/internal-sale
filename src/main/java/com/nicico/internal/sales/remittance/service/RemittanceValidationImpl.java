package com.nicico.internal.sales.remittance.service;

import com.nicico.internal.sales.deposit.model.CashDepositModel;
import com.nicico.internal.sales.deposit.repository.CashDepositRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.gaam.repository.GaamRepository;
import com.nicico.internal.sales.goods.service.GoodsService;
import com.nicico.internal.sales.goods.special.repository.PreciousMetalRepository;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.loading.repository.LoadingPlaceRepository;
import com.nicico.internal.sales.pms.service.PMSRemittanceService;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.dto.RemittanceCreateDto;
import com.nicico.internal.sales.remittance.model.RemittanceTradeDataProviderModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceTradeDataProviderRepository;
import com.nicico.internal.sales.wf.service.RemittanceProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RemittanceValidationImpl implements RemittanceValidation {
	private static final String INVALID_REMITTANCE_DATA_MESSAGE = "اطلاعات ثبت حواله نادرست است";

	private static final Set<WorkflowApproveStatus> ACTIVE_STATUSES = EnumSet.of(
			WorkflowApproveStatus.IN_PROGRESS,
			WorkflowApproveStatus.ACCEPTED,
			WorkflowApproveStatus.DRAFT
	);
	private final RemittanceTradeDataProviderRepository remittanceTradeDataProviderRepository;
	private final GoodsService goodsService;
	private final LoadingPlaceRepository loadingPlaceRepository;
	private final RemittanceMasterRepository remittanceMasterRepository;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final RemittanceProcessService remittanceProcessService;
	private final PMSRemittanceService pmsRemittanceService;
	private final LcRepository lcRepository;
	private final GaamRepository gaamRepository;
	private final ExtraBillRepository extraBillRepository;
	private final CashDepositRepository cashDepositRepository;
	private final PreciousMetalRepository preciousMetalRepository;

	@Override
	public void validateCreateTradeRemittance(RemittanceCreateDto dto) {
		validateUserCanStartProcess();
		List<String> errors = new ArrayList<>();
		RemittanceTradeDataProviderModel trade = fetchTrade(dto.getTradeId(), errors);

		pmsRemittanceService.pmsRemittanceValidationErrorList(errors, trade.getEconomicCode(), trade.getNationalCode(),
				trade.getGoodName(), trade.getSettlementDate(), trade.getFinalAmount());

		if (isActiveStatus(trade.getWorkflowApproveStatus())) {
			throw new InternalSaleCustomException.DuplicateEntityException("این کد پرداخت قبلا ثبت شده است");
		}

		if ("نامشخص".equals(trade.getSettlementTypeDesc())) {
			throw new InternalSaleCustomException.DuplicateEntityException("نوع تسویه هنوز در وضعیت نامشخص است");
		}
		remittanceMasterRepository.findAllByTradeId(dto.getTradeId()).stream()
				.filter(remittance -> isActiveStatus(remittance.getWorkflowApproveStatus()))
				.findAny()
				.ifPresent(remittance -> {
					errors.add("برای این معامله قبلا حواله ثبت شده است");
					throw new InternalSaleCustomException.ValidationException(INVALID_REMITTANCE_DATA_MESSAGE, errors);
				});
		validatePreciousMetal(dto, trade.getGoodId(), errors);
		validateLoadingPlace(dto.getLoadingPortId(), errors);
		if (!errors.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(INVALID_REMITTANCE_DATA_MESSAGE, errors);
		}
	}

	@Override
	public void validateCreateProformaRemittance(RemittanceCreateDto dto) {
		validateUserCanStartProcess();
		List<String> errors = new ArrayList<>();
		ProformaDetailModel proformaDetail = fetchProformaDetail(dto.getTradeId(), errors);
		var detailModel = proformaDetailRepository.findById(dto.getTradeId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						INVALID_REMITTANCE_DATA_MESSAGE));
		ProformaMasterModel masterModel=proformaMasterRepository.findById(detailModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						INVALID_REMITTANCE_DATA_MESSAGE));

		if (masterModel.getWorkflowApproveStatus() != WorkflowApproveStatus.ACCEPTED) {
			errors.add("این پیش فاکتور هنوز تایید نشده است");
			throw new InternalSaleCustomException.ValidationException(INVALID_REMITTANCE_DATA_MESSAGE, errors);
		}

		var lc = lcRepository.findAllByProformaMasterId(masterModel.getId())
				.stream().filter(x->x.getWorkflowApproveStatus()==WorkflowApproveStatus.ACCEPTED)
				.toList()
				.size();
		var gaam= (long) gaamRepository.findAllByProformaMasterId(masterModel.getId())
				.stream().filter(x->x.getWorkflowApproveStatus()==WorkflowApproveStatus.ACCEPTED)
				.toList()
				.size();
		var extraBill=extraBillRepository.findAllByProformaMasterId(masterModel.getId())
				.stream().filter(x->x.getWorkflowApproveStatus()==WorkflowApproveStatus.ACCEPTED)
				.toList()
				.size();

		var deposit=cashDepositRepository.findAllByProformaMasterId(masterModel.getId())
				.stream().filter(CashDepositModel::isReckoningSend)
				.toList()
				.size();

		if ((lc+gaam+extraBill+deposit)==0) {
			errors.add("برای این پیش فاکتور هنوز نوعی از اعتبار صادر نشده است");
		}

		if (proformaMasterRepository.existsById(masterModel.getId())) {
			remittanceMasterRepository.findFirstByPaymentCodeOrderByIdDesc(masterModel.getPaymentCode())
					.filter(remittance -> isActiveStatus(remittance.getWorkflowApproveStatus()))
					.ifPresent(remittance -> {
						errors.add("برای این پیش فاکتور قبلا حواله ثبت شده است");
						throw new InternalSaleCustomException.ValidationException(INVALID_REMITTANCE_DATA_MESSAGE, errors);
					});
		}


//		remittanceMasterRepository.findAllByProformaNo(proformaDetail.getPerformaNo()).stream()
//				.filter(remittance -> isActiveStatus(remittance.getWorkflowApproveStatus()))
//				.findAny()
//				.ifPresent(remittance -> {
//					errors.add("برای این پیش فاکتور قبلا حواله ثبت شده است");
//					throw new InternalSaleCustomException.ValidationException(INVALID_REMITTANCE_DATA_MESSAGE, errors);
//				});

		if (isPreciousMetal(masterModel.getGoodId())) {
			validatePreciousMetal(dto, proformaDetail.getProformaMasterModel().getGoodId(), errors);
		}
		validateLoadingPlace(dto.getLoadingPortId(), errors);

		if (!errors.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(INVALID_REMITTANCE_DATA_MESSAGE, errors);
		}
	}

	private boolean isPreciousMetal(long goodId) {
			return preciousMetalRepository.existsById(goodId);

	}
	private RemittanceTradeDataProviderModel fetchTrade(Long tradeId, List<String> errors) {
		return remittanceTradeDataProviderRepository.findFirstByIdOrderByContractDateDesc(tradeId)
				.orElseThrow(() -> {
					errors.add("معامله با شناسه مورد نظر وجود ندارد");
					return new InternalSaleCustomException.ValidationException(INVALID_REMITTANCE_DATA_MESSAGE, errors);
				});
	}
	private ProformaDetailModel fetchProformaDetail(Long tradeId, List<String> errors) {
		return proformaDetailRepository.findById(tradeId)
				.orElseThrow(() -> {
					errors.add("پیش فاکتور با شناسه مورد نظر وجود ندارد");
					return new InternalSaleCustomException.ValidationException(INVALID_REMITTANCE_DATA_MESSAGE, errors);
				});
	}

	private void validatePreciousMetal(RemittanceCreateDto dto, Long goodId, List<String> errors) {
		if (goodsService.isPreciousMetal(goodId) && dto.getNetWeight() == null) {
			errors.add("وزن خالص خشک برای کالاهای فلزات گرانبها الزامی است");
		}
	}

	private void validateLoadingPlace(Long loadingPortId, List<String> errors) {
		if (!loadingPlaceRepository.existsById(loadingPortId)) {
			errors.add("محل بارگیری معتبر نیست");
		}
	}

	private void validateUserCanStartProcess() {
		remittanceProcessService.canStartProcess();
	}

	private boolean isActiveStatus(WorkflowApproveStatus status) {
		return ACTIVE_STATUSES.contains(status);
	}
}