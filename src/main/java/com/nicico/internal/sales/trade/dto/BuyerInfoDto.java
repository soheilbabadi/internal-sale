package com.nicico.internal.sales.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class BuyerInfoDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 5204871775460795370L;
	private String buyerName;
	private String buyerNationalCode;
}