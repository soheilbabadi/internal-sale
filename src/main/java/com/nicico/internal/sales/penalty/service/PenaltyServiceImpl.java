package com.nicico.internal.sales.penalty.service;


import com.nicico.copper.common.util.date.DateUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.penalty.dto.PenaltyDto;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.trade.repository.TradeExtractRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class PenaltyServiceImpl {
	private final DimDateService dimDateService;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final TradeExtractRepository tradeExtractRepository;
	private final DateUtil dateUtil;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public PenaltyDto calculatePenaltyAmount(BigDecimal amount, long lateDays, BigDecimal commission) {
		BigDecimal penaltyAmount;
		BigDecimal commissionAmount = BigDecimal.ZERO;
		if (lateDays <= 3) {
			penaltyAmount = amount.multiply(BigDecimal.valueOf(0.0025)).multiply(BigDecimal.valueOf(lateDays));
		} else {
			penaltyAmount = amount.multiply(BigDecimal.valueOf(0.05));
			commissionAmount = commission.multiply(BigDecimal.valueOf(0.01)).multiply(amount);
		}
		return new PenaltyDto(penaltyAmount, commissionAmount);
	}

	public PenaltyDto calculatePenalty(long performaId) {
		var performaDetailModel = proformaDetailRepository.findById(performaId).orElseThrow(() -> new InternalSaleCustomException.ValidationException("اطلاعات پیش فاکتور وجود ندارد"));
		var masterModel = proformaMasterRepository.findById(performaDetailModel.getProformaMasterId()).orElseThrow(() -> new InternalSaleCustomException.ValidationException("اطلاعات پیش فاکتور وجود ندارد"));

		LocalDate localDate = DateUtility.toLocalDate(performaDetailModel.getPerformaDate());
		LocalDate finishDate = DateUtility.toLocalDate(dimDateService.addWorkingDays(DateUtility.toDate(localDate), masterModel.getDeadlineDays()).getShortDate());
		if (finishDate.isBefore(LocalDate.now())) {
			return new PenaltyDto(BigDecimal.ZERO, BigDecimal.ZERO);
		}
		long lateDays = dimDateService.countWorkingDays(DateUtility.toDate(finishDate), new Date());
		BigDecimal commissionAmount = performaDetailModel.getFinalPrice().multiply(BigDecimal.valueOf(masterModel.getCommissionPercentage())).divide(BigDecimal.valueOf(100), RoundingMode.UP);
		return calculatePenaltyAmount(performaDetailModel.getFinalPrice(), lateDays, commissionAmount);
	}
}
