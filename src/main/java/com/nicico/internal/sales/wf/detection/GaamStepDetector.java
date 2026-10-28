package com.nicico.internal.sales.wf.detection;

import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;

public interface GaamStepDetector {
	Acknowledgment updateStep(GaamModel gaamModel);

	void updateAllAcknowledgments();

	Acknowledgment detectStep(GaamModel gaamModel);
}
