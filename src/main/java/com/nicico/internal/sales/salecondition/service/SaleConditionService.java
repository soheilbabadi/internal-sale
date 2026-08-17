package com.nicico.internal.sales.salecondition.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.salecondition.dto.SaleConditionDto;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;

import java.util.Date;
import java.util.List;

public interface SaleConditionService {
	SaleConditionDto.Info save(SaleConditionDto.Create request);

	SaleConditionDto.Info getCurrentRule(long goodId);

	List<SaleConditionDto.Info> getGoodsHistory(long goodId);

	List<SaleConditionDto.Info> getCurrentRule();

	SearchDTO.SearchRs<SaleConditionDto.Info> search(SearchDTO.SearchRq request);

	SaleConditionModel getSaleConditionByPaymentCode(String contractNo);

	SaleConditionModel getOnSpecificDateModel(long goodId, Date targetDate);

	SaleConditionDto.Info getOnSpecificDate(long goodId, Date targetDate);

//	SaleConditionModel findByCommodityId(Long commodityId);

	SaleConditionDto.Info findByPaymentCode(String paymentCode);

	SaleConditionModel findByPaymentCodeModel(String paymentCode);
}
