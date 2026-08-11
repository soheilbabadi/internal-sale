package com.nicico.internal.sales.extrabill.dto;


import com.nicico.internal.sales.extrabill.model.ProformaBankBillReportModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProformaBankBillReportMapper {
	ProformaBankBillReportDto.Info toDTO(ProformaBankBillReportModel model);

	ProformaBankBillReportModel fromDTO(ProformaBankBillReportDto.Create request);

}
