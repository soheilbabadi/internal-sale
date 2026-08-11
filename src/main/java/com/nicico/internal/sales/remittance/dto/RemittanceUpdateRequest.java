package com.nicico.internal.sales.remittance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemittanceUpdateRequest implements Serializable {
	private Long remittanceId;
	private String description;
}
