package com.nicico.internal.sales.bank.dto;

import com.nicico.internal.sales.config.BaseClassModel;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IssuingBankWithPmsIdDto extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -436607832387464574L;
	@Schema(name = "شناسه بانک")
	private long id;
	@Schema(name = "نام بانک")
	private String bankName;
	@Schema(name = "نام شعبه")
	private String branchName;
	@Schema(name = "کد شعبه")
	private String branchCode;
	@Schema(name = "استان شعبه")
	private String province;
	@Schema(name = "شهر شعبه")
	private String city;

	@Schema(name = "کد بانک")
	private String bankCode;
	@Schema(name = "baseNosaCode", description = "کد نوسا بانک در سیستم حسابداری")
	private String baseNosaCode;
	private String pmsLcBankId;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("IssuingBankDto.Info")
	@NoArgsConstructor
	public static class Info extends IssuingBankWithPmsIdDto {
	}
}
