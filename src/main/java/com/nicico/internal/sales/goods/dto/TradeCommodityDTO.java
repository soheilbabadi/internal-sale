package com.nicico.internal.sales.goods.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeCommodityDTO implements Serializable {
	private Long commodityCode;
	private String persianName;
	private String symbol;
}
