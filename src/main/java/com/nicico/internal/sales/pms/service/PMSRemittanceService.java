package com.nicico.internal.sales.pms.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface PMSRemittanceService {
    void create(Long remittanceId, String username, boolean resend) throws IOException;

	void update(Long remittanceId, String username) throws IOException;

	void pmsRemittanceValidationErrorList(List<String> errors,
	                                      String customerEconomicCode,
	                                      String customerNationalCode,
	                                      String goodsName,
	                                      String issueDate,
	                                      BigDecimal amount
	);
}
