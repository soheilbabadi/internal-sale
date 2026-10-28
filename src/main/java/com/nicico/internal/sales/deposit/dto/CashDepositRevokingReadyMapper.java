package com.nicico.internal.sales.deposit.dto;
import com.nicico.internal.sales.deposit.model.CashDepositReadyReckoningModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CashDepositRevokingReadyMapper {
	CashDepositReadyReckoningDto.Info toDTO(CashDepositReadyReckoningModel model);

	CashDepositReadyReckoningModel fromDTO(CashDepositReadyReckoningDto.Create request);

}
