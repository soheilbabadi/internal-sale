package com.nicico.internal.sales.gaam.mapper;


import com.nicico.internal.sales.gaam.dto.GaamDto;
import com.nicico.internal.sales.gaam.model.GaamModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GaamMapper {
	GaamDto.Info toDTO(GaamModel model);

	GaamModel fromDTO(GaamDto.Create request);

}
