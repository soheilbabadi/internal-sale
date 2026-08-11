package com.nicico.internal.sales.loading.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;

@Audited
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "T_INS_LOADING_PLACE")
public class LoadingPlaceModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = 6313070493614504250L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_LOADING")
	@SequenceGenerator(name = "SEQ_INS_LOADING", sequenceName = "SEQ_INS_LOADING", allocationSize = 1)
	private Long id;
	@Column(name = "C_PLACE_TITLE", unique = true)
	private String placeTitle;
	@Column(name = "C_PLACE_VALUE", unique = true)
	private String placeValue;
	@Column(name = "N_PMS_LOADING_ID")
	private Long pmsLoadingId;
}
