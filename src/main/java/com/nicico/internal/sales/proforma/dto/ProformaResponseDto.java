package com.nicico.internal.sales.proforma.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProformaResponseDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -7128471758955760886L;
	private ProformaMasterDTO masterDTO;
	private List<ProformaDetailDto> detailDtoList;
}
