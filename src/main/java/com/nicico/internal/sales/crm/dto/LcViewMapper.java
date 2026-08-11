package com.nicico.internal.sales.crm.dto;

import com.nicico.internal.sales.crm.model.LcWithProformaView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LcViewMapper {
	LcWithProformaDto.Info toDTO(LcWithProformaView request);

	LcWithProformaView fromDTO(LcWithProformaDto.Create request);
}