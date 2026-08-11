package com.nicico.internal.sales.trade.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.goods.dto.TradeCommodityDTO;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.repository.GoodsRepository;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import com.nicico.internal.sales.ins.customer.dto.TradeBuyerDTO;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.trade.dto.BuyerInfoDto;
import com.nicico.internal.sales.trade.dto.TradeExtractDto;
import com.nicico.internal.sales.trade.dto.TradeExtractMapper;
import com.nicico.internal.sales.trade.dto.TradeExtractStartProformaMapper;
import com.nicico.internal.sales.trade.model.TradeExtractModel;
import com.nicico.internal.sales.trade.repository.TradeExtractRepository;
import com.nicico.internal.sales.trade.repository.TradeExtractStartProformaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeExtractServiceImpl implements TradeExtractService {
	private final TradeExtractRepository tradeExtractRepository;
	private final TradeExtractMapper mapper;
	private final CustomerRepository customerRepository;
	private final IMETradeRepository imeTradeRepository;
	private final GoodsRepository goodsRepository;
	private final TradeExtractStartProformaRepository tradeExtractStartProformaRepository;
	private final TradeExtractStartProformaMapper extractStartProformaMapper;


	@Override
	public TradeExtractDto.Info getByPaymentCode(String paymentCode) {
		return tradeExtractRepository.findFirstByPaymentCodeOrderByIdDesc(paymentCode).map(mapper::toDTO).orElse(null);
	}

	@Override
	public List<TradeExtractDto.Info> getAll() {
		return tradeExtractRepository.findAll().stream().sorted(Comparator.comparing(TradeExtractModel::getContractDate).reversed()).limit(50).map(mapper::toDTO).toList();
	}

	@Override
	public SearchDTO.SearchRs<TradeExtractDto.Info> searchProformaStartable(SearchDTO.SearchRq request) {
		return SearchUtil.search(tradeExtractStartProformaRepository, request, extractStartProformaMapper::toDTO);
	}

	@Override
	public SearchDTO.SearchRs<TradeExtractDto.Info> search(SearchDTO.SearchRq request) {

		return SearchUtil.search(tradeExtractRepository, request, mapper::toDTO);
	}


	@Override
	public List<BuyerInfoDto> listAllBuyerInfo() {
		return tradeExtractRepository.findAll().stream().map(trade -> new BuyerInfoDto(trade.getBuyerName(), trade.getBuyerNationalCode())).toList();
	}

	@Override
	public byte[] excel(SearchDTO.SearchRq request) throws IOException {
		SearchDTO.SearchRs<TradeExtractDto.Info> trades = search(request);
		if (trades.getTotalCount() > 0) {
			XSSFWorkbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("trades");
			sheet.setRightToLeft(true);
			Row header = sheet.createRow(0);
			CellStyle headerStyle = createHeaderStyle(workbook);
			String[] headers = getHeaders();
			IntStream.range(0, headers.length).forEach(i -> {
				Cell headerCell = header.createCell(i);
				headerCell.setCellValue(headers[i]);
				headerCell.setCellStyle(headerStyle);
				sheet.autoSizeColumn(i);
			});
			AtomicInteger rowIndex = new AtomicInteger(1);
			trades.getList().forEach(trade -> {
				Row row = sheet.createRow(rowIndex.getAndIncrement());
				populateRowWithTradeData(row, trade);
			});
			try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
				workbook.write(byteArrayOutputStream);
				return byteArrayOutputStream.toByteArray();
			}
		}
		return new byte[0];
	}

	@Override
	public List<TradeBuyerDTO> listDistinctBuyersNotInCustomers() {
		return imeTradeRepository.findDistinctBuyersNotInCustomers().stream().map(p -> new TradeBuyerDTO(p.getBuyerNationalCode(), p.getBuyerName())).toList();
	}

	private String[] getHeaders() {
		return new String[]{"تاریخ قرارداد", "نام کالا", "نام خریدار", "شناسه ملی خریدار", "درصد نقدی", "شماره قرارداد", "نوع تسویه", "مقدار", "قیمت واحد", "قیمت واحد اعتباری", "مبلغ مالیات ارزش افزوده", "مبلغ بخش اعتباری", "مبلغ بخش نقدی", "مبلغ نهایی"};
	}

	@Override
	public List<TradeCommodityDTO> listDistinctCommoditiesInTrades() {
		return imeTradeRepository.findDistinctCommoditiesNotInGoods().stream().map(p -> new TradeCommodityDTO(p.getCommodityCode(), p.getPersianName(), p.getSymbol())).collect(Collectors.toList());
	}

	@Override
	public void syncDataTrade() {
		var buyer = listDistinctBuyersNotInCustomers();
		buyer.forEach(buyerInfoDto -> {
			CustomerModel customerModel = new CustomerModel();
			customerModel.setNationalCode(buyerInfoDto.getBuyerNationalCode());
			customerModel.setName(buyerInfoDto.getBuyerName());
			customerModel.setRegisterNumber(buyerInfoDto.getBuyerNationalCode());
			customerModel.setCreatedBy(SecurityUtil.getUsername());
			customerModel.setCreatedDate(new Date());
			customerModel.setVersion(0);
			customerRepository.save(customerModel);
		});
		var commodity = listDistinctCommoditiesInTrades();
		commodity.forEach(commodityInfoDto -> {
			GoodsModel goodsModel = new GoodsModel();
			goodsModel.setImeCommodityId(commodityInfoDto.getCommodityCode());
			goodsModel.setName(commodityInfoDto.getPersianName());
			goodsModel.setDescription(commodityInfoDto.getPersianName());
			goodsModel.setImeCommoditySymbol(commodityInfoDto.getSymbol());
			goodsModel.setCreatedBy(SecurityUtil.getUsername());
			goodsModel.setCreatedDate(new Date());
			goodsModel.setVersion(0);
			goodsRepository.save(goodsModel);
		});
	}

	private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		headerStyle.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);
		XSSFFont font = workbook.createFont();
		font.setFontName("Sahel");
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		headerStyle.setFont(font);
		return headerStyle;
	}

	private void populateRowWithTradeData(Row row, TradeExtractDto.Info trade) {
		row.createCell(0).setCellValue(trade.getContractDate());
		row.createCell(1).setCellValue(trade.getCommodityName());
		row.createCell(2).setCellValue(trade.getBuyerName());
		row.createCell(3).setCellValue(trade.getBuyerNationalCode());
		row.createCell(4).setCellValue(trade.getCashPercentage().doubleValue());
		row.createCell(5).setCellValue(trade.getContractNo());
		row.createCell(6).setCellValue(trade.getSettlementTypeDesc());
		row.createCell(7).setCellValue(trade.getUnitCount());
		row.createCell(8).setCellValue(trade.getUnitPrice());
		row.createCell(9).setCellValue(trade.getUnitPrice());
		row.createCell(10).setCellValue(trade.getVatAmount().longValue());
		row.createCell(11).setCellValue(trade.getCreditAmount().longValue());
		row.createCell(12).setCellValue(trade.getCashAmount().longValue());
		row.createCell(13).setCellValue(trade.getFinalAmount().longValue());
	}
}
