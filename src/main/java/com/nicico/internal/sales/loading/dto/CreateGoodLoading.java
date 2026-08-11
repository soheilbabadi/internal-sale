package com.nicico.internal.sales.loading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGoodLoading implements java.io.Serializable {
	@Serial
	private static final long serialVersionUID = -5659483848575464166L;
	private Long goodsId;
	private Long loadingPlaceId;
}
