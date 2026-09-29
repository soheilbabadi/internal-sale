package com.nicico.internal.sales.lc.dto;

import com.nicico.internal.sales.lc.model.LcRevokingReadyModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LcRevokingReadyMapper {
	LcDto.Info toDTO(LcRevokingReadyModel model);

	LcRevokingReadyModel fromDTO(LcDto.Create request);
}
