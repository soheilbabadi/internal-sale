package com.nicico.internal.sales.ins.customer.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.ins.customer.dto.CustomerDTO;
import com.nicico.internal.sales.ins.customer.dto.CustomerMapper;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.trade.service.TradeExtractService;
import com.nicico.internal.sales.util.TextUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
	private final CustomerRepository repository;
	private final CustomerMapper customerMapper;
	private final TradeExtractService tradeExtractService;

	@Override
	public SearchDTO.SearchRs<CustomerDTO.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, customerMapper::toDTO);
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
	public CustomerDTO.Info update(Long id, CustomerDTO.Create request) {
		var model = repository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						"اطلاعات مشتری پیدا نشد"));
		BeanUtils.copyProperties(request, model, "id");
		model.setLastModifiedDate(new Date());
		model.setLastModifiedBy(SecurityUtil.getUsername());
		var updatedCustomer = repository.saveAndFlush(model);
		return customerMapper.toDTO(updatedCustomer);
	}

	@Override
	public CustomerDTO.Info save(CustomerDTO.Create request) {
		if (hasSimilarName(request.getName()) || repository.existsByNationalCode(request.getNationalCode())) {
			throw new InternalSaleCustomException.DuplicateEntityException(
					"این مشتری قبلا ثبت شده است");
		}
		CustomerModel model = customerMapper.fromDTO(request);
		model.setLastModifiedDate(new Date());
		model.setLastModifiedBy(SecurityUtil.getUsername());
		if (request.getRegisterNumber() == null) {
			model.setRegisterNumber(String.valueOf(RandomUtils.nextInt(10000, 99999999)));
		}
		CustomerModel save = repository.save(model);
		return customerMapper.toDTO(save);
	}

	@Override
	public void importTradeData() {
		var tradeList = tradeExtractService.listDistinctBuyersNotInCustomers();
		tradeList.forEach(trade -> {
			if (!repository.existsByNationalCode(trade.getBuyerNationalCode())) {
				var customer = new CustomerModel();
				customer.setName(trade.getBuyerName());
				customer.setNationalCode(trade.getBuyerNationalCode());
				repository.save(customer);
				log.info("Imported customer from trade: {}", customer);
			}
		});
		var commodityList = tradeExtractService.listDistinctBuyersNotInCustomers();
		commodityList.forEach(trade -> {
			if (!repository.existsByNationalCode(trade.getBuyerNationalCode())) {
				var customer = new CustomerModel();
				customer.setName(trade.getBuyerName());
				customer.setNationalCode(trade.getBuyerNationalCode());
				repository.save(customer);
				log.info("Imported customer from ime trade: {}", customer);
			}
		});
	}

	public List<CustomerDTO.Info> findSimilarNames(String name) {
		return repository.findAll().stream().filter(customer -> TextUtility.getSimilarity(customer.getName(), name) > 85.0).map(customerMapper::toDTO).toList();
	}

	private boolean hasSimilarName(String name) {
		return repository.findAll().stream().anyMatch(customer -> TextUtility.getSimilarity(customer.getName(), name) > 99.0);
	}
}
