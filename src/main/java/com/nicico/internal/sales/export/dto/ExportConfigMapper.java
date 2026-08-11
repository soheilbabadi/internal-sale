package com.nicico.internal.sales.export.dto;

import com.nicico.internal.sales.export.model.ExportNotificationConfigModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExportConfigMapper {
	ExportNotificationConfigDto.Info toDTO(ExportNotificationConfigModel request);

	ExportNotificationConfigModel fromDTO(ExportNotificationConfigDto.Create request);
}
