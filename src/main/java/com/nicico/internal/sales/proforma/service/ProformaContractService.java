package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.proforma.dto.PerfomaCreateRequest;
import com.nicico.internal.sales.proforma.dto.PerformerCreateRevealRequest;
import com.nicico.internal.sales.proforma.dto.ProformaModelResponse;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;

import java.math.BigDecimal;

public interface ProformaContractService {
	ProformaModelResponse getContractDetail(PerfomaCreateRequest requestDto);

	ProformaModelResponse getContractDetailReversal(PerformerCreateRevealRequest requestDto);

	IMETradeModel getTradeModel(String paymentCode);

	IMETradeModel getTradeModel(Long tradeId);

	GoodsModel getGoodsModel(String paymentCode);

	SaleConditionModel getSaleConditionModel(String paymentCode);

	BigDecimal getVat(Integer jalaliYear);

	GoodsBucketModel getGoodBucketModel(String paymentCode);

	CustomerModel getCustomerModel(String nationalCode);

	String getCleanName(GoodsModel goodsModel);


}
