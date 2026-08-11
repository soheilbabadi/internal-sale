//package com.nicico.internal.sales.pricing.repository;
//
//import com.nicico.internal.sales.pricing.model.CommodityModel;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.Date;
//import java.util.Optional;
//
/// **
// * Repository برای مدیریت نرخ های کالا
// * Repository for managing commodity prices
// */
//@Repository
//public interface CommodityRepository extends JpaRepository<CommodityModel, Long> {
//
//    /**
//     * جستجوی آخرین نرخ های کالا
//     * Find the latest commodity prices
//     *
//     * @return Optional containing the latest commodity record
//     */
//    @Query(value = "SELECT c FROM CommodityModel c ORDER BY c.date DESC")
//    Optional<CommodityModel> findLatest();
//
//    /**
//     * جستجوی نرخ های کالا برای تاریخ مشخص
//     * Find commodity prices by date
//     *
//     * @param date تاریخ / Date
//     * @return Optional containing commodity record if found
//     */
//    Optional<CommodityModel> findByDate(Date date);
//
//    /**
//     * جستجوی نرخ های کالا بر اساس تاریخ شمسی 
//     * Find commodity prices by IR date
//     *
//     * @param irDate تاریخ شمسی  / IR date string
//     * @return Optional containing commodity record if found
//     */
//    Optional<CommodityModel> findByIrDate(String irDate);
//}
//
