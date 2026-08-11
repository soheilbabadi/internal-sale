package com.nicico.internal.sales.proforma.records;


public record PricingFormulaResult(long unitPrice,
                                   long unitPriceCredit,
                                   double creditPercent,
                                   double cashQuantity,
                                   double creditQuantity,
                                   long cashAmount,
                                   long creditAmount,
                                   long vatCashAmount,
                                   long vatCreditAmount,
                                   long vatAmount,
                                   long totalAmount,
                                   long finalAmount) {
}