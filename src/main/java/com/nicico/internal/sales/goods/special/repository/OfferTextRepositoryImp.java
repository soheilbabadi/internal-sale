package com.nicico.internal.sales.goods.special.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OfferTextRepositoryImp implements OfferTextRepository {
	private final JdbcTemplate jdbcTemplate;

	@Override
	public String getDescriptionByPaymentCode(String paymentCode) {
		String sql = "SELECT tio.DESCRIPTION FROM TBL_IME_TRADE tit INNER JOIN tbl_ime_ps_offers tio ON tit.OFFER_CODE = tio.ID WHERE tit.PAYMENT_CODE = ? FETCH FIRST 1 ROWS ONLY";
		try {
			return jdbcTemplate.queryForObject(sql, String.class, paymentCode);
		} catch (EmptyResultDataAccessException e) {
			return "";
		}
	}
}
