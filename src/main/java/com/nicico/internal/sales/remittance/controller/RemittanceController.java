package com.nicico.internal.sales.remittance.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.fms.service.FmsDocumentService;
import com.nicico.internal.sales.notification.service.NotificationService;
import com.nicico.internal.sales.remittance.dto.LotNumberRequest;
import com.nicico.internal.sales.remittance.dto.RemittanceCreateDto;
import com.nicico.internal.sales.remittance.dto.RemittanceMasterDto;
import com.nicico.internal.sales.remittance.dto.RemittanceUpdateRequest;
import com.nicico.internal.sales.remittance.enums.RemittanceSourceType;
import com.nicico.internal.sales.remittance.service.RemittanceDataProvider;
import com.nicico.internal.sales.remittance.service.RemittanceService;
import com.nicico.internal.sales.remittance.service.RemittanceTaxService;
import com.nicico.internal.sales.schedule.RemittanceScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/remittance")
@PreAuthorize("@secUtil.hasAuthority('R_INS_REMITTANCE')")
public class RemittanceController {
	private final RemittanceService remittanceService;
	private final RemittanceDataProvider remittanceDataProvider;
	private final RemittanceTaxService taxService;
//	private final ExportDocService exportDocService;

	private final RestTemplate restTemplate;
	private final FmsDocumentService fmsDocumentService;
	private final RemittanceScheduler remittanceScheduler;
	private final NotificationService notificationService;

	@Value("${nicico.pdf-api}")
	private String pdfConvertorUrl;

	@PreAuthorize("@secUtil.hasAuthority('C_INS_REMITTANCE')")
	@PostMapping("/update-description")
	public ResponseEntity<RemittanceMasterDto> updateDescription(@RequestBody RemittanceUpdateRequest
			                                                             remittanceMasterDto) {
		RemittanceMasterDto resultDto = remittanceService.updateDescription(remittanceMasterDto);
		notificationService.sendEmailWithEditedRemittanceAttachment(resultDto.getId());
		return ResponseEntity.ok(resultDto);
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_REMITTANCE')")
	@PostMapping("/create")
	public ResponseEntity<RemittanceMasterDto.Info> create(@RequestBody RemittanceCreateDto createDto) {
		if (createDto.getSourceType() == RemittanceSourceType.TRADE)
			return ResponseEntity.ok(remittanceService.createFromTrade(createDto));
		return ResponseEntity.ok(remittanceService.createFromProforma(createDto));
	}


	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(remittanceService.search(searchRq));
	}


	@PostMapping("/search-trade")
	public ResponseEntity<?> searchTrade(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(remittanceService.searchFromTrade(searchRq));
	}


	@PostMapping("/search-proforma")
	public ResponseEntity<?> searchProforma(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(remittanceService.searchFromProforma(searchRq));
	}


	@PostMapping("/tax")
	public ResponseEntity<Long> calculateTax(@RequestBody RemittanceCreateDto createDto) {
		return ResponseEntity.ok(taxService.calculateTax(createDto).longValue());
	}

	@GetMapping("/get-by-master/{id}")
	public ResponseEntity<RemittanceMasterDto> getByMasterId(@PathVariable Long id) {
		return ResponseEntity.ok(remittanceService.getDetailById(id));
	}

	@PostMapping("/delivery-deadline")
	public ResponseEntity<Date> calculateDeliveryDeadlineProforma(@RequestBody RemittanceCreateDto createDto) {

		if (createDto.getSourceType() == RemittanceSourceType.TRADE)
			return ResponseEntity.ok(remittanceDataProvider.lastDeliveryDeadlineTrade(createDto));

		return ResponseEntity.ok(remittanceDataProvider.lastDeliveryDeadlineProforma(createDto));

	}


	@GetMapping(value = "/export/{remittanceId}")
	public ResponseEntity<byte[]> exportRemittanceDoc(@PathVariable Long remittanceId) {
		return ResponseEntity.ok(fmsDocumentService.getOrCreateRemittancePdf(remittanceId).getContent());
	}

	@GetMapping(value = "/export-pdf/{remittanceId}")
	public ResponseEntity<byte[]> exportRemittancePdf(@PathVariable Long remittanceId) {
		return ResponseEntity.ok(fmsDocumentService.getOrCreateRemittancePdf(remittanceId).getContent());
	}


//
//	private ResponseEntity<byte[]> convertDocListToPdf(List<XWPFDocument> docList) {
//		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
//		if (!docList.isEmpty()) {
//			int i = 0;
//			for (XWPFDocument doc : docList) {
//				i++;
//				PipedInputStream in = new PipedInputStream();
//				new Thread(() -> {
//					try (PipedOutputStream out = new PipedOutputStream(in)) {
//						doc.write(out);
//					} catch (IOException iox) {
//						throw new InternalSaleCustomException.ValidationException(iox.getMessage());
//					}
//				}).start();
//				bodyMap.add("files", new MultipartInputStreamFileResource(in, i + ".doc"));
//			}
//			bodyMap.add("merge", "true");
//			RequestEntity<MultiValueMap<String, Object>> request = RequestEntity.post(URI.create(pdfConvertorUrl)).contentType(MediaType.MULTIPART_FORM_DATA).body(bodyMap);
//
//			return restTemplate.exchange(request, byte[].class);
//
//		} else {
//			throw new InternalSaleCustomException.FileContentException("خطایی در هنگام نوشتن فایل اتفاق افتاد");
//		}
//	}


	@GetMapping("/get-by-contract-number/{contractNo}")
	public ResponseEntity<List<RemittanceMasterDto.Info>> getByContractNo(@PathVariable Long contractNo) {
		return ResponseEntity.ok(remittanceService.getByContractNo(contractNo));
	}


	@PostMapping("/trigger-remittance")
	public ResponseEntity<String> triggerRemittanceScheduler() {
		try {
			remittanceScheduler.processRemittanceData();
			return ResponseEntity.ok("Remittance scheduler.sql triggered successfully");
		} catch (Exception e) {
			log.error("Error triggering remittance scheduler.sql manually", e);
			return ResponseEntity.internalServerError()
					.body("Failed to trigger scheduler.sql: " + e.getMessage());
		}
	}

	@PostMapping("/get-lot-number")
	public ResponseEntity<String> getLotNumber(@RequestBody LotNumberRequest request) {
		return ResponseEntity.ok(remittanceDataProvider.getLotnumber(request.getTradeId(), request.getSourceType()));
	}

}