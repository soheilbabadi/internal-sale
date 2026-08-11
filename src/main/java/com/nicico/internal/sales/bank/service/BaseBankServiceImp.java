package com.nicico.internal.sales.bank.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.BaseBankDto;
import com.nicico.internal.sales.bank.dto.BaseBankMapper;
import com.nicico.internal.sales.bank.repository.BaseBankRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.nicico.internal.sales.util.date.DateUtility.getJalaliYear;

@Service
@RequiredArgsConstructor
public class BaseBankServiceImp implements BaseBankService {
	private static final String MSG_BASE_BANK_NOT_FOUND = "بانک پیدا نشد";
	private final BaseBankMapper mapper;
	private final BaseBankRepository repository;


	@Override
	public BaseBankDto save(BaseBankDto.Create dto) {
		repository.updateBaseNosaCodeWithYearSuffix(String.format("%02d", getJalaliYear(new Date()) % 100));
		var bankBranch = mapper.fromDTO(dto);
		repository.save(bankBranch);
		return mapper.toDTO(bankBranch);
	}

	@Override
	public SearchDTO.SearchRs<BaseBankDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}

	@Override
	public BaseBankDto.Info getById(Long id) {
		var model = repository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_BASE_BANK_NOT_FOUND));
		return mapper.toDTO(model);
	}

	@Transactional
	@Override
	public int updateAllBaseNosaCodes() {
		String yearSuffix = getCurrentJalaliYear();
		return repository.updateBaseNosaCodeWithYearSuffix(yearSuffix);
	}


	private String getCurrentJalaliYear() {
		Integer year = getJalaliYear(new Date());
		return String.format("%02d", year % 100);
	}

}
