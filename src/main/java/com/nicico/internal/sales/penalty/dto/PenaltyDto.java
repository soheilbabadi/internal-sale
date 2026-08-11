package com.nicico.internal.sales.penalty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenaltyDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 9181008159599310752L;
	@Schema(description = "مبلغ جریمه", name = "penaltyAmount", example = "1000000")
	private BigDecimal penaltyAmount;
	@Schema(description = "مبلغ کمیسیون که خریدار ", name = "commissionAmount", example = "1000000")
	@Digits(integer = 8, fraction = 2)
	private BigDecimal commissionAmount;
}
