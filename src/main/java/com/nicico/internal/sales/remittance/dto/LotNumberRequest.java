package com.nicico.internal.sales.remittance.dto;

import com.nicico.internal.sales.remittance.enums.RemittanceSourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LotNumberRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Schema(name = "tradeId", description = "شناسه معامله یا پیش فاکتور", example = "123")
	@NotNull(message = "شناسه معامله الزامی است")
	private Long tradeId;

	@Schema(name = "sourceType", description = "نوع منبع (TRADE یا PROFORMA)")
	@NotNull(message = "نوع منبع الزامی است")
	private RemittanceSourceType sourceType;
}

