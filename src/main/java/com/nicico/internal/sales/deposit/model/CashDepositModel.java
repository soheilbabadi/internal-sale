package com.nicico.internal.sales.deposit.model;
import com.nicico.internal.sales.config.BaseClassModel;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.proforma.enums.DepositSlipType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

@Audited
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "T_INS_CASH_DEPOSIT")
public class CashDepositModel extends BaseClassModel {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_CASHDEPOSIT")
	@SequenceGenerator(name = "SEQ_INS_CASHDEPOSIT", sequenceName = "SEQ_INS_CASHDEPOSIT", allocationSize = 1)
	private Long id;
	@Schema(description = "کد تفصیلی")
	@Column(name = "C_NOSA_CODE", length = 50)
	private String nosaCode;
	@Schema(description = "تاریخ صدور")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_ISSUE_DATE")
	private Date issueDate;
	@Schema(description = "شناسه جزئیات پیش فاکتور", name = "proformaDetailId", example = "52")
	@Column(name = "F_PROFORMA_DETAIL_ID")
	private Long proformaDetailId;

	@Column(name = "F_PERFORMA_MASTER_ID")
	private Long proformaMasterId;
	@Schema(description = "شماره قرارداد", name = "contractNo", example = "123456")
	@Column(name = "N_CONTRACT_NO")
	private Long contractNo;

	@Column(name = "F_TRADE_ID")
	private Long tradeId;
	@Enumerated(EnumType.STRING)
	@Column(name = "C_ACKNOWLEDGMENT", length = 50)
	@Schema(description = "تاییدیه نوع تسویه و نوع حواله")
	private Acknowledgment acknowledgment;

	@Schema(description = "ایمیل تاییدیه ارسال شده است یا خیر")
	@Column(name = "IS_RECKONING_SEND")
	private boolean isReckoningSend = false;

	@Schema(description = "تاریخ ارسال ایمیل تاییدیه", name = "reckoningSendDate")
	@Column(name = "D_RECKONING_SEND_DATE")
	private Date reckoningSendDate;


	@Column(name = "N_DEPOSIT_SLIP_WITH_TAX")
	private BigDecimal depositSlipWithTax;

	@Column(name = "N_DEPOSIT_SLIP_WITHOUT_TAX")
	private BigDecimal depositSlipWithoutTax ;

	@Enumerated(EnumType.STRING)
	@Column(name = "C_DEPOSIT_SLIP_TYPE", length = 20)
	private DepositSlipType depositSlipType;

}