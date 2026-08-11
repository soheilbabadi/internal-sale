package com.nicico.internal.sales.proforma.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreciousMetalProfomaCreateRequest extends BaseOrderRequest {

	@Serial
	private static final long serialVersionUID = -1759931369880058605L;

	@Schema(description = "وزن خالص خشک", name = "netWeight")
	private BigDecimal netWeight;
}