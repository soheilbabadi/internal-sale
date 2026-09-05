package com.nicico.internal.sales.wf.service;

import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;

public interface LcAcknowledgmentDeterminer {
	Acknowledgment determine(LcModel lcModel);
}
