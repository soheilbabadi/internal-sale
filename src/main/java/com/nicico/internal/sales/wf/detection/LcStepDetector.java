package com.nicico.internal.sales.wf.detection;

import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;

public interface LcStepDetector {
	void updateAllAcknowledgments();

	Acknowledgment updateStep(LcModel lcModel);

	Acknowledgment detectStep(LcModel LcModel);
}
