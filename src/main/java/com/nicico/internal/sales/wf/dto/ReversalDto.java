package com.nicico.internal.sales.wf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReversalDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 2504643473751989136L;
	private Long proformaMasterId;
	private List<Long> performaDetailIds;
}
