package com.nicico.internal.sales.loading.services;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.loading.dto.IssuePlaceDto;
import com.nicico.internal.sales.loading.dto.IssuePlaceMapper;
import com.nicico.internal.sales.loading.model.IssuePlaceModel;
import com.nicico.internal.sales.loading.repository.IssuePlaceRepository;
import com.nicico.internal.sales.util.HashUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssuePlaceServiceImpl implements IssuePlaceService {
	private static final String MSG_LOADING_PLACE_ERROR = "محل صدور وجود ندارد";
	private final IssuePlaceRepository repository;
	private final IssuePlaceMapper mapper;

	@Override
	public SearchDTO.SearchRs<IssuePlaceDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}

	@Override
	public IssuePlaceDto.Info save(IssuePlaceDto.Create request) {
		IssuePlaceModel model = mapper.fromDTO(request);
		model.setPlaceValue(HashUtility.generateSHA256(request.getPlaceTitle()));
		IssuePlaceModel save = repository.save(model);
		return mapper.toDTO(save);
	}

	@Override
	public void deleteById(long id) {
		repository.deleteById(id);
	}

	@Override
	public IssuePlaceDto.Info get(long id) {
		return mapper.toDTO(repository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_LOADING_PLACE_ERROR)));
	}

	@Override
	public List<IssuePlaceDto.Info> getAll() {
		var list = repository.findAll();
		return list.stream().map(mapper::toDTO).toList();
	}


}
