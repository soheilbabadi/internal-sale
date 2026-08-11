package com.nicico.internal.sales.bank.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class IssuingBankDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -436607832387464574L;

	@Schema(name = "شناسه بانک")
	private Long id;

	@Schema(name = "نام بانک")
	@NotBlank(message = "نام بانک نمی تواند خالی باشد")
	@Size(max = 200, message = "نام بانک نمی تواند بیشتر از ۲۰۰ کاراکتر باشد")
	private String bankName;

	@Schema(name = "نام شعبه")
	@NotBlank(message = "نام شعبه نمی تواند خالی باشد")
	@Size(max = 200, message = "نام شعبه نمی تواند بیشتر از ۲۰۰ کاراکتر باشد")
	private String branchName;

	@Schema(name = "کد شعبه")
	@Size(max = 200, message = "کد شعبه نمی تواند بیشتر از ۲۰۰ کاراکتر باشد")
	private String branchCode;

	@Schema(name = "استان شعبه")
	@Size(max = 200, message = "استان شعبه نمی تواند بیشتر از ۲۰۰ کاراکتر باشد")
	private String province;

	@Schema(name = "شهر شعبه")
	@Size(max = 200, message = "شهر شعبه نمی تواند بیشتر از ۲۰۰ کاراکتر باشد")
	private String city;

	@Schema(name = "کد بانک")
	@Size(max = 50, message = "کد بانک نمی تواند بیشتر از ۵۰ کاراکتر باشد")
	private String bankCode;

	@Schema(name = "baseNosaCode", description = "کد نوسا بانک در سیستم حسابداری")
	@NotBlank(message = "کد نوسا نمی تواند خالی باشد")
	@Size(max = 50, message = "کد نوسا نمی تواند بیشتر از ۵۰ کاراکتر باشد")
	private String baseNosaCode;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("IssuingBankDto.Create")
	@NoArgsConstructor
	public static class Create extends IssuingBankDto {
		@Serial
		private static final long serialVersionUID = -7026713351296540537L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("IssuingBankDto.Info")
	@NoArgsConstructor
	public static class Info extends IssuingBankDto {
		@Serial
		private static final long serialVersionUID = 8641387470770159481L;
	}
}
