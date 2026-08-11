package com.nicico.internal.sales.schedule;

import com.nicico.internal.sales.ins.customer.service.CustomerService;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.pms.service.PMSLcService;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.trade.repository.TradeExtractRepository;
import com.nicico.internal.sales.wf.service.ProcessStatusDeterminerService;
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
	private final LcRepository lcRepository;
	private final ProcessStatusDeterminerService processStatusDeterminerService;


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
}
