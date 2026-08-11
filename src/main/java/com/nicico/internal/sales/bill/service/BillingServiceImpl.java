package com.nicico.internal.sales.bill.service;

import com.nicico.internal.sales.bill.dto.BillingStatus;
import com.nicico.internal.sales.bill.model.BillingMasterModel;
import com.nicico.internal.sales.bill.model.BillingWeightingDetailModel;
import com.nicico.internal.sales.bill.model.PmsWeighingDetailExtractModel;
import com.nicico.internal.sales.bill.repository.BillingMasterRepository;
import com.nicico.internal.sales.bill.repository.RemittanceWeighingDetailRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
import com.nicico.internal.sales.salecondition.service.SaleConditionService;
import com.nicico.internal.sales.util.date.DateUtility;
import com.nicico.internal.sales.vat.model.TaxVatModel;
import com.nicico.internal.sales.vat.repository.VatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl {

	private static final String EMPTY_STRING = "";
	private static final String DEFAULT_PLACEHOLDER = "-";
	private static final String MSG_BILLING_NOT_FOUND = "صورتحساب وجود ندارد";
	private static final String MSG_PROFORMA_LC_ISSUE_NOT_FOUND = "Proforma LC Issue not found for the given ID";
	private final RemittanceMasterRepository remittanceMasterRepository;
	private final VatRepository vatRepository;
	private final BillingMasterRepository billingMasterRepository;
	private final RemittanceWeighingDetailRepository remittanceWeighingDetailRepository;
	private final SaleConditionService saleConditionService;

	public String createBilling(Long remittanceId) {
		var remittanceMaster = remittanceMasterRepository.findById(remittanceId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						MSG_PROFORMA_LC_ISSUE_NOT_FOUND));

		TaxVatModel tatax = vatRepository.findByJalaliYear(DateUtility.getJalaliYear(new Date())).get();

		BillingMasterModel billingMaster = new BillingMasterModel();
		billingMaster.setId(UUID.randomUUID());
		billingMaster.setRemittanceMaster(remittanceMaster);
		billingMaster.setBillingDate(new Date());
		billingMaster.setBillingStatus(BillingStatus.DRAFT);
		billingMaster.setTotalFinalAmount(remittanceMaster.getTotalFinalAmount());
		billingMaster.setTotalFinalQuantity(remittanceMaster.getTotalQuantity());
		billingMaster.setSerialNo("BL" + System.currentTimeMillis());
		billingMaster.setDelayPenaltyApplied(false);
		billingMaster.setEmissionTaxPercentage(tatax.getEmissionTax());
		billingMaster.setEmissionTaxAmonut(remittanceMaster.getTotalFinalAmount()
				.multiply(tatax.getEmissionTax())
				.divide(BigDecimal.valueOf(100)));
		billingMaster.setTaxAmount(remittanceMaster.getTaxAmount());
		billingMaster.setPenaltyAmount(BigDecimal.ZERO);
		billingMaster.setProcessId(DEFAULT_PLACEHOLDER);
		billingMaster.setReversalProcessId(DEFAULT_PLACEHOLDER);
		billingMaster.setWorkflowApproveStatus(WorkflowApproveStatus.NOT_STARTED);

		List<PmsWeighingDetailExtractModel> pmsWeighingDetailExtractModels =
				remittanceWeighingDetailRepository.findAllByContractNo(remittanceMaster.getContractNo());

		if (pmsWeighingDetailExtractModels.isEmpty()) {
			throw new InternalSaleCustomException.ResourceNotFoundException(MSG_BILLING_NOT_FOUND + remittanceMaster.getContractNo());
		}

		final BillingMasterModel finalBillingMaster = billingMaster;
		List<BillingWeightingDetailModel> weightingDetails = pmsWeighingDetailExtractModels.stream()
				.map(pms -> {
					BillingWeightingDetailModel detail = new BillingWeightingDetailModel();
					detail.setFinal(pms.isFinal());
					detail.setFinalQuantity(pms.getTotalRealWeight());
					detail.setCanceled(false);
					detail.setBillingMaster(finalBillingMaster);
					detail.setRemittanceMaster(remittanceMaster);
					detail.setRemittanceDate(remittanceMaster.getRemittanceDate());
					return detail;
				})
				.collect(Collectors.toList());

		billingMaster.setBillingWeighingDetails(weightingDetails);
		billingMasterRepository.save(billingMaster);

		return billingMaster.getSerialNo();
	}


	public void updatePenalty(UUID billingId) {

		BillingMasterModel billingMasterModel = billingMasterRepository.findById(billingId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						MSG_PROFORMA_LC_ISSUE_NOT_FOUND));


		billingMasterModel.setBillingStatus(BillingStatus.ISSUED);

		SaleConditionModel saleConditionModel = saleConditionService.findByPaymentCodeModel(billingMasterModel.getRemittanceMaster().getPaymentCode());
		saleConditionModel.getPaymentDeferral();
	}

}
