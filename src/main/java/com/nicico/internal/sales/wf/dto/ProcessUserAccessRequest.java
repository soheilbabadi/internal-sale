package com.nicico.internal.sales.wf.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProcessUserAccessRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = 1264024630987938008L;
	@Schema(description = "شناسه کاربر", example = "1", name = "userId")
	private Long userId;
	@Schema(description = "عنوان فرایند", example = "فروش داخلی", name = "sale")
	private String processTitle;
	@Schema(description = "عنوان متغیر فرایند", example = "Proforma", name = "processVariable")
	private String processVariable;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("ProcessUserAccessDto.Info")
	@NoArgsConstructor
	public static class Info extends ProcessUserAccessRequest {
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
	public static class Create extends ProcessUserAccessRequest {
		@Serial
		private static final long serialVersionUID = 5113007817865605640L;
	}
}
