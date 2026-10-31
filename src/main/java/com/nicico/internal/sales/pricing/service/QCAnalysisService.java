package com.nicico.internal.sales.pricing.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.pricing.dto.QCAnalysisItemDto;
import com.nicico.internal.sales.pricing.dto.QCAnalysisMasterDto;
import com.nicico.internal.sales.pricing.enums.QCAnalysisStage;

import java.util.List;

public interface QCAnalysisService {

	QCAnalysisMasterDto.Info save(QCAnalysisMasterDto.SaveRequest request);

	SearchDTO.SearchRs<QCAnalysisMasterDto.Info> search(SearchDTO.SearchRq request);

	QCAnalysisMasterDto.Info findById(Long id);

	QCAnalysisMasterDto.Info findByIndicatorNo(String indicatorNo);

	List<QCAnalysisMasterDto.ParentOption> getParentOptions(Long commodityId, QCAnalysisStage stage);

	List<QCAnalysisItemDto> getParentItems(Long parentMasterId);

	void delete(Long id);

	// ==========================================
	// CRUD مجزا برای آیتم‌ها (Detail Items)
	// ==========================================

	QCAnalysisItemDto findItemById(Long itemId);

	QCAnalysisItemDto saveItem(Long masterId, QCAnalysisItemDto itemDto);

	QCAnalysisItemDto updateItem(Long itemId, QCAnalysisItemDto itemDto);

	void deleteItem(Long itemId);
}
