package com.nicico.internal.sales.broker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrokerDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -552535385170580386L;

	private Long id;

	@Schema(description = "نام", example = "شرکت بازرگانی ایران")
	@NotBlank(message = "نام الزامی است")
	@Size(max = 255, message = "نام نباید بیشتر از ۲۵۵ کاراکتر باشد")
	private String name;

	@Schema(description = "کد ملی", example = "1234567890")
	@NotBlank(message = "کد ملی الزامی است")
	@Pattern(regexp = "^\\d{10}$", message = "کد ملی باید ۱۰ رقم باشد")
	private String nationalCode;

	@Schema(description = "تلفن", example = "021-1234-5678")
	@Pattern(regexp = "^[\\d\\-]{1,50}$", message = "شماره تلفن فقط می تواند شامل اعداد و خط تیره باشد و حداکثر ۵۰ کاراکتر باشد")
	@Size(max = 50, message = "شماره تلفن نباید بیشتر از ۵۰ کاراکتر باشد")
	private String phone;

	@Schema(description = "کد اقتصادی", example = "1234567890")
	@Pattern(regexp = "^\\d{10,14}$", message = "کد اقتصادی باید بین ۱۰ تا ۱۴ رقم باشد")
	@Size(max = 50, message = "کد اقتصادی نباید بیشتر از ۵۰ کاراکتر باشد")
	private String economicCode;

	@Schema(description = "کد پستی", example = "1234567890")
	@Pattern(regexp = "^\\d{10}$", message = "کد پستی باید ۱۰ رقم باشد")
	@Size(max = 10, message = "کد پستی نباید بیشتر از ۱۰ کاراکتر باشد")
	private String postCode;

	@Schema(description = "ایمیل", example = "example@example.com")
	@Email(message = "فرمت ایمیل صحیح نیست")
	@Size(max = 100, message = "ایمیل نباید بیشتر از ۱۰۰ کاراکتر باشد")
	private String email;

	@Schema(description = "شماره موبایل", example = "09123456789")
	@Pattern(regexp = "^09\\d{9}$", message = "شماره موبایل باید با ۰۹ شروع شده و ۱۱ رقم باشد")
	@Size(max = 50, message = "شماره موبایل نباید بیشتر از ۵۰ کاراکتر باشد")
	private String mobile;

	@Schema(description = "نام مسئول", example = "محمدرضا")
	@Size(max = 100, message = "نام مسئول نباید بیشتر از ۱۰۰ کاراکتر باشد")
	private String coordinator;

	@Schema(description = "آدرس")
	@Size(max = 4000, message = "آدرس نباید بیشتر از ۴۰۰۰ کاراکتر باشد")
	private String address;

	@Schema(description = "نام مدیر عامل")
	@Size(max = 100, message = "نام مدیر عامل نباید بیشتر از ۱۰۰ کاراکتر باشد")
	private String ceoName;

	@Schema(description = "شماره تماس مدیر عامل")
	@Pattern(regexp = "^[\\d\\-]{1,20}$", message = "شماره تماس مدیر عامل فقط می تواند شامل اعداد و خط تیره باشد و حداکثر ۲۰ کاراکتر باشد")
	@Size(max = 20, message = "شماره تماس مدیر عامل نباید بیشتر از ۲۰ کاراکتر باشد")
	private String ceoPhone;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("BrokerDto.Create")
	@NoArgsConstructor
	public static class Create extends BrokerDto {
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("BrokerDto.Info")
	@NoArgsConstructor
	public static class Info extends BrokerDto {
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}
}