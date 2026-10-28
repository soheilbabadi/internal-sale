package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.enums.DepositSlipType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashDepositCreateRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = -1759931369880058605L;

	private Long contractNo;
	private Long tradeId;
	private BigDecimal cashDepositAmountWithTax;
	private BigDecimal cashDepositAmountWithoutTax;
	private DepositSlipType depositSlipType;
	private boolean isPreciousMetal;
	private BigDecimal netWeight;
	private BigDecimal vatAmount;
	private BigDecimal unitPrice;
	private Date orderDate;
}
