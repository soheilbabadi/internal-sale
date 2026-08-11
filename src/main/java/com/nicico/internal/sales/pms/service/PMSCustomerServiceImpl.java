package com.nicico.internal.sales.pms.service;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.grid.TotalResponse;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.pms.dto.PMSCustomerDTO;
import com.nicico.internal.sales.pms.dto.PMSCustomerMapper;
import com.nicico.internal.sales.pms.model.PMSCustomerModel;
import com.nicico.internal.sales.pms.repository.PMSCustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PMSCustomerServiceImpl implements PMSCustomerService {
	private final PMSCustomerRepository customerRepository;
	private final PMSCustomerMapper customerMapper;


	@Override
	public SearchDTO.SearchRs<PMSCustomerDTO.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(customerRepository, request, customerMapper::toDTO);
	}

	@Override
	public TotalResponse<PMSCustomerDTO.Info> search(NICICOCriteria request) {
		return SearchUtil.search(customerRepository, request, customerMapper::toDTO);
	}

	@Override
	public PMSCustomerModel findByEconomicCodeOrRegisterNumber(String economicCode, String registerNumber) {
		if (registerNumber == null || economicCode == null) {
			throw new InternalSaleCustomException.ValidationException("کد اقتصادی مشتری ثبت نشده است");
		}
		log.info("economicCode" + economicCode);
		log.info("registerNumber" + registerNumber);
		return customerRepository.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(economicCode,
						registerNumber)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("مشتری در سیستم لجستیک ثبت نشده است"));
	}
}
