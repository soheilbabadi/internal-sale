package com.nicico.internal.sales.goods.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "T_INS_GOODS_BUCKET")
@Builder
@Audited
@AllArgsConstructor
@NoArgsConstructor
public class GoodsBucketModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -5775379242665837293L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ins_goods_bucket")
	@SequenceGenerator(name = "seq_ins_goods_bucket", sequenceName = "seq_ins_goods_bucket", allocationSize = 1)
	private Long id;
	@Column(nullable = false, name = "N_GOOD_ID")
	private Long goodId;
	@Column(nullable = false, name = "N_IME_COMMODITY_ID")
	private Long imeCommodityId;
	@Column(nullable = false, name = "C_GOOD_NAME", length = 100)
	private String goodName;
	@Column(name = "C_IME_COMMODITY_SYMBOL", length = 100)
	private String imeCommoditySymbol;
	@Column(name = "N_PACKAGING_SIZE")
	private BigDecimal packagingSize;
	@Column(name = "C_PACKING_NAME", length = 100)
	private String packingName;
	@Column(name = "N_PACKING_ID")
	private Integer packingId;
	@Column(name = "N_CASH_PERCENTAGE")
	private BigDecimal cashPercentage;
	@Column(name = "N_COMMISSION")
	private Double commission;
	@Column(name = "D_START_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	@Column(name = "D_EXPIRE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expireDate;
	@Column(name = "N_DIVISIBILITY_CHECK")
	private BigDecimal divisibilityCheck;

}
