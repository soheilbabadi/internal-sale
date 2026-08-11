package com.nicico.internal.sales.goods.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "T_INS_PMS_MAPPING")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PmsMappingModel {
	@Id
	@Column(name = "N_PMS_ID")
	private Long pmsId;

	@Column(name = "C_GOOD_NAME")
	private String goodName;
	@Column(name = "C_PMS_NAME")
	private String pmsName;
	@Column(name = "n_PMS_packing_code")
	private Long pmsPackingCode;

}
