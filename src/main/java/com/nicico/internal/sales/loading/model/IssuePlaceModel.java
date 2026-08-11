package com.nicico.internal.sales.loading.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "T_INS_ISSUE_PLACE")
@Audited
public class IssuePlaceModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -7493518172380224686L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_LOADING")
	@SequenceGenerator(name = "SEQ_INS_LOADING", sequenceName = "SEQ_INS_LOADING", allocationSize = 1)
	private Long id;
	@Column(name = "C_PLACE_TITLE", unique = true, nullable = false)
	private String placeTitle;
	@Column(name = "C_PLACE_VALUE", unique = true, nullable = false)
	private String placeValue;
}
