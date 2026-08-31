package com.nicico.internal.sales.export.service;

import com.nicico.copper.common.util.date.DateUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.notification.dto.MultipartInputStreamFileResource;
import com.nicico.internal.sales.proforma.dto.DocumentReplacement;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.enums.IssueSourceType;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.util.TextUtility;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportDocServiceImpl implements ExportDocService {
	private static final String PROFORMA_NOT_FOUND_MESSAGE = "پیش فاکتور وجود ندارد";
	private static final String FILE_WRITE_ERROR_MESSAGE = "خطایی در هنگام نوشتن فایل اتفاق افتاد";
	private static final String DECIMAL_FORMAT_PATTERN = "#,###.#####";
	private static final String EMPTY_STRING = "";
	private static final String DEFAULT_PLACEHOLDER = "-";

	private final DateUtil dateUtil;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ExecutorService executorService = Executors.newCachedThreadPool();
	private final RestTemplate restTemplate;
	private final DecimalFormat formatter = new DecimalFormat(DECIMAL_FORMAT_PATTERN);
	private final DecimalFormat floatFormatter = new DecimalFormat("#,##0.00");
	private final RemittanceMasterRepository remittanceMasterRepository;
	private final LcRepository lcRepository;
	private final ProformaMasterRepository proformaMasterRepository;

	// Configuration properties
	@Value("${nicico.static-files.internal-sale-pre-invoice}")
	private String preInvoiceFileAddress;
	@Value("${nicico.static-files.internal-sale-pre-invoice-signed}")
	private String preInvoiceFileAddressSigned;
	@Value("${nicico.static-files.internal-sale-pre-invoice-cash}")
	private String preInvoiceCashFileAddress;
	@Value("${nicico.static-files.internal-sale-pre-invoice-cash-signed}")
	private String preInvoiceCashFileAddressSigned;
	@Value("${nicico.static-files.internal-sale-remittance}")
	private String remittanceFile;
	@Value("${nicico.static-files.internal-sale-remittance-signed}")
	private String remittanceFileSigned;

	@Value("${nicico.static-files.internal-sale-gaam-sign-zero}")
	private String gaamSignZeroFile;
	@Value("${nicico.static-files.internal-sale-gaam-sign}")
	private String gaamSignFile;

	@Value("${nicico.static-files.internal-sale-extra-bill-signed-zero}")
	private String extraBillFileSignedZero;

	@Value("${nicico.static-files.internal-sale-extra-bill-signed}")
	private String extraBillFileSigned;


	@Value("${nicico.pdf-api}")
	private String pdfConvertorUrl;

	@Override
	public byte[] exportProformaDoc(Long detailId) {
		log.debug("Exporting document for detail ID: {}", detailId);
		ProformaDetailModel proforma = findProformaDetail(detailId);
		if (proforma.getProformaReversalStatus() == ProformaReversalStatus.CANCELED) {
			return new byte[0];
		}
		String templatePath = determineTemplatePath(proforma);
		try (FileInputStream fileInputStream = new FileInputStream(templatePath)) {
			List<DocumentReplacement> replacements = createDocumentReplacements(detailId);
			return processDocument(fileInputStream, replacements);
		} catch (IOException e) {
			throw new InternalSaleCustomException.FileContentException(FILE_WRITE_ERROR_MESSAGE);
		}
	}


	@Override
	public byte[] exportProformaDocOnlySigned(Long detailId) {

		ProformaDetailModel proforma = findProformaDetail(detailId);
		if (proforma.getProformaReversalStatus() == ProformaReversalStatus.CANCELED) {
			log.warn("Proforma with ID {} is canceled, returning empty byte array", detailId);
			return new byte[0];
		}

		String filePath = switch (proforma.getProformaIssueType()) {
			case LETTER_OF_CREDIT_OPENING -> preInvoiceFileAddressSigned;
			default -> preInvoiceCashFileAddressSigned;
		};

		try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
			List<DocumentReplacement> replacements = createDocumentReplacements(detailId);
			return processDocument(fileInputStream, replacements);
		} catch (IOException e) {
			throw new InternalSaleCustomException.FileContentException(FILE_WRITE_ERROR_MESSAGE);
		}
	}


	@Override
	public byte[] exportProformaPdf(Long proformaDetailId) {
		log.debug("Exporting PDF for proforma detail ID: {}", proformaDetailId);
		try {
			byte[] docBytes = exportRemittanceDoc(proformaDetailId);
			XWPFDocument xwpfDocument = new XWPFDocument(new ByteArrayInputStream(docBytes));
			MultiValueMap<String, Object> requestBody = createMultipartRequestBody(List.of(xwpfDocument));
			RequestEntity<MultiValueMap<String, Object>> request = createPdfConversionRequest(requestBody);
			return restTemplate.exchange(request, byte[].class).getBody();
		} catch (Exception ex) {
			throw new InternalSaleCustomException.FileContentException(FILE_WRITE_ERROR_MESSAGE);
		}
	}

	private ProformaDetailModel findProformaDetail(Long detailId) {
		return proformaDetailRepository.findById(detailId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(PROFORMA_NOT_FOUND_MESSAGE));
	}


	private ProformaMasterModel findProformaMaster(Long masterId) {
		return proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(PROFORMA_NOT_FOUND_MESSAGE));
	}

	private String determineTemplatePath(ProformaDetailModel proforma) {
		ProformaMasterModel master = findProformaMaster(proforma.getProformaMasterId());

		boolean isApproved = master.getWorkflowApproveStatus() == WorkflowApproveStatus.ACCEPTED;
		boolean isZeroExtraBillPercent = !master.getProformaDetailModelLists().isEmpty()
				&& master.getProformaDetailModelLists().get(0).getExtraBillOfPercent() != null
				&& master.getProformaDetailModelLists().get(0).getExtraBillOfPercent().compareTo(BigDecimal.ZERO) == 0;
		return switch (master.getProformaIssueType()) {
			case FROM_CREDIT_FACILITIES -> isApproved ? preInvoiceCashFileAddressSigned : preInvoiceCashFileAddress;
			case LETTER_OF_CREDIT_OPENING -> isApproved ? preInvoiceFileAddressSigned : preInvoiceFileAddress;
			case EXTRA_BILL_OF_EXCHANGE -> isZeroExtraBillPercent ? extraBillFileSignedZero : extraBillFileSigned;
			case GAM_BONDS -> isZeroExtraBillPercent ? gaamSignZeroFile : gaamSignFile;
			default -> preInvoiceFileAddress;

		};
	}

	private MultiValueMap<String, Object> createMultipartRequestBody(List<XWPFDocument> documents) {
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		for (int i = 0; i < documents.size(); i++) {
			XWPFDocument doc = documents.get(i);
			PipedInputStream inputStream = new PipedInputStream();
			CompletableFuture.runAsync(() -> writeDocumentToStream(doc, inputStream), executorService);
			bodyMap.add("files", new MultipartInputStreamFileResource(inputStream, (i + 1) + ".doc"));
		}
		bodyMap.add("merge", "true");
		return bodyMap;
	}

	private void writeDocumentToStream(XWPFDocument doc, PipedInputStream inputStream) {
		try (PipedOutputStream outputStream = new PipedOutputStream(inputStream)) {
			doc.write(outputStream);
		} catch (IOException ex) {
			log.error("Error writing document to stream: {}", ex.getMessage());
			throw new InternalSaleCustomException.FileContentException(FILE_WRITE_ERROR_MESSAGE);
		}
	}

	private RequestEntity<MultiValueMap<String, Object>> createPdfConversionRequest(MultiValueMap<String, Object> body) {
		return RequestEntity.post(URI.create(pdfConvertorUrl)).contentType(MediaType.MULTIPART_FORM_DATA).body(body);
	}

	private List<DocumentReplacement> createDocumentReplacements(Long proformaDetailId) {
		ProformaDetailModel detailModel = findProformaDetail(proformaDetailId);
		ProformaMasterModel masterModel = findProformaMaster(detailModel.getProformaMasterId());
		if (detailModel.getProformaReversalStatus() == ProformaReversalStatus.CANCELED) {
			return new ArrayList<>();
		}
		List<DocumentReplacement> replacements = new ArrayList<>();
		addBasicReplacements(replacements, detailModel, masterModel);
		addReversalTextReplacement(replacements, detailModel);
		return replacements;
	}

	private void addBasicReplacements(List<DocumentReplacement> replacements, ProformaDetailModel detailModel, ProformaMasterModel masterModel) {
		try {
			BigDecimal quantity = detailModel.getProformaGoodItemModels().get(0).getCreditQuantity();
			BigDecimal unitPrice = detailModel.getProformaGoodItemModels().get(0).getUnitPriceCredit();

			BigDecimal totalAmount = quantity.multiply(unitPrice);
			BigDecimal tax = detailModel.getProformaGoodItemModels().get(0).getVatCreditAmount();
			BigDecimal finalAmount = totalAmount.add(tax);
			String buyerAddress = TextUtility.shortenAddress(masterModel.getAddress());
			String contractNo = String.valueOf(masterModel.getContractNo());
			String proformaDate = DateUtility.getJalaliDate(detailModel.getPerformaDate());
			String storageCost = floatFormatter.format(detailModel.getStorageCost());

			BigDecimal extraAmount = BigDecimal.ZERO;
			BigDecimal extraPercent = detailModel.getExtraBillOfPercent();
			BigDecimal finalAmountWithExtra = finalAmount;

			if (extraPercent != null && extraPercent.compareTo(BigDecimal.ZERO) > 0) {
				// Calculate: finalAmount * (1 + extraPercent/100)
				BigDecimal factor = BigDecimal.ONE.add(
						extraPercent.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
				);
				extraAmount = finalAmount.multiply(factor);
				finalAmountWithExtra = extraAmount; // extraAmount IS the final amount with extra
			}

			replacements.addAll(List.of(
					new DocumentReplacement("BUYER_NAME", masterModel.getCustomerName()),
					new DocumentReplacement("BUYER_ADDRESS", buyerAddress),
					new DocumentReplacement("PHONE", masterModel.getPhone()),
					new DocumentReplacement("BUYER_ECONOMIC_CODE", masterModel.getEconomicCode()),
					new DocumentReplacement("BUYER_REGISTER_CODE", masterModel.getNationalCode()),
					new DocumentReplacement("CONTRACT_NO", contractNo),
					new DocumentReplacement("PRODUCT_NAME", detailModel.getProformaGoodItemModels().get(0).getGoodName()),
					new DocumentReplacement("VALUE", formatter.format(quantity)),
					new DocumentReplacement("UNIT_PRICE", formatter.format(unitPrice)),
					new DocumentReplacement("VALIDITY_DAYS", detailModel.getDeadlineDays().toString()),
					new DocumentReplacement("N_STORAGE_DEAD", detailModel.getStorageDeadline().toString()),
					new DocumentReplacement("STORAGE_COST", storageCost),
					new DocumentReplacement("TOTAL_CHARACTER_PRICE", numberToString(finalAmountWithExtra)),
					new DocumentReplacement("N_PAYMENT_DEFERRAL", detailModel.getPaymentDeferral().toString()),
					new DocumentReplacement("N_CREDIT_EXPIRE_PERIOD", detailModel.getCreditExpirePeriod().toString()),
					new DocumentReplacement("TOTAL_PRICE", formatter.format(totalAmount)),
					new DocumentReplacement("FINAL_PRICE", formatter.format(finalAmount)),
					new DocumentReplacement("TAX", formatter.format(tax)),
					new DocumentReplacement("C_PERFORMA_DATE", proformaDate),
					new DocumentReplacement("C_PERFORMA_NO", detailModel.getPerformaNo()),
					new DocumentReplacement("CONTRACT_DATE", DateUtility.getJalaliDate(detailModel.getOrderDate())),
					new DocumentReplacement("N_EXTRA_BILL_OF_PERCENT", formatter.format(extraPercent != null ? extraPercent : BigDecimal.ZERO)),
					new DocumentReplacement("N_EXTRA_BILL_OF_AMOUNT", formatter.format(finalAmountWithExtra)),
					new DocumentReplacement("N_GAM_CERTIFICATE_COUNT", formatter.format(detailModel.getGamCertificateCount())),
					new DocumentReplacement("N_SHIPPING_DEAD", detailModel.getShippingDeadline().toString())
			));

		} catch (Exception exception) {
			throw new InternalSaleCustomException.FileContentException(exception.getMessage());
		}
	}

	private void addRemittanceBasicReplacements(List<DocumentReplacement> replacements, RemittanceMasterModel masterModel) {
		String lcDate = DateUtility.getJalaliDate(masterModel.getLcExpiryDate());
		lcDate = lcDate.contains("1348") ? DEFAULT_PLACEHOLDER : lcDate;
		String proformaDate = DateUtility.getJalaliDate(masterModel.getProformaDate());
		proformaDate = proformaDate.contains("1348") ? DEFAULT_PLACEHOLDER : proformaDate;
		String description = masterModel.getDescription() != null ? masterModel.getDescription() : DEFAULT_PLACEHOLDER;
		String remittanceIssueDate = DateUtility.getJalaliDate(masterModel.getRemittanceDate());
		remittanceIssueDate = remittanceIssueDate.contains("1348") ? DEFAULT_PLACEHOLDER : remittanceIssueDate;
		String tradingBankNme = DEFAULT_PLACEHOLDER;
		String issuerBank = DEFAULT_PLACEHOLDER;
		if (!masterModel.getLcNo().equals(DEFAULT_PLACEHOLDER)) {
			var lc = lcRepository.findById(masterModel.getLcId()).orElse(null);
			if (lc != null) {
				tradingBankNme = lc.getTradingBankTitle() + " - " + lc.getIssuerBankBranchName();
				issuerBank = lc.getIssuerBankName() + " - " + lc.getIssuerBankBranchName();
			}
		}
		String remittanceN = DEFAULT_PLACEHOLDER;
		if (!(masterModel.getPmsId() == null)) {
			remittanceN = masterModel.getPmsId();
		}

		BigDecimal remittanceUnitPriceCredit;
		BigDecimal remittanceQuantityCredit;
		BigDecimal formattedRemittanceQuantityCash = BigDecimal.ZERO;
		String formattedRemittanceUnitPriceCredit = DEFAULT_PLACEHOLDER;
		String formattedRemittanceQuantityCredit = DEFAULT_PLACEHOLDER;
		String remittanceUnitPriceCreditWord = DEFAULT_PLACEHOLDER;
		String remittanceQuantityCreditWord = DEFAULT_PLACEHOLDER;

// Only assign values if not CASH type
		if (masterModel.getIssueSourceType() != IssueSourceType.CASH) {
			remittanceUnitPriceCredit = masterModel.getRemittanceUnitPriceCredit();
			remittanceQuantityCredit = masterModel.getRemittanceQuantityCredit();

			formattedRemittanceUnitPriceCredit = formatter.format(remittanceUnitPriceCredit);
			formattedRemittanceQuantityCredit = formatter.format(remittanceQuantityCredit);
			formattedRemittanceQuantityCash = masterModel.getRemittanceQuantityCash();
			remittanceUnitPriceCreditWord = numberToString(remittanceUnitPriceCredit);
			remittanceQuantityCreditWord = numberToString(remittanceQuantityCredit);
		}

		replacements.addAll(List.of(
				new DocumentReplacement("C_REMITTANCE_DATE", remittanceIssueDate)
				, new DocumentReplacement("N_REMITTANCE_QUANTITY_CASH_W", numberToString(formattedRemittanceQuantityCash))
				, new DocumentReplacement("N_REMITTANCE_QUANTITY_W", this.numberToString(masterModel.getRemittanceQuantity()))
				, new DocumentReplacement("C_CASH_UNITPRICE_W", numberToString((masterModel.getRemittanceUnitPriceCash())))
				, new DocumentReplacement("N_REMITTANCE_QUANTITY_CASH", formatter.format(formattedRemittanceQuantityCash))
				, new DocumentReplacement("N_REMITTANCE_QUANTITY_CREDIT_W", remittanceQuantityCreditWord)
				, new DocumentReplacement("N_REMITTANCE_UNIT_PRICE_CREDIT_W", remittanceUnitPriceCreditWord)
				, new DocumentReplacement("N_REMITTANCE_UNIT_PRICE_CREDIT", formattedRemittanceUnitPriceCredit)
				, new DocumentReplacement("N_REMITTANCE_QUANTITY_CREDIT", formattedRemittanceQuantityCredit)
				, new DocumentReplacement("C_REMITTANCE_NO", remittanceN)
				, new DocumentReplacement("C_VALIDITY_DATE", DateUtility.getJalaliDate(masterModel.getValidityDate()))
				, new DocumentReplacement("C_ISSUE_SOURCE_TYPE", masterModel.getIssueSourceType().getValue())
				, new DocumentReplacement("BUYER_NAME", masterModel.getCustomerName())
				, new DocumentReplacement("BUYER_REGISTER_CODE", masterModel.getNationalCode())
				, new DocumentReplacement("BUYER_ECONOMIC_CODE", masterModel.getEconomicCode())
				, new DocumentReplacement("C_LOADING_PORT", masterModel.getLoadingPort())
				, new DocumentReplacement("PRODUCT_NAME", masterModel.getGoodName())
				, new DocumentReplacement("CONTRACT_DATE", DateUtility.getJalaliDate(masterModel.getContractDate()))
				, new DocumentReplacement("CONTRACT_NO", masterModel.getContractNo())
				, new DocumentReplacement("C_SELLER_BROKER_NAME", masterModel.getSellerBrokerName())
				, new DocumentReplacement("C_PACKING_NAME", masterModel.getPackingName())
				, new DocumentReplacement("C_LOT_NUMBER", masterModel.getLotNumber())
				, new DocumentReplacement("N_CASH_PERCENTAGE", formatter.format(masterModel.getCashPercentage()))
				, new DocumentReplacement("N_CREDIT_PERCENTAGE", formatter.format(masterModel.getCreditPercentage()))
				, new DocumentReplacement("C_BUYER_BROKER_NAME", masterModel.getBuyerBrokerName())
				, new DocumentReplacement("N_REMITTANCE_QUANTITY", formatter.format(masterModel.getRemittanceQuantity()))
				, new DocumentReplacement("UNIT_PRICE", formatter.format(masterModel.getRemittanceUnitPriceCash()))
				, new DocumentReplacement("C_ISSUER_BANK_NAME", issuerBank)
				, new DocumentReplacement("C_TRADER_BANK_NAME", tradingBankNme)
				, new DocumentReplacement("C_LC_NO", masterModel.getLcNo())
				, new DocumentReplacement("C_PROFORMA_NO", masterModel.getProformaNo())
				, new DocumentReplacement("C_LC_ISSUE_DATE", lcDate)
				, new DocumentReplacement("C_PROFORMA_DATE", proformaDate)
				, new DocumentReplacement("C_REMITTANCE_DATE", remittanceIssueDate)
				, new DocumentReplacement("C_DESCRIPTION", description)
				, new DocumentReplacement("C_ISSUER_NAME", masterModel.getIssuerName())
		));
	}

	private void addReversalTextReplacement(List<DocumentReplacement> replacements, ProformaDetailModel detailModel) {
		String reversalText = EMPTY_STRING;

		if (detailModel.getProformaReversalStatus() == ProformaReversalStatus.NORMAL) {
			replacements.add(new DocumentReplacement("C_REVERSAL_TEXT", EMPTY_STRING));

		} else if (detailModel.getProformaReversalStatus() == ProformaReversalStatus.EDITED) {
			reversalText = processReversalText(detailModel);
		}

		replacements.add(new DocumentReplacement("C_REVERSAL_TEXT", reversalText));
	}

	private String processReversalText(ProformaDetailModel detailModel) {

		List<String> proformaList = getCanceledProformaNo(detailModel.getProformaMasterId());
		if (proformaList.isEmpty()) {
			return EMPTY_STRING;
		}
		return buildReversalMessage(proformaList, detailModel);
	}

	private String buildReversalMessage(List<String> proformaList, ProformaDetailModel detailModel) {
		if (detailModel.getProformaReversalStatus() == ProformaReversalStatus.NORMAL) return EMPTY_STRING;
		String proformaNumbers = String.join("، ", proformaList);
		String proformaDate = DateUtility.getJalaliDate(detailModel.getPerformaDate());
		String baseMessage = "شایان ذکر است پیش فاکتور%s صادره قبلی به شماره%s %s مورخ %s ابطال و کان لم یکن می باشد.";
		if (proformaList.size() == 1) {
			return String.format(baseMessage, EMPTY_STRING, EMPTY_STRING, proformaNumbers, proformaDate);
		} else {
			return String.format(baseMessage, "های", " های", proformaNumbers, proformaDate);
		}
	}


	private List<String> getCanceledProformaNo(Long masterId) {
		var masterModel = proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException("پیش فاکتور پیدا نشد"));
		return masterModel.getProformaDetailModelLists().stream().filter(detailModel -> detailModel.getProformaReversalStatus() == ProformaReversalStatus.CANCELED).map(ProformaDetailModel::getPerformaNo).collect(Collectors.toSet()).stream().toList();
	}

	private byte[] processDocument(FileInputStream fileInputStream, List<DocumentReplacement> replacements) throws IOException {
		try (XWPFDocument document = new XWPFDocument(fileInputStream)) {
			for (DocumentReplacement replacement : replacements) {
				replaceTextInDocument(document, replacement);
			}
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				document.write(outputStream);
				return outputStream.toByteArray();
			}
		}
	}

	private void replaceTextInDocument(XWPFDocument document, DocumentReplacement replacement) {
		String searchKey = replacement.keyForReplace();
		String replaceValue = replacement.valueToReplace();

		// Replace in paragraphs
		document.getParagraphs().stream().flatMap(p -> p.getRuns().stream()).forEach(run -> replaceTextInRun(run, searchKey, replaceValue));

		// Replace in tables
		document.getTables().stream().flatMap(table -> table.getRows().stream()).flatMap(row -> row.getTableCells().stream()).flatMap(cell -> cell.getParagraphs().stream()).flatMap(paragraph -> paragraph.getRuns().stream()).forEach(run -> replaceTextInRun(run, searchKey, replaceValue));

		// Replace in headers
		document.getHeaderList().stream().flatMap(header -> header.getTables().stream()).flatMap(table -> table.getRows().stream()).flatMap(row -> row.getTableCells().stream()).flatMap(cell -> cell.getParagraphs().stream()).flatMap(paragraph -> paragraph.getRuns().stream()).forEach(run -> replaceTextInRun(run, searchKey, replaceValue));
	}

	private void replaceTextInRun(XWPFRun run, String searchKey, String replaceValue) {
		String text = run.getText(0);
		if (text != null && text.contains(searchKey)) {
			run.setText(text.replace(searchKey, replaceValue), 0);
		}
	}

	private List<DocumentReplacement> createRemittanceDocumentReplacements(RemittanceMasterModel remittanceMasterModel) {
		List<DocumentReplacement> replacements = new ArrayList<>();
		addRemittanceBasicReplacements(replacements, remittanceMasterModel);
		return replacements;
	}

	@Override
	public String numberToString(BigDecimal price) {
		String result = dateUtil.numberToString(price.toString());
		return Optional.of(result).filter(r -> r.startsWith("صد")).map(r -> r.replaceFirst("صد", "یکصد")).orElse(result);
	}

	@Override
	public byte[] exportRemittanceDoc(long remittanceId) {

		log.debug("Exporting document for remittance ID: {}", remittanceId);

		RemittanceMasterModel masterModel = remittanceMasterRepository.findById(remittanceId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						"حواله پیدا نشد"));

		String templatePath;
		if (masterModel.getWorkflowApproveStatus() == WorkflowApproveStatus.ACCEPTED) {
			templatePath = remittanceFileSigned;
		} else {
			templatePath = remittanceFile;
		}

		try (FileInputStream fileInputStream = new FileInputStream(templatePath)) {
			List<DocumentReplacement> replacements = createRemittanceDocumentReplacements(masterModel);
			return processDocument(fileInputStream, replacements);
		} catch (IOException e) {
			log.error("Error processing document for detail ID {}: {}", masterModel.getId(), e.getMessage());
			throw new InternalSaleCustomException.FileContentException(FILE_WRITE_ERROR_MESSAGE);
		}
	}

	@Override
	public byte[] exportRemittancePdf(long remittanceId) {
		log.debug("Exporting PDF for remittance detail ID: {}", remittanceId);

		try {
			byte[] docBytes = exportRemittanceDoc(remittanceId);
			XWPFDocument xwpfDocument = new XWPFDocument(new ByteArrayInputStream(docBytes));
			MultiValueMap<String, Object> requestBody = createMultipartRequestBody(List.of(xwpfDocument));
			RequestEntity<MultiValueMap<String, Object>> request = createPdfConversionRequest(requestBody);
			return restTemplate.exchange(request, byte[].class).getBody();

		} catch (Exception ex) {
			log.error("Error converting to PDF for proforma detail ID {}: {}", remittanceId, ex.getMessage());
			throw new InternalSaleCustomException.FileContentException(FILE_WRITE_ERROR_MESSAGE);
		}
	}

	@Override
	public byte[] convertDocListToPdf(List<XWPFDocument> docList) {
		if (docList.isEmpty()) {
			throw new InternalSaleCustomException.FileContentException(FILE_WRITE_ERROR_MESSAGE);
		}
		MultiValueMap<String, Object> requestBody = createMultipartRequestBody(docList);
		RequestEntity<MultiValueMap<String, Object>> request = createPdfConversionRequest(requestBody);
		return restTemplate.exchange(request, byte[].class).getBody();
	}
}