package com.nicico.internal.sales.goods.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.dto.GoodsDTO;
import com.nicico.internal.sales.goods.dto.GoodsMapper;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.model.PmsMappingModel;
import com.nicico.internal.sales.goods.repository.GoodsRepository;
import com.nicico.internal.sales.goods.repository.PmsMappingRepository;
import com.nicico.internal.sales.goods.special.dto.PreciousMetalDto;
import com.nicico.internal.sales.goods.special.dto.PreciousMetalMapper;
import com.nicico.internal.sales.goods.special.model.PreciousMetalModel;
import com.nicico.internal.sales.goods.special.repository.PreciousMetalRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {

	private static final String MSG_GOOD_NOT_FOUND = "کالای مورد نظر وجود ندارد";

	private final GoodsRepository goodsRepository;
	private final GoodsMapper goodsMapper;
	private final PmsMappingRepository pmsMappingRepository;
	private final PreciousMetalRepository preciousMetalRepository;
	private final PreciousMetalMapper preciousMetalMapper;
	private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

	@Override
	public GoodsDTO.Info save(GoodsDTO.Create request) {
		GoodsModel model = goodsMapper.fromDTO(request);
		GoodsModel save = goodsRepository.save(model);
		return goodsMapper.toDTO(save);
	}

	@Override
	public SearchDTO.SearchRs<GoodsDTO.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(goodsRepository, request, goodsMapper::toDTO);
	}

	public List<GoodsDTO.Info> list() {
		return goodsRepository.findAll().stream().distinct().map(goodsMapper::toDTO).toList();
	}

	@Override
	public GoodsDTO.Info findById(Long id) {
		var goodsModel = goodsRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_GOOD_NOT_FOUND));
		return goodsMapper.toDTO(goodsModel);
	}

	@Override
	public void delete(Long id) {
		goodsRepository.deleteById(id);
	}

	@Override
	public String getCleanName(long id) {
		var goodsModel = goodsRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_GOOD_NOT_FOUND));
		return goodsModel.getName().replace(goodsModel.getImeCommoditySymbol(), "").replace("_", "").trim();
	}


	@Override
	public Long findPmsIdByGoodName(String goodName) {
		return pmsMappingRepository.findAll().stream().max((a, b) -> {
			double simA = similarity.apply(goodName, a.getGoodName());
			double simB = similarity.apply(goodName, b.getGoodName());
			return Double.compare(simA, simB);
		}).map(PmsMappingModel::getPmsId).orElse(null);
	}

	@Override
	public PreciousMetalDto.Info savePreciousMetal(long goodId) {

		GoodsModel good = goodsRepository.findById(goodId)
				.orElseThrow(() ->
						new InternalSaleCustomException.ValidationException(MSG_GOOD_NOT_FOUND));

		return preciousMetalRepository.findById(goodId)
				.map(preciousMetalMapper::toDTO)
				.orElseGet(() -> createPreciousMetal(good));
	}

	@Override
	public SearchDTO.SearchRs<PreciousMetalDto.Info> searchPreciousMetal(SearchDTO.SearchRq request) {
		return SearchUtil.search(preciousMetalRepository, request, preciousMetalMapper::toDTO);
	}

	@Override
	public List<GoodsDTO.Info> acceptableGoods() {

		Set<Long> preciousMetalIds = preciousMetalRepository.findAll()
				.stream()
				.map(PreciousMetalModel::getId)
				.collect(Collectors.toSet());

		return goodsRepository.findAll()
				.stream()
				.filter(good -> !preciousMetalIds.contains(good.getId()))
				.map(goodsMapper::toDTO)
				.toList();
	}

	@Override
	public void delete(long id) {
		goodsRepository.deleteById(id);
	}

	@Override
	public boolean isPreciousMetal(long goodId) {
		return goodsRepository.existsById(goodId);
	}

	private PreciousMetalDto.Info createPreciousMetal(GoodsModel good) {

		PreciousMetalModel model = PreciousMetalModel.builder()
				.id(good.getId())
				.name(good.getDescription())
				.imeCommodityId(good.getImeCommodityId())
				.imeCommoditySymbol(good.getImeCommoditySymbol())
				.build();

		return preciousMetalMapper.toDTO(preciousMetalRepository.save(model));
	}
}
