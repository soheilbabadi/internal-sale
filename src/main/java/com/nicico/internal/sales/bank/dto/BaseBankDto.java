package com.nicico.internal.sales.bank.dto;

import com.nicico.internal.sales.config.BaseClassModel;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseBankDto extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -1581550731897231279L;

	@Schema(name = "id", description = "شناسه بانک پایه")
	@NotNull(message = "شناسه بانک پایه نمی تواند خالی باشد")
	private long id;

	@Schema(name = "bankCode", description = "کد بانک", maxLength = 50)
	@NotBlank(message = "کد بانک نمی تواند خالی باشد")
	@Size(max = 50, message = "کد بانک نمی تواند بیشتر از ۵۰ کاراکتر باشد")
	private String bankCode;

	@Schema(name = "bankTitle", description = "عنوان بانک", maxLength = 100)
	@NotBlank(message = "عنوان بانک نمی تواند خالی باشد")
	@Size(max = 100, message = "عنوان بانک نمی تواند بیشتر از ۱۰۰ کاراکتر باشد")
	private String bankTitle;

	@Schema(name = "baseNosaCode", description = "کد نوسا بانک در سیستم حسابداری", maxLength = 50)
	@NotBlank(message = "کد نوسا نمی تواند خالی باشد")
	@Size(max = 50, message = "کد نوسا نمی تواند بیشتر از ۵۰ کاراکتر باشد")
	private String baseNosaCode;

	@Schema(name = "nationalCode", description = "کد ملی بانک", maxLength = 15)
	@NotBlank(message = "کد ملی نمی تواند خالی باشد")
	@Size(max = 15, message = "کد ملی نمی تواند بیشتر از ۱۵ کاراکتر باشد")
	private String nationalCode;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("BaseBankDto.Create")
	@NoArgsConstructor
	public static class Create extends BaseBankDto {
		@Serial
		private static final long serialVersionUID = -7026713351296540537L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("BaseBankDto.Info")
	@NoArgsConstructor
	public static class Info extends BaseBankDto {
		@Serial
		private static final long serialVersionUID = 8641387470770159481L;
	}
}
