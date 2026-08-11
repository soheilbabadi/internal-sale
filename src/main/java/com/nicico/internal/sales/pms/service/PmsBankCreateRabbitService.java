package com.nicico.internal.sales.pms.service;

import com.nicico.internal.sales.bank.model.IssuingBankWithPmsIdView;
import com.nicico.internal.sales.pms.dto.PMSCreateBankDto;

public interface PmsBankCreateRabbitService {
	void createBank(PMSCreateBankDto.Create bank);

	void createBank(IssuingBankWithPmsIdView bank);

	void createBank(Long issueBankId);
}
