package com.nicico.internal.sales.history.dto;

import com.nicico.internal.sales.history.model.HistoryExtractMasterModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistoryExtractMapper {
	HistoryExtractMasterDto.Info toDTO(HistoryExtractMasterModel request);

	HistoryExtractMasterModel fromDTO(HistoryExtractMasterDto.Create request);
}
