package com.nicico.internal.sales.trade.dto;

import com.nicico.internal.sales.trade.model.TradeExtractModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TradeExtractMapper {
	TradeExtractDto.Info toDTO(TradeExtractModel request);

	TradeExtractModel fromDTO(TradeExtractDto.Create request);
}
