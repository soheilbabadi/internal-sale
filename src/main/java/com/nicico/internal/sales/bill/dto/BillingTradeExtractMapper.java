package com.nicico.internal.sales.bill.dto;

import com.nicico.internal.sales.bill.model.BillingTradeExtractModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BillingTradeExtractMapper {
	BillingTradeExtractDto.Info toDTO(BillingTradeExtractModel request);

	BillingTradeExtractModel fromDTO(BillingTradeExtractDto.Create request);
}
