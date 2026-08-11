package com.nicico.internal.sales.lc.dto.request;

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
public class UpdateAcceptedLcRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = 6313070493614504250L;
	@Schema(description = "شماره پیش فاکتور")
	private String proformaNo;
	@Schema(description = "شماره اعتبار اسنادی")
	private String lcNo;
	@Schema(description = "تاریخ گشایش اعتبار")
	private Date lcDate;
	@Schema(name = "شناسه بانک")
	private Long tradingBankId;
	@Schema(description = "تاریخ انقضای اعتبار اسنادی", name = "lcExpiryDate", example = "2023-10-01T00:00:00Z")
	private Date lcExpiryDate;
	@Schema(description = "تاریخ سررسید پرداخت وجه اعتبار اسنادی", name = "settlementDueDate", example = "2023-10-01T00:00:00Z")
	private Date settlementDueDate;
	@Schema(name = "شناسه بانک گشایش اعتبار اسنادی")
	private Long issuerBankId;
	@Schema(description = "شناسه فایل پیوست اعتبار اسنادی")
	private String lcAttachmentId;
	@Schema(description = "شناسه فایل اصلاحیه بارنامه")
	private String dispatchAttachmentId;

	@Schema(description = "شناسه سند ابلاغیه", name = "notificationDocumentId", example = "12345")
	private String notificationDocumentId;

	@Schema(description = "کد نوسا", name = "nosaCode", example = "NOSA123456")
	private String nosaCode;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("LcUpdateDto.Info")
	public static class Info extends UpdateAcceptedLcRequest {
		@Serial
		private static final long serialVersionUID = 7315549626741921075L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("LcUpdateDto.Create")
	public static class Create extends UpdateAcceptedLcRequest {
		@Serial
		private static final long serialVersionUID = -6875168459138326735L;
	}
}
