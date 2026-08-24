package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.proforma.dto.PerfomaCreateRequest;
import com.nicico.internal.sales.proforma.dto.PerformerCreateRevealRequest;
import com.nicico.internal.sales.proforma.dto.ProformaCreationContext;
import com.nicico.internal.sales.proforma.dto.ProformaModelResponse;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;

import java.math.BigDecimal;

public interface ProformaContractService {
	ProformaModelResponse getContractDetail(PerfomaCreateRequest requestDto);

	ProformaModelResponse getContractDetailReversal(PerformerCreateRevealRequest requestDto);

	/**
	 * Fetches and assembles all data required for proforma creation.
	 * This centralizes data fetching logic to avoid duplication across services.
	 */
	ProformaCreationContext getProformaCreationData(Long tradeId, String paymentCode, Integer jalaliYear);

	GoodsModel findGoodsModelByCommodityCode(Long commodityCode);

	IMETradeModel getTradeModel(String paymentCode);

	IMETradeModel getTradeModel(Long tradeId);

	GoodsModel getGoodsModel(String paymentCode);

	SaleConditionModel getSaleConditionModel(String paymentCode);

	BigDecimal getVat(Integer jalaliYear);

	GoodsBucketModel getGoodBucketModel(String paymentCode);

	CustomerModel getCustomerModel(String nationalCode);

	String getCleanName(GoodsModel goodsModel);


}
