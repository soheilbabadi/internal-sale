//package com.nicico.internal.sales.bill.service;
//
//import com.nicico.internal.sales.bill.repository.RemittanceWeighingSummaryRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class BillingValidationServiceImpl {
//
//    private static final String REMITTANCE_NOT_FOUND_MESSAGE = "حواله ای با این مشخصات ثبت نشده است";
//    private static final String REMITTANCE_NOT_FINALIZED_MESSAGE = "حواله هنوز در سیستم لجستیک نهایی نشده است";
//
//    private final RemittanceWeighingSummaryRepository remittanceWeighingSummaryRepository;
//
//    public List<String> validateStartBill(String contractNo) {
//        List<String> errors = new ArrayList<>();
//
//        var remittance = remittanceWeighingSummaryRepository.findFirstByContractNo(contractNo)
//                .orElse(null);
//        if (remittance == null) {
//            errors.add(REMITTANCE_NOT_FOUND_MESSAGE);
//            return errors;
//        }
//
//        if (!remittance.isFinal()) {
//            errors.add(REMITTANCE_NOT_FINALIZED_MESSAGE);
//        }
//        return errors;
//    }
//
//}
//
//
