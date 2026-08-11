package com.nicico.internal.sales.crm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrmApprovedCompanyDto {
	@Schema(description = "نام شرکت", example = "دنیای مس کاشان")
	private String name;

	@Schema(description = "کد شرکت", example = "CMP-001")
	private String code;

	@Schema(description = "شناسه یکتای CRM (GUID)", example = "c8e6b2d0-34a8-4f49-8f6e-4f7ac2d5f197")
	private String guid;

	@Schema(description = "کد اقتصادی", example = "1234567890", name = "economicCode")
	private String economicCode;

	@Schema(description = "کد ملی", example = "1234567890", name = "nationalCode")
	private String nationalCode;

	@Schema(description = "شناسه داخلی شرکت", example = "125", name = "id")
	private Long id;

	@Schema(description = "کد HRM", example = "HRM-2201", name = "hrmCode")
	private String hrmCode;

	@Schema(description = "شناسه HRM", example = "45266", name = "hrmId")
	private Long hrmId;

	@Schema(description = "کد حسابداری", example = "ACC-9981", name = "accountingCode")
	private String accountingCode;

	@Schema(description = "شناسه حسابداری", example = "8743", name = "accountingId")
	private Long accountingId;
}