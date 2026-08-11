package com.nicico.internal.sales.proforma.records;

import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.SaleType;

import java.math.BigDecimal;
import java.util.Date;

public record DetailModelSpec(Integer jalaliYear,
                              Integer storageDeadline,
                              BigDecimal storageCost,
                              Integer creditExpirePeriod,
                              Integer shippingDeadline,
                              Integer paymentDeferral,
                              Integer deadlineDays,
                              String proformaNo,
                              Date performaDate,
                              SaleType saleType,
                              String settlementType,
                              ProformaIssueType proformaIssueType,
                              Date orderDate,
                              String contractDate,
                              ProformaReversalStatus proformaReversalStatus) {
}