package com.nicico.internal.sales.ins.customer.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.ins.customer.dto.CustomerContactDto;
import com.nicico.internal.sales.ins.customer.dto.CustomerContactMapper;
import com.nicico.internal.sales.ins.customer.model.CustomerContactModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerContactRepository;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.util.TextUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class CustomerContactServiceImpl implements CustomerContactService {
	private final CustomerContactRepository repository;
	private final CustomerContactMapper mapper;
	private final CustomerRepository customerRepository;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public CustomerContactDto.Create save(CustomerContactDto.Create request) {
		var customer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						"اطلاعات مشتری وجود ندارد"));
		if (customer.getAddress() == null || customer.getEmail() == null || customer.getMobile() == null || customer.getPostCode() == null) {
			request.setDefault(true);
		}
		request.setValid(true);
		CustomerContactModel model = mapper.fromDTO(request);
		repository.saveAndFlush(model);
		setDefaultContact(model);
		customer.setLastModifiedDate(new Date());
		customer.setLastModifiedBy((SecurityUtil.getUsername()));
		customerRepository.saveAndFlush(customer);
		return mapper.toDTO(model);
	}

	@Override
	public SearchDTO.SearchRs<CustomerContactDto.Create> filterByCustomer(long customerId, SearchDTO.SearchRq request) {
		request.setCount(1000);
		var result = SearchUtil.search(repository, request, mapper::toDTO);
		result.getList().removeIf(customer -> !customer.isValid() || customer.getCustomerId() != customerId);
		return result;
	}

	@Override
	public void delete(Long id) {
		var customer = repository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						"اطلاعات تماس مشتری پیدا نشد"));
		if (customer.isDefault()) {
			throw new InternalSaleCustomException.ValidationException(
					"امکان حذف اطلاعات تماس پیشفرض وجود ندارد");
		}
		repository.deleteById(id);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public CustomerContactDto.Create update(Long id, CustomerContactDto.Create request) {
		var customer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						"اطلاعات مشتری وجود ندارد"));
		request.setAddress(TextUtility.reformatAddress(request.getAddress()));
		var contactModel = repository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						"اطلاعات تماس مشتری پیدا نشد"));
		BeanUtils.copyProperties(request, contactModel, "id");
		var updatedCustomer = repository.saveAndFlush(contactModel);
		if (updatedCustomer.isDefault()) {
			setDefaultContact(contactModel);
		}
		customer.setLastModifiedDate(new Date());
		customer.setLastModifiedBy((SecurityUtil.getUsername()));
		customerRepository.saveAndFlush(customer);
		return mapper.toDTO(updatedCustomer);
	}

	public void setDefaultContact(CustomerContactModel contact) {
		var otherContacts = repository.findAllByCustomerId(contact.getCustomerId());
		otherContacts.forEach(c -> {
			if (!Objects.equals(c.getId(), contact.getId())) {
				c.setDefault(false);
				repository.saveAndFlush(c);
			}
		});
		contact.setDefault(true);
		repository.save(contact);
		var customer = customerRepository.findById(contact.getCustomerId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						"اطلاعات مشتری وجود ندارد"));
		customer.setAddress(contact.getAddress());
		customer.setEmail(contact.getEmail());
		customer.setPhone(contact.getPhone());
		customer.setCoordinator(contact.getCoordinator());
		customer.setPostCode(contact.getPostCode());
		customer.setMobile(contact.getMobile());
		customerRepository.save(customer);
	}
}