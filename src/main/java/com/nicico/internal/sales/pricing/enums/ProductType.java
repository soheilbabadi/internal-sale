package com.nicico.internal.sales.pricing.enums;


import lombok.Getter;

@Getter
public enum ProductType {
	COPPER_CATHODE("مس کاتد"),
	COPPER_LEACHING_CATHODE("مس کاتد لیچینگ"),
	MOLYBDENUM_SULFIDE("سولفور مولیبدن"),
	MOLYBDENUM_OXIDE("اکسید مولیبدن"),
	ANODE_SLIME("کنسانتره فلزات گرانبها"),
	GRANULATED_SLAG("سرباره گرانوله"),
	COPPER_ROD("مفتول مس"),
	COPPER_CONCENTRATE("کنسانتره مس");

	private final String persianName;

	ProductType(String persianName) {
		this.persianName = persianName;
	}

}