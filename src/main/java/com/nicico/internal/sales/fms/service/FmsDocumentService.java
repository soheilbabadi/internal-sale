package com.nicico.internal.sales.fms.service;

import com.fgostar.fms.sdk.model.FmsFile;

/**
 * Service interface for managing document files (Proforma and Remittance) in FMS.
 */
public interface FmsDocumentService {

    /**
     * Uploads an existing proforma PDF to FMS and returns the file info.
     * Assumes the PDF already exists (e.g., generated previously).
     */
    FmsFile uploadProformaPdfToFms(Long masterId);

    /**
     * Gets the proforma PDF from FMS if it exists, otherwise generates, uploads, and returns it.
     */
    FmsFile getOrCreateProformaPdf(Long masterId);

    /**
     * Gets the proforma PDF bytes. If not in FMS, generates, uploads, and returns bytes.
     */
    byte[] getProformaPdfBytes(Long detailId);

    /**
     * Downloads the proforma PDF directly from FMS using the stored UUID.
     */
    FmsFile downloadProformaPdfFromFms(Long detailId);

    /**
     * Uploads an existing remittance PDF to FMS and returns the file info.
     * Assumes the PDF already exists.
     */
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
     * Downloads the remittance PDF directly from FMS using the stored UUID.
     */
    FmsFile downloadRemittancePdfFromFms(Long masterId);
}
