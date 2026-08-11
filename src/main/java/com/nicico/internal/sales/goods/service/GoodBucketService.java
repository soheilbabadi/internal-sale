package com.nicico.internal.sales.goods.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.goods.dto.GoodBucketDto;
import com.nicico.internal.sales.goods.dto.GoodBucketRequest;
import com.nicico.internal.sales.goods.model.GoodsBucketModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface GoodBucketService {
	SearchDTO.SearchRs<GoodBucketDto.Info> search(SearchDTO.SearchRq request);

	GoodBucketDto createGoodBucket(GoodBucketRequest request);


	GoodsBucketModel getGoodBucketActiveModel(Long goodId);

	BigDecimal getGoodBucket(Long goodId);

	GoodBucketDto.Info getOnSpecificDate(long goodId, Date targetDate);

	GoodsBucketModel getOnSpecificDateModel(long goodId, Date targetDate);

	GoodBucketDto.Info findByCommodityId(Long commodityId);

	List<GoodBucketDto.Info> getHistory(Long goodId);

	GoodsBucketModel findByPaymentCodeModel(String paymentCode);
}
