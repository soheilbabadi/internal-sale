package com.nicico.internal.sales.proforma.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Data
public abstract class MixedProformaRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = 8178744733558050537L;

	@Schema(description = "شناسه آگهی عرضه", name = "tradeId", example = "12")
	private Long tradeId;
	private Date orderDate;
	private List<MixedProformaDetailRequest> mixedProformaDetailRequests;
}