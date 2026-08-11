package com.nicico.internal.sales.lc.dto;

import com.nicico.internal.sales.lc.model.LcIssueProviderModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LcIssueMapper {
	LcIssueProviderDto.Info toDTO(LcIssueProviderModel model);

	LcIssueProviderModel fromDTO(LcIssueProviderDto.Create request);

}
