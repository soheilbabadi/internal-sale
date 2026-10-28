package com.nicico.internal.sales.remittance.mapper;

import com.nicico.internal.sales.remittance.dto.RemittanceTradeDataProviderDto;
import com.nicico.internal.sales.remittance.model.RemittanceSulfuricAcidDataProviderModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RemittanceSulfuricAcidDataProviderMapper {
	RemittanceTradeDataProviderDto.Info toDTO(RemittanceSulfuricAcidDataProviderModel model);

	RemittanceSulfuricAcidDataProviderModel fromDTO(RemittanceTradeDataProviderDto.Create request);
}
