package com.nicico.internal.sales.extrabill.dto;


import com.nicico.internal.sales.extrabill.model.ProformaBankBillModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProformaBankBillMapper {
	ProformaBankBillDto.Info toDTO(ProformaBankBillModel model);

	ProformaBankBillModel fromDTO(ProformaBankBillDto.Create request);

}
