package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;

import java.math.BigDecimal;

public record PerformaDetailGenerator(PerfomaCreateRequest requestDto,
                                      IMETradeModel tradeModel,
                                      BigDecimal vat,
                                      GoodsModel good,
                                      Integer jalaliYear,
                                      GoodsBucketModel goodsBucketModel,
                                      SaleConditionModel saleConditionModel) {
}