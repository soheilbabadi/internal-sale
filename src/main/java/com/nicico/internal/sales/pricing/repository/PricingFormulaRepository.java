//package com.nicico.internal.sales.pricing.repository;
//
//import com.nicico.internal.sales.pricing.model.PricingFormulaModel;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
/// **
// * Repository برای مدیریت فرمول های قیمت گذاری
// * Repository for managing pricing formulas
// */
//@Repository
//public interface PricingFormulaRepository extends JpaRepository<PricingFormulaModel, Long> {
//
//    /**
//     * جستجوی فرمول بر اساس کد محصول و وضعیت فعال
//     * Find formula by product type/code and active status
//     *
//     * @param productType کد نوع محصول / Product type code
//     * @return Optional containing the formula if found
//     */
//    Optional<PricingFormulaModel> findByCodeAndActiveTrue(String productType);
//
//    /**
//     * جستجوی فرمول بر اساس کد
//     * Find formula by code
//     *
//     * @param code کد فرمول / Formula code
//     * @return Optional containing the formula if found
//     */
//    Optional<PricingFormulaModel> findByCode(String code);
//
//    /**
//     * جستجوی آخرین نسخه فعال فرمول
//     * Find the latest active version of formula
//     *
//     * @param code کد فرمول / Formula code
//     * @return Optional containing the latest active formula
//     */
//    @Query(value = "SELECT f FROM PricingFormulaModel f WHERE f.code = :code AND f.active = true ORDER BY f.formulaVersion DESC")
//    Optional<PricingFormulaModel> findLatestActiveByCode(String code);
//}
//
