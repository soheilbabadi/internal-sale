package com.nicico.internal.sales.pricing.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.pricing.dto.FieldMetadataDto;
import com.nicico.internal.sales.pricing.dto.PricingCommodityDto;
import com.nicico.internal.sales.pricing.dto.PricingCommodityWithAvgDto;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface PricingCommodityService {

	PricingCommodityDto.Info save(PricingCommodityDto.Create request);

	SearchDTO.SearchRs<PricingCommodityWithAvgDto.Info> search(SearchDTO.SearchRq request);

	PricingCommodityDto.Info findById(Long id);

	PricingCommodityDto.Info findByShortDate(Date shortDate);

	PricingCommodityDto.Info findByPersianShortDate(String persianShortDate);

	List<PricingCommodityDto.Info> findByDateRange(Date startDate, Date endDate);

	PricingCommodityDto.Info findLatest();

	List<PricingCommodityDto.Info> findLatest5();

	void delete(Long id);

	void deleteByShortDate(Date shortDate);

	boolean existsByShortDate(Date shortDate);

	boolean existsByPersianShortDate(String persianShortDate);

	// Average methods - now throw exception instead of returning null
	BigDecimal getAverageMolybdenumPrice(Date date);

	BigDecimal getAverageMolybdenumAmPmPrice(Date date);

	BigDecimal getAveragePlatinumPrice(Date date);

	BigDecimal getAveragePalladiumPrice(Date date);

	BigDecimal getAverageSeleniumPrice(Date date);

	FieldMetadataDto toMetadata(Field field);
}