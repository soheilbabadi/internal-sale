package com.nicico.internal.sales.loading.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Audited
@Table(name = "T_INS_LOADING_GOODS", uniqueConstraints = {@UniqueConstraint(columnNames = {"N_GOOD_ID", "N_LOADING_PLACE_ID"})})
public class LoadingGoodsModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = 6313070493614504250L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_LOADING")
	@SequenceGenerator(name = "SEQ_INS_LOADING", sequenceName = "SEQ_INS_LOADING", allocationSize = 1)
	private Long id;
	@Column(name = "N_LOADING_PLACE_ID", nullable = false)
	private Long loadingPlaceId;
	@Column(name = "N_GOOD_ID", nullable = false)
	private Long goodId;
}
