package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
import com.nicico.internal.sales.trade.model.TradeExtractModel;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO to hold all data required for Proforma creation.
 * This consolidates data fetching from multiple repositories into a single object.
 */
@Getter
@Builder
public class ProformaCreationContext {
    
    private final TradeExtractModel tradeExtract;
    private final IMETradeModel tradeModel;
    private final GoodsModel goodsModel;
    private final SaleConditionModel saleConditionModel;
    private final GoodsBucketModel goodsBucketModel;
    private final CustomerModel customerModel;
    private final Integer jalaliYear;
    
}
