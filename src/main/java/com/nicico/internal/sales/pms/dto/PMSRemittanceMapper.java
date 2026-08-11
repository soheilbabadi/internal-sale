package com.nicico.internal.sales.pms.dto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PMSRemittanceMapper {
	PMSRemittanceDTO.Nullables toNullablesDTO(PMSRemittanceDTO pmsRemittanceDTO);

	PMSRemittanceDTO.Create toCreateDTO(PMSRemittanceDTO.Nullables pmsRemittance, String user, String pass, String username);

	PMSRemittanceDTO.Update toUpdateDTO(PMSRemittanceDTO.Nullables pmsRemittance, String user, String pass, String username);
}
