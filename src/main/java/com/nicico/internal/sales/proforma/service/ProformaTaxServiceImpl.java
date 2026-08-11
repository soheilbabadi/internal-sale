package com.nicico.internal.sales.proforma.service;

import com.nicico.copper.common.util.date.DateUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.vat.repository.VatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProformaTaxServiceImpl implements ProformaTaxService {
	private final VatRepository taxVatRepository;

	@Override
	public TaxItem getTaxItem(BigDecimal amount, Integer financialYear) {
		var entity = taxVatRepository.findByJalaliYear(financialYear).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("مالیات برای سال " + financialYear + " تعریف نشده است"));
		return new TaxItem(entity.getVatCoefficient(), financialYear);
	}

	@Override
	public TaxItem getTaxItem(BigDecimal amount, LocalDate financialDate) {
		var financialYear = Integer.parseInt(DateUtil.convertMiToKh(financialDate.toString().substring(0, 4)));
		return getTaxItem(amount, financialYear);
	}
}
