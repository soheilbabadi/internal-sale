package com.nicico.internal.sales.proforma.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashSaleCreateRequest extends BaseOrderRequest {
	@Serial
	private static final long serialVersionUID = -1759931369880058605L;

	private BigDecimal netWeight;
	private boolean cashPercentTotal;
}
