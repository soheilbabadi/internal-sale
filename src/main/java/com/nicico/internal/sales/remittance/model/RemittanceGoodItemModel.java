package com.nicico.internal.sales.remittance.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "T_INS_REMITTANCE_GOOD_ITEM")
public class RemittanceGoodItemModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -4013840544922077545L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_PERFORMA_GOOD_ITEM")
	@SequenceGenerator(name = "SEQ_INS_PERFORMA_GOOD_ITEM", sequenceName = "SEQ_INS_PERFORMA_GOOD_ITEM", allocationSize = 1)
	private Long id;
	@Column(name = "N_GOOD_ID")
	private Long goodId;
	@Column(name = "C_GOOD_NAME", length = 100)
	private String goodName;
	@Column(name = "N_UNIT_ID")
	private Long unitId;
	@Column(name = "N_QUANTITY", precision = 10, scale = 3)
	private BigDecimal quantity;
	@Column(name = "N_CREDIT_AMOUNT")
	private BigDecimal creditAmount;
	@Column(name = "N_CASH_AMOUNT")
	private BigDecimal cashAmount;
	@Column(name = "N_TOTAL_AMOUNT")
	private BigDecimal totalAmount;
	@Column(name = "N_VAT_AMOUNT")
	private BigDecimal vatAmount;
	@Column(name = "N_UNIT_PRICE_CREDIT")
	private BigDecimal unitPriceCredit;
	@Column(name = "N_UNIT_PRICE_CASH")
	private BigDecimal unitPriceCash;
	@Column(name = "N_NET_QUANTITY", precision = 10, scale = 3)
	private BigDecimal netQuantity;
	@Column(name = "N_FINAL_AMOUNT")
	private BigDecimal finalAmount;
	@Column(name = "N_UNIT_PRICE")
	private BigDecimal unitPrice;
	@Column(name = "N_CREDIT_QUANTITY", precision = 10, scale = 3)
	private BigDecimal creditQuantity;
	@Column(name = "C_LOT_NUMBER")
	private String lotNumber;
	@Column(name = "N_CREDIT_PERCENTAGE")
	private BigDecimal creditPercentage;
	@ManyToOne(targetEntity = RemittanceMasterModel.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "F_REMITTANCE_ID", referencedColumnName = "ID")
	@EqualsAndHashCode.Exclude
	private RemittanceMasterModel remittanceMasterModel;
}
