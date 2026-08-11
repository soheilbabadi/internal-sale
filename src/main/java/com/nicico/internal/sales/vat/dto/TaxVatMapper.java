package com.nicico.internal.sales.vat.dto;

import com.nicico.internal.sales.vat.model.TaxVatModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaxVatMapper {
	VatDTO.Info toDTO(TaxVatModel imeTrade);

	TaxVatModel fromDTO(VatDTO.Create imeTrade);

}
