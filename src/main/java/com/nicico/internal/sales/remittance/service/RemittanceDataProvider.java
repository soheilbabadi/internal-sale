package com.nicico.internal.sales.remittance.service;

import com.nicico.internal.sales.remittance.dto.RemittanceCreateDto;
import com.nicico.internal.sales.remittance.enums.RemittanceSourceType;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;

import java.util.Date;

public interface RemittanceDataProvider {

	RemittanceMasterModel getRemittanceFromProforma(RemittanceCreateDto request);

	RemittanceMasterModel getRemittanceFromTrade(RemittanceCreateDto request);

	String getLotnumber(Long tradeId, RemittanceSourceType sourceType);

	String extractLotNumber(String paymentCode);

	Date lastDeliveryDeadlineProforma(RemittanceCreateDto request);

	Date lastDeliveryDeadlineTrade(RemittanceCreateDto request);
}
