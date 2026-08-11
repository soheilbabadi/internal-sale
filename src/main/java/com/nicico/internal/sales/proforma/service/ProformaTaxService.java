package com.nicico.internal.sales.proforma.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ProformaTaxService {
	TaxItem getTaxItem(BigDecimal amount, Integer financialYear);

	TaxItem getTaxItem(BigDecimal amount, LocalDate financialDate);

	record TaxItem(BigDecimal vat,
	               Integer financialYear) {
	}
}
