package com.nicico.internal.sales.extrabill.dto;
import com.nicico.internal.sales.extrabill.model.ExtraBillReadyReckoningModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProformaBankBillReadyReckoningMapper {
	ProformaBankBillReportDto.Info toDTO(ExtraBillReadyReckoningModel model);

	ExtraBillReadyReckoningModel fromDTO(ProformaBankBillReportDto.Create request);

}
