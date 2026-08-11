//package com.nicico.internal.sales.pricing.service;
//
//
//import lombok.Builder;
//import lombok.Data;
//
//import java.math.BigDecimal;
//import java.util.Map;
//
//@Data
//@Builder
//public class CalculationResult {
//    private boolean success;
//    private BigDecimal calculatedPrice;
//    private BigDecimal defaultPrice;
//    private String errorMessage;
//    private Map<String, Object> details;
//
//    public static CalculationResult success(BigDecimal price, BigDecimal defaultPrice) {
//        return CalculationResult.builder()
//                .success(true)
//                .calculatedPrice(price)
//                .defaultPrice(defaultPrice)
//                .build();
//    }
//
//    public static CalculationResult error(String message) {
//        return CalculationResult.builder()
//                .success(false)
//                .errorMessage(message)
//                .build();
//    }
//}