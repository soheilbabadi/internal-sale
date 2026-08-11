package com.nicico.internal.sales.export.model;

import com.nicico.internal.sales.config.BaseClassModel;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "T_INS_EXPORT_NOTIFICATION_CONFIG")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "مدل پیکربندی اطلاع رسانی موجودیت ها")
public class ExportNotificationConfigModel extends BaseClassModel {

	@Serial
	private static final long serialVersionUID = 8605754660587552474L;

	@Id
	@Column(name = "ID")
	@Schema(description = "شناسه یکتا", example = "1")
	private Long id;


	@Enumerated(EnumType.STRING)
	@Column(name = "C_ENTITY_TYPE", nullable = false, unique = true, length = 30)
	@Schema(description = "نوع موجودیت (پیش فاکتور، اعتبار اسنادی، حواله فروش)", example = "PROFORMA")
	private EntityTypeEnum entityType;

	@Column(name = "B_SEND_EMAIL", nullable = false)
	@Schema(description = "آیا ایمیل ارسال شود؟", example = "true")
	private Boolean sendEmail = true;

	@Column(name = "B_SEND_SMS", nullable = false)
	@Schema(description = "آیا پیامک ارسال شود؟", example = "false")
	private Boolean sendSms = false;


	@Column(name = "B_SEND_PMS", nullable = false)
	@Schema(description = "آیا در سیستم لجستیک ذخیره شود؟", example = "true")
	private Boolean sendPms = true;


}
