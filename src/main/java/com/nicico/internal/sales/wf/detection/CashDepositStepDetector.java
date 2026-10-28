package com.nicico.internal.sales.wf.detection;

import com.nicico.internal.sales.deposit.model.CashDepositModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;

public interface CashDepositStepDetector {
	void updateAllAcknowledgments();

	Acknowledgment detectStep(CashDepositModel cashDepositModel);
	Acknowledgment updateStep(CashDepositModel cashDepositModel);
}
