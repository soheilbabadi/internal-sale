package com.nicico.internal.sales.bank.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.IssuingBankDto;
import com.nicico.internal.sales.bank.dto.IssuingBankMapper;
import com.nicico.internal.sales.bank.model.BaseBankModel;
import com.nicico.internal.sales.bank.model.IssuingBankModel;
import com.nicico.internal.sales.bank.repository.BaseBankRepository;
import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.nicico.internal.sales.util.date.DateUtility.getJalaliYear;

@RequiredArgsConstructor
@Service
public class IssuingServiceImpl implements IssuingBankService {
	private static final String MSG_PROFORMA_LC_ISSUE_NOT_FOUND = "اطلاعات ثبت بانک نادرست است";
	private final IssuingBankRepository repository;
	private final IssuingBankMapper mapper;
	private final BaseBankRepository baseBankRepository;

	public IssuingBankDto save(IssuingBankDto.Create dto) {
		baseBankRepository.updateBaseNosaCodeWithYearSuffix(String.format("%02d", getJalaliYear(new Date()) % 100));
		Optional<IssuingBankModel> existingBank = Optional.empty();
		if (dto.getId() != null) {
			existingBank = repository.findById(dto.getId());
		}

		IssuingBankModel issuingBankModel;

		if (existingBank.isPresent()) {
			issuingBankModel = existingBank.get();
			issuingBankModel.setBranchCode(dto.getBranchCode());
			issuingBankModel.setBranchName(dto.getBranchName());
			issuingBankModel.setCity(dto.getCity());
			issuingBankModel.setBankName(dto.getBankName());
			issuingBankModel.setBankCode(dto.getBankCode());
		} else {
			issuingBankModel = mapper.fromDTO(dto);
			issuingBankModel.setId(null);
		}
		BaseBankModel baseBankModel = baseBankRepository
				.findById(Long.valueOf(dto.getBankCode()))
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_PROFORMA_LC_ISSUE_NOT_FOUND));

		issuingBankModel.setBaseNosaCode(baseBankModel.getBaseNosaCode());
		repository.save(issuingBankModel);

		return mapper.toDTO(issuingBankModel);

	}

	public SearchDTO.SearchRs<IssuingBankDto.Info> search(SearchDTO.SearchRq request) {

		return SearchUtil.search(repository, request, mapper::toDTO);
	}

	@Override
	public List<IssuingBankDto.Info> getAll() {
		var list = repository.findAll();
		return list.stream().map(mapper::toDTO).toList();
	}

	@Override
	public IssuingBankDto.Info getById(long id) {
		var entity = repository.findById(id).orElse(null);
		return mapper.toDTO(entity);
	}
}
