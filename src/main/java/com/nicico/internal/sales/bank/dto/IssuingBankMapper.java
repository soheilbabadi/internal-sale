package com.nicico.internal.sales.bank.dto;

import com.nicico.internal.sales.bank.model.IssuingBankModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IssuingBankMapper {
	IssuingBankDto.Info toDTO(IssuingBankModel model);

	IssuingBankModel fromDTO(IssuingBankDto.Create request);
}
