package com.nicico.internal.sales.history.dto;

import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.proforma.dto.ProformaResponseDto;
import com.nicico.internal.sales.remittance.dto.RemittanceGoodItemDto;
import com.nicico.internal.sales.remittance.dto.RemittanceMasterDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HistoryDetailResponse implements Serializable {

	@Serial
	private static final long serialVersionUID = -125054632475631393L;

	private ProformaResponseDto proformaResponseDto;
	private List<LcDto> lcDtoList;
	private RemittanceMasterDto remittanceMasterDto;
	private List<RemittanceGoodItemDto> remittanceGoodItemDtoList;
}
