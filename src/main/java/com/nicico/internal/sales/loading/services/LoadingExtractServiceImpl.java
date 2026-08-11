package com.nicico.internal.sales.loading.services;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.loading.dto.CreateGoodLoading;
import com.nicico.internal.sales.loading.dto.CreateGoodLoadingBatch;
import com.nicico.internal.sales.loading.dto.LoadingExtractDto;
import com.nicico.internal.sales.loading.dto.LoadingExtractMapper;
import com.nicico.internal.sales.loading.model.LoadingGoodsModel;
import com.nicico.internal.sales.loading.repository.LoadingExtractRepository;
import com.nicico.internal.sales.loading.repository.LoadingGoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoadingExtractServiceImpl implements LoadingExtractService {
	private static final String MSG_LOADING_GOODS_NOT_FOUND = "محل بارگیری وجود ندارد";
	private final LoadingExtractMapper mapper;
	private final LoadingGoodsRepository loadingGoodsRepository;
	private final LoadingExtractRepository loadingExtractRepository;

	@Override
	public SearchDTO.SearchRs<LoadingExtractDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(loadingExtractRepository, request, mapper::toDTO);
	}

	@Override
	public LoadingExtractDto.Info get(long id) {
		return mapper.toDTO(loadingExtractRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_LOADING_GOODS_NOT_FOUND)));
	}

	@Override
	public List<LoadingExtractDto.Info> getAll() {
		var list = loadingExtractRepository.findAll();
		return list.stream().map(mapper::toDTO).toList();
	}

	@Override
	public void save(CreateGoodLoading request) {
		var existing = loadingGoodsRepository.findByGoodIdAndLoadingPlaceId(request.getGoodsId(), request.getLoadingPlaceId());
		LoadingGoodsModel loadingGoods;
		if (existing.isPresent()) {
			loadingGoods = existing.get();
		} else {
			loadingGoods = new LoadingGoodsModel();
		}
		loadingGoods.setGoodId(request.getGoodsId());
		loadingGoods.setLoadingPlaceId(request.getLoadingPlaceId());
		loadingGoodsRepository.save(loadingGoods);
	}

	@Override
	public void saveBatch(CreateGoodLoadingBatch request) {
		List<LoadingGoodsModel> loadingGoodsList = new ArrayList<>();
		for (var item : request.getGoodId()) {
			var existing = loadingGoodsRepository.findByGoodIdAndLoadingPlaceId(item, request.getLoadingPlaceId());
			if (existing.isPresent()) {
				var loadingGoods = existing.get();
				loadingGoods.setGoodId(item);
				loadingGoods.setLoadingPlaceId(request.getLoadingPlaceId());
				loadingGoodsList.add(loadingGoods);
			} else {
				var loadingGoods = new LoadingGoodsModel();
				loadingGoods.setGoodId(item);
				loadingGoods.setLoadingPlaceId(request.getLoadingPlaceId());
				loadingGoodsList.add(loadingGoods);
			}
		}
		loadingGoodsRepository.saveAll(loadingGoodsList);
	}

	@Override
	public void deleteById(Long id) {
		loadingGoodsRepository.deleteById(id);
	}


}
