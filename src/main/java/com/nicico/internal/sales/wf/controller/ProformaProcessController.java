package com.nicico.internal.sales.wf.controller;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.wf.dto.GroupTaskActionDto;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.service.ProformaProcessService;
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
@RequestMapping("/api/v1/ins/proforma/proforma-process")
@PreAuthorize("@secUtil.hasAuthority('C_INS_PROFORMA')")
public class ProformaProcessController {
	private final ProformaProcessService proformaProcessService;

	@Operation(summary = "شروع فرآیند پیش فاکتور", description = "با دریافت شناسه پیش فاکتور اصلی (Master ID)،  فرایند مربوط به پیش فاکتور را آغاز می کند. این عملیات شامل ایجاد تسک های اولیه، تنظیم وضعیت اولیه پیش فاکتور و شروع جریان کاری مطابق با قوانین و ضوابط تعیین شده می باشد. خروجی شامل اطلاعات نمونه فرآیند ایجاد شده می باشد.")
	@PutMapping("/start-proforma-master/{masterId}")
	public ResponseEntity<ProcessInstance> startPerformaProcess(@PathVariable long masterId) {
		return ResponseEntity.ok(proformaProcessService.startProformaProcess(masterId));
	}

	@Operation(summary = "راه اندازی مجدد فرآیندهای ناموفق", description = "این متد به صورت خودکار فرآیندهایی که در زمان شروع با خطا مواجه شده اند را شناسایی و مجدداً راه اندازی می کند. این عملیات برای بازیابی خودکار فرآیندهای معیوب و جلوگیری از وقفه در جریان کاری استفاده می شود.")
	@PostMapping("/start-failed-process")
	public ResponseEntity<Void> startFailedProcess() {
		proformaProcessService.startFailedProcess();
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "رد تسک فرآیند پیش فاکتور", description = "این متد برای رد کردن یک تسک در  فرایند پیش فاکتور استفاده می شود. با دریافت اطلاعات تسک شامل شناسه تسک، توضیحات و علت رد، عملیات رد را انجام داده و جریان کاری را به مرحله قبلی بازمی گرداند یا متوقف می کند. کاربر باید علت رد را به صورت کامل و دقیق وارد نماید.")
	@PutMapping("/reject-task")
	public ResponseEntity<HttpStatus> rejectTask(@RequestBody @Valid TaskActionDto taskActionDto) throws Exception {
		proformaProcessService.rejectTask(taskActionDto);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "تایید تسک فرآیند پیش فاکتور", description = "این متد برای تایید یک تسک در  فرایند پیش فاکتور استفاده می شود. با دریافت اطلاعات تسک شامل شناسه تسک، توضیحات و اقدام انجام شده، عملیات تایید را انجام داده و جریان کاری را به مرحله بعد هدایت می کند. پس از تایید، وضعیت پیش فاکتور به روزرسانی می شود.")
	@PutMapping("/approve-task")
	public ResponseEntity<HttpStatus> approveTask(@RequestBody @Valid TaskActionDto taskActionDto) throws Exception {
		proformaProcessService.approveTask(taskActionDto);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "تایید گروهی تسک های فرآیند پیش فاکتور", description = "این متد امکان تایید همزمان چندین تسک را در  فرایند پیش فاکتور فراهم می کند. با دریافت لیست شناسه تسک ها و توضیحات مشترک، تمام تسک های موجود در لیست را به صورت یکجا تایید می کند. در صورت بروز خطا در هر یک از تسک ها، عملیات متوقف شده و خطا گزارش می شود. این متد برای افزایش بهره وری در تایید انبوه تسک ها طراحی شده است.")
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
				proformaProcessService.approveTask(dto);
			} catch (Exception ex) {
				log.error("Failed to approve task {}: {}", taskId, ex.getMessage(), ex);
				throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
			}
		}

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "رد گروهی تسک های فرآیند پیش فاکتور", description = "این متد امکان رد همزمان چندین تسک را در  فرایند پیش فاکتور فراهم می کند. با دریافت لیست شناسه تسک ها و توضیحات مشترک، تمام تسک های موجود در لیست را به صورت یکجا رد می کند. در صورت بروز خطا در هر یک از تسک ها، عملیات متوقف شده و خطا گزارش می شود. این متد برای افزایش بهره وری در رد انبوه تسک ها طراحی شده است.")
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
				proformaProcessService.rejectTask(dto);
			} catch (Exception ex) {
				throw new InternalSaleCustomException.BpmsClientException("خطا در اتصال به کارتابل", new ArrayList<>(Collections.singletonList(ex.getMessage())));
			}
		}

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "بروزرسانی وضعیت پیش فاکتورها", description = "این متد به صورت دستی وضعیت تمام پیش فاکتورهای در حال انجام را با وضعیت جاری  فرایند همگام سازی می کند. در صورت وجود مغایرت بین وضعیت پیش فاکتور و وضعیت تسک ها، این عملیات آن ها را اصلاح و به روزرسانی می نماید. این قابلیت برای رفع مشکلات همگام سازی و تصحیح وضعیت های ناهمخوان استفاده می شود.")
	@GetMapping("/refresh-status")
	public ResponseEntity<Void> refreshStatus() {
		proformaProcessService.refreshProformaStatus();
		return ResponseEntity.ok().build();
	}
}