package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@RequiredArgsConstructor
@Data
public abstract class MixedProformaDetailRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = 8178744733558050537L;

	private ProformaIssueType proformaIssueType;
	private BigDecimal netWeight;


}