package com.nicico.internal.sales.pms.service;

import com.nicico.internal.sales.gaam.model.GaamModel;

import java.io.IOException;

public interface PMSGaamService {
	void createPMSGaam(Long proformaMasterId, String username, boolean resend);

	void createPMSGaam(Long gaamId, boolean resend) throws IOException;

	void sendGaamModelToPMS(GaamModel model, String username);

	void updatePmsGaam(String pmsId, String username);

	void savePmsId(Long gaamId, String pmsBillId);
}
