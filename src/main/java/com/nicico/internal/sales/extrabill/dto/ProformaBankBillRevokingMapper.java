package com.nicico.internal.sales.extrabill.dto;


import com.nicico.internal.sales.extrabill.model.ProformaBankBillReadyRevoking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProformaBankBillRevokingMapper {
	ProformaBankBillReportDto.Info toDTO(ProformaBankBillReadyRevoking model);

	ProformaBankBillReadyRevoking fromDTO(ProformaBankBillReportDto.Create request);

}
