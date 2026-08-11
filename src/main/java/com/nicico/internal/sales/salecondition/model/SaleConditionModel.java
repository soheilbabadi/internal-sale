package com.nicico.internal.sales.salecondition.model;

import com.nicico.internal.sales.config.BaseClassModel;
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
@Entity
@Builder
@Table(name = "T_INS_SALE_CONDITION")
public class SaleConditionModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -8232856503968765193L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_SALE_CONDITION")
	@SequenceGenerator(name = "SEQ_INS_SALE_CONDITION", sequenceName = "SEQ_INS_SALE_CONDITION", allocationSize = 1)
	@Column(name = "ID")
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_START_DATE", nullable = false)
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "D_EXPIRE_DATE")
	private Date expireDate;
	@Column(name = "N_STORAGE_DEADLINE", nullable = false)
	private Integer storageDeadline;
	@Column(name = "N_STORAGE_COST", nullable = false, precision = 10, scale = 2)
	private BigDecimal storageCost;
	@Column(name = "N_CREDIT_EXPIRE_PERIOD", nullable = false)
	private Integer creditExpirePeriod;
	@Column(name = "N_SHIPPING_DEADLINE", nullable = false)
	private Integer shippingDeadline;
	@Column(name = "N_PAYMENT_DEFERRAL", nullable = false)
	private Integer paymentDeferral;
	@Column(name = "N_GOOD_ID", nullable = false)
	private Long goodId;
	@Column(nullable = false, name = "C_GOOD_NAME")
	private String goodName;
	@Column(name = "N_IME_COMMODITY_ID")
	private Long imeCommodityId;
	@Column(name = "C_IME_COMMODITY_SYMBOL", length = 100)
	private String imeCommoditySymbol;

	@Column(name = "N_EXTRA_BILL_OF_EXCHANGE_PERCENT", nullable = false, precision = 10, scale = 2, columnDefinition = "NUMBER(10,2) DEFAULT 0")
	@Builder.Default
	private BigDecimal extraBillOfExchangePercent = BigDecimal.ZERO;


	@Column(name = "N_EXTRA_GAM_CERTIFICATE_PERCENT", nullable = false, precision = 10, scale = 2, columnDefinition = "NUMBER(10,2) DEFAULT 0")
	@Builder.Default
	private BigDecimal extraGamCertificatePercent = BigDecimal.ZERO;
}