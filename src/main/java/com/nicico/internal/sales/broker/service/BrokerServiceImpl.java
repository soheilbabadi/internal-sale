package com.nicico.internal.sales.broker.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.broker.dto.BrokerDto;
import com.nicico.internal.sales.broker.mapper.BrokerMapper;
import com.nicico.internal.sales.broker.model.BrokerModel;
import com.nicico.internal.sales.broker.repository.BrokerRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BrokerServiceImpl implements BrokerService {
	private static final String BROKER_NOT_FOUND_MESSAGE = "کارگزار  تعریف نشده است";
	private static final String TRADE_NOT_FOUND_MESSAGE = "آگهی عرضه وجود ندارد";
	private final BrokerRepository repository;
	private final BrokerMapper mapper;
	private final IMETradeRepository imeTradeRepository;

	@Override
	public BrokerDto.Info save(BrokerDto.Create request) {
		BrokerModel model = repository.findById(request.getId()).orElseThrow(() -> new InternalSaleCustomException.ValidationException(BROKER_NOT_FOUND_MESSAGE));
		BeanUtils.copyProperties(request, model);
		repository.save(model);
		return mapper.toDTO(model);
	}

	@Override
	public SearchDTO.SearchRs<BrokerDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}

	@Override
	public BrokerDto.Info getById(long id) {
		BrokerModel model = repository.findById(id).orElseThrow(() -> new InternalSaleCustomException.ValidationException(BROKER_NOT_FOUND_MESSAGE));
		return mapper.toDTO(model);
	}

	@Override
	public List<BrokerDto.Info> getAll() {
		List<BrokerModel> models = repository.findAll();
		models.sort(Comparator.comparing(BrokerModel::getId));
		return models.stream().map(mapper::toDTO).toList();
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
	public Map<String, String> contactMissing(Long id) {
		BrokerModel model = repository.findById(id).orElseThrow(() -> new InternalSaleCustomException.ValidationException(BROKER_NOT_FOUND_MESSAGE));
		java.util.Map<String, String> errors = new java.util.LinkedHashMap<>();
		if (model.getPhone() == null || model.getPhone().trim().isEmpty()) {
			errors.put("phone", "شماره تماس وارد نشده است");
		}
		if (model.getPostCode() == null || model.getPostCode().trim().isEmpty()) {
			errors.put("postCode", "کد پستی وارد نشده است");
		}
		if (model.getEmail() == null || model.getEmail().trim().isEmpty()) {
			errors.put("email", "ایمیل وارد نشده است");
		}
		if (model.getAddress() == null || model.getAddress().trim().isEmpty()) {
			errors.put("address", "آدرس وارد نشده است");
		}
		if (model.getCeoName() == null || model.getCeoName().trim().isEmpty()) {
			errors.put("ceoName", "نام مدیرعامل وارد نشده است");
		}
		return errors;
	}

	@Override
	public BrokerDto.Info getByTradeId(long id) {
		IMETradeModel trade = imeTradeRepository.findFirstByIdOrderByIdDesc(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(TRADE_NOT_FOUND_MESSAGE));

		var broker = repository.findById(Long.valueOf(trade.getSellerBrokerCode()))
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(BROKER_NOT_FOUND_MESSAGE));
		return mapper.toDTO(broker);
	}
}