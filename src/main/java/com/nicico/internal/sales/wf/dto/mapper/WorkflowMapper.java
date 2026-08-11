package com.nicico.internal.sales.wf.dto.mapper;

import com.nicico.internal.sales.wf.dto.WorkflowDto;
import com.nicico.internal.sales.wf.model.WorkflowModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkflowMapper {
	WorkflowDto.Info toDTO(WorkflowModel request);

	WorkflowModel fromDTO(WorkflowDto.Create request);
}