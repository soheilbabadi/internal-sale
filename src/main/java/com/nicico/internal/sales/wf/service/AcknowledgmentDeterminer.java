package com.nicico.internal.sales.wf.service;

import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.model.LcModel;

public interface AcknowledgmentDeterminer {

	/**
	 * Determines the acknowledgment status for an Extra Bank Bill model.
	 *
	 * @param extraBankBillModel the extra bank bill model
	 * @return the determined acknowledgment status
	 */
	Acknowledgment determine(ExtraBankBillModel extraBankBillModel);

	/**
	 * Determines the acknowledgment status for a GAAM model.
	 *
	 * @param gaamModel the GAAM model
	 * @return the determined acknowledgment status
	 */
	Acknowledgment determine(GaamModel gaamModel);

	/**
	 * Determines the acknowledgment status for an LC model.
	 *
	 * @param lcModel the LC model
	 * @return the determined acknowledgment status
	 */
	Acknowledgment determine(LcModel lcModel);
}
