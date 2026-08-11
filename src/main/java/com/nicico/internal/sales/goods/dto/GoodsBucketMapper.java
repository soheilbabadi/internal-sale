package com.nicico.internal.sales.goods.dto;

import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GoodsBucketMapper {
	GoodBucketDto.Info toDTO(GoodsBucketModel request);

	GoodsBucketModel fromDTO(GoodBucketDto.Create request);
}