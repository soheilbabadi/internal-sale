package com.nicico.internal.sales.bill.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bill.dto.BillingTradeExtractDto;
import com.nicico.internal.sales.bill.dto.BillingTradeExtractMapper;
import com.nicico.internal.sales.bill.repository.BillingTradeExtractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillingDataProviderServiceImpl implements BillingDataProviderService {
	private final BillingTradeExtractMapper billingTradeExtractMapper;
	private final BillingTradeExtractRepository billingTradeExtractRepository;


	@Override
	public SearchDTO.SearchRs<BillingTradeExtractDto.Info> searchBillingStartDataProvider(SearchDTO.SearchRq request) {
		return SearchUtil.search(billingTradeExtractRepository, request, billingTradeExtractMapper::toDTO);
	}


}
