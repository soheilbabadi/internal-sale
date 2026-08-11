package com.nicico.internal.sales.pms.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HavStatusEnum {
	TANZIM_HAVALEH(241, "در مرحله تنظيم حواله"),
	TAIID_HAVALEH(242, "در مرحله تاييد حواله"),
	TAIID_BARGIRI_TAKHLIEH(243, "در مرحله تاييد بارگيري / تخليه"),
	DAR_HAL_BARGIRI_TAKHLIEH(261, "در حال بارگيري/تخليه"),
	KHATAMEH_BARGIRI_TAKHLIEH(262, "در مرحله  خاتمه بارگيري/تخليه"),
	DAR_HAL_TASVIEH_HESAB(263, "درحال تسويه حساب"),
	TAIID_KHATAMEH_TASVIEH(264, "در مرحله تاييد خاتمه تسويه حساب"),
	TAIID_TASVIEH_HESAB(277, "در مرحله تاييد تسويه حساب"),
	TAIID_KHATAMEH_HAVALEH(278, "در مرحله تاييد خاتمه حواله"),
	KHATAMEH_BASTAN_HAVALEH(279, "خاتمه و بستن حواله");
	@JsonValue
	private final Integer id;
	private final String status;

	@JsonCreator
	// متد برای پیدا کردن enum از روی id
	public static HavStatusEnum fromId(Integer id) {
		for (HavStatusEnum status : values()) {
			if (status.getId().equals(id)) {
				return status;
			}
		}
		throw new InternalSaleCustomException.ValidationException("Invalid HAV_STATUS id: " + id);
	}
}