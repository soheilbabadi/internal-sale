package com.nicico.internal.sales.wf.service;

import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;

public interface GaamAcknowledgmentDeterminer {
	Acknowledgment determine(GaamModel gaamModel);
}
