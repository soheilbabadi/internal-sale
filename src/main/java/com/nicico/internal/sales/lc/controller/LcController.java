package com.nicico.internal.sales.lc.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.EOperator;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.lc.dto.LcAuditDto;
import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.lc.dto.LcFilesDto;
import com.nicico.internal.sales.lc.dto.request.LcCancelRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateAcceptedLcRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateStartedLcRequest;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.nosa.LcNosaCodeService;
import com.nicico.internal.sales.lc.service.LcService;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/// /@Tag(name = "PMS LC", description = "مدیریت ارتباط با سیستم PMS برای ایجاد LC")
@PreAuthorize("@secUtil.hasAuthority('R_INS_LC')")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/lc")
public class LcController {
	private final LcService lcService;
	private final LcNosaCodeService lcNosaCodeService;

	@Operation(
			summary = "به روزرسانی اطلاعات LC",
			description = "این متد اطلاعات اعتبار اسنادی (LC) را در وضعیت شروع شده به روزرسانی می کند. شامل اطلاعات پایه، تاریخ ها و مبالغ LC می باشد."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	@PutMapping("/update-lc-info")
	public ResponseEntity<LcDto.Info> updateLc(@RequestBody UpdateStartedLcRequest lcRequest) {
		return ResponseEntity.ok(lcService.updateStartedLc(lcRequest));
	}

	@Operation(
			summary = "به روزرسانی فایل های LC",
			description = "با دریافت لیست فایل های ضمیمه، اطلاعات فایل های اعتبار اسنادی را به روزرسانی می کند. این شامل فایل های پیوست، تصاویر و مدارک مورد نیاز می باشد."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	@PutMapping(value = "/update-lc-files")
	public ResponseEntity<LcFilesDto> updateLcFiles(@RequestBody LcFilesDto lcFiles) {
		return ResponseEntity.ok(lcService.updateLcFiles(lcFiles));
	}

	@Operation(
			summary = "محاسبه تاریخ سررسید اعتبار LC",
			description = "با دریافت اطلاعات LC، تاریخ سررسید انقضای اعتبار اسنادی را بر اساس قوانین و ضوابط محاسبه و بازمی گرداند."
	)
	@PostMapping("/calculate-lc-expire-date")
	public ResponseEntity<Date> calculateSettlementDueDate(@RequestBody UpdateStartedLcRequest lcRequest) {
		return ResponseEntity.ok(lcService.calculateExpireDate(lcRequest));
	}

	@Operation(
			summary = "جستجوی اعتبارات اسنادی",
			description = "این متد امکان جستجوی پیشرفته اعتبارات اسنادی را با استفاده از معیارهای مختلف فراهم می کند. شامل فیلتر بر اساس وضعیت، تاریخ، مبلغ و سایر فیلدهای مرتبط می باشد."
	)
	@PostMapping("/search")
	public ResponseEntity<SearchDTO.SearchRs<LcDto.Info>> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (criteria != null && !criteria.isEmpty()) {
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}

		return ResponseEntity.ok(lcService.search(searchRq));
	}

	@Operation(
			summary = "لیست LCهای قابل به روزرسانی",
			description = "لیست اعتبارات اسنادی که وضعیت آن ها پذیرفته شده (ACCEPTED) بوده و قابلیت ویرایش و به روزرسانی دارند. این لیست برای نمایش به کاربران مجاز جهت انجام تغییرات ارائه می شود."
	)
	@PostMapping("/list-updatable")
	public ResponseEntity<SearchDTO.SearchRs<LcDto.Info>> getUpdatable(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		if (searchRq == null) {
			searchRq = new SearchDTO.SearchRq();
		}
		SearchDTO.CriteriaRq statusCriteria = new SearchDTO.CriteriaRq();
		statusCriteria.setFieldName("workflowApproveStatus");
		statusCriteria.setOperator(EOperator.equals);
		statusCriteria.setValue(WorkflowApproveStatus.ACCEPTED);
		searchRq.getCriteria().getCriteria().add(statusCriteria);
		return ResponseEntity.ok(lcService.search(searchRq));
	}

	@Operation(
			summary = "دریافت اطلاعات LC",
			description = "با دریافت شناسه یکتای اعتبار اسنادی، اطلاعات کامل آن شامل جزئیات پایه، وضعیت، تاریخ ها و اطلاعات مرتبط با فرآیند را بازمی گرداند."
	)
	@GetMapping("/lc-data/{id}")
	public ResponseEntity<LcDto.Info> getLcData(@PathVariable Long id) {
		return ResponseEntity.ok(lcService.getLcData(id));
	}

	@Operation(
			summary = "دریافت LCهای مرتبط با پیش فاکتور",
			description = "با دریافت شناسه پیش فاکتور اصلی، لیست تمام اعتبارات اسنادی مرتبط با آن را بازمی گرداند. این شامل LCهای ایجاد شده بر اساس آیتم های پیش فاکتور می باشد."
	)
	@GetMapping("/get-by-proforma-master-id/{proformaMasterId}")
	public ResponseEntity<List<LcDto.Info>> getAllLcDataByProformaMasterId(@PathVariable Long proformaMasterId) {
		return ResponseEntity.ok(lcService.getAllLcDataByProformaMasterId(proformaMasterId));
	}

	@Operation(
			summary = "دریافت LCهای مرتبط با شناسه فرآیند",
			description = "با دریافت شناسه نمونه فرآیند (Process Instance ID)، لیست تمام اعتبارات اسنادی مرتبط با آن  فرایند را بازمی گرداند."
	)
	@GetMapping("/get-by-process-instance-id/{processInstanceId}")
	public ResponseEntity<List<LcDto.Info>> getAllLcDataByProcessInstanceId(@PathVariable String processInstanceId) {
		return ResponseEntity.ok(lcService.getAllLcDataByProcessInstanceId(processInstanceId));
	}

	@Operation(
			summary = "بررسی نیاز به فایل ارسال",
			description = "بررسی می کند که آیا برای اعتبار اسنادی مشخص، نیاز به ارسال فایل (Dispatch File) وجود دارد یا خیر. نتیجه به صورت boolean بازگردانده می شود."
	)
	@GetMapping("is-required-dispatch/{lcId}")
	public ResponseEntity<Boolean> isRequireDispatch(@PathVariable Long lcId) {
		return ResponseEntity.ok(lcService.getLcData(lcId).getRequireDispatchFile());
	}

	@Operation(
			summary = "دریافت LCهای ناموفق",
			description = "لیست اعتبارات اسنادی که در فرآیند ایجاد یا به روزرسانی با خطا مواجه شده اند را به صورت صفحه بندی شده بازمی گرداند. این لیست برای بررسی و رفع خطاهای سیستمی استفاده می شود."
	)
	@GetMapping("/get-failed-lc")
	public ResponseEntity<List<LcDto.Info>> getFailed(@RequestParam(defaultValue = "0") Integer pageIndex, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "id") String sortColumn) {
		Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(sortColumn));
		return ResponseEntity.ok(lcService.getFailedLc(pageable, Sort.by(sortColumn)));
	}

	@Operation(
			summary = "دریافت LC بر اساس شناسه جزییات پیش فاکتور",
			description = "با دریافت شناسه جزییات پیش فاکتور (Proforma Detail ID)، اطلاعات اعتبار اسنادی مرتبط با آن را بازمی گرداند. هر جزییات پیش فاکتور می تواند یک LC داشته باشد."
	)
	@GetMapping("/get-by-proforma-detail-id/{detailId}")
	public ResponseEntity<LcDto.Info> getLcDataByProformaDetailId(@PathVariable Long detailId) {
		return ResponseEntity.ok(lcService.getByProformaDetailId(detailId));
	}

	@Operation(
			summary = "دریافت کد نوسا بانک",
			description = "با دریافت شناسه بانک، کد نوسا (Nosa Code) مربوط به آن بانک را بازمی گرداند. این کد برای ارتباط با سامانه های بانکی مورد استفاده قرار می گیرد."
	)
	@GetMapping("/get-nosa-code/{bankId}")
	public ResponseEntity<String> getNosaCode(@PathVariable Long bankId) {
		return ResponseEntity.ok(lcNosaCodeService.getNosaCode(bankId));
	}

	@Operation(
			summary = "به روزرسانی LC پذیرفته شده",
			description = "این متد اطلاعات اعتبار اسنادی را در وضعیت پذیرفته شده (ACCEPTED) به روزرسانی می کند. شامل تغییرات در مبالغ، تاریخ ها و اطلاعات بانکی می باشد. ورودی باید معتبر و کامل باشد."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	@PutMapping(value = "/update-accepted-lc", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LcDto.Info> updateLc(@Valid @RequestBody UpdateAcceptedLcRequest updateAcceptedLcRequest) {
		return ResponseEntity.ok(lcService.updateCurrentAcceptedLc(updateAcceptedLcRequest));
	}

	@Operation(
			summary = "تاریخچه تغییرات LC",
			description = "لیست کامل تاریخچه تغییرات اعتبار اسنادی را شامل تمام ویرایش ها، به روزرسانی ها و تغییرات وضعیت به همراه زمان و کاربر انجام دهنده بازمی گرداند."
	)
	@GetMapping("/audit-history/{lcId}")
	public ResponseEntity<List<LcAuditDto>> getAuditHistory(@PathVariable Long lcId) {
		return ResponseEntity.ok(lcService.getAuditHistory(lcId));
	}

	@Operation(
			summary = "ارسال ایمیل تایید تسویه",
			description = "برای اعتبار اسنادی مشخص، ایمیل تایید تسویه حساب به ذینفع و کارگزار ارسال می شود. این عملیات پس از اتمام فرآیند تسویه و تایید نهایی انجام می گردد."
	)
//	@PreAuthorize("@secUtil.hasAuthority('C_INS_SEND_NOTIFICATION')")
	@PostMapping("/send-reckoning-lc/{lcId}")
	public ResponseEntity<Void> sendReckoningEmail(@PathVariable Long lcId) {
		lcService.sendReckoningEmail(lcId);
		return ResponseEntity.ok().build();
	}

	@Operation(
			summary = "LCهای بدون ایمیل تایید تسویه",
			description = "لیست اعتبارات اسنادی که نامه تایید تسویه برای آن ها ارسال نشده و موعد ارسال آن نیز گذشته است. این لیست برای پیگیری و ارسال ایمیل های معوق استفاده می شود."
	)
	@GetMapping("/get-unsent-reckoning")
	public ResponseEntity<List<LcDto.Info>> findUnsentReckoning() {
		return ResponseEntity.ok(lcService.findUnsentReckoning());
	}

	@Operation(
			summary = "LCهای آماده برای تایید تسویه",
			description = "لیست اعتبارات اسنادی که شرایط ارسال تایید تسویه را دارند و آماده ارسال ایمیل تاییدیه به ذینفع و کارگزار می باشند. این خروجی به صورت SearchRs برگردانده می شود تا فیلتر و مرتب سازی نیز ساده باشد."
	)
	@GetMapping("/get-ready-reckoning")
	public ResponseEntity<SearchDTO.SearchRs<LcDto.Info>> findReadyReckoning(@RequestParam(required = false) MultiValueMap<String, String> criteria) {
		SearchDTO.SearchRq searchRq = null;
		if (criteria != null && !criteria.isEmpty()) {
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}

		return ResponseEntity.ok(lcService.findReadyReckoning(searchRq));
	}

	@Operation(
			summary = "جستجو در LCهای آماده برای تایید تسویه",
			description = "امکان جستجو، فیلتر و مرتب سازی در LCهای آماده برای تایید تسویه را فراهم می کند. علاوه بر فیلترهای ارسالی کاربر، شرط contractDate بزرگتر از 1405/03/01 نیز به صورت پیش فرض اعمال می شود."
	)
	@PostMapping("/get-ready-reckoning")
	public ResponseEntity<SearchDTO.SearchRs<LcDto.Info>> findReadyReckoning(@RequestBody(required = false) SearchDTO.SearchRq searchRq,
	                                                                         @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (criteria != null && !criteria.isEmpty()) {
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		}

		return ResponseEntity.ok(lcService.findReadyReckoning(searchRq));
	}

	@Operation(
			summary = "ابطال اعتبار اسنادی",
			description = "با دریافت درخواست ابطال شامل شناسه LC و دلیل ابطال، عملیات لغو اعتبار اسنادی انجام می شود. پس از ابطال، وضعیت LC به 'لغو شده' تغییر می یابد و عملیات بعدی غیرفعال می شود."
	)
	@PreAuthorize("@secUtil.hasAuthority('D_INS_LC')")
	@PutMapping("/cancel-lc")
	public ResponseEntity<?> cancelLc(@RequestBody LcCancelRequest request) {
		lcService.cancel(request);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(
			summary = "لیست دلایل ابطال اعتبار اسنادی",
			description = "لیست تمام دلایل قابل قبول برای ابطال اعتبار اسنادی را شامل کلید و مقدار (به فارسی) بازمی گرداند. این لیست برای نمایش در فرم های ابطال مورد استفاده قرار می گیرد."
	)
	@PreAuthorize("@secUtil.hasAuthority('D_INS_LC')")
	@GetMapping("/get-cancellation-reasons")
	public List<Map<String, String>> getLcCancellationReasonsAsEnums() {
		List<Map<String, String>> result = new ArrayList<>();
		for (LcCancellationReason reason : LcCancellationReason.values()) {
			Map<String, String> map = new HashMap<>();
			map.put("key", reason.name());
			map.put("value", reason.getValue());
			result.add(map);
		}
		return result;
	}

	@Operation(
			summary = "تولید محتوای ایمیل کارگزار",
			description = "با دریافت شناسه LC، محتوای ایمیل مخصوص کارگزار شامل اطلاعات کامل اعتبار اسنادی، تاریخ ها، مبالغ و شرایط را تولید و به صورت رشته متنی بازمی گرداند."
	)
	@GetMapping("/get-broker-email-content/{lcId}")
	public ResponseEntity<String> generateLcBrokerEmailContent(@PathVariable Long lcId) {
		return ResponseEntity.ok(lcService.generateLcBrokerEmailContent(lcId));
	}

	@Operation(
			summary = "دریافت جزییات تاریخچه تسک ها",
			description = "لیست کامل تاریخچه تسک های مربوط به اعتبار اسنادی را شامل تمام فعالیت های انجام شده در  فرایند همراه با زمان و وضعیت هر تسک بازمی گرداند."
	)
	@GetMapping("/get-task-history-detail/{lcId}")
	public ResponseEntity<?> getLcHistoryDetail(@PathVariable Long lcId) {
		return ResponseEntity.ok(lcService.getLcHistoryDetail(lcId));
	}

	@Operation(
			summary = "به روزرسانی تاییدیه دریافت LC",
			description = "وضعیت تایید دریافت اعتبار اسنادی را برای LC مشخص به روزرسانی می کند. این عملیات زمانی انجام می شود که کاربر دریافت مدارک و اطلاعات LC را تایید نماید."
	)
	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	@PutMapping("/update-acknowledgment/{lcId}")
	public ResponseEntity<Void> updateLcAcknowledgment(@PathVariable Long lcId) {
		lcService.updateLcAcknowledgment(lcId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(
			summary = "بررسی وضعیت تاییدیه دریافت LC",
			description = "وضعیت تایید دریافت اعتبار اسنادی را بررسی و نتیجه را بازمی گرداند. این متد مشخص می کند که آیا کاربر مدارک و اطلاعات LC را تایید کرده است یا خیر."
	)
	@GetMapping("/get-acknowledgment/{lcId}")
	public ResponseEntity<?> determineAcknowledgment(@PathVariable Long lcId) {
		return ResponseEntity.ok(lcService.determineAcknowledgment(lcId));
	}

	@Operation(
			summary = "گزارش تسک های کاربر",
			description = "لیست تمام تسک های انجام شده توسط کاربران برای اعتبار اسنادی مشخص را شامل فعالیت ها، تاریخ انجام و وضعیت هر تسک بازمی گرداند. این گزارش برای پیگیری عملکرد کاربران استفاده می شود."
	)
	@GetMapping("/get-task-history/{lcId}")
	public ResponseEntity<?> getUserTasksReport(@PathVariable Long lcId) {
		return ResponseEntity.ok(lcService.getUserTasksReport(lcId));
	}

	@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
	@PutMapping("/update-all-acknowledgment")
	public ResponseEntity<Void> updateAllLcAcknowledgment() {
		lcService.updateAllAcknowledgments();
		return new ResponseEntity<>(HttpStatus.OK);
	}


}