package com.nicico.internal.sales.goods.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "T_INS_GOODS")
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = 7842194808789669354L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_GOODS")
	@SequenceGenerator(name = "SEQ_INS_GOODS", sequenceName = "SEQ_INS_GOODS", allocationSize = 1)
	@Column(name = "ID")
	private Long id;
	@NotBlank
	@Column(nullable = false, name = "C_NAME")
	private String name;
	@Column(name = "C_NAME_EN")
	private String nameEn;
	@Column(name = "N_PMS_GOODS_ID")
	private Long pmsGoodsId;
	@Column(name = "N_IME_COMMODITY_ID", nullable = false, unique = true)
	private Long imeCommodityId;
	@Column(name = "N_IME_COMMODITY_PARENT_ID")
	private Long imeCommodityParentId;
	@Column(name = "C_IME_COMMODITY_SYMBOL", length = 100, unique = true)
	private String imeCommoditySymbol;
}