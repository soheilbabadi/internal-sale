package com.nicico.internal.sales.bank.dto;

import com.nicico.internal.sales.bank.model.TradingBankModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankMapper {
	TradingBankDto.Info toDTO(TradingBankModel model);

	TradingBankModel fromDTO(TradingBankDto.Create request);
}
