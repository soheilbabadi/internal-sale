//package com.nicico.internal.sales.pricing.service;
//
//import com.nicico.internal.sales.pricing.model.CommodityModel;
//import com.nicico.internal.sales.pricing.repository.CommodityRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
/// **
// * سرویس پیاده سازی شده برای مدیریت نرخ های کالا
// * Implementation service for managing commodity prices
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class CommodityServiceImpl implements CommodityService {
//
//    private final CommodityRepository commodityRepository;
//
//
//    @Override
//    @Transactional(readOnly = true)
//    public CommodityModel getLatestCommodity() {
//        log.debug("دریافت آخرین نرخ های کالا / Fetching latest commodity prices");
//        return commodityRepository.findLatest()
//                .orElseThrow(() -> {
//                    log.error("نرخ های کالا یافت نشد / No commodity prices found");
//                    return new RuntimeException("نرخ های کالا در سیستم ثبت نشده اند");
//                });
//    }
//
//
//    @Override
//    @Transactional(readOnly = true)
//    public Optional<CommodityModel> getCommodityByDate(String date) {
//        log.debug("دریافت نرخ های کالا برای تاریخ: {} / Fetching commodity prices for date: {}", date, date);
//        try {
//            // تبدیل تاریخ string به Date object
//            return commodityRepository.findByIrDate(date);
//        } catch (Exception e) {
//            log.error("خطا در جستجوی نرخ های کالا: {} / Error fetching commodity prices: {}", date, e.getMessage(), e);
//            return Optional.empty();
//        }
//    }
//
//
//    @Override
//    public CommodityModel saveCommodity(CommodityModel commodity) {
//        log.info("ذخیره نرخ های کالا برای تاریخ: {} / Saving commodity prices for date: {}",
//                commodity.getIrDate(), commodity.getIrDate());
//        try {
//            CommodityModel saved = commodityRepository.save(commodity);
//            log.info("نرخ های کالا با موفقیت ذخیره شدند / Commodity prices saved successfully");
//            return saved;
//        } catch (Exception e) {
//            log.error("خطا در ذخیره نرخ های کالا: {} / Error saving commodity prices: {}", e.getMessage(), e);
//            throw new RuntimeException("خطا در ذخیره نرخ های کالا", e);
//        }
//    }
//
//
//    @Override
//    public void deleteCommodity(Long id) {
//        log.info("حذف نرخ های کالا با شناسه: {} / Deleting commodity prices with id: {}", id, id);
//        try {
//            commodityRepository.deleteById(id);
//            log.info("نرخ های کالا با موفقیت حذف شدند / Commodity prices deleted successfully");
//        } catch (Exception e) {
//            log.error("خطا در حذف نرخ های کالا: {} / Error deleting commodity prices: {}", e.getMessage(), e);
//            throw new RuntimeException("خطا در حذف نرخ های کالا", e);
//        }
//    }
//}
//
