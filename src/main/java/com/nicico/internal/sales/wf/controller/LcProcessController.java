package com.nicico.internal.sales.wf.controller;

import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.service.LcProcessService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@Slf4j
@PreAuthorize("@secUtil.hasAuthority('C_INS_LC')")
@RequestMapping("/api/v1/ins/proforma/process-lc")
public class LcProcessController {

	private final LcProcessService lcProcessService;

	@Operation(
			summary = "شروع فرآیند LC",
			description = "با دریافت شناسه پیش فاکتور اصلی (Master ID)،  فرایند ایجاد اعتبار اسنادی را آغاز می کند. این عملیات شامل ایجاد تسک های اولیه، تنظیم وضعیت اولیه LC و شروع جریان کاری مطابق با قوانین و ضوابط تعیین شده می باشد."
	)
	@PostMapping("/start-lc/{masterId}")
	public ResponseEntity<?> startLcProcess(@PathVariable Long masterId) {
		return ResponseEntity.ok(lcProcessService.startLcProcess(masterId));
	}

	@Operation(
			summary = "تایید تسک",
			description = "این متد برای تایید یک تسک در  فرایند LC استفاده می شود. با دریافت اطلاعات تسک شامل شناسه تسک، توضیحات و اقدام انجام شده، عملیات تایید را انجام داده و جریان کاری را به مرحله بعد هدایت می کند. پس از تایید، وضعیت LC به روزرسانی می شود."
	)
	@PutMapping("/approve-task")
	public ResponseEntity<HttpStatus> approveTask(@RequestBody @Valid TaskActionDto taskActionDto) {
		lcProcessService.approveTask(taskActionDto);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "رد تسک", description = "این متد برای رد کردن یک تسک در  فرایند LC استفاده می شود. با دریافت اطلاعات تسک شامل شناسه تسک، توضیحات و علت رد، عملیات رد را انجام داده و جریان کاری را به مرحله قبلی بازمی گرداند یا متوقف می کند. کاربر باید علت رد را به صورت کامل وارد نماید.")
	@PutMapping("/reject-task")
	public ResponseEntity<HttpStatus> rejectTask(@RequestBody @Valid TaskActionDto taskActionDto) {
		lcProcessService.rejectTask(taskActionDto);
		return ResponseEntity.ok().build();
	}

	@Operation(
			summary = "بروزرسانی وضعیت LC",
			description = "این متد به صورت دستی وضعیت تمام اعتبارات اسنادی در حال انجام را با وضعیت جاری  فرایند همگام سازی می کند. در صورت وجود مغایرت بین وضعیت LC و وضعیت تسک ها، این عملیات آن ها را اصلاح و به روزرسانی می نماید."
	)
	@GetMapping("/refresh-status")
	public ResponseEntity<Void> refreshStatus() {
		lcProcessService.refreshLcStatus();
		return ResponseEntity.ok().build();
	}

	@Operation(
			summary = "تشخیص مرحله جاری LC",
			description = "با دریافت شناسه اعتبار اسنادی، مرحله جاری  فرایند آن را شناسایی و بازمی گرداند. این شامل مراحلی مانند 'در انتظار تایید کارشناس'، 'در انتظار تایید مدیریت'، 'تایید نهایی' و سایر گام های فرآیند می باشد."
	)
	@GetMapping("/detect-step/{lcId}")
	public ResponseEntity<String> detectLcStep(@PathVariable Long lcId) {
		return ResponseEntity.ok(lcProcessService.detectLcStep(lcId).name());
	}
}