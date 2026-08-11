package com.nicico.internal.sales.proforma.records;

import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.proforma.dto.PreciousMetalProfomaCreateRequest;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;

import java.math.BigDecimal;

public record PreciousMetalDetailGenerator(PreciousMetalProfomaCreateRequest requestDto,
                                           IMETradeModel tradeModel,
                                           BigDecimal vat,
                                           GoodsModel good,
                                           Integer jalaliYear,
                                           CustomerModel customerModel,
                                           GoodsBucketModel goodsBucketModel,
                                           SaleConditionModel saleConditionModel) {
}