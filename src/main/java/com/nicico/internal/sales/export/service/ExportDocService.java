package com.nicico.internal.sales.export.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.math.BigDecimal;
import java.util.List;

public interface ExportDocService {
	byte[] exportProformaDoc(Long detailId);

	byte[] exportProformaDocOnlySigned(Long detailId);

	byte[] exportProformaPdf(Long proformaDetailId);

	String numberToString(BigDecimal price);

	byte[] exportRemittanceDoc(long remittanceId);

	byte[] exportRemittancePdf(long remittanceId);

	byte[] convertDocListToPdf(List<XWPFDocument> docList);
}
