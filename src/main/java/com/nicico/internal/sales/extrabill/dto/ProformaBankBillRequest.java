package com.nicico.internal.sales.extrabill.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProformaBankBillRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = 9158835413259590123L;

	@NotNull(message = "شناسه  نمی تواند خالی باشد")
	@Schema(description = "شناسه برات ثبت شده")
	private Long id;


	@NotNull(message = "شناسه بانک صادرکننده نمی تواند خالی باشد")
	@Schema(description = "نام بانک صادر کننده برات")
	private Long issuerBankId;

	@NotBlank(message = "کد تفصیلی نمی تواند خالی باشد")
	@Schema(description = "کد تفصیلی")
	private String nosaCode;

	@NotBlank(message = "کد سپام نمی تواند خالی باشد")
	@Schema(description = "کد سپام")
	private String sepamCode;

	@NotBlank(message = "شناسه خزانه داری نمی تواند خالی باشد")
	@Schema(description = "شناسه خزانه داری")
	private String treasuryId;

	@NotNull(message = "تاریخ صدور برات نمی تواند خالی باشد")
	@Schema(description = "تاریخ صدور برات")
	private Date issueDate;

	@NotNull(message = "تاریخ سررسید نمی تواند خالی باشد")
	@Schema(description = "تاریخ سررسید")
	private Date dueDate;

	@NotNull(message = "شناسه جزئیات پیش فاکتور نمی تواند خالی باشد")
	@Schema(description = "شناسه جزئیات پیش فاکتور", name = "proformaDetailId", example = "52")
	private Long proformaDetailId;

	@Schema(description = "شناسه فایل پیوست برات")
	private String extraBillFileId;


}