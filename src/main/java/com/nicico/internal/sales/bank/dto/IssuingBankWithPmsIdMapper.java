package com.nicico.internal.sales.bank.dto;

import com.nicico.internal.sales.bank.model.IssuingBankWithPmsIdView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IssuingBankWithPmsIdMapper {
	IssuingBankWithPmsIdDto.Info toDTO(IssuingBankWithPmsIdView model);
}
