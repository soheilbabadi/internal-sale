package com.nicico.internal.sales.wf.controller;

import com.nicico.copper.common.domain.criteria.NICICOCriteria;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.wf.dto.ProcessUserAccessRequest;
import com.nicico.internal.sales.wf.service.ProcessUserAccessService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@PreAuthorize("@secUtil.hasAuthority('R_INS_PROFORMA_WF')")
@RequestMapping("/api/v1/ins/proforma/process-user-access")
public class ProcessUserAccessController {
	private final ProcessUserAccessService service;

	@Operation(summary = "دریافت دسترسی های کاربر جاری", description = "لیست کامل دسترسی های کاربر احراز هویت شده به فرآیندهای مختلف را بازمی گرداند. این شامل مجوزهای مشاهده، ایجاد، ویرایش و حذف برای هر فرآیند می باشد. اطلاعات بر اساس نقش و مجوزهای تعریف شده برای کاربر در سیستم محاسبه می شود.")
	@GetMapping("/my-access")
	public ResponseEntity<?> getMyAccess() {
		return ResponseEntity.ok(service.getMyAccess());
	}

	@Operation(summary = "دریافت لیست کاربران", description = "لیست کاربران سیستم را بر اساس نام کامل (Full Name) جستجو و بازمی گرداند. در صورت عدم ارسال پارامتر، تمام کاربران فعال سیستم نمایش داده می شوند. این متد برای انتخاب کاربران در فرم های مدیریت دسترسی استفاده می شود.")
	@GetMapping("/users")
	public ResponseEntity<?> getUserList(@RequestParam(required = false) String fullName) {
		return ResponseEntity.ok(service.getUserList(fullName));
	}

	@Operation(summary = "حذف دسترسی بر اساس عنوان فرآیند", description = "با دریافت عنوان فرآیند (Process Title)، تمام دسترسی های تعریف شده برای آن فرآیند را حذف می کند. این عملیات برای پاکسازی دسترسی های یک فرآیند خاص و یا زمانی که فرآیندی از سیستم حذف می شود، مورد استفاده قرار می گیرد.")
	@PreAuthorize("@secUtil.hasAuthority('D_INS_PROFORMA_WF')")
	@DeleteMapping("/delete-by_title/{processTitle}")
	public ResponseEntity<?> delete(@PathVariable String processTitle) {
		service.delete(processTitle);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "حذف دسترسی بر اساس شناسه", description = "با دریافت شناسه یکتای دسترسی، یک رکورد دسترسی مشخص را حذف می کند. این عملیات برای حذف دسترسی یک کاربر خاص از یک فرآیند مشخص استفاده می شود.")
	@PreAuthorize("@secUtil.hasAuthority('D_INS_PROFORMA_WF')")
	@DeleteMapping("{id}")
	public ResponseEntity<?> deleteById(@PathVariable Long id) {
		service.deleteById(id);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "ایجاد یا به روزرسانی دسترسی کاربر به فرآیند", description = "این متد برای تعریف یا ویرایش دسترسی یک کاربر به یک  فرایند استفاده می شود. ورودی شامل شناسه کاربر، عنوان فرآیند و سطح دسترسی (مشاهده، ایجاد، ویرایش، حذف) می باشد. در صورت وجود رکورد قبلی، به روزرسانی و در غیر این صورت، رکورد جدید ایجاد می شود.")
	@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA_WF')")
	@PostMapping("/save")
	public ResponseEntity<?> save(@RequestBody ProcessUserAccessRequest request) {
		return ResponseEntity.ok(service.save(request));
	}

	@Operation(summary = "جستجوی دسترسی های کاربران به فرآیندها", description = "این متد امکان جستجوی پیشرفته دسترسی های کاربران به  فرایندها را فراهم می کند. شامل فیلتر بر اساس عنوان فرآیند، شناسه کاربر، نام کاربر، سطح دسترسی و سایر معیارهای مرتبط می باشد. نتیجه به صورت صفحه بندی شده بازگردانده می شود.")
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody(required = false) SearchDTO.SearchRq searchRq, @RequestParam(required = false) MultiValueMap<String, String> criteria) {
		if (!Arrays.isNullOrEmpty(criteria.keySet().toArray()))
			searchRq = SearchUtil.createSearchRq(NICICOCriteria.of(criteria));
		return ResponseEntity.ok(service.search(searchRq));
	}
}