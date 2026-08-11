package com.nicico.internal.sales.wf.dto.mapper;

import com.nicico.internal.sales.wf.dto.ProcessUserAccessDto;
import com.nicico.internal.sales.wf.model.ProcessUserAccessModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProcessUserAccessMapper {
	ProcessUserAccessDto.Info toDTO(ProcessUserAccessModel request);

	ProcessUserAccessModel fromDTO(ProcessUserAccessDto.Create request);
}