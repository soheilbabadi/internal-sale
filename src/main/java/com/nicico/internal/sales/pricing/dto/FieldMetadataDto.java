package com.nicico.internal.sales.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldMetadataDto {

	private String fieldName;
	private String fieldType;
	private String description;
}