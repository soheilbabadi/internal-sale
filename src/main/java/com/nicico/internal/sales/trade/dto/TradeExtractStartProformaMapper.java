package com.nicico.internal.sales.trade.dto;

import com.nicico.internal.sales.trade.model.TradeExtractStartProformaModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TradeExtractStartProformaMapper {
	TradeExtractDto.Info toDTO(TradeExtractStartProformaModel request);

	TradeExtractStartProformaModel fromDTO(TradeExtractDto.Create request);
}
