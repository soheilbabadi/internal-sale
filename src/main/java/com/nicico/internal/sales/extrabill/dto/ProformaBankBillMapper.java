package com.nicico.internal.sales.extrabill.dto;


import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProformaBankBillMapper {
	ProformaBankBillDto.Info toDTO(ExtraBankBillModel model);

	ExtraBankBillModel fromDTO(ProformaBankBillDto.Create request);

}
