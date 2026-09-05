package com.nicico.internal.sales.wf.service;

import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;

public interface ExtraBillAcknowledgmentDeterminer {
	Acknowledgment determine(ExtraBankBillModel extraBankBillModel);
}
