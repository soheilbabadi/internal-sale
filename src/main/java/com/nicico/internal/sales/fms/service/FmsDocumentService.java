package com.nicico.internal.sales.fms.service;

/**
 * Service interface for managing document files (Proforma and Remittance) in FMS.
 */
public interface FmsDocumentService {



    /**
     * Gets the proforma PDF from FMS if it exists, otherwise generates, uploads, and returns it.
     */
//    FmsFile getOrCreateProformaPdf(Long detailId);

    /**
     * Gets the proforma PDF bytes. If not in FMS, generates, uploads, and returns bytes.
     */
    byte[] getProformaPdfBytes(Long detailId);


    /**
     * Gets the remittance PDF from FMS if it exists, otherwise generates, uploads, and returns it.
     */
//    FmsFile getOrCreateRemittancePdf(Long masterId);

    /**
     * Gets the remittance PDF bytes. If not in FMS, generates, uploads, and returns bytes.
     */
 byte[] getRemittancePdfBytes(Long masterId);


}
