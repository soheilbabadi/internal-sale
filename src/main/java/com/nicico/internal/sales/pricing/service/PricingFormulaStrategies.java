//package com.nicico.internal.sales.performa.service.pricing;
//
//import com.nicico.internal.sales.performa.records.PricingFormulaInput;
//import com.nicico.internal.sales.performa.records.PricingFormulaResult;
//
//public final class PricingFormulaStrategies {
//
//	public static final PricingFormulaStrategy CASH_SALE_STANDARD = input -> {
//		long unitPrice = Math.round(input.unitPrice());
//		double creditPercent = 100.0 - input.cashPercentage();
//		double cashQuantity = input.quantity() - ((input.quantity() * creditPercent) / 100.0);
//		double creditQuantity = (input.quantity() * creditPercent) / 100.0;
//
//		long creditAmount = Math.round(creditQuantity * unitPrice);
//		long cashAmount = Math.round(cashQuantity * unitPrice);
//		long vatCashAmount = Math.round(input.vatRate() * cashAmount);
//		long vatCreditAmount = Math.round(input.vatRate() * creditAmount);
//		long vatAmount = vatCreditAmount + vatCashAmount;
//		long totalAmount = cashAmount + creditAmount;
//		long finalAmount = totalAmount + vatAmount;
//
//		return new PricingFormulaResult(
//				unitPrice,
//				unitPrice,
//				creditPercent,
//				cashQuantity,
//				creditQuantity,
//				cashAmount,
//				creditAmount,
//				vatCashAmount,
//				vatCreditAmount,
//				vatAmount,
//				totalAmount,
//				finalAmount);
//	};
//	public static final PricingFormulaStrategy CONTRACT_WITH_COMMISSION = input -> {
//		long unitPrice = Math.round(input.unitPrice());
//		double creditPercent = 100.0 - input.cashPercentage();
//		long quantity = Math.round(input.quantity());
//		long creditQuantity = (long) ((quantity * creditPercent) / 100.0);
//		long cashQuantity = quantity - creditQuantity;
//
//		double additionalValue = (input.commissionPercentage() + 100.0) / 100.0;
//		long unitPriceCredit = (long) Math.ceil(unitPrice * additionalValue);
//
//		long creditAmount = creditQuantity * unitPriceCredit;
//		long cashAmount = cashQuantity * unitPrice;
//		long vatCashAmount = (long) (input.vatRate() * cashAmount);
//		long vatCreditAmount = (long) (input.vatRate() * creditAmount);
//		long vatAmount = vatCreditAmount + vatCashAmount;
//		long totalAmount = cashAmount + creditAmount;
//		long finalAmount = totalAmount + vatAmount;
//
//		return new PricingFormulaResult(
//				unitPrice,
//				unitPriceCredit,
//				creditPercent,
//				cashQuantity,
//				creditQuantity,
//				cashAmount,
//				creditAmount,
//				vatCashAmount,
//				vatCreditAmount,
//				vatAmount,
//				totalAmount,
//				finalAmount);
//	};
//	public static final PricingFormulaStrategy PRECIOUS_WITH_COMMISSION = input -> {
//		long unitPrice = (long) Math.ceil(input.unitPrice());
//		double creditPercent = 100.0 - input.cashPercentage();
//		double creditQuantity = input.netWeight() - (input.quantity() * input.cashPercentage() / 100.0);
//		double cashQuantity = input.netWeight() - creditQuantity;
//
//		double additionalValue = (input.commissionPercentage() + 100.0) / 100.0;
//		long unitPriceCredit = (long) Math.ceil(unitPrice * additionalValue);
//
//		long creditAmount = (long) (creditQuantity * unitPriceCredit);
//		long cashAmount = (long) (cashQuantity * unitPrice);
//		long vatCashAmount = (long) (input.vatRate() * cashAmount);
//		long vatCreditAmount = (long) (input.vatRate() * creditAmount);
//		long vatAmount = vatCreditAmount + vatCashAmount;
//		long totalAmount = cashAmount + creditAmount;
//		long finalAmount = totalAmount + vatAmount;
//
//		return new PricingFormulaResult(
//				unitPrice,
//				unitPriceCredit,
//				creditPercent,
//				cashQuantity,
//				creditQuantity,
//				cashAmount,
//				creditAmount,
//				vatCashAmount,
//				vatCreditAmount,
//				vatAmount,
//				totalAmount,
//				finalAmount);
//	};
//	public static final PricingFormulaStrategy CASH_SALE_PRECIOUS = input -> {
//		double cashPercentage = input.cashPercentTotal() ? 100.0 : input.cashPercentage();
//		double creditPercentage = input.cashPercentTotal() ? 100.0 : 100.0 - cashPercentage;
//
//		double cashQuantity = input.cashPercentTotal() ? 0.0 : input.netWeight() * (cashPercentage / 100.0);
//		double creditQuantity = input.cashPercentTotal() ? input.netWeight() : input.netWeight() - cashQuantity;
//
//		long unitPrice = Math.round(input.unitPrice());
//		long creditAmount = Math.round(creditQuantity * unitPrice);
//		long cashAmount = Math.round(cashQuantity * unitPrice);
//		long vatCashAmount = Math.round(input.vatRate() * cashAmount);
//		long vatCreditAmount = Math.round(input.vatRate() * creditAmount);
//		long vatAmount = vatCreditAmount + vatCashAmount;
//		long totalAmount = cashAmount + creditAmount;
//		long finalAmount = totalAmount + vatAmount;
//
//		return new PricingFormulaResult(
//				unitPrice,
//				unitPrice,
//				creditPercentage,
//				cashQuantity,
//				creditQuantity,
//				cashAmount,
//				creditAmount,
//				vatCashAmount,
//				vatCreditAmount,
//				vatAmount,
//				totalAmount,
//				finalAmount);
//	};
//
//	private PricingFormulaStrategies() {
//	}
//
//	public interface PricingFormulaStrategy {
//		PricingFormulaResult calculate(PricingFormulaInput input);
//	}
//
//
//}
