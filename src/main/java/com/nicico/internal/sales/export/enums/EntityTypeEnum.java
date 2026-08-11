package com.nicico.internal.sales.export.enums;

import lombok.Getter;

@Getter
public enum EntityTypeEnum {
	PROFORMA("پیش فاکتور"),
	LETTER_OF_CREDIT("اعتبار اسنادی"),
	SALES_SLIP("حواله فروش"),
	CUSTOMER("مشتری"),
	BANK("بانکها"),
	GOODS("کالا");

	private final String persianName;

	EntityTypeEnum(String persianName) {
		this.persianName = persianName;
	}


	public static EntityTypeEnum fromString(String text) {
		for (EntityTypeEnum type : EntityTypeEnum.values()) {
			if (type.name().equalsIgnoreCase(text) ||
					type.getPersianName().equals(text)) {
				return type;
			}
		}
		return null;
	}

}