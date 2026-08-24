package com.nicico.internal.sales.pricing.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.pricing.dto.*;
import com.nicico.internal.sales.pricing.model.PricingCommodityModel;
import com.nicico.internal.sales.pricing.repository.PricingCommodityRepository;
import com.nicico.internal.sales.pricing.repository.PricingCommodityWithAvgRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PricingCommodityServiceImpl implements PricingCommodityService {

	private static final String MSG_PRICE_COMMODITY_ALREADY_EXISTS = "اطلاعات قیمت کالا برای این تاریخ قبلاً ثبت شده است";
	private static final String MSG_COMMODITY_PRICE_DATA_MISSING = "اطلاعات قیمت کالا موجود نمی باشد";
	private static final BigDecimal TWO = BigDecimal.valueOf(2);

	private final PricingCommodityRepository repository;
	private final PricingCommodityMapper mapper;
	private final PricingCommodityAvgMapper pricingCommodityAvgMapper;
	private final PricingCommodityWithAvgRepository pricingCommodityWithAvgRepository;


	@Override
	@Transactional
	public PricingCommodityDto.Info save(PricingCommodityDto.Create request) {
		PricingCommodityModel model = mapper.fromDTO(request);
		PricingCommodityModel saved = repository.save(model);
		return mapper.toDTO(saved);
	}


	@Override
	public SearchDTO.SearchRs<PricingCommodityWithAvgDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(pricingCommodityWithAvgRepository, request, pricingCommodityAvgMapper::toDTO);
	}


	@Override
	public PricingCommodityDto.Info findById(Long id) {
		return repository.findById(id)
				.map(mapper::toDTO)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_COMMODITY_PRICE_DATA_MISSING));
	}

	@Override
	public PricingCommodityDto.Info findByShortDate(Date shortDate) {
		return repository.findByShortDate(shortDate)
				.map(mapper::toDTO)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_COMMODITY_PRICE_DATA_MISSING));
	}

	@Override
	public PricingCommodityDto.Info findByPersianShortDate(String persianShortDate) {
		return repository.findByPersianShortDate(persianShortDate)
				.map(mapper::toDTO)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_COMMODITY_PRICE_DATA_MISSING));
	}

	@Override
	public List<PricingCommodityDto.Info> findByDateRange(Date startDate, Date endDate) {
		return repository.findByShortDateBetween(startDate, endDate)
				.stream()
				.map(mapper::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	public PricingCommodityDto.Info findLatest() {
		return repository.findTopByOrderByShortDateDesc()
				.map(mapper::toDTO)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_COMMODITY_PRICE_DATA_MISSING));
	}

	@Override
	public List<PricingCommodityDto.Info> findLatest5() {
		return repository.findTop5ByOrderByShortDateDesc()
				.stream()
				.map(mapper::toDTO)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new InternalSaleCustomException.ResourceNotFoundException(MSG_COMMODITY_PRICE_DATA_MISSING);
		}
		repository.deleteById(id);
	}

	@Override
	@Transactional
	public void deleteByShortDate(Date shortDate) {
		if (!repository.existsByShortDate(shortDate)) {
			throw new InternalSaleCustomException.ResourceNotFoundException(MSG_COMMODITY_PRICE_DATA_MISSING);
		}
		repository.deleteByShortDate(shortDate);
	}

	@Override
	public boolean existsByShortDate(Date shortDate) {
		return repository.existsByShortDate(shortDate);
	}

	@Override
	public boolean existsByPersianShortDate(String persianShortDate) {
		return repository.existsByPersianShortDate(persianShortDate);
	}

	@Override
	public BigDecimal getAverageMolybdenumPrice(Date date) {
		PricingCommodityModel model = getByDateOrThrow(date);
		return calculateAverage(model, PricingCommodityModel::getMolybdenumLow, PricingCommodityModel::getMolybdenumHigh);
	}

	@Override
	public BigDecimal getAverageMolybdenumAmPmPrice(Date date) {
		PricingCommodityModel model = getByDateOrThrow(date);
		return calculateAverage(model, PricingCommodityModel::getMolybdenumAm, PricingCommodityModel::getMolybdenumPm);
	}

	@Override
	public BigDecimal getAveragePlatinumPrice(Date date) {
		PricingCommodityModel model = getByDateOrThrow(date);
		return calculateAverage(model, PricingCommodityModel::getPlatinumAm, PricingCommodityModel::getPlatinumPm);
	}

	@Override
	public BigDecimal getAveragePalladiumPrice(Date date) {
		PricingCommodityModel model = getByDateOrThrow(date);
		return calculateAverage(model, PricingCommodityModel::getPalladiumAm, PricingCommodityModel::getPalladiumPm);
	}

	@Override
	public BigDecimal getAverageSeleniumPrice(Date date) {
		PricingCommodityModel model = getByDateOrThrow(date);
		return calculateAverage(model, PricingCommodityModel::getSeleniumBid, PricingCommodityModel::getSeleniumAsk);
	}

	private PricingCommodityModel getByDateOrThrow(Date date) {
		return repository.findByShortDate(date)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_COMMODITY_PRICE_DATA_MISSING));
	}

	private BigDecimal calculateAverage(PricingCommodityModel model,
	                                    Function<PricingCommodityModel, BigDecimal> firstExtractor,
	                                    Function<PricingCommodityModel, BigDecimal> secondExtractor) {
		BigDecimal first = firstExtractor.apply(model);
		BigDecimal second = secondExtractor.apply(model);

		if (first == null && second == null) {
			throw new InternalSaleCustomException.ResourceNotFoundException(PricingCommodityServiceImpl.MSG_COMMODITY_PRICE_DATA_MISSING);
		}

		if (first == null) {
			return second.setScale(3, RoundingMode.HALF_UP);
		}
		if (second == null) {
			return first.setScale(3, RoundingMode.HALF_UP);
		}
		return first.add(second).divide(TWO, 3, RoundingMode.HALF_UP);
	}

	@Override
	public FieldMetadataDto toMetadata(Field field) {

		Schema schema = field.getAnnotation(Schema.class);

		return new FieldMetadataDto(
				field.getName(),
				field.getType().getSimpleName(),
				schema != null ? schema.description() : ""
		);
	}
}