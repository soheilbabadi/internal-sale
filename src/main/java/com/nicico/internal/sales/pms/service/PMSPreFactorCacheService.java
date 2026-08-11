package com.nicico.internal.sales.pms.service;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.pms.repository.PMSCustomerRepository;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.repository.ProformaGoodItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service جداگانه برای Cache کردن
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PMSPreFactorCacheService {
	private final ProformaGoodItemRepository performaGoodItemRepository;
	private final CustomerRepository customerRepository;
	private final PMSCustomerRepository pmsCustomerRepository;

	@Cacheable(value = "performaGoodItems", key = "#proformaMasterId")
	public List<ProformaGoodItemModel> getPerformaGoodItems(Long proformaMasterId) {
		log.info("Loading from DB - ProformaMasterId: {}", proformaMasterId);
		var result = performaGoodItemRepository.findActiveItemsWithProformaMasterId(proformaMasterId);
		if (result.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(
					"آیتم کالای پیش فاکتور با شناسه " + proformaMasterId + " وجود ندارد"
			);

		}

		return result;

	}

	@Cacheable(value = "customers", key = "#nationalCode")
	public CustomerModel getCustomerByNationalCode(String nationalCode) {
		log.info("Loading customer from DB - NationalCode: {}", nationalCode);
		return customerRepository.findByNationalCode(nationalCode)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						"مشتری با شناسه ملی " + nationalCode + " یافت نشد "
				));
	}

	@Cacheable(value = "pmsCustomers", key = "#economicCode")
	public void getPmsCustomerByEconomicCode(String economicCode) {
		if (economicCode == null) {
			throw new InternalSaleCustomException.ValidationException("کد اقتصادی نمی تواند خالی باشد");
		}
		log.info("Loading PMS customer from DB - EconomicCode: {}", economicCode);
		pmsCustomerRepository
				.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(
						economicCode, economicCode)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						"مشتری PMS با کد اقتصادی " + economicCode + " وجود ندارد"
				));
	}
}