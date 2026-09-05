package com.nicico.internal.sales.crm.controller;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.crm.dto.CrmApprovedCompanyDto;
import com.nicico.internal.sales.crm.dto.LcWithProformaDto;
import com.nicico.internal.sales.crm.service.CrmHistoryService;
import com.nicico.internal.sales.fms.service.FmsDocumentService;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.history.dto.HistoryExtractMasterDto;
import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.notification.dto.MultipartInputStreamFileResource;
import com.nicico.internal.sales.proforma.dto.ProformaMasterDTO;
import com.nicico.internal.sales.proforma.dto.ProformaResponseDto;
import com.nicico.internal.sales.remittance.dto.RemittanceMasterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/crm")
@Slf4j
public class CrmController {

private final CrmHistoryService historyService;
private final FmsDocumentService fmsDocumentService;
private final RestTemplate restTemplate;
@Value("${nicico.pdf-api}")
private String pdfConvertorUrl;

@Operation(summary = "دریافت لیست شرکت های تأییدشده", description = "فهرست شرکت هایی که در CRM تایید شده اند را به همراه شناسه ها و کدهای مرجع بازمی گرداند.")
@GetMapping("/approved-company-requests")
public ResponseEntity<List<CrmApprovedCompanyDto>> getApprovedCompanyRequests() {
return ResponseEntity.ok(historyService.getApprovedCompanyRequests());
}

@Operation(summary = "جستجوی تاریخچه با فیلترهای پیش فرض", description = "تاریخچه خلاصه وضعیت قرارداد، پیش فاکتور، اعتبار اسنادی و حواله را با فیلترهای پیش فرض برمی گرداند.")
@GetMapping("/filtered-search")
public ResponseEntity<SearchDTO.SearchRs<HistoryExtractMasterDto.Info>> getFilteredHistory() {
return ResponseEntity.ok(historyService.getFilteredHistory());
}

@Operation(summary = "لیست پیش فاکتورها", description = "فهرست پیش فاکتورهای موجود را برای نمایش در CRM برمی گرداند.")
@GetMapping("/proforma-list")
public ResponseEntity<SearchDTO.SearchRs<ProformaMasterDTO.Info>> getProformaList() {
return ResponseEntity.ok(historyService.getProformaList());
}

@Operation(summary = "مشاهده جزئیات پیش فاکتور", description = "جزئیات کامل پیش فاکتور شامل اطلاعات master و آیتم های جزئیات را بر اساس شناسه برمی گرداند.")
@GetMapping("/proforma-detail/{id}")
public ResponseEntity<ProformaResponseDto> getProformaDetailById(
@Parameter(description = "شناسه پیش فاکتور", required = true, example = "45")
@PathVariable long id) {
return ResponseEntity.ok(historyService.getProformaDetailById(id));
}

@Operation(summary = "جستجوی اعتبار اسنادی", description = "اعتبارات اسنادی مرتبط با پیش فاکتورها را با معیارهای جستجو، صفحه بندی و فیلتر برمی گرداند.")
@PostMapping("/lc-search")
public ResponseEntity<SearchDTO.SearchRs<LcWithProformaDto.Info>> searchLc(@RequestBody SearchDTO.SearchRq request) {
return ResponseEntity.ok(historyService.searchLc(request));
}

@Operation(summary = "اعتبارات اسنادی مرتبط با پیش فاکتور", description = "لیست تمام اعتبارات اسنادی ثبت شده برای یک پیش فاکتور اصلی را برمی گرداند.")
@GetMapping("/lc-by-proforma-master-id/{proformaMasterId}")
public ResponseEntity<List<LcDto.Info>> getAllLcDataByProformaMasterId(
@Parameter(description = "شناسه پیش فاکتور اصلی", required = true, example = "45")
@PathVariable Long proformaMasterId) {
return ResponseEntity.ok(historyService.getAllLcDataByProformaMasterId(proformaMasterId));
}

@Operation(summary = "جستجوی پیش فاکتور", description = "پیش فاکتورها را با معیارهای جستجو و صفحه بندی برای CRM واکشی می کند.")
@PostMapping("/proforma-search")
public ResponseEntity<SearchDTO.SearchRs<ProformaMasterDTO.Info.Info>> searchProforma(@RequestBody SearchDTO.SearchRq request) {
return ResponseEntity.ok(historyService.searchProforma(request));
}

@Operation(summary = "جستجوی حواله", description = "حواله ها را با قابلیت صفحه بندی و فیلتر برای مصرف CRM جستجو می کند.")
@PostMapping("/remittance-search")
public ResponseEntity<SearchDTO.SearchRs<RemittanceMasterDto.Info>> remittanceSearch(@RequestBody SearchDTO.SearchRq request) {
return ResponseEntity.ok(historyService.searchRemittance(request));
}

@Operation(summary = "حواله های مرتبط با پیش فاکتور", description = "تمام حواله های مرتبط با یک پیش فاکتور اصلی را بازمی گرداند.")
@GetMapping("/remittance-by-proforma-master-id/{proformaMasterId}")
public ResponseEntity<List<RemittanceMasterDto.Info>> getAllByProformaMasterId(
@Parameter(description = "شناسه پیش فاکتور اصلی", required = true, example = "45")
@PathVariable Long proformaMasterId) {
return ResponseEntity.ok(historyService.getAllByProformaMasterId(proformaMasterId));
}

@Operation(summary = "جزئیات حواله", description = "اطلاعات کامل یک حواله را بر اساس شناسه آن بازمی گرداند.")
@GetMapping("/remittance-by-id/{id}")
public ResponseEntity<RemittanceMasterDto> getRemittanceDetailById(
@Parameter(description = "شناسه حواله", required = true, example = "67")
@PathVariable Long id) {
return ResponseEntity.ok(historyService.getRemittanceDetailById(id));
}

@Operation(summary = "خروجی Word حواله", description = "فایل Word حواله را به صورت دانلودی برمی گرداند.")
@GetMapping("/remittance-export-doc/{id}")
public ResponseEntity<byte[]> exportRemittanceDoc(
@Parameter(description = "شناسه حواله", required = true, example = "67")
@PathVariable long id) {
byte[] doc = historyService.exportRemittanceDoc(id);
return ResponseEntity.ok()
.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"remittance_" + id + ".docx\"")
.contentType(MediaType.APPLICATION_OCTET_STREAM)
.contentLength(doc.length)
.body(doc);
}

@Operation(summary = "خروجی Word پیش فاکتور", description = "فایل Word پیش فاکتور را به صورت دانلودی برمی گرداند.")
@GetMapping("/proforma-export-doc/{id}")
public ResponseEntity<byte[]> exportProformaDoc(
@Parameter(description = "شناسه پیش فاکتور", required = true, example = "45")
@PathVariable long id) {
byte[] doc = historyService.exportProformaDoc(id);
return ResponseEntity.ok()
.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"proforma_" + id + ".docx\"")
.contentType(MediaType.APPLICATION_OCTET_STREAM)
.contentLength(doc.length)
.body(doc);
}

@Operation(summary = "خروجی PDF حواله", description = "فایل PDF حواله را پس از تبدیل فایل Word مربوطه برمی گرداند.")
@GetMapping("/remittance-export-pdf/{id}")
public ResponseEntity<byte[]> exportRemittancePdf(
@Parameter(description = "شناسه حواله", required = true, example = "67")
@PathVariable long id) {
byte[] pdfBytes = fmsDocumentService.getRemittancePdfBytes(id);

if (pdfBytes == null) {
return ResponseEntity.notFound().build();
}

return ResponseEntity.ok()
.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"remittance_" + id + ".pdf\"")
.contentType(MediaType.APPLICATION_PDF)
.contentLength(pdfBytes.length)
.body(pdfBytes);
}

@Operation(summary = "خروجی PDF پیش فاکتور", description = "فایل PDF پیش فاکتور را پس از تبدیل فایل Word مربوطه برمی گرداند.")
@GetMapping("/proforma-export-pdf/{id}")
public ResponseEntity<byte[]> exportProformaPdf(
@Parameter(description = "شناسه پیش فاکتور", required = true, example = "45")
@PathVariable long id) {
byte[] pdfBytes = fmsDocumentService.getProformaPdfBytes(id);

if (pdfBytes == null) {
return ResponseEntity.notFound().build();
}

return ResponseEntity.ok()
.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"proforma_" + id + ".pdf\"")
.contentType(MediaType.APPLICATION_PDF)
.contentLength(pdfBytes.length)
.body(pdfBytes);
}

private ResponseEntity<byte[]> convertDocListToPdf(List<XWPFDocument> docList) {
MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
if (!docList.isEmpty()) {
int i = 0;
for (XWPFDocument doc : docList) {
i++;
PipedInputStream in = new PipedInputStream();
new Thread(() -> {
try (PipedOutputStream out = new PipedOutputStream(in)) {
doc.write(out);
} catch (IOException iox) {
throw new InternalSaleCustomException.ValidationException(iox.getMessage());
}
}).start();
bodyMap.add("files", new MultipartInputStreamFileResource(in, i + ".doc"));
}
bodyMap.add("merge", "true");
RequestEntity<MultiValueMap<String, Object>> request = RequestEntity.post(URI.create(pdfConvertorUrl)).contentType(MediaType.MULTIPART_FORM_DATA).body(bodyMap);

return restTemplate.exchange(request, byte[].class);

} else {
throw new InternalSaleCustomException.FileContentException("خطایی در هنگام نوشتن فایل اتفاق افتاد");
}
}
}
