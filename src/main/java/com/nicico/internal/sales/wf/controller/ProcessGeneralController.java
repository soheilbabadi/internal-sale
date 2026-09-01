package com.nicico.internal.sales.wf.controller;

import com.nicico.bpmsclient.model.flowable.process.ProcessInsHistoryDTO;
import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.GridDTO;
import com.nicico.bpmsclient.model.flowable.task.TaskDetail;
import com.nicico.bpmsclient.model.flowable.task.TaskInfo;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.bpmsclient.model.request.TaskSearchDto;
import com.nicico.internal.sales.wf.dto.WorkflowDto;
import com.nicico.internal.sales.wf.enums.*;
import com.nicico.internal.sales.wf.service.ProcessService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/proforma/process-general")
public class ProcessGeneralController {

	private final ProcessService processService;

	@Operation(summary = "دریافت تسک های یک فرآیند", description = "لیست تسک های مرتبط با یک نمونه فرآیند را برمی گرداند")
	@GetMapping("/instance-tasks/{processInstanceId}")
	public ResponseEntity<List<TaskInfo>> getInstanceTasks(@PathVariable String processInstanceId) {
		return ResponseEntity.ok(processService.getInstanceTasks(processInstanceId));
	}

	@Operation(summary = "دریافت متغیرهای فرآیند", description = "لیست متغیرهای تعریف شده برای نوع فرآیند موردنظر را برمی گرداند")
	@GetMapping("/get-process-variable/{processTitle}")
	public ResponseEntity<Map<String, String>> getProformaProcessVariable(@PathVariable String processTitle) {

		Supplier<Stream<? extends Enum<?>>> enumSupplier;
		switch (processTitle.toUpperCase()) {
			case "PREINVOICE":
				enumSupplier = () -> Arrays.stream(ProformaProcessVariable.values());
				break;
			case "LC":
				enumSupplier = () -> Arrays.stream(LcProcessVariable.values());
				break;
			case "REVERSAL":
				enumSupplier = () -> Arrays.stream(ReversalProcessVariable.values());
				break;
			case "REMITTANCE":
				enumSupplier = () -> Arrays.stream(RemittanceProcessVariable.values());
				break;

			case "EXTRA_BILL":
				enumSupplier = () -> Arrays.stream(ExtraBillProcessVariable.values());
				break;
			default:
				return ResponseEntity.notFound().build();
		}

		Map<String, String> response = enumSupplier.get().collect(Collectors.toMap(
				Enum::name,
				e -> {
					if (e instanceof ProformaProcessVariable) {
						return ((ProformaProcessVariable) e).getValue();
					}
					if (e instanceof LcProcessVariable) {
						return ((LcProcessVariable) e).getValue();
					}
					if (e instanceof ReversalProcessVariable) {
						return ((ReversalProcessVariable) e).getValue();
					}
					if (e instanceof CashSaleProcessVariable) {
						return ((CashSaleProcessVariable) e).getValue();
					}
					if (e instanceof RemittanceProcessVariable) {
						return ((RemittanceProcessVariable) e).getValue();
					}

					if (e instanceof ExtraBillProcessVariable) {
						return ((ExtraBillProcessVariable) e).getValue();
					}


					return null;
				}
		));

		return ResponseEntity.ok(response);
	}

	@Operation(
			summary = "جستجوی فرآیندها",
			description = "دریافت لیست فرآیندها با صفحه بندی"
	)
	@GetMapping("/search-process")
	public ResponseEntity<?> searchProcess(
			@RequestParam(defaultValue = "0") Integer pageIndex,
			@RequestParam(defaultValue = "10") Integer pageSize) {

		Pageable pageable = PageRequest.of(
				pageIndex,
				pageSize,
				Sort.by("version").descending()
		);

		return ResponseEntity.ok(processService.searchProcess(pageable));
	}

	@Operation(summary = "تعداد تسک های کاربر", description = "تعداد تسک های موجود در کارتابل کاربر را برمی گرداند")
	@GetMapping("/task-count")
	public ResponseEntity<Long> taskCount() {
		return ResponseEntity.ok(processService.taskCount());
	}

	@Operation(summary = "لغو نمونه فرآیند", description = "نمونه فرآیند موردنظر را لغو می کند")
	@PutMapping("/cancel-process-instance/{processInstanceId}")
	public ResponseEntity<ProcessInstance> cancelProcessInstance(@PathVariable String processInstanceId) {
		return ResponseEntity.ok(processService.cancelProcessInstance(processInstanceId));
	}

	@Operation(summary = "بررسی پایان فرآیند", description = "وضعیت پایان یافتن نمونه فرآیند را بررسی می کند")
	@GetMapping("/check-process-instance-ended/{processInstanceId}")
	public ResponseEntity<Boolean> checkProcessInstanceEnded(@PathVariable String processInstanceId) {
		return ResponseEntity.ok(processService.checkProcessInstanceEnded(processInstanceId));
	}

	@Operation(summary = "جزئیات تسک", description = "اطلاعات کامل تسک موردنظر را برمی گرداند")
	@GetMapping("/task-detail/{taskId}")
	public ResponseEntity<TaskDetail> getTaskDetail(@PathVariable String taskId) {
		return ResponseEntity.ok(processService.getTaskDetail(taskId));
	}

	@Operation(summary = "تاریخچه فرآیند بر اساس شناسه", description = "تاریخچه کامل نمونه فرآیند را بر اساس شناسه آن برمی گرداند")
	@GetMapping("/process-instance-history-by-id/{processInstanceId}")
	public ResponseEntity<ProcessInstanceHistory> getProcessInstanceHistoryById(
			@PathVariable String processInstanceId) {

		return ResponseEntity.ok(
				processService.getProcessInstanceHistoryById(processInstanceId)
		);
	}

	@Operation(summary = "تاریخچه گردش کار", description = "گردش کار و اقدامات انجام شده روی فرآیند را نمایش می دهد")
	@GetMapping("/process-instance-history/{processInstanceId}")
	public ResponseEntity<ProcessInsHistoryDTO> getProcessInstanceHistory(
			@PathVariable String processInstanceId) {

		return ResponseEntity.ok(
				processService.getProcessInstanceHistory(processInstanceId)
		);
	}

	@Operation(summary = "گزارش کاربران فرآیند", description = "گزارش اقدامات کاربران در فرآیند را برمی گرداند")
	@GetMapping("/get-report/{processInstanceId}")
	public ResponseEntity<Map<String, List<UserTaskReportDTO>>> getReport(
			@PathVariable String processInstanceId) {

		return ResponseEntity.ok(
				processService.getUserTasksReport(processInstanceId)
		);
	}

	@Operation(summary = "جستجوی کارتابل", description = "جستجو در تسک های کارتابل بر اساس فیلترهای ارسالی")
	@PostMapping("/search-task-inbox")
	public ResponseEntity<GridDTO> searchInbox(
			@RequestBody  TaskSearchDto taskSearchDto,
			@RequestParam(defaultValue = "0") Integer pageIndex,
			@RequestParam(defaultValue = "10") Integer pageSize) {

		return ResponseEntity.ok(
				processService.searchTaskInbox(
						taskSearchDto,
						pageIndex,
						pageSize
				)
		);
	}

	@Operation(summary = "ایجاد فرآیند جدید", description = "تعریف و ایجاد یک فرآیند جدید در سامانه")
	@PostMapping
	public ResponseEntity<String> createProcess(
			@RequestBody @Valid WorkflowDto.Create dto) {

		return ResponseEntity.ok(
				processService.createProcess(dto)
		);
	}
}