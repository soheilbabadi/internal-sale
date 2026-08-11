package com.nicico.internal.sales.export.dto;

import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "مدل پیکربندی اطلاع رسانی موجودیت ها")
public class ExportNotificationConfigDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -4410403529474606697L;

	private Long id;

	@Schema(description = "نوع موجودیت (پیش فاکتور، اعتبار اسنادی، حواله فروش)", example = "PROFORMA")
	private EntityTypeEnum entityType;

	@Schema(description = "آیا ایمیل ارسال شود؟", example = "true")
	private Boolean sendEmail = true;

	@Schema(description = "آیا پیامک ارسال شود؟", example = "false")
	private Boolean sendSms = false;

	@Schema(description = "آیا در سیستم لجستیک ذخیره شود؟", example = "true")
	private Boolean sendPms = true;


	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("ExportNotificationConfigDto.Create")
	@NoArgsConstructor
	public static class Create extends ExportNotificationConfigDto {
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("ExportNotificationConfigDto.Info")
	@NoArgsConstructor
	public static class Info extends ExportNotificationConfigDto {
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}
}
