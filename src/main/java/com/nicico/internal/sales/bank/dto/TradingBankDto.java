package com.nicico.internal.sales.bank.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradingBankDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1558252096712527624L;
	@Schema(name = "شناسه بانک")
	private long id;
	@NotBlank(message = "نام بانک نمی تواند خالی باشد")
	@Schema(name = "نام بانک")
	private String bankTitle;
	@NotBlank(message = "عنوان شعبه نمی تواند خالی باشد")
	@Schema(name = "عنوان شعبه")
	private String bankBranchTitle;
	@NotBlank(message = "کد شعبه نمی تواند خالی باشد")
	@Schema(name = "کد شعبه")
	private String branchCode;
	@Size(max = 100, message = "شماره حساب سپرده نمی تواند بیشتر از ۱۰۰ کاراکتر باشد")
	@Schema(name = "حساب سپرده")
	private String accountNumber;
	@Pattern(regexp = "^IR\\d{24}$", message = "شماره شبا باید با IR شروع شود و 24 رقم بعد از آن باشد")
	@Size(max = 26, message = "شماره شبا نمی تواند بیشتر از ۲۶ کاراکتر باشد")
	@Schema(name = "شماره شبا", example = "IR0696000000010324200001")
	private String iban;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("TradingBankDto.Create")
	@NoArgsConstructor
	public static class Create extends TradingBankDto {
		@Serial
		private static final long serialVersionUID = -7026713351296540537L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("TradingBankDto.Info")
	@NoArgsConstructor
	public static class Info extends TradingBankDto {
		@Serial
		private static final long serialVersionUID = 8641387470770159481L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
		private String description;
		private Integer version;
	}
}
