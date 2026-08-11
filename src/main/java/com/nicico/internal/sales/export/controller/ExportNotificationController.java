package com.nicico.internal.sales.export.controller;


import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.export.dto.ExportNotificationConfigDto;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.service.ExportNotificationConfigService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

//@Tag(name = "تنظیمات اعلان خروجی", description = "مدیریت تنظیمات اعلان های خروجی")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ins/notification-config")
@PreAuthorize("@secUtil.hasAuthority('C_INS_NOTIFICATION')")

public class ExportNotificationController {

	private final ExportNotificationConfigService exportNotificationConfigService;

	@Operation(summary = "دریافت تنظیم بر اساس شناسه")
	@GetMapping("/{id}")
	public ResponseEntity<?> getBrokerById(@PathVariable Long id) {
		ExportNotificationConfigDto.Info broker = exportNotificationConfigService.getById(id);
		return ResponseEntity.ok(broker);
	}

	@Operation(summary = "دریافت تنظیم بر اساس نوع موجودیت")
	@GetMapping("/get-by-type/{enumType}")
	public ResponseEntity<?> getAllByType(@PathVariable EntityTypeEnum enumType) {
		ExportNotificationConfigDto.Info configServiceByEntityType = exportNotificationConfigService.getByEntityType(enumType);
		return ResponseEntity.ok(configServiceByEntityType);
	}

	@Operation(summary = "ذخیره تنظیمات اعلان")
	@PostMapping("/save")
	public ResponseEntity<ExportNotificationConfigDto.Info> save(@RequestBody ExportNotificationConfigDto.Create request) {
		return ResponseEntity.ok(exportNotificationConfigService.save(request));
	}

	@Operation(summary = "جستجوی تنظیمات اعلان")
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody SearchDTO.SearchRq request) {
		SearchDTO.SearchRs<ExportNotificationConfigDto.Info> result = exportNotificationConfigService.search(request);
		return ResponseEntity.ok(result);
	}
}
