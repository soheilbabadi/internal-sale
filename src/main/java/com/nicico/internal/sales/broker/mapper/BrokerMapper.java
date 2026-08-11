package com.nicico.internal.sales.broker.mapper;

import com.nicico.internal.sales.broker.dto.BrokerDto;
import com.nicico.internal.sales.broker.model.BrokerModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrokerMapper {
	BrokerDto.Info toDTO(BrokerModel request);

	BrokerModel fromDTO(BrokerDto.Create request);
}
