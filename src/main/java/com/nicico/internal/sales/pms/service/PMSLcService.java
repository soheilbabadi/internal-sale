package com.nicico.internal.sales.pms.service;

import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.lc.model.LcModel;

import java.io.IOException;
import java.util.List;

public interface PMSLcService {
	void createPMSLc(Long proformaMasterId, String username, boolean resend);

	void updatePmsLc(String pmsId, String username);


	void createPMSLc(Long lcID, boolean resend) throws IOException;

	void sendLcModelToPMS(LcModel model, String username);

	List<LcDto.Info> findRemittanceLcWithoutPmsId();
}