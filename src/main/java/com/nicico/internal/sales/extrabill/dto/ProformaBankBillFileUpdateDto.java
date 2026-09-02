package com.nicico.internal.sales.extrabill.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO برای بروزرسانی فایل های پیوست برات
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProformaBankBillFileUpdateDto {

	@Schema(description = "شناسه برات")
	private long id;


	@Schema(description = "شناسه فایل اصلاحیه")
	private String dispatchAttachmentId;
}
