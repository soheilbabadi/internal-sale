package com.nicico.internal.sales.wf.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "اطلاعات کاربر")
public class UserDataDto {

	@Schema(description = "شناسه کاربر")
	private Long id;

	@Schema(description = "نام کاربری")
	private String username;

	@Schema(description = "نام و نام خانوادگی")
	private String fullName;

	@Schema(description = "کد ملی")
	private String nationalCode;

	@Schema(description = "وضعیت کاربر (Disabled, Enabled, Locked)")
	private String status;
}