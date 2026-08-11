package com.nicico.internal.sales.vat.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.vat.dto.TaxVatMapper;
import com.nicico.internal.sales.vat.dto.VatDTO;
import com.nicico.internal.sales.vat.model.TaxVatModel;
import com.nicico.internal.sales.vat.repository.VatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VatServiceImpl implements VatService {
	private final VatRepository repository;
	private final TaxVatMapper mapper;

	@Transactional
	@Override
	public VatDTO.Info save(VatDTO.Create request) {
		repository.findByJalaliYear(request.getJalaliYear()).ifPresentOrElse(taxVatModel -> {
			taxVatModel.setTaxCoefficient(request.getTaxCoefficient());
			taxVatModel.setVatCoefficient(request.getVatCoefficient());
			taxVatModel.setEmissionTax(request.getEmissionTax());
			repository.save(taxVatModel);
		}, () -> {
			var model = new TaxVatModel();
			model.setId(Long.valueOf(request.getJalaliYear()));
			model.setJalaliYear(request.getJalaliYear());
			model.setTaxCoefficient(request.getTaxCoefficient());
			model.setVatCoefficient(request.getVatCoefficient());
			model.setEmissionTax(request.getEmissionTax());
			repository.save(model);
		});
		return mapper.toDTO(repository.findByJalaliYear(request.getJalaliYear()).orElseThrow());
	}

	@Override
	public SearchDTO.SearchRs<VatDTO.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}

	@Override
	public void delete(Long ids) {
		repository.deleteById(ids);
	}
}
