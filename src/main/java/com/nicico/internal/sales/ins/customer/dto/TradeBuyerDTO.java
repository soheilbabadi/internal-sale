package com.nicico.internal.sales.ins.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeBuyerDTO implements Serializable {
	private String buyerNationalCode;
	private String buyerName;
}
