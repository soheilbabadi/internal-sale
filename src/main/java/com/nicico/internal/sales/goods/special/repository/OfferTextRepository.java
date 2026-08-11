package com.nicico.internal.sales.goods.special.repository;

public interface OfferTextRepository {
	String getDescriptionByPaymentCode(String paymentCode);
}
