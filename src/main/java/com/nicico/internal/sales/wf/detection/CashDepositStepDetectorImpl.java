package com.nicico.internal.sales.wf.detection;

import com.nicico.internal.sales.deposit.model.CashDepositModel;
import com.nicico.internal.sales.deposit.repository.CashDepositRepository;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.wf.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashDepositStepDetectorImpl implements CashDepositStepDetector {

	private static final String APPROVED_KEY = "approved";

	private final ProcessService processService;
	private final CashDepositRepository cashDepositRepository;
	private final ProformaMasterRepository proformaMasterRepository;

	@Override
	public void updateAllAcknowledgments() {
		List<CashDepositModel> cashDepositModels = cashDepositRepository.findAll();

		for (CashDepositModel item : cashDepositModels) {
			if (item.getAcknowledgment() == Acknowledgment.CANCELED || item.getAcknowledgment() == Acknowledgment.FINISHED)
				return;
			updateStep(item);
		}
	}

	@Override
	public Acknowledgment updateStep(CashDepositModel cashDepositModel) {
		Acknowledgment acknowledgment = detectStep(cashDepositModel);
		cashDepositModel.setAcknowledgment(acknowledgment);
		cashDepositRepository.saveAndFlush(cashDepositModel);
		return acknowledgment;
	}

	@Override
	public Acknowledgment detectStep(CashDepositModel cashDepositModel) {

		if (cashDepositModel.isReckoningSend()) {
			return Acknowledgment.REMITTANCE;
		}

		var proformaMaster = proformaMasterRepository.findById(cashDepositModel.getProformaMasterId()).orElse(null);
		if (proformaMaster == null) {
			return Acknowledgment.FINISHED;
		}
		WorkflowApproveStatus status = proformaMaster.getWorkflowApproveStatus();

		return resolveCurrentAcknowledgment(status);
	}

	private Acknowledgment resolveCurrentAcknowledgment(WorkflowApproveStatus step) {
		if (step == null) {
			return Acknowledgment.FINISHED;
		}

		return switch (step) {
			case ACCEPTED -> Acknowledgment.FINISHED;
			case REVERSAL, CANCELED -> Acknowledgment.CANCELED;
			default -> Acknowledgment.RECKONING;
		};

	}


}
