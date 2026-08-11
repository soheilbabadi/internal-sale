package com.nicico.internal.sales.proforma.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;

@Data
@AllArgsConstructor

@Builder
public class PerfomaCreateRequest extends BaseOrderRequest {
	@Serial
	private static final long serialVersionUID = -1759931369880058605L;
}
