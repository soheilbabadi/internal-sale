package com.nicico.internal.sales.proforma.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PerformerCreateRevealRequest extends BaseOrderRequest {
	@Serial
	private static final long serialVersionUID = -1759931369880058605L;
	private List<String> exitsProformaNo;
	private Long masterId;
}
