package com.nicico.internal.sales.wf.controller;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.wf.dto.GroupTaskActionDto;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.service.RemittanceProcessService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;

@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/proforma/process-remittance")
@PreAuthorize("@secUtil.hasAuthority('C_INS_REMITTANCE')")
public class RemittanceProcessController {
	private final RemittanceProcessService remittanceProcessService;

	@Operation(summary = "شروع فرآیند حواله", description = "با دریافت شناسه پیش  فاکتور اصلی (Master ID)،  فرایند مربوط به حواله را آغاز می  کند. این عملیات شامل ایجاد تسک  های اولیه، تنظیم وضعیت اولیه حواله و شروع جریان کاری مطابق با قوانین و ضوابط تعیین شده می  باشد. خروجی شامل اطلاعات نمونه فرآیند ایجاد شده می  باشد.")
	@PostMapping("/start-remittance/{masterId}")
	public ResponseEntity<?> startLcProcess(@PathVariable Long masterId) {
		return ResponseEntity.ok(remittanceProcessService.startProcess(masterId));
	}

	@Operation(summary = "تایید تسک فرآیند حواله", description = "این متد برای تایید یک تسک در  فرایند حواله استفاده می  شود. با دریافت اطلاعات تسک شامل شناسه تسک و توضیحات، عملیات تایید را انجام داده و جریان کاری را به مرحله بعد هدایت می  کند. پس از تایید، وضعیت حواله به  روزرسانی شده و مراحل بعدی فرآیند آغاز می  گردد.")
	@PutMapping("/approve-task")
	public ResponseEntity<HttpStatus> approveTask(@RequestBody @Valid TaskActionDto taskActionDto) throws Exception {
		taskActionDto.setApprove(true);
		remittanceProcessService.approveTask(taskActionDto);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "رد تسک فرآیند حواله", description = "این متد برای رد کردن یک تسک در  فرایند حواله استفاده می  شود. با دریافت اطلاعات تسک شامل شناسه تسک و توضیحات، عملیات رد را انجام داده و جریان کاری را به مرحله قبلی بازمی  گرداند یا متوقف می  کند. کاربر باید علت رد را در توضیحات وارد نماید.")
	@PutMapping("/reject-task")
	public ResponseEntity<HttpStatus> rejectTask(@RequestBody @Valid TaskActionDto taskActionDto) {
		taskActionDto.setApprove(false);
		remittanceProcessService.rejectTask(taskActionDto);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "تایید گروهی تسک  های فرآیند حواله", description = "این متد امکان تایید همزمان چندین تسک را در  فرایند حواله فراهم می  کند. با دریافت لیست شناسه تسک  ها و توضیحات مشترک، تمام تسک  های موجود در لیست را به صورت یکجا تایید می  کند. در صورت بروز خطا در هر یک از تسک  ها، عملیات متوقف شده و خطا به همراه شناسه تسک ناموفق گزارش می  شود.")
	@PutMapping("/approve-task-group")
	public ResponseEntity<?> approveGroupTask(@RequestBody @Valid GroupTaskActionDto taskActionDto) {
		if (taskActionDto == null || taskActionDto.getTaskId() == null || taskActionDto.getTaskId().isEmpty()) {
			return ResponseEntity.badRequest().body("taskId list must not be empty");
		}
		for (String taskId : taskActionDto.getTaskId()) {
			try {
				TaskActionDto dto = new TaskActionDto();
				dto.setTaskId(taskId);
				dto.setApprove(true);
				dto.setComment(taskActionDto.getComment());
				remittanceProcessService.approveTask(dto);
			} catch (Exception ex) {
				log.error("Failed to approve task {}: {}", taskId, ex.getMessage(), ex);
				throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
			}
		}

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "رد گروهی تسک  های فرآیند حواله", description = "این متد امکان رد همزمان چندین تسک را در  فرایند حواله فراهم می  کند. با دریافت لیست شناسه تسک  ها و توضیحات مشترک، تمام تسک  های موجود در لیست را به صورت یکجا رد می  کند. در صورت بروز خطا در هر یک از تسک  ها، عملیات متوقف شده و خطا به همراه شناسه تسک ناموفق گزارش می  شود.")
	@PutMapping("/reject-task-group")
	public ResponseEntity<?> rejectGroupTask(@RequestBody @Valid GroupTaskActionDto taskActionDto) {
		if (taskActionDto == null || taskActionDto.getTaskId() == null || taskActionDto.getTaskId().isEmpty()) {
			return ResponseEntity.badRequest().body("taskId list must not be empty");
		}
		for (String taskId : taskActionDto.getTaskId()) {
			try {
				TaskActionDto dto = new TaskActionDto();
				dto.setTaskId(taskId);
				dto.setApprove(false);
				dto.setComment(taskActionDto.getComment());
				remittanceProcessService.rejectTask(dto);
			} catch (Exception ex) {
				log.error("Failed to reject task {}: {}", taskId, ex.getMessage(), ex);
				throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
			}
		}
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "بروزرسانی وضعیت حواله  ها", description = "این متد به صورت دستی وضعیت تمام حواله  های در حال انجام را با وضعیت جاری  فرایند همگام  سازی می  کند. در صورت وجود مغایرت بین وضعیت حواله و وضعیت تسک  ها، این عملیات آن  ها را اصلاح و به  روزرسانی می  نماید. این قابلیت برای رفع مشکلات همگام  سازی و تصحیح وضعیت  های ناهمخوان در فرآیندهای حواله استفاده می  شود.")
	@GetMapping("/refresh-status")
	public ResponseEntity<Void> refreshStatus() {
		remittanceProcessService.refreshRemittanceStatus();
		return ResponseEntity.ok().build();
	}
}