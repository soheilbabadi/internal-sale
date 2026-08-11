package com.nicico.internal.sales.pricing.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.penalty.repository.DimDateRepository;
import com.nicico.internal.sales.pricing.dto.*;
import com.nicico.internal.sales.pricing.model.PricingCurrencyModel;
import com.nicico.internal.sales.pricing.model.PricingCurrencyTypeModel;
import com.nicico.internal.sales.pricing.repository.PricingCurrencyRepository;
import com.nicico.internal.sales.pricing.repository.PricingCurrencyTypeRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingCurrencyServiceImpl implements PricingCurrencyService {

	private static final String MSG_ISSUING_DATE_NOT_VALID = "تاریخ وارد شده معتبر نیست";
	private static final String MSG_ISSUING_DATE_DUPLICATE = "برای این تاریخ قبلا نرخ وارد شده است";
	private static final String MSG_PRICE_TYPE_NOT_VALID = "نوع ارز قیمتگذاری تعیین نشده است";
	private final PricingCurrencyRepository repository;
	private final PricingCurrencyMapper mapper;
	private final PricingCurrencyTypeMapper typeMapper;
	private final DimDateRepository dimDateRepository;
	private final PricingCurrencyTypeRepository pricingCurrencyTypeRepository;

	@Override
	public SearchDTO.SearchRs<PricingCurrencyDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, mapper::toDTO);
	}


	@Override
	public PricingCurrencyDto.Info save(PricingCurrencyRequest request) {
		int[] dateParts = DateUtility.extractPersianDateParts(request.getPersianShortDate());
		Long year = (long) dateParts[0];
		Long month = (long) dateParts[1];
		Long day = (long) dateParts[2];


		var dateRecord = dimDateRepository.findByPersianYearAndPersianMonthAndPersianDay(year, month, day)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_ISSUING_DATE_NOT_VALID));


		var priceType = pricingCurrencyTypeRepository.findTopByOrderByIdDesc()
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_PRICE_TYPE_NOT_VALID));

		PricingCurrencyModel model = new PricingCurrencyModel();
		BeanUtils.copyProperties(request, model);
		model.setDayOfWeek(dateRecord.getDayOfWeek());
		model.setRateType(priceType.getRateType());
		model.setShortDate(dateRecord.getShortDate());

		var savedModel = repository.save(model);
		return mapper.toDTO(savedModel);
	}

	@Override
	public void deleteById(String persianShortDate) {
		var entity = repository.findByPersianShortDate(persianShortDate)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_ISSUING_DATE_NOT_VALID));
		repository.delete(entity);
	}

	@Override
	public PricingCurrencyTypeDto.Info saveType(PricingCurrencyTypeDto request) {
		PricingCurrencyTypeModel pricingCurrencyTypeModel = new PricingCurrencyTypeModel();
		pricingCurrencyTypeModel.setRateType(request.getRateType());
		var savedModel = pricingCurrencyTypeRepository.save(pricingCurrencyTypeModel);
		return typeMapper.toDTO(savedModel);
	}


	@Override
	public BigDecimal getAverageUsdRateForLastDay() {
		PricingCurrencyModel lastRecord = repository.findTopByOrderByShortDateDesc()
				.orElseThrow(() -> new RuntimeException("No currency data found"));

		BigDecimal buyRate = lastRecord.getUsdIrrBuy();
		BigDecimal sellRate = lastRecord.getUsdIrrSell();

		if (buyRate == null || sellRate == null) {
			throw new InternalSaleCustomException.ResourceNotFoundException("USD buy or sell rate is null for date: " + lastRecord.getPersianShortDate());
		}

		return buyRate.add(sellRate)
				.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
	}

	@Override
	public LastUsdRateInfo getLastUsdRateInfo() {
		PricingCurrencyModel lastRecord = repository.findTopByOrderByShortDateDesc()
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("No currency data found"));

		BigDecimal buyRate = lastRecord.getUsdIrrBuy();
		BigDecimal sellRate = lastRecord.getUsdIrrSell();
		BigDecimal average = buyRate.add(sellRate)
				.divide(BigDecimal.valueOf(2), 2, RoundingMode.UP);

		return new LastUsdRateInfo(
				lastRecord.getPersianShortDate(),
				buyRate,
				sellRate,
				average
		);
	}

	@Override
	public String getLastPricingType() {
		var priceType = pricingCurrencyTypeRepository.findTopByOrderByIdDesc()
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_PRICE_TYPE_NOT_VALID));
		return priceType.getRateType();
	}


}