package com.nicico.internal.sales.remittance.mapper;

import com.nicico.internal.sales.remittance.dto.RemittanceProformaDataProviderDto;
import com.nicico.internal.sales.remittance.model.RemittanceProformaDataProviderModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RemittanceProformaDataProviderMapper {
	RemittanceProformaDataProviderDto.Info toDTO(RemittanceProformaDataProviderModel model);

	RemittanceProformaDataProviderModel fromDTO(RemittanceProformaDataProviderDto.Create request);
}
