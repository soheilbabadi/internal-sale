package com.nicico.internal.sales.goods.special.service;

public interface OfferTextProcess {
	String findDescriptionByPaymentCode(String paymentCode);

	String getSeleniumPercent(String text);

	String getBasketNumber(String text);

	String hasSelenium(String text);

	String extractLotNumber(String text);

	boolean containsMetalPercentage(String text);

	double getCommission(String text);
}
