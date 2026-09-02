package com.nicico.internal.sales.goods.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.goods.dto.GoodBucketDto;
import com.nicico.internal.sales.goods.dto.GoodBucketRequest;
import com.nicico.internal.sales.goods.service.GoodBucketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Tag(name = "ضرایب فروش کالا", description = "APIهای مربوط به مدیریت ضرایب فروش کالاها")
@PreAuthorize("@secUtil.hasAuthority('R_INS_GOODS')")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/goods/good-bucket")
public class GoodBucketController {

	private final GoodBucketService goodBucketService;

	@Operation(summary = "ایجاد ضریب فروش جدید",
			description = "این متد یک رکورد جدید برای ضریب فروش کالا ایجاد می کند")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ضریب فروش با موفقیت ایجاد شد",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = GoodBucketDto.class),
							examples = @ExampleObject(value = """
									{
									  "id": 1,
									  "goodId": 101,
									  "imeCommodityId": 2001,
									  "goodName": "روغن موتور",
									  "startDate": "2026-01-01T00:00:00.000+00:00",
									  "expireDate": "2026-12-31T23:59:59.999+00:00",
									  "packagingSize": 500.00,
									  "packingId": 156,
									  "packingName": "بشکه",
									  "cashPercentage": 53.00,
									  "commission": 6.0,
									  "divisibilityCheck": 1.00,
									  "imeCommoditySymbol": "OIL"
									}
									"""))),
			@ApiResponse(responseCode = "400", description = "درخواست نامعتبر"),
			@ApiResponse(responseCode = "403", description = "دسترسی غیرمجاز"),
			@ApiResponse(responseCode = "409", description = "تداخل با رکورد موجود")
	})
	@PreAuthorize("@secUtil.hasAuthority('C_INS_GOODS')")
	@PostMapping()
	public ResponseEntity<GoodBucketDto> createGoodBucket(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "اطلاعات ضریب فروش جدید",
					required = true,
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = GoodBucketRequest.class),
							examples = @ExampleObject(value = """
									{
									  "goodId": 101,
									  "imeCommodityId": 2001,
									  "startDate": "2026-01-01T00:00:00.000Z",
									  "expireDate": "2026-12-31T23:59:59.999Z",
									  "packagingSize": 500.00,
									  "packingId": 156,
									  "cashPercentage": 53.00,
									  "commission": 6.0,
									  "divisibilityCheck": 1.00,
									  "comment": "ضریب فروش اولیه برای روغن موتور"
									}
									""")
					)
			)
			@RequestBody GoodBucketRequest request) {
		return ResponseEntity.ok(goodBucketService.createGoodBucket(request));
	}

	@Operation(summary = "جستجوی ضرایب فروش",
			description = "جستجوی پیشرفته با استفاده از معیارهای مختلف")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "جستجو با موفقیت انجام شد",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = GoodBucketDto.Info.class),
							examples = @ExampleObject(value = """
									{
									  "content": [
									    {
									      "id": 1,
									      "goodId": 101,
									      "imeCommodityId": 2001,
									      "goodName": "روغن موتور",
									      "startDate": "2026-01-01T00:00:00.000+00:00",
									      "expireDate": "2026-12-31T23:59:59.999+00:00",
									      "cashPercentage": 53.00,
									      "commission": 6.0,
									      "createdDate": "2026-01-01T10:30:00.000+00:00",
									      "lastModifiedDate": "2026-01-02T15:20:00.000+00:00",
									      "createdBy": "admin",
									      "lastModifiedBy": "admin"
									    }
									  ],
									  "pageable": {
									    "pageNumber": 0,
									    "pageSize": 20
									  },
									  "totalElements": 1
									}
									""")))
	})
	@PostMapping("/search")
	public ResponseEntity<?> search(
			@RequestBody(required = false)
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "معیارهای جستجو",
					content = @Content(
							mediaType = "application/json",
							examples = @ExampleObject(value = """
									{
									  "page": 0,
									  "size": 20,
									  "sort": ["startDate", "DESC"],
									  "criteria": [
									    {
									      "key": "goodName",
									      "operation": "CONTAINS",
									      "value": "روغن"
									    }
									  ]
									}
									""")
					)
			)
			SearchDTO.SearchRq searchRq,
			@Parameter(description = "معیارهای جستجو به صورت پارامترهای URL",
					example = "goodName=روغن&startDate>2026-01-01")
			@RequestParam(required = false) MultiValueMap<String, String> criteria) {

		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(goodBucketService.search(searchRq));
	}

	@Operation(summary = "خواندن ضریب فروش بر اساس شناسه کالا",
			description = "دریافت آخرین ضریب فروش معتبر برای یک کالا")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "عملیات با موفقیت انجام شد",
					content = @Content(mediaType = "application/json",
							examples = @ExampleObject(value = "53.00"))),
			@ApiResponse(responseCode = "404", description = "کالا یافت نشد")
	})
	@Parameter(name = "goodId", description = "شناسه کالا", required = true, example = "101")
	@GetMapping("/get-bucket/{goodId}")
	public ResponseEntity<BigDecimal> getGoodBucket(@PathVariable Long goodId) {
		return ResponseEntity.ok(goodBucketService.getGoodBucket(goodId));
	}

	@Operation(summary = "دریافت ضریب فروش در تاریخ مشخص",
			description = "دریافت ضریب فروش یک کالا در یک تاریخ معین")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "عملیات با موفقیت انجام شد",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = GoodBucketDto.class),
							examples = @ExampleObject(value = """
									{
									  "id": 1,
									  "goodId": 101,
									  "imeCommodityId": 2001,
									  "goodName": "روغن موتور",
									  "startDate": "2026-01-01T00:00:00.000+00:00",
									  "expireDate": "2026-12-31T23:59:59.999+00:00",
									  "cashPercentage": 53.00,
									  "commission": 6.0
									}
									"""))),
			@ApiResponse(responseCode = "404", description = "رکوردی برای تاریخ مورد نظر یافت نشد")
	})
	@Parameter(name = "goodId", description = "شناسه کالا", required = true, example = "101")
	@Parameter(name = "targetDate", description = "تاریخ مورد نظر (فرمت: ISO 8601)",
			required = true, example = "2026-06-15T00:00:00.000Z")
	@GetMapping("/get-by-date/{goodId}/{targetDate}")
	public ResponseEntity<GoodBucketDto> getOnSpecificDate(
			@PathVariable Long goodId,
			@PathVariable Date targetDate) {
		return ResponseEntity.ok(goodBucketService.getOnSpecificDate(goodId, targetDate));
	}

	@Operation(summary = "دریافت ضریب فروش فعلی کالا",
			description = "دریافت ضریب فروش معتبر در زمان حال برای یک کالا")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "عملیات با موفقیت انجام شد",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = GoodBucketDto.class),
							examples = @ExampleObject(value = """
									{
									  "id": 1,
									  "goodId": 101,
									  "imeCommodityId": 2001,
									  "goodName": "روغن موتور",
									  "startDate": "2026-01-01T00:00:00.000+00:00",
									  "expireDate": "2026-12-31T23:59:59.999+00:00",
									  "cashPercentage": 53.00,
									  "commission": 6.0,
									  "divisibilityCheck": 1.00,
									  "imeCommoditySymbol": "OIL"
									}
									"""))),
			@ApiResponse(responseCode = "404", description = "کالا یافت نشد")
	})
	@Parameter(name = "goodId", description = "شناسه کالا", required = true, example = "101")
	@GetMapping("/get-by-good/{goodId}")
	public ResponseEntity<GoodBucketDto> getCurrentGoodBucket(@PathVariable Long goodId) {
		return ResponseEntity.ok(goodBucketService.getOnSpecificDate(goodId, new Date()));
	}

	@Operation(summary = "تاریخچه ضرایب فروش کالا",
			description = "دریافت تمام رکوردهای ضرایب فروش یک کالا به ترتیب زمانی")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "عملیات با موفقیت انجام شد",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = GoodBucketDto.Info.class),
							examples = @ExampleObject(value = """
									[
									  {
									    "id": 1,
									    "goodId": 101,
									    "imeCommodityId": 2001,
									    "goodName": "روغن موتور",
									    "startDate": "2026-01-01T00:00:00.000+00:00",
									    "expireDate": "2026-06-30T23:59:59.999+00:00",
									    "cashPercentage": 53.00,
									    "commission": 6.0,
									    "createdDate": "2026-01-01T10:30:00.000+00:00",
									    "createdBy": "admin"
									  },
									  {
									    "id": 2,
									    "goodId": 101,
									    "imeCommodityId": 2001,
									    "goodName": "روغن موتور",
									    "startDate": "2026-07-01T00:00:00.000+00:00",
									    "expireDate": "2026-12-31T23:59:59.999+00:00",
									    "cashPercentage": 55.00,
									    "commission": 6.5,
									    "createdDate": "2026-06-30T14:20:00.000+00:00",
									    "createdBy": "admin"
									  }
									]
									"""))),
			@ApiResponse(responseCode = "404", description = "کالا یافت نشد")
	})
	@Parameter(name = "goodId", description = "شناسه کالا", required = true, example = "101")
	@GetMapping("/get-history/{goodId}")
	public ResponseEntity<List<GoodBucketDto.Info>> getHistory(@PathVariable Long goodId) {
		return ResponseEntity.ok(goodBucketService.getHistory(goodId));
	}

	@Operation(summary = "دریافت ضریب فروش بر اساس شناسه کالا در بورس",
			description = "دریافت ضریب فروش یک کالا با استفاده از شناسه کالا در بورس کالا")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "عملیات با موفقیت انجام شد",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = GoodBucketDto.class),
							examples = @ExampleObject(value = """
									{
									  "id": 1,
									  "goodId": 101,
									  "imeCommodityId": 2001,
									  "goodName": "روغن موتور",
									  "startDate": "2026-01-01T00:00:00.000+00:00",
									  "expireDate": "2026-12-31T23:59:59.999+00:00",
									  "cashPercentage": 53.00,
									  "imeCommoditySymbol": "OIL"
									}
									"""))),
			@ApiResponse(responseCode = "404", description = "کالا در بورس یافت نشد")
	})
	@Parameter(name = "commodityId", description = "شناسه کالا در بورس کالا",
			required = true, example = "2001")
	@GetMapping("/get-by-commodity/{commodityId}")
	public ResponseEntity<GoodBucketDto> findByCommodityId(@PathVariable Long commodityId) {
		return ResponseEntity.ok(goodBucketService.findByCommodityId(commodityId));
	}
}