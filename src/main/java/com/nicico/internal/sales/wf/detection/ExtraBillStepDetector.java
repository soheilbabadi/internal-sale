package com.nicico.internal.sales.wf.detection;

import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;

public interface ExtraBillStepDetector {
	void updateAllAcknowledgments();

	Acknowledgment detectStep(ExtraBankBillModel extraBankBillModel);
	Acknowledgment updateStep(ExtraBankBillModel extraBankBillModel);
}
