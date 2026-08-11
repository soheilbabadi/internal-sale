package com.nicico.internal.sales.proforma.service;

import java.util.Date;
import java.util.List;

public interface ProformaSerialService {
	List<String> getProformaSerial(int proformaCount);

	Date getProformaDate();
}
