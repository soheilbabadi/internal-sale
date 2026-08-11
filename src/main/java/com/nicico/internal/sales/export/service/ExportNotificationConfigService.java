package com.nicico.internal.sales.export.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.export.dto.ExportNotificationConfigDto;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;

public interface ExportNotificationConfigService {
	SearchDTO.SearchRs<ExportNotificationConfigDto.Info> search(SearchDTO.SearchRq request);

	ExportNotificationConfigDto.Info getById(long id);

	ExportNotificationConfigDto.Info getByEntityType(EntityTypeEnum entityTypeEnum);

	ExportNotificationConfigDto.Info save(ExportNotificationConfigDto.Create request);

	void initializeDefaultConfigurations();


}
