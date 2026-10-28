package com.nicico.internal.sales.deposit.dto;

import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.proforma.enums.DepositSlipType;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CashDepositDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;

	@Schema(description = "کد تفصیلی")
	private String nosaCode;
	@Schema(description = "تاریخ صدور")
	private Date issueDate;
	@Schema(description = "شناسه جزئیات پیش فاکتور", name = "proformaDetailId", example = "52")
	private Long proformaDetailId;

	private Long proformaMasterId;

	@Schema(description = "شماره قرارداد", name = "contractNo", example = "123456")
	private Long contractNo;

	private Long tradeId;

	@Schema(description = "تاییدیه نوع تسویه و نوع حواله")
	private Acknowledgment acknowledgment ;

	@Schema(description = "ایمیل تاییدیه ارسال شده است یا خیر")
	private boolean isReckoningSend = false;

	@Schema(description = "تاریخ ارسال ایمیل تاییدیه", name = "reckoningSendDate")
	private Date reckoningSendDate;

	private BigDecimal depositSlipWithTax ;

	private BigDecimal depositSlipWithoutTax ;

	private DepositSlipType depositSlipType ;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("CashDepositDto.Info")
	public static class Info extends CashDepositDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921076L;
	}

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("CashDepositDto.Create")
	public static class Create extends CashDepositDto {
		@Serial
		private static final long serialVersionUID = 7315549626741921077L;
	}

}