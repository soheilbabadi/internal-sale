package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
import com.nicico.internal.sales.trade.model.TradeExtractModel;

import java.math.BigDecimal;

public record CashSaleDetailGenerator(CashSaleCreateRequest requestDto,
                                      IMETradeModel tradeModel,
                                      BigDecimal vat,
                                      GoodsModel good,
                                      Integer jalaliYear,
                                      CustomerModel customerModel,
                                      GoodsBucketModel goodsBucketModel,
                                      SaleConditionModel saleConditionModel,
                                      TradeExtractModel tradeExtract, boolean cashPercentTotal) {
}