package com.nicico.internal.sales.ime.broker.dto;

import com.nicico.internal.sales.ime.broker.model.IMEBrokerModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IMEBrokerMapper {
	IMEBrokerDTO.Info toDTO(IMEBrokerModel imeTrade);
}
