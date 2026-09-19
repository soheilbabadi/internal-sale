package com.nicico.internal.sales.fms.service;

import com.fgostar.fms.sdk.model.FmsFile;

/**
 * Service interface for managing document files (Proforma and Remittance) in FMS.
 */
public interface FmsDocumentService {


	/**
	 * Gets the proforma PDF from FMS if it exists, otherwise generates, uploads, and returns it.
	 */
	FmsFile getOrCreateProformaPdf(Long detailId);

	/**
	 * Gets the proforma PDF bytes. If not in FMS, generates, uploads, and returns bytes.
	 */
	byte[] getProformaPdfBytes(Long detailId);


	FmsFile downloadProformaPdfFromFms(Long detailId);

	FmsFile uploadRemittancePdfToFms(Long masterId);

	/**
	 * Gets the remittance PDF from FMS if it exists, otherwise generates, uploads, and returns it.
	 */
	FmsFile getOrCreateRemittancePdf(Long masterId);

	/**
	 * Gets the remittance PDF bytes. If not in FMS, generates, uploads, and returns bytes.
	 */
	byte[] getRemittancePdfBytes(Long masterId);

	/**
	 * Performs a lightweight request to FMS to validate connectivity and authorization.
	 */
	boolean testConnection();


}
