package com.nicico.internal.sales.goods.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.dto.GoodBucketDto;
import com.nicico.internal.sales.goods.dto.GoodBucketRequest;
import com.nicico.internal.sales.goods.dto.GoodsBucketMapper;
import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.repository.GoodBucketRepository;
import com.nicico.internal.sales.goods.repository.GoodsRepository;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoodBucketServiceImpl implements GoodBucketService {

	private static final String MSG_GOOD_NOT_FOUND = "مشخصات کالا وجود ندارد";
	private static final String MSG_BUCKET_NOT_FOUND = "ضرایب پیش فاکتور برای این کالا تعریف نشده است";
	private static final String MSG_TRADE_NOT_FOUND = "آگهی عرضه وجود ندارد";
	private static final String MSG_START_DATE_REQUIRED = "تاریخ شروع الزامی است";

	private static final ZoneId APP_ZONE = ZoneId.of("Asia/Tehran");

	private final GoodBucketRepository goodBucketRepository;
	private final GoodsRepository goodsRepository;
	private final GoodsBucketMapper goodsBucketMapper;
	private final IMETradeRepository imeTradeRepository;


	// ---------- Query methods ----------
	@Override
	public GoodsBucketModel getOnSpecificDateModel(long goodId, Date targetDate) {
		List<GoodsBucketModel> candidates = goodBucketRepository.findActiveByGoodIdAndDate(goodId, targetDate);

		// Prefer a bucket with an explicit closed range covering the date; fall back to an open-ended one
		Optional<GoodsBucketModel> result = candidates.stream()
				.filter(b -> b.getExpireDate() != null)
				.max(Comparator.comparing(GoodsBucketModel::getId));

		if (result.isEmpty()) {
			result = candidates.stream()
					.filter(b -> b.getExpireDate() == null)
					.max(Comparator.comparing(GoodsBucketModel::getId));
		}

		return result.orElseThrow(() ->
				new InternalSaleCustomException.ValidationException(MSG_BUCKET_NOT_FOUND));
	}

	@Override
	public GoodsBucketModel getGoodBucketActiveModel(Long goodId) {
		return getOnSpecificDateModel(goodId, new Date());
	}

	@Override
	public BigDecimal getGoodBucket(Long goodId) {
		return getGoodBucketActiveModel(goodId).getPackagingSize();
	}

	@Override
	public GoodBucketDto.Info getOnSpecificDate(long goodId, Date targetDate) {
		return goodsBucketMapper.toDTO(getOnSpecificDateModel(goodId, targetDate));
	}

	@Override
	public GoodBucketDto.Info findByCommodityId(Long commodityId) {
		var good = goodsRepository.findByImeCommodityId(commodityId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_GOOD_NOT_FOUND));
		return getOnSpecificDate(good.getId(), new Date());
	}

	@Override
	public List<GoodBucketDto.Info> getHistory(Long goodId) {
		return goodBucketRepository.findAllByGoodId(goodId).stream()
				.map(goodsBucketMapper::toDTO)
				.sorted(Comparator.comparing(GoodBucketDto.Info::getStartDate).reversed())
				.collect(Collectors.toList());
	}

	@Override
	public GoodsBucketModel findByPaymentCodeModel(String paymentCode) {
		var trade = imeTradeRepository.findFirstByPaymentCodeOrderByIdDesc(paymentCode)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_TRADE_NOT_FOUND));

		var good = goodsRepository.findByImeCommodityId(Long.valueOf(trade.getCommodityCode()))
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_GOOD_NOT_FOUND));

		// Convert the trade's contract date (already a Date) to midnight and pass it
		Date contractDate = DateUtility.truncateToMidnight(DateUtility.toGregorianDate(trade.getContractDate()));
		return getOnSpecificDateModel(good.getId(), contractDate);
	}

	// ---------- Create/Update methods ----------

	@Override
	@Transactional
	public GoodBucketDto.Info createGoodBucket(GoodBucketRequest request) {
		if (request.getStartDate() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_START_DATE_REQUIRED);
		}

		GoodsModel good = goodsRepository.findById(request.getGoodId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_GOOD_NOT_FOUND));

		// Expire any currently active (non‑expired) buckets for this good
		Date expireDate = DateUtility.subtractDay(request.getStartDate(), 1);
		List<GoodsBucketModel> activeRecords = goodBucketRepository.findAllByGoodId(good.getId()).stream()
				.filter(item -> item.getExpireDate() == null)
				.collect(Collectors.toList());

		if (!activeRecords.isEmpty()) {
			activeRecords.forEach(item -> item.setExpireDate(expireDate));
			goodBucketRepository.saveAllAndFlush(activeRecords);
			log.info("Updated {} active records for goodId {} to expire at {}",
					activeRecords.size(), good.getId(), expireDate);
		}

		GoodsBucketModel bucketModel = buildBucketModel(good, request);
		GoodsBucketModel savedModel = goodBucketRepository.save(bucketModel);
		return goodsBucketMapper.toDTO(savedModel);
	}

	// ---------- Search ----------

	@Override
	public SearchDTO.SearchRs<GoodBucketDto.Info> search(SearchDTO.SearchRq request) {
		request.setCount(1000);
		request.setStartIndex(0);
		var result = SearchUtil.search(goodBucketRepository, request, goodsBucketMapper::toDTO);
		// Only return currently active buckets (expireDate == null)
		result.setList(result.getList().stream()
				.filter(item -> item.getExpireDate() == null)
				.sorted(Comparator.comparing(GoodBucketDto.Info::getId).reversed())
				.collect(Collectors.toList()));
		return result;
	}

	// ---------- Private helpers ----------

	private GoodsBucketModel buildBucketModel(GoodsModel good, GoodBucketRequest request) {
		GoodsBucketModel bucketModel = new GoodsBucketModel();
		bucketModel.setGoodId(good.getId());
		bucketModel.setImeCommodityId(good.getImeCommodityId());
		bucketModel.setGoodName(good.getName());
		bucketModel.setExpireDate(null);
		bucketModel.setStartDate(DateUtility.truncateToMidnight(request.getStartDate()));
		bucketModel.setDivisibilityCheck(request.getDivisibilityCheck());
		bucketModel.setCashPercentage(request.getCashPercentage());
		bucketModel.setPackingId(calculatePackingId(request.getPackingName()));
		bucketModel.setPackingName(request.getPackingName());
		bucketModel.setPackagingSize(request.getPackagingSize());
		bucketModel.setCommission(request.getCommission());
		bucketModel.setImeCommoditySymbol(good.getImeCommoditySymbol());
		return bucketModel;
	}

	private int calculatePackingId(String packingName) {
		return packingName != null ? packingName.trim().toLowerCase().hashCode() : 0;
	}


}