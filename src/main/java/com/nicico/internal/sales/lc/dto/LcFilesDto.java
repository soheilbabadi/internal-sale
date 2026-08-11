package com.nicico.internal.sales.lc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LcFilesDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -2307619530472754937L;

	@Schema(description = "شناسه پیش فاکتور")
	private Long proformaId;
	@NotBlank(message = "بارگزاری فایل ابلاغیه الزامی میباشد")
	private String notificationFileId;
	private String dispatchFileId;
	private String nosaCode;
}
