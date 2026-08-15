package com.example.controller;

import com.example.dto.ProformaBankBillFileUpdateDto;
import com.example.dto.ProformaBankBillDto;
import com.example.service.ProformaBankBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * کنترلر برات - شامل متد بروزرسانی فایل‌های پیوست
 */
@RestController
@RequestMapping("/api/proforma-bank-bill") // یا مسیر مناسب دیگر
@RequiredArgsConstructor
public class ProformaBankBillController {

    private final ProformaBankBillService service;

    @Operation(summary = "ثبت برات جدید", description = "یک برات جدید بر اساس اطلاعات دریافتی ایجاد و ذخیره می‌کند. نیاز به مجوز C_INS_EXTRA_BILL دارد.")
    @PreAuthorize("@secUtil.hasAuthority('C_INS_EXTRA_BILL')")
    @PostMapping("/save")
    public ResponseEntity<ProformaBankBillDto.Info> save(@RequestBody ProformaBankBillRequest proformaBankBillRequest) {
        return ResponseEntity.ok(service.save(proformaBankBillRequest));
    }

    /**
     * بروزرسانی فایل‌های پیوست برات
     * این متد فقط فیلدهای extraBillFileId و dispatchAttachmentId را بروزرسانی می‌کند
     * نیاز به مجوز C_UPD_EXTRA_BILL دارد
     */
    @Operation(
        summary = "بروزرسانی فایل‌های پیوست برات", 
        description = "فیلدهای extraBillFileId و dispatchAttachmentId را بروزرسانی می‌کند. نیاز به مجوز C_UPD_EXTRA_BILL دارد."
    )
    @PreAuthorize("@secUtil.hasAuthority('C_UPD_EXTRA_BILL')")
    @PutMapping("/update-files")
    @ApiResponse(responseCode = "200", description = "بروزرسانی با موفقیت انجام شد", 
                 content = @Content(schema = @Schema(implementation = ProformaBankBillDto.Info.class)))
    public ResponseEntity<ProformaBankBillDto.Info> updateBillFiles(
            @RequestBody ProformaBankBillFileUpdateDto updateDto) {
        
        return ResponseEntity.ok(service.updateBillFiles(updateDto));
    }
}
