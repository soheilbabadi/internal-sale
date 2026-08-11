package com.nicico.internal.sales.extrabill.dto;


import com.nicico.internal.sales.extrabill.model.ExtraBillIssueProviderModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExtraBillIssueMapper {
	ExtraBillIssueProviderDto.Info toDTO(ExtraBillIssueProviderModel model);

	ExtraBillIssueProviderModel fromDTO(ExtraBillIssueProviderDto.Create request);

}
