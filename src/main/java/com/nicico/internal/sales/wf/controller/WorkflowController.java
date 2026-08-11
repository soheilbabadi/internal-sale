package com.nicico.internal.sales.wf.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.wf.dto.WorkflowDto;
import com.nicico.internal.sales.wf.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/performa/workflow")
@PreAuthorize("@secUtil.hasAuthority('R_INS_PROFORMA_WF')")
public class WorkflowController {
	private final WorkflowService service;

	@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA_WF')")
	@Operation(summary = "تعریف فرآیند جدید", description = "یک فرآیند جدید در سیستم ایجاد می کند. ورودی شامل اطلاعات کامل فرآیند مانند عنوان، توضیحات، مراحل، نقش های مجاز و سایر تنظیمات مرتبط می باشد")
	@PostMapping
	public ResponseEntity<HttpStatus> save(@RequestBody WorkflowDto.Create request) {
		service.save(request);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Operation(summary = "جستجوی  فرایندها", description = "امکان جستجوی پیشرفته  فرایندها  را فراهم می کندنتیجه به صورت صفحه بندی شده بازگردانده می شود.")
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!org.bouncycastle.util.Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}

	@Operation(summary = "دریافت اطلاعات  فرایند", description = "دریافت اطلاعات  فرایند")
	@GetMapping("/{id}")
	public ResponseEntity<WorkflowDto.Info> get(@PathVariable String id) {
		return ResponseEntity.ok(service.get(id));
	}


	@Operation(summary = "دریافت لیست تمام  فرایندها", description = "لیست خلاصه تمام  فرایندهای فروش داخلی ثبت شده در سیستم BPMS را بازمی گرداند")
	@GetMapping()
	public ResponseEntity<List<Map<String, String>>> getAll() {
		return ResponseEntity.ok(service.getAll());
	}

	@PreAuthorize("@secUtil.hasAuthority('D_INS_PROFORMA_WF')")
	@Operation(summary = "حذف  فرایند", description = "با دریافت شناسه یکتای  فرایند، آن را از سیستم BPMS حذف می کند. این عملیات تنها برای فرآیندهایی قابل انجام است که هیچ نمونه در حال اجرایی نداشته باشند. در صورت وجود نمونه های فعال، حذف با خطا مواجه می شود.")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable String id) {
		service.delete(id);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "دریافت لیست کامل  فرایندها", description = "لیست کامل اطلاعات تمام  فرایندها ثبت شده در سیستم BPMS را شامل عنوان، توضیحات، مراحل، نقش های مجاز و وضعیت بازمی گرداند. این متد برای استفاده در بخش های مدیریتی و گزارش گیری پیشرفته طراحی شده است.")
	@GetMapping("/get-dto-list")
	public ResponseEntity<List<WorkflowDto.Info>> getDtoList() {
		return ResponseEntity.ok(service.getDtoList());
	}
}