//package com.nicico.internal.sales.pricing.repository;
//
//import com.nicico.internal.sales.pricing.model.CurrencyModel;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
/// **
// * Repository برای مدیریت نرخ های ارز
// * Repository for managing currency exchange rates
// */
//@Repository
//public interface CurrencyRepository extends JpaRepository<CurrencyModel, Long> {
//
//    /**
//     * جستجوی آخرین نرخ های ارز
//     * Find the latest currency exchange rates
//     *
//     * @return Optional containing the latest currency record
//     */
//    @Query(value = "SELECT c FROM CurrencyModel c ORDER BY c.date DESC")
//    Optional<CurrencyModel> findLatest();
//
//    /**
//     * جستجوی نرخ های ارز برای تاریخ مشخص
//     * Find currency rates by date
//     *
//     * @param date تاریخ / Date
//     * @return Optional containing currency record if found
//     */
//    Optional<CurrencyModel> findByDate(LocalDate date);
//
//    /**
//     * جستجوی نرخ های ارز بر اساس تاریخ شمسی 
//     * Find currency rates by IR date
//     *
//     * @param irDate تاریخ شمسی  / IR date string
//     * @return Optional containing currency record if found
//     */
//    Optional<CurrencyModel> findByIrDate(String irDate);
//}
//
