package com.nicico.internal.sales.loading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGoodLoadingBatch implements java.io.Serializable {
	@Serial
	private static final long serialVersionUID = -5659483848575464166L;
	private List<Long> goodId;
	private Long loadingPlaceId;
}
