package com.nicico.internal.sales.bank.dto;

import com.nicico.internal.sales.bank.model.BaseBankModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BaseBankMapper {
	BaseBankDto.Info toDTO(BaseBankModel model);

	BaseBankModel fromDTO(BaseBankDto.Create request);
}
