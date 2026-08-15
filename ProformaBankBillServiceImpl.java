package com.example.service.impl;

import com.example.dto.ProformaBankBillFileUpdateDto;
import com.example.dto.ProformaBankBillDto;
import com.example.service.ProformaBankBillService;
import com.example.entity.ProformaBankBill;
import com.example.repository.ProformaBankBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * پیاده‌سازی سرویس برات - متد بروزرسانی فایل‌های پیوست
 */
@Service
@RequiredArgsConstructor
public class ProformaBankBillServiceImpl implements ProformaBankBillService {

    private final ProformaBankBillRepository repository;
    // سایر وابستگی‌ها و متدها...

    @Override
    public ProformaBankBillDto.Info save(ProformaBankBillRequest request) {
        // پیاده‌سازی متد save
        // ...
        return null;
    }

    /**
     * بروزرسانی فایل‌های پیوست برات
     * این متد فقط فیلدهای extraBillFileId و dispatchAttachmentId را بروزرسانی می‌کند
     */
    @Override
    @Transactional
    public ProformaBankBillDto.Info updateBillFiles(ProformaBankBillFileUpdateDto updateDto) {
        // یافتن برات بر اساس شناسه
        ProformaBankBill bill = repository.findById(updateDto.getId())
            .orElseThrow(() -> new RuntimeException("برات با شناسه " + updateDto.getId() + " یافت نشد"));

        // بروزرسانی فیلدهای مورد نظر
        if (updateDto.getExtraBillFileId() != null) {
            bill.setExtraBillFileId(updateDto.getExtraBillFileId());
        }
        
        if (updateDto.getDispatchAttachmentId() != null) {
            bill.setDispatchAttachmentId(updateDto.getDispatchAttachmentId());
        }

        // ذخیره تغییرات
        ProformaBankBill savedBill = repository.save(bill);

        // تبدیل به DTO و بازگشت
        return mapToInfo(savedBill);
    }

    // متد کمکی برای تبدیل Entity به DTO
    private ProformaBankBillDto.Info mapToInfo(ProformaBankBill bill) {
        return ProformaBankBillDto.Info.builder()
            .id(bill.getId())
            .extraBillFileId(bill.getExtraBillFileId())
            .dispatchAttachmentId(bill.getDispatchAttachmentId())
            // سایر فیلدها...
            .build();
    }
}
