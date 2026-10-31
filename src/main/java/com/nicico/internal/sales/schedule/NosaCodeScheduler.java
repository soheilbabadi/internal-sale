package com.nicico.internal.sales.schedule;


import com.nicico.internal.sales.customer.model.CustomerModel;
import com.nicico.internal.sales.customer.repository.CustomerRepository;
import com.nicico.internal.sales.customer.service.CustomerService;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.extrabill.service.ExtraBillService;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.gaam.repository.GaamRepository;
import com.nicico.internal.sales.gaam.service.GaamService;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.lc.service.LcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NosaCodeScheduler {

	private final LcRepository lcRepository;
	private final LcService lcService;

	private final GaamRepository gaamRepository;
	private final GaamService gaamService;

	private final ExtraBillRepository extraBillRepository;
	private final ExtraBillService extraBillService;

	private final CustomerRepository customerRepository;
	private final CustomerService customerService;

	@Value("${schedule.nosa.batch-size:50}")
	private int batchSize;

	@Value("${schedule.nosa.enabled:true}")
	private boolean enabled;

	/**
	 * اسکجول ۱: تخصیص کد نوسا برای اعتبار اسنادی (LC)
	 */
	@Scheduled(cron = "${schedule.nosa.lc.cron}")
	public void assignLcNosaCodes() {
		if (!enabled) {
			log.debug("LC NosaCodeScheduler is disabled.");
			return;
		}

		try {
			long totalCount = lcRepository.countLcsReadyForNosaAssignment();
			if (totalCount == 0) {
				log.debug("No LCs found requiring Nosa code assignment.");
				return;
			}

			int effectiveBatchSize = batchSize > 0 ? batchSize : 50;
			int totalPages = (int) Math.ceil((double) totalCount / effectiveBatchSize);

			log.info("Starting LC NosaCodeScheduler: Total count = {}, Batch size = {}, Total pages = {}",
					totalCount, effectiveBatchSize, totalPages);

			int successCount = 0;
			int failCount = 0;

			for (int page = 0; page < totalPages; page++) {
				Pageable pageable = PageRequest.of(page, effectiveBatchSize);
				List<LcModel> lcs = lcRepository.findLcsReadyForNosaAssignment(pageable);
				if (lcs == null || lcs.isEmpty()) {
					break;
				}

				log.info("Processing LC page {}/{} ({} records)...", page + 1, totalPages, lcs.size());

				for (LcModel lc : lcs) {
					try {
						log.info("Assigning Nosa code for LC id: {}, LC No: {}", lc.getId(), lc.getLcNo());
						String nosaCode = lcService.createDetail(lc.getId());
						if (nosaCode != null) {
							log.info("Successfully assigned Nosa code {} to LC id: {}", nosaCode, lc.getId());
							successCount++;
						} else {
							log.warn("Nosa code generation returned null for LC id: {}", lc.getId());
							failCount++;
						}
					} catch (Exception e) {
						log.error("Failed to assign Nosa code for LC id: {}, error: {}", lc.getId(), e.getMessage(), e);
						failCount++;
					}
				}
			}

			log.info("Finished LC NosaCodeScheduler: Total = {}, Successful = {}, Failed = {}",
					totalCount, successCount, failCount);

		} catch (Exception e) {
			log.error("Error executing LC NosaCodeScheduler", e);
		}
	}

	/**
	 * اسکجول ۲: تخصیص کد نوسا برای اوراق گام (GAAM)
	 */
	@Scheduled(cron = "${schedule.nosa.gaam.cron}")
	public void assignGaamNosaCodes() {
		if (!enabled) {
			log.debug("GAAM NosaCodeScheduler is disabled.");
			return;
		}

		try {
			long totalCount = gaamRepository.countGaamsReadyForNosaAssignment();
			if (totalCount == 0) {
				log.debug("No GAAMs found requiring Nosa code assignment.");
				return;
			}

			int effectiveBatchSize = batchSize > 0 ? batchSize : 50;
			int totalPages = (int) Math.ceil((double) totalCount / effectiveBatchSize);

			log.info("Starting GAAM NosaCodeScheduler: Total count = {}, Batch size = {}, Total pages = {}",
					totalCount, effectiveBatchSize, totalPages);

			int successCount = 0;
			int failCount = 0;

			for (int page = 0; page < totalPages; page++) {
				Pageable pageable = PageRequest.of(page, effectiveBatchSize);
				List<GaamModel> gaams = gaamRepository.findGaamsReadyForNosaAssignment(pageable);
				if (gaams == null || gaams.isEmpty()) {
					break;
				}

				log.info("Processing GAAM page {}/{} ({} records)...", page + 1, totalPages, gaams.size());

				for (GaamModel gaam : gaams) {
					try {
						String docNo = gaam.getSepamCode() != null ? gaam.getSepamCode() : gaam.getTreasuryId();
						log.info("Assigning Nosa code for GAAM id: {}, docNo: {}", gaam.getId(), docNo);
						String nosaCode = gaamService.createDetail(gaam.getId());
						if (nosaCode != null) {
							log.info("Successfully assigned Nosa code {} to GAAM id: {}", nosaCode, gaam.getId());
							successCount++;
						} else {
							log.warn("Nosa code generation returned null for GAAM id: {}", gaam.getId());
							failCount++;
						}
					} catch (Exception e) {
						log.error("Failed to assign Nosa code for GAAM id: {}, error: {}", gaam.getId(), e.getMessage(), e);
						failCount++;
					}
				}
			}

			log.info("Finished GAAM NosaCodeScheduler: Total = {}, Successful = {}, Failed = {}",
					totalCount, successCount, failCount);

		} catch (Exception e) {
			log.error("Error executing GAAM NosaCodeScheduler", e);
		}
	}

	/**
	 * اسکجول ۳: تخصیص کد نوسا برای برات بانکی (ExtraBill)
	 */
	@Scheduled(cron = "${schedule.nosa.extrabill.cron}")
	public void assignExtraBillNosaCodes() {
		if (!enabled) {
			log.debug("ExtraBill NosaCodeScheduler is disabled.");
			return;
		}

		try {
			long totalCount = extraBillRepository.countExtraBillsReadyForNosaAssignment();
			if (totalCount == 0) {
				log.debug("No ExtraBills found requiring Nosa code assignment.");
				return;
			}

			int effectiveBatchSize = batchSize > 0 ? batchSize : 50;
			int totalPages = (int) Math.ceil((double) totalCount / effectiveBatchSize);

			log.info("Starting ExtraBill NosaCodeScheduler: Total count = {}, Batch size = {}, Total pages = {}",
					totalCount, effectiveBatchSize, totalPages);

			int successCount = 0;
			int failCount = 0;

			for (int page = 0; page < totalPages; page++) {
				Pageable pageable = PageRequest.of(page, effectiveBatchSize);
				List<ExtraBankBillModel> extraBills = extraBillRepository.findExtraBillsReadyForNosaAssignment(pageable);
				if (extraBills == null || extraBills.isEmpty()) {
					break;
				}

				log.info("Processing ExtraBill page {}/{} ({} records)...", page + 1, totalPages, extraBills.size());

				for (ExtraBankBillModel bill : extraBills) {
					try {
						String docNo = bill.getSepamCode() != null ? bill.getSepamCode() : bill.getTreasuryId();
						log.info("Assigning Nosa code for ExtraBill id: {}, docNo: {}", bill.getId(), docNo);
						String nosaCode = extraBillService.createDetail(bill.getId());
						if (nosaCode != null) {
							log.info("Successfully assigned Nosa code {} to ExtraBill id: {}", nosaCode, bill.getId());
							successCount++;
						} else {
							log.warn("Nosa code generation returned null for ExtraBill id: {}", bill.getId());
							failCount++;
						}
					} catch (Exception e) {
						log.error("Failed to assign Nosa code for ExtraBill id: {}, error: {}", bill.getId(), e.getMessage(), e);
						failCount++;
					}
				}
			}

			log.info("Finished ExtraBill NosaCodeScheduler: Total = {}, Successful = {}, Failed = {}",
					totalCount, successCount, failCount);

		} catch (Exception e) {
			log.error("Error executing ExtraBill NosaCodeScheduler", e);
		}
	}

	/**
	 * اسکجول ۴: تخصیص کد نوسا برای مشتریان بدون کد نوسا (Customer)
	 */
	@Scheduled(cron = "${schedule.nosa.customer.cron}")
	public void assignCustomerNosaCodes() {
		if (!enabled) {
			log.debug("Customer NosaCodeScheduler is disabled.");
			return;
		}

		try {
			long totalCount = customerRepository.countCustomersReadyForNosaAssignment();
			if (totalCount == 0) {
				log.debug("No customers found requiring Nosa code assignment.");
				return;
			}

			int effectiveBatchSize = batchSize > 0 ? batchSize : 50;
			int totalPages = (int) Math.ceil((double) totalCount / effectiveBatchSize);

			log.info("Starting Customer NosaCodeScheduler: Total count = {}, Batch size = {}, Total pages = {}",
					totalCount, effectiveBatchSize, totalPages);

			int successCount = 0;
			int failCount = 0;

			for (int page = 0; page < totalPages; page++) {
				Pageable pageable = PageRequest.of(page, effectiveBatchSize);
				List<CustomerModel> customers = customerRepository.findCustomersReadyForNosaAssignment(pageable);
				if (customers == null || customers.isEmpty()) {
					break;
				}

				log.info("Processing Customer page {}/{} ({} records)...", page + 1, totalPages, customers.size());

				for (CustomerModel customer : customers) {
					try {
						log.info("Assigning Nosa code for Customer id: {}, name: {}, nationalCode: {}",
								customer.getId(), customer.getName(), customer.getNationalCode());
						String nosaCode = customerService.saveAccDetailCode(customer.getId());
						if (nosaCode != null) {
							log.info("Successfully assigned Nosa code {} to Customer id: {}", nosaCode, customer.getId());
							successCount++;
						} else {
							log.warn("Nosa code generation returned null for Customer id: {}", customer.getId());
							failCount++;
						}
					} catch (Exception e) {
						log.error("Failed to assign Nosa code for Customer id: {}, error: {}", customer.getId(), e.getMessage(), e);
						failCount++;
					}
				}
			}

			log.info("Finished Customer NosaCodeScheduler: Total = {}, Successful = {}, Failed = {}",
					totalCount, successCount, failCount);

		} catch (Exception e) {
			log.error("Error executing Customer NosaCodeScheduler", e);
		}
	}

}
