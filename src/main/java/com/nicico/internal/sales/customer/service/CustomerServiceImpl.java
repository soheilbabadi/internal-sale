package com.nicico.internal.sales.customer.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.accounting.dto.CreateCompanyDetailDto;
import com.nicico.internal.sales.accounting.dto.CreatePersonDetailDto;
import com.nicico.internal.sales.accounting.dto.DetailDto;
import com.nicico.internal.sales.accounting.service.AccountingDetailService;
import com.nicico.internal.sales.accounting.util.NationalCodeValidator;
import com.nicico.internal.sales.customer.dto.CustomerDTO;
import com.nicico.internal.sales.customer.dto.CustomerMapper;
import com.nicico.internal.sales.customer.model.CustomerModel;
import com.nicico.internal.sales.customer.repository.CustomerRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.trade.service.TradeExtractService;
import com.nicico.internal.sales.util.TextUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;
	private final TradeExtractService tradeExtractService;
	private final CustomerValidationService customerValidationService;
	private final AccountingDetailService accountingDetailService;

	@Override
	public SearchDTO.SearchRs<CustomerDTO.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(customerRepository, request, customerMapper::toDTO);
	}

	@Override
	public void delete(Long id) {
		return;
	}

	@Override
	public CustomerDTO.Info update(Long id, CustomerDTO.Create request) {
		customerValidationService.validateUpdateCustomer(request);

		var model = customerRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						"اطلاعات مشتری پیدا نشد"));
		BeanUtils.copyProperties(request, model, "id");
		model.setLastModifiedDate(new Date());
		model.setLastModifiedBy(SecurityUtil.getUsername());
		var updatedCustomer = customerRepository.saveAndFlush(model);
		return customerMapper.toDTO(updatedCustomer);
	}

	@Override
	public CustomerDTO.Info save(CustomerDTO.Create request) {

		importTradeData();

		customerValidationService.validateCreateCustomer(request);

		CustomerModel model = customerMapper.fromDTO(request);
		model.setLastModifiedDate(new Date());
		model.setLastModifiedBy(SecurityUtil.getUsername());
		if (request.getRegisterNumber() == null) {
			model.setRegisterNumber(String.valueOf(RandomUtils.nextInt(10000, 99999999)));
		}
		CustomerModel save = customerRepository.save(model);
		return customerMapper.toDTO(save);
	}

	@Override
	public void importTradeData() {
		var tradeList = tradeExtractService.listDistinctBuyersNotInCustomers();
		tradeList.forEach(trade -> {
			if (!customerRepository.existsByNationalCode(trade.getBuyerNationalCode())) {
				var customer = new CustomerModel();
				customer.setName(trade.getBuyerName());
				customer.setNationalCode(trade.getBuyerNationalCode());
				customerRepository.save(customer);
				log.info("Imported customer from trade: {}", customer);
			}
		});
		var commodityList = tradeExtractService.listDistinctBuyersNotInCustomers();
		commodityList.forEach(trade -> {
			if (!customerRepository.existsByNationalCode(trade.getBuyerNationalCode())) {
				var customer = new CustomerModel();
				customer.setName(trade.getBuyerName());
				customer.setNationalCode(trade.getBuyerNationalCode());
				customerRepository.save(customer);
				log.info("Imported customer from ime trade: {}", customer);
			}
		});
	}


	@Override
	public List<CustomerDTO.Info> findSimilarNames(String name) {
		return customerRepository.findAll().stream().filter(customer ->
				TextUtility.getSimilarity(customer.getName(), name) > 85.0)
				.map(customerMapper::toDTO).toList();
	}

	@Override
	@Transactional
	public String saveAccDetailCode(Long id) {
		var model = customerRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						"اطلاعات مشتری پیدا نشد"));

		if (StringUtils.isBlank(model.getNationalCode())) {
			throw new InternalSaleCustomException.ValidationException("شناسه یا کد ملی مشتری نمی تواند خالی باشد");
		}

		String normalizedNationalCode = NationalCodeValidator.normalize(model.getNationalCode());
		boolean isCompany = NationalCodeValidator.isCompany(normalizedNationalCode);

		Optional<DetailDto> existingDetail = isCompany
				? accountingDetailService.findDetailForCompany(normalizedNationalCode)
				: accountingDetailService.findDetailForPerson(normalizedNationalCode);

		if (existingDetail.isPresent()) {
			String detailCode = existingDetail.get().getCode();
			if (!Objects.equals(model.getNosaCode(), detailCode)) {
				model.setNosaCode(detailCode);
				customerRepository.save(model);
			}
			return detailCode;
		}

		DetailDto createdDetail;
		if (isCompany) {
			CreateCompanyDetailDto req = CreateCompanyDetailDto.builder()
					.companyName(model.getName())
					.nationalId(normalizedNationalCode)
					.detailNameLatin(model.getNameEn())
					.build();
			createdDetail = accountingDetailService.createDetailForCompany(req);
		} else {
			CreatePersonDetailDto req = CreatePersonDetailDto.builder()
					.fullName(model.getName())
					.nationalCode(normalizedNationalCode)
					.build();
			createdDetail = accountingDetailService.createDetailForPerson(req);
		}

		model.setNosaCode(createdDetail.getCode());
		customerRepository.save(model);
		return createdDetail.getCode();
	}

}
