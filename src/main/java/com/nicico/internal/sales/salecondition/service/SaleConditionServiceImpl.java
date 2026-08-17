package com.nicico.internal.sales.salecondition.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.repository.GoodsRepository;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import com.nicico.internal.sales.salecondition.dto.SaleConditionDto;
import com.nicico.internal.sales.salecondition.dto.SaleConditionMapper;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
import com.nicico.internal.sales.salecondition.repository.SaleConditionRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleConditionServiceImpl implements SaleConditionService {

	private static final String MSG_GOOD_NOT_FOUND = "کالای مورد نظر وجود ندارد";
	private static final String MSG_TRADE_NOT_FOUND = "آگهی عرضه وجود ندارد";
	private static final String MSG_PAYMENT_CODE_NOT_FOUND = "کد پرداخت وجود ندارد";
	private static final String MSG_BUCKET_NOT_FOUND = "ضرایب پیش فاکتور برای این کالا تعریف نشده است";
	private static final String MSG_START_DATE_EMPTY = "تاریخ شروع نمی تواند خالی باشد";
	private static final String MSG_INPUT_EMPTY_OR_ZERO = "مقدارهای ورودی نمی تواند خالی یا برابر با صفر باشد";
	private final SaleConditionRepository saleConditionRepository;
	private final SaleConditionMapper mapper;
	private final GoodsRepository goodsRepository;
	private final IMETradeRepository imeTradeRepository;

	@Transactional
	@Override
	public SaleConditionDto.Info save(SaleConditionDto.Create request) {
		validateSaleCondition(request);
		Date now = new Date();
		if (request.getStartDate() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_START_DATE_EMPTY);
		}
		Date expireDate = DateUtility.subtractDay(request.getStartDate(),1);
		List<SaleConditionModel> rulesToExpire = saleConditionRepository.findAllByGoodId(request.getGoodId()).stream()
				.filter(rule -> rule.getExpireDate() == null || rule.getExpireDate().after(now))
				.toList();
		if (!rulesToExpire.isEmpty()) {
			rulesToExpire.forEach(rule -> rule.setExpireDate(expireDate));
			saleConditionRepository.saveAllAndFlush(rulesToExpire);
		}
		var good = goodsRepository.findById(request.getGoodId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						MSG_GOOD_NOT_FOUND));
		request.setGoodName(good.getName());
		request.setImeCommodityId(good.getImeCommodityId());
		request.setImeCommoditySymbol(good.getImeCommoditySymbol());
		request.setExpireDate(null);
		SaleConditionModel savedRule = saleConditionRepository.save(mapper.fromDTO(request));
		return mapper.toDTO(savedRule);
	}


	@Override
	public SaleConditionDto.Info getCurrentRule(long goodId) {
		return this.getOnSpecificDateModel(goodId, new Date()) != null ? mapper.toDTO(this.getOnSpecificDateModel(goodId, new Date())) : null;
	}

	@Override
	public List<SaleConditionDto.Info> getGoodsHistory(long goodId) {
		return saleConditionRepository.findAllByGoodId(goodId).stream().map(mapper::toDTO).sorted(Comparator.comparing(SaleConditionDto.Info::getStartDate).reversed()).distinct().toList();
	}

	@Override
	public List<SaleConditionDto.Info> getCurrentRule() {
		var existingRule = saleConditionRepository.findAll().stream().filter(item -> item.getExpireDate() == null).toList();
		List<SaleConditionDto.Info> result = new ArrayList<>();
		for (SaleConditionModel rule : existingRule) {
			result.add(mapper.toDTO(rule));
		}
		return result;
	}

	@Override
	public SearchDTO.SearchRs<SaleConditionDto.Info> search(SearchDTO.SearchRq request) {
		request.setCount(1000);
		request.setStartIndex(0);
		var list = SearchUtil.search(saleConditionRepository, request, mapper::toDTO);
		list.getList().removeIf(item -> item.getExpireDate() != null);
		list.getList().sort(Comparator.comparing(SaleConditionDto.Info::getId).reversed());
		return list;
	}

	private void validateSaleCondition(SaleConditionDto.Create request) {
		if (request.getStorageDeadline() == null || request.getStorageDeadline() == 0
				|| request.getStorageCost() == null || request.getStorageCost().compareTo(BigDecimal.ZERO) == 0
				|| request.getCreditExpirePeriod() == null || request.getCreditExpirePeriod() == 0
				|| request.getShippingDeadline() == null || request.getShippingDeadline() == 0
				|| request.getPaymentDeferral() == null || request.getPaymentDeferral() == 0
				|| request.getGoodId() == null || request.getGoodId() == 0) {
			throw new InternalSaleCustomException.ValidationException(MSG_INPUT_EMPTY_OR_ZERO);
		}
		if (request.getExtraBillOfExchangePercent() == null) {
			request.setExtraBillOfExchangePercent(BigDecimal.ZERO);
		}
		if (request.getExtraGamCertificatePercent() == null) {
			request.setExtraGamCertificatePercent(BigDecimal.ZERO);
		}
	}

	@Override
	public SaleConditionModel getSaleConditionByPaymentCode(String paymentCode) {
		var trade = imeTradeRepository.findFirstByPaymentCodeOrderByIdDesc(paymentCode)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						MSG_PAYMENT_CODE_NOT_FOUND));
		var good = goodsRepository.findByImeCommodityId(Long.valueOf(trade.getCommodityCode()))
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						MSG_GOOD_NOT_FOUND));

		log.info("get sales condition for good: "+ good.getName()+ " on date ->" + DateUtility.toGregorianDate(trade.getContractDate()));

		return this.getOnSpecificDateModel(good.getId(), DateUtility.toGregorianDate(trade.getContractDate()));
	}

	@Override
	public SaleConditionModel getOnSpecificDateModel(long goodId, Date targetDate) {
		List<SaleConditionModel> candidates = saleConditionRepository.findActiveByGoodIdAndDate(goodId, targetDate);

		// اولویت اول: آیتم‌های بدون expireDate (expireDate == null)
		Optional<SaleConditionModel> model = candidates.stream()
				.filter(item -> item.getExpireDate() == null)
				.max(Comparator.comparing(SaleConditionModel::getId));

		// اولویت دوم: آیتم‌های دارای expireDate (expireDate != null)
		if (model.isEmpty()) {
			model = candidates.stream()
					.filter(item -> item.getExpireDate() != null)
					.max(Comparator.comparing(SaleConditionModel::getId));
		}

		// اولویت سوم: Fallback - همه رکوردهای بدون expireDate
		if (model.isEmpty()) {
			model = saleConditionRepository.findAllByGoodId(goodId).stream()
					.filter(item -> item.getExpireDate() == null)
					.max(Comparator.comparing(SaleConditionModel::getId));
		}

		return model.orElseThrow(() ->
				new InternalSaleCustomException.ValidationException(MSG_BUCKET_NOT_FOUND));
	}

	@Override
	public SaleConditionDto.Info getOnSpecificDate(long goodId, Date targetDate) {
		return mapper.toDTO(this.getOnSpecificDateModel(goodId, targetDate));
	}

//	@Override
//	public SaleConditionModel findByCommodityId(Long commodityId) {
//		var good = goodsRepository.findByImeCommodityId(commodityId)
//				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
//						MSG_GOOD_NOT_FOUND));
//		return this.getOnSpecificDateModel(good.getId(), new Date());
//	}

	@Override
	public SaleConditionDto.Info findByPaymentCode(String paymentCode) {
		return mapper.toDTO(this.findByPaymentCodeModel(paymentCode));
	}

	@Override
	public SaleConditionModel findByPaymentCodeModel(String paymentCode) {
		var trade = imeTradeRepository.findFirstByPaymentCodeOrderByIdDesc(paymentCode)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						MSG_TRADE_NOT_FOUND));
		var good = goodsRepository.findByImeCommodityId(Long.valueOf(trade.getCommodityCode()))
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						MSG_GOOD_NOT_FOUND));
		return this.getOnSpecificDateModel(good.getId(), DateUtility.toGregorianDate(trade.getContractDate()));
	}
}
