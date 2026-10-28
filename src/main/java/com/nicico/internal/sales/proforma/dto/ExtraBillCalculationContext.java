package com.nicico.internal.sales.proforma.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Context holding calculation inputs and outputs for extra bill of exchange.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "کانتکست محاسبات اضافه برات / درصد الحاقیه")
public class ExtraBillCalculationContext implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(description = "مبلغ پایه / جمع کل کالاها")
	private BigDecimal totalPrice;

	@Schema(description = "درصد اضافه برات خام (مثلا ۵ درصد)")
	private BigDecimal extraBillPercent;

	@Schema(description = "نسبت درصد (تقسیم بر ۱۰۰)")
	private BigDecimal extraPercentRatio;

	@Schema(description = "مبلغ اضافه شده به مبلغ کل (extraBillOfExchangeAmount)")
	private BigDecimal extraBillOfExchangeAmount;

	@Schema(description = "مبلغ نهایی (finalPrice = totalPrice * (1 + ratio))")
	private BigDecimal finalPrice;
}
