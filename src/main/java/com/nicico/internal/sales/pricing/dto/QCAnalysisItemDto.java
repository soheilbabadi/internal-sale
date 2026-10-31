package com.nicico.internal.sales.pricing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class QCAnalysisItemDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long analysisMasterId;

	@Schema(description = "شماره ردیف")
	private Integer rowNo;

	@Schema(description = "شماره اولین بشکه / تک بشکه")
	private Long fromBarrelNo;

	@Schema(description = "شماره آخرین بشکه (در صورت بازه)")
	private Long toBarrelNo;

	@Schema(description = "بازه شماره بشکه نمایشی")
	private String barrelRange;

	@Schema(description = "شناسه لات")
	private String lotNo;

	@Schema(description = "تعداد کل بشکه‌ها")
	private Integer totalBarrelsCount;

	@Schema(description = "شناسه محل بارگیری")
	private Long loadingPlaceId;

	@Schema(description = "عنوان محل بارگیری")
	private String loadingPlace;

	// مقادیر وزنی و ریالی
	private BigDecimal grossWetWeight;
	private BigDecimal netDryWeight;
	private BigDecimal offerLotWeightMt;
	private BigDecimal pricePerKgBarrel;

	// عیارهای مرحله جاری
	private BigDecimal moisturePct;
	private BigDecimal agPct;
	private BigDecimal agKg;
	private BigDecimal agPpm;
	private BigDecimal auPct;
	private BigDecimal auKg;
	private BigDecimal auPpm;
	private BigDecimal sePct;
	private BigDecimal seKg;
	private BigDecimal cuPct;
	private BigDecimal cuKg;
	private BigDecimal ptPpm;
	private BigDecimal pdPpm;
	private BigDecimal moPct;
	private BigDecimal fePct;
	private BigDecimal sPct;
	private BigDecimal pbPct;
	private BigDecimal oilPct;

	// فیلدهای مس و سولفور
	private BigDecimal cuCalcPct;
	private BigDecimal auCalcPpm;
	private BigDecimal agCalcPpm;
	private BigDecimal smeltingCost;
	private BigDecimal refiningCost;
	private BigDecimal cathodePrice;
	private BigDecimal oxidePrice;
	private BigDecimal fractionDeduction;

	// میانگین‌ها
	private BigDecimal avgMoisturePct;
	private BigDecimal avgAgPct;
	private BigDecimal avgAgKg;
	private BigDecimal avgAgPpm;
	private BigDecimal avgAuPct;
	private BigDecimal avgAuKg;
	private BigDecimal avgAuPpm;
	private BigDecimal avgSePct;
	private BigDecimal avgSeKg;
	private BigDecimal avgCuPct;
	private BigDecimal avgCuKg;
	private BigDecimal avgPtPpm;
	private BigDecimal avgPdPpm;
	private BigDecimal avgMoPct;
	private BigDecimal avgFePct;
	private BigDecimal avgSPct;
	private BigDecimal avgPbPct;
	private BigDecimal avgOilPct;
	private BigDecimal avgCuCalcPct;
	private BigDecimal avgAuCalcPpm;
	private BigDecimal avgAgCalcPpm;

	private String remarks;
}
