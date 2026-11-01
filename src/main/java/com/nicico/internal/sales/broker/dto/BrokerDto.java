package com.nicico.internal.sales.broker.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
	private String name;

	@Schema(description = "کد ملی", example = "1234567890")
	private String nationalCode;

	@Schema(description = "تلفن", example = "021-1234-5678")
	private String phone;

	@Schema(description = "کد اقتصادی", example = "1234567890")
	private String economicCode;

	@Schema(description = "کد پستی", example = "1234567890")
	private String postCode;

	@Schema(description = "ایمیل", example = "example@example.com")
	private String email;

	@Schema(description = "شماره موبایل", example = "09123456789")
	private String mobile;

	@Schema(description = "نام مسئول", example = "محمدرضا")
	private String coordinator;

	@Schema(description = "آدرس")
	private String address;

	@Schema(description = "نام مدیر عامل")
	private String ceoName;

	@Schema(description = "شماره تماس مدیر عامل")
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