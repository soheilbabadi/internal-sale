package com.nicico.internal.sales.goods.special.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serial;

@Audited
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "T_INS_PRECIOUS_METAL")
public class PreciousMetalModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -5643169544482054073L;
	@Id
	@Column(name = "ID")
	private Long id;
	@Column(nullable = false, name = "C_NAME", unique = true)
	private String name;
	@Column(name = "N_IME_COMMODITY_ID")
	private Long imeCommodityId;
	@Column(name = "C_IME_COMMODITY_SYMBOL", length = 100)
	private String imeCommoditySymbol;
}
