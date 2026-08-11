package com.nicico.internal.sales.lc.dto;

import com.nicico.internal.sales.lc.model.LcModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LcMapper {
	LcDto.Info toDTO(LcModel model);

	LcModel fromDTO(LcDto.Create request);
}
