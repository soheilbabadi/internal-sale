package com.nicico.internal.sales.gaam.mapper;
import com.nicico.internal.sales.gaam.dto.GaamReportDto;
import com.nicico.internal.sales.gaam.model.GaamlReadyReckoningModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GaamRevokingMapper {
	GaamReportDto.Info toDTO(GaamlReadyReckoningModel model);

	GaamlReadyReckoningModel fromDTO(GaamReportDto.Create request);

}
