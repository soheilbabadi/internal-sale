package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProformaDetailMapper {
	ProformaDetailDto.Info toDTO(ProformaDetailModel request);

	ProformaDetailModel fromDTO(ProformaDetailDto.Create request);
}
