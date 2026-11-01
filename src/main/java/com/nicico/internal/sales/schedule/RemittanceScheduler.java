package com.nicico.internal.sales.schedule;

import com.nicico.internal.sales.customer.service.CustomerService;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.pms.service.PMSLcService;
import com.nicico.internal.sales.pms.service.PmsCustomerCreateRabbitService;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.trade.repository.TradeExtractRepository;
import com.nicico.internal.sales.wf.detection.ProcessHistoryService;
import com.nicico.internal.sales.wf.service.LcProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemittanceScheduler {
	private final TradeExtractRepository tradeRepository;

	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final CustomerService customerService;
	private final PMSLcService pmsLcService;
	private final PmsCustomerCreateRabbitService pmsCustomerCreateRabbitService;
	private final LcRepository lcRepository;
	private final ProcessHistoryService processHistoryService;
	private final LcProcessService lcProcessService;
	@Scheduled(cron = "0 */10 * * * ?")
	public void processRemittanceData() {
		try {
			customerService.importTradeData();
			tradeRepository.updateSettlementType();
			proformaMasterRepository.syncSettlementTypeFromDetails();
			proformaDetailRepository.updateSettlementTypeFromTradeSettlement();

		} catch (Exception e) {
			log.error("Error executing Remittance Scheduler Job", e);
		}
	}

	/**
	 * LC workflow convergence job.
	 * <p>
	 * Order matters:
	 *  1. refreshStatus() pulls the authoritative BPMS state for every IN_PROGRESS LC. Rows whose
	 *     workflow ended (ACCEPTED/CANCELED + final acknowledgment) or that already carry a pmsLcId
	 *     are settled here first.
	 *  2. sendToPms() then picks up ONLY the rows still waiting to be provisioned downstream
	 *     (workflowApproveStatus = ACCEPTED and pmsLcId IS NULL - see
	 *     LcRepository.findRemittanceLcWithoutPmsId).
	 *     Because step 1 runs before this, an LC that just converged to its final state is never sent
	 *     redundantly, and conversely an LC whose async PMS response was lost (e.g. savePmsId hit an
	 *     optimistic-lock conflict and skipped the write) gets resent here instead of staying stuck.
	 * <p>
	 * Each step has its own try/catch so a BPMS outage cannot block the PMS send (and vice versa).
	 */
	@Scheduled(cron = "0 */10 * * * ?")
	public void syncLcWorkflowAndSendToPms() {
		try {
			lcProcessService.refreshStatus();
		} catch (Exception e) {
			log.error("خطا در اجرای اسکجول بروزرسانی وضعیت LC", e);
		}

		try {
			pmsLcService.findRemittanceLcWithoutPmsId();
		} catch (Exception e) {
			log.error("خطا در اجرای اسکجول ارسال LC به PMS", e);
		}

		try {
			pmsCustomerCreateRabbitService.sendCustomersWithoutPmsCode();
		} catch (Exception e) {
			log.error("خطا در اجرای اسکجول ارسال مشتری به PMS", e);
		}
	}
}
