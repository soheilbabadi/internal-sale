package com.nicico.internal.sales.wf.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProcessUserAccessDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1264024630987938008L;
	private Long id;
	@Schema(description = "شناسه کاربر", example = "1", name = "userId")
	private Long userId;
	@Schema(description = "شناسه فرایند", example = "421234235gffdgvfb1", name = "processId")
	private String processId;
	@Schema(description = "عنوان فرایند", example = "فروش داخلی", name = "sale")
	private String processTitle;
	@Schema(description = "عنوان فارسی فرایند", example = "فروش داخلی", name = "processLocalTitle")
	private String processLocalTitle;
	@Schema(description = "متغیر فرایند", example = "proforma", name = "processVariable")
	private String processVariable;
	@Schema(description = "عنوان متغیر فرایند", example = "تایید فروش داخلی", name = "processVariableTitle")
	private String processVariableTitle;
	@Schema(description = " نام کاربری", example = "ali_db", name = "username")
	private String username;
	@Schema(description = " نام کامل", example = "علی مقیمی", name = "fullName")
	private String fullName;
	@Schema(description = " کد ملی", example = "0076567813", name = "nationalCode")
	private String nationalCode;

	public String getNationalCode() {
		int maskedLength = nationalCode.length() - 4;
		return "*".repeat(maskedLength) + nationalCode.substring(maskedLength);
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("ProcessUserAccessDto.Info")
	@NoArgsConstructor
	public static class Info extends ProcessUserAccessDto {
		@Serial
		private static final long serialVersionUID = 446441151362666829L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("ProcessUserAccessDto.Create")
	@NoArgsConstructor
	public static class Create extends ProcessUserAccessDto {
		@Serial
		private static final long serialVersionUID = 5113007817865605640L;
	}
}
