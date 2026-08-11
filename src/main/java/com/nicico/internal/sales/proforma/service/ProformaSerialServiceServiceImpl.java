package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProformaSerialServiceServiceImpl implements ProformaSerialService {
	private final ProformaDetailRepository proformaDetailRepository;

	@Override
	public List<String> getProformaSerial(int proformaCount) {
		if (proformaCount <= 0) {
			throw new InternalSaleCustomException.ValidationException("تعداد شماره های درخواستی حداقل یک باشد");
		}
		final int jalaliYear = DateUtility.getCurrentJalaliYear();
		final String yearPrefix = String.valueOf(jalaliYear);
		int baseNumber = Optional.ofNullable(proformaDetailRepository.findFirstProformaNumber()).filter(serial -> !serial.isEmpty()).filter(serial -> serial.startsWith(yearPrefix)).map(serial -> Integer.parseInt(serial.substring(yearPrefix.length()))).orElse(0);
		return IntStream.rangeClosed(1, proformaCount).mapToObj(i -> String.format("%s%05d", yearPrefix, baseNumber + i)).toList();
	}

	@Override
	public Date getProformaDate() {
		return new Date();
	}
}
