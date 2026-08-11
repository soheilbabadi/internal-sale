package com.nicico.internal.sales.ins.customer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class CustomerDTO {

	@Schema(description = "شناسه مشتری", example = "1")
	private Long id;

	@Schema(description = "نام مشتری", example = "شرکت نمونه")
	@NotBlank(message = "نام مشتری الزامی است.")
	@Size(max = 100, message = "نام مشتری نمی تواند بیشتر از 255 کاراکتر باشد.")
	private String name;

	@Schema(description = "نام انگلیسی مشتری", example = "Sample Co")
	@Size(max = 100, message = "نام انگلیسی مشتری نمی تواند بیشتر از 255 کاراکتر باشد.")
	private String nameEn;

	@Schema(description = "کد ملی/شناسه ملی", example = "1234567890")
	@NotBlank(message = "شناسه ملی الزامی است.")
	@Pattern(regexp = "\\d{10}", message = "شناسه ملی باید دقیقاً 10 رقم باشد.")
	private String nationalCode;

	@Schema(description = "کد مشتری در PMS", example = "PMS-123")
	private String pmsCustomerCode;

	@Schema(description = "شناسه مشتری در سامانه بورس کالا", example = "1001")
	private Long imeCustomerId;

	@Schema(description = "شماره تلفن", example = "02112345678")
	@Size(max = 20, message = "شماره تلفن نامعتبر است.")
	private String phone;

	@Schema(description = "کد اقتصادی", example = "411111111111")
	@Size(max = 50, message = "کد اقتصادی نمی تواند بیشتر از 50 کاراکتر باشد.")
	private String economicCode;

	@Schema(description = "شماره ثبت", example = "123456")
	@Size(max = 50, message = "شماره ثبت نمی تواند بیشتر از 50 کاراکتر باشد.")
	private String registerNumber;

	@Schema(description = "کد پستی", example = "1234567890")
	@Pattern(regexp = "\\d{10}", message = "کد پستی باید 10 رقم باشد.")
	private String postCode;

	@Schema(description = "آدرس", example = "تهران، خیابان ولیعصر")
	@Size(max = 4000, message = "آدرس بیش از حد طولانی است.")
	private String address;

	@Schema(description = "ایمیل", example = "test@example.com")
	@Email(message = "ایمیل نامعتبر است.")
	private String email;

	@Schema(description = "موبایل", example = "09123456789")
	@Pattern(regexp = "^09\\d{9}$", message = "شماره موبایل باید 11 رقم و با 09 شروع شود.")
	private String mobile;

	@Schema(description = "نام مسئول", example = "محمدرضا")
	@Size(max = 100, message = "نام مسئول نمی تواند بیشتر از 100 کاراکتر باشد.")
	private String coordinator;

	@Schema(description = "نام مدیر عامل", example = "علی رضایی")
	@Size(max = 100, message = "نام مدیرعامل نمی تواند بیشتر از 100 کاراکتر باشد.")
	private String ceoName;

	@Schema(description = "شماره تماس مدیر عامل", example = "09120000000")
	@Size(max = 20, message = "شماره مدیرعامل نامعتبر است.")
	private String ceoPhone;

	@Schema(description = "کد نقش تاجر", example = "TRD-01")
	@Size(max = 50, message = "کد نقش تاجر نمی تواند بیشتر از 50 کاراکتر باشد.")
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