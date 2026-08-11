package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProformaGoodItemMapper {
	ProformaGoodItemDto.Info toDTO(ProformaGoodItemModel request);

	ProformaGoodItemModel fromDTO(ProformaGoodItemDto.Create request);
}
