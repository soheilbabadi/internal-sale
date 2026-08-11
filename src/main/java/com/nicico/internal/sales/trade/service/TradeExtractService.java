package com.nicico.internal.sales.trade.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.goods.dto.TradeCommodityDTO;
import com.nicico.internal.sales.ins.customer.dto.TradeBuyerDTO;
import com.nicico.internal.sales.trade.dto.BuyerInfoDto;
import com.nicico.internal.sales.trade.dto.TradeExtractDto;

import java.io.IOException;
import java.util.List;

public interface TradeExtractService {
	TradeExtractDto.Info getByPaymentCode(String paymentCode);

	List<TradeExtractDto.Info> getAll();

	SearchDTO.SearchRs<TradeExtractDto.Info> searchProformaStartable(SearchDTO.SearchRq request);

	SearchDTO.SearchRs<TradeExtractDto.Info> search(SearchDTO.SearchRq request);

	List<BuyerInfoDto> listAllBuyerInfo();

	byte[] excel(SearchDTO.SearchRq request) throws IOException;

	List<TradeBuyerDTO> listDistinctBuyersNotInCustomers();

	List<TradeCommodityDTO> listDistinctCommoditiesInTrades();

	void syncDataTrade();
}
