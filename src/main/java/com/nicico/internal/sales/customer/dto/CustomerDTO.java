package com.nicico.internal.sales.customer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class CustomerDTO {

	@Schema(description = "شناسه مشتری", example = "1")
	private Long id;

	@Schema(description = "نام مشتری", example = "شرکت نمونه")
	private String name;

	@Schema(description = "نام انگلیسی مشتری", example = "Sample Co")
	private String nameEn;

	@Schema(description = "کد ملی/شناسه ملی", example = "1234567890")
	private String nationalCode;

	@Schema(description = "کد مشتری در PMS", example = "PMS-123")
	private String pmsCustomerCode;

	@Schema(description = "شناسه مشتری در سامانه بورس کالا", example = "1001")
	private Long imeCustomerId;

	@Schema(description = "شماره تلفن", example = "02112345678")
	private String phone;

	@Schema(description = "کد اقتصادی", example = "411111111111")
	private String economicCode;

	@Schema(description = "شماره ثبت", example = "123456")
	private String registerNumber;

	@Schema(description = "کد پستی", example = "1234567890")
	private String postCode;

	@Schema(description = "آدرس", example = "تهران، خیابان ولیعصر")
	private String address;

	@Schema(description = "ایمیل", example = "test@example.com")
	private String email;

	@Schema(description = "موبایل", example = "09123456789")
	private String mobile;

	@Schema(description = "نام مسئول", example = "محمدرضا")
	private String coordinator;

	@Schema(description = "نام مدیر عامل", example = "علی رضایی")
	private String ceoName;

	@Schema(description = "شماره تماس مدیر عامل", example = "09120000000")
	private String ceoPhone;

	@Schema(description = "کد نقش تاجر", example = "TRD-01")
	private String tradeRoleCode;

	@Data
	@NoArgsConstructor
	@ApiModel("CustomerDTO.Create")
	public static class Create extends CustomerDTO {
	}

	@Data
	@NoArgsConstructor
	@ApiModel("CustomerDTO.Info")
	public static class Info extends CustomerDTO {
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@ApiModel("CustomerDTO.Delete")
	public static class Delete {
		private List<Long> ids;
	}
}