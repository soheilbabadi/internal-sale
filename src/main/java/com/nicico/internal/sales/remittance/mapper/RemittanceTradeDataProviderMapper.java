package com.nicico.internal.sales.remittance.mapper;

import com.nicico.internal.sales.remittance.dto.RemittanceTradeDataProviderDto;
import com.nicico.internal.sales.remittance.model.RemittanceTradeDataProviderModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RemittanceTradeDataProviderMapper {
	RemittanceTradeDataProviderDto.Info toDTO(RemittanceTradeDataProviderModel model);

	RemittanceTradeDataProviderModel fromDTO(RemittanceTradeDataProviderDto.Create request);
}
