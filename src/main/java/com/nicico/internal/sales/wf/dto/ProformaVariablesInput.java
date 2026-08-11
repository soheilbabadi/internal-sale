package com.nicico.internal.sales.wf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProformaVariablesInput implements Serializable {
	@Serial
	private static final long serialVersionUID = 9114643488025535161L;
	private long proformaMasterId;
	private String contractDate;
	private long goodId;
	private String goodName;
	private String customerName;
	private String contractNo;
	private Double commission;
}
