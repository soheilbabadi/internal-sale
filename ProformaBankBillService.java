package com.example.service;

import com.example.dto.ProformaBankBillFileUpdateDto;
import com.example.dto.ProformaBankBillDto;

/**
 * متد بروزرسانی فایل‌های پیوست برات در لایه سرویس
 */
public interface ProformaBankBillService {

    /**
     * ذخیره برات جدید
     */
    ProformaBankBillDto.Info save(ProformaBankBillRequest request);

    /**
     * بروزرسانی فایل‌های پیوست برات
     * @param updateDto اطلاعات بروزرسانی شامل شناسه برات و فایل‌های پیوست
     * @return اطلاعات برات بروزرسانی شده
     */
    ProformaBankBillDto.Info updateBillFiles(ProformaBankBillFileUpdateDto updateDto);
}
