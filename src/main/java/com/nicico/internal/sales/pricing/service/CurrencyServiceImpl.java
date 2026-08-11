//package com.nicico.internal.sales.pricing.service;
//
//import com.nicico.internal.sales.pricing.model.CurrencyModel;
//import com.nicico.internal.sales.pricing.repository.CurrencyRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class CurrencyServiceImpl implements CurrencyService {
//
//    private final CurrencyRepository currencyRepository;
//
//
//    @Override
//    @Transactional(readOnly = true)
//    public CurrencyModel getLatestCurrencyRate() {
//        log.debug("دریافت آخرین نرخ های ارز / Fetching latest currency exchange rates");
//        return currencyRepository.findLatest()
//                .orElseThrow(() -> {
//                    log.error("نرخ های ارز یافت نشد / No currency rates found");
//                    return new RuntimeException("نرخ های ارز در سیستم ثبت نشده اند");
//                });
//    }
//
//
//    @Override
//    @Transactional(readOnly = true)
//    public Optional<CurrencyModel> getCurrencyByDate(String date) {
//        log.debug("دریافت نرخ های ارز برای تاریخ: {} / Fetching currency rates for date: {}", date, date);
//        try {
//            return currencyRepository.findByIrDate(date);
//        } catch (Exception e) {
//            log.error("خطا در جستجوی نرخ های ارز: {} / Error fetching currency rates: {}", date, e.getMessage(), e);
//            return Optional.empty();
//        }
//    }
//
//    @Override
//    public CurrencyModel saveCurrency(CurrencyModel currency) {
//        log.info("ذخیره نرخ های ارز برای تاریخ: {} / Saving currency rates for date: {}",
//                currency.getIrDate(), currency.getIrDate());
//        try {
//            CurrencyModel saved = currencyRepository.save(currency);
//            log.info("نرخ های ارز با موفقیت ذخیره شدند / Currency rates saved successfully");
//            return saved;
//        } catch (Exception e) {
//            log.error("خطا در ذخیره نرخ های ارز: {} / Error saving currency rates: {}", e.getMessage(), e);
//            throw new RuntimeException("خطا در ذخیره نرخ های ارز", e);
//        }
//    }
//
//
//    @Override
//    public void deleteCurrency(Long id) {
//        log.info("حذف نرخ های ارز با شناسه: {} / Deleting currency rates with id: {}", id, id);
//        try {
//            currencyRepository.deleteById(id);
//            log.info("نرخ های ارز با موفقیت حذف شدند / Currency rates deleted successfully");
//        } catch (Exception e) {
//            log.error("خطا در حذف نرخ های ارز: {} / Error deleting currency rates: {}", e.getMessage(), e);
//            throw new RuntimeException("خطا در حذف نرخ های ارز", e);
//        }
//    }
//}
//
