package com.nicico.internal.sales.extrabill.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProformaBankBillRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = 9158835413259590123L;


	@Schema(description = "نام بانک صادر کننده برات")
	private Long issuerBankId;

	@Schema(description = "شناسه بانک عامل")
	private Long agentBankId;

	@Schema(description = "کد تفصیلی")
	private String nosaCode;

	@Schema(description = "کد سپام")
	private String sepamCode;

	@Schema(description = "شناسه خزانه داری")
	private String treasuryId;

	@Schema(description = "تاریخ صدور برات")
	private Date issueDate;

	@Schema(description = "تاریخ سررسید")
	private Date dueDate;

	@Schema(description = "شناسه جزئیات پیش فاکتور", name = "proformaDetailId", example = "52")
	private Long proformaDetailId;


}