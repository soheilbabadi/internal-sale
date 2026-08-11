package com.nicico.internal.sales.loading.services;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.loading.dto.LoadingPlaceDto;
import com.nicico.internal.sales.loading.dto.LoadingPlaceMapper;
import com.nicico.internal.sales.loading.repository.LoadingPlaceRepository;
import com.nicico.internal.sales.util.HashUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadingPlaceServiceImpl implements LoadingPlaceService {

	private static final String MSG_LOADING_PLACE_NOT_FOUND = "محل بارگیری پیدا نشد";

	private final LoadingPlaceRepository repository;
	private final LoadingPlaceMapper mapper;

	@Override
	public SearchDTO.SearchRs<LoadingPlaceDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}

	@Override
	public LoadingPlaceDto.Info get(long id) {
		return mapper.toDTO(repository.findById(id).orElseThrow(() ->
				new InternalSaleCustomException.ValidationException(MSG_LOADING_PLACE_NOT_FOUND)));
	}

	@Override
	public List<LoadingPlaceDto.Info> getAll() {
		var list = repository.findAll();
		return list.stream().map(mapper::toDTO).toList();
	}

	@Override
	public LoadingPlaceDto.Info save(LoadingPlaceDto.Create request) {
		request.setPlaceValue(HashUtility.generateSHA256(request.getPlaceTitle()));
		var existing = repository.findByPlaceValue(request.getPlaceValue());
		if (existing.isPresent()) {
			var model = existing.get();

			model.setPlaceTitle(request.getPlaceTitle());
			model.setPlaceValue(request.getPlaceValue());
			var saved = repository.save(model);
			return mapper.toDTO(saved);
		} else {
			var model = mapper.fromDTO(request);
			var save = repository.save(model);
			return mapper.toDTO(save);
		}
	}
}