package com.nicico.internal.sales.goods.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.goods.dto.GoodsDTO;
import com.nicico.internal.sales.goods.special.dto.PreciousMetalDto;

import java.util.List;

public interface GoodsService {
	GoodsDTO.Info save(GoodsDTO.Create request);

	PreciousMetalDto.Info savePreciousMetal(long goodId);

	SearchDTO.SearchRs<GoodsDTO.Info> search(SearchDTO.SearchRq request);

	List<GoodsDTO.Info> list();

	GoodsDTO.Info findById(Long id);

	void delete(Long id);

	String getCleanName(long id);


	Long findPmsIdByGoodName(String goodName);

	SearchDTO.SearchRs<PreciousMetalDto.Info> searchPreciousMetal(SearchDTO.SearchRq request);

	List<GoodsDTO.Info> acceptableGoods();

	void delete(long id);

	boolean isPreciousMetal(long goodId);
}
