package com.nicico.internal.sales.pms.service;

public interface PMSProformaService {
	void createPreFactorFromProformaMasterId(Long proformaMasterId, String userName, boolean resend);

	void validateForCreatePreFactorFromProformaMasterId(Long proformaMasterId);
}
