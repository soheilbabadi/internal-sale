package com.nicico.internal.sales.lc.service;

import com.nicico.internal.sales.broker.model.BrokerModel;

public interface CreditHelper {
	BrokerModel fetchBrokerForTrade(Long tradeId);
}
