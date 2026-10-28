package com.nicico.internal.sales.deposit.dto;
import com.nicico.internal.sales.deposit.model.CashDepositModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CashDepositMapper {
	CashDepositDto.Info toDTO(CashDepositModel model);

	CashDepositModel fromDTO(CashDepositDto.Create request);

}
