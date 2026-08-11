package com.nicico.internal.sales.lc.service;

import com.nicico.internal.sales.bank.model.IssuingBankModel;
import com.nicico.internal.sales.bank.model.TradingBankModel;
import com.nicico.internal.sales.broker.model.BrokerModel;
import com.nicico.internal.sales.lc.dto.request.LcBrokerEmailRequest;
import com.nicico.internal.sales.lc.dto.request.LcCancelRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateAcceptedLcRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateStartedLcRequest;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;

import java.util.Date;

public interface LcServiceHelper {
	ProformaDetailModel findProformaDetail(Long proformaId);

	LcModel findLcModel(Long proformaId);

	TradingBankModel findBankBranch(Long requestId, Long fallbackId);

	IssuingBankModel findIssuingBank(Long requestId, Long fallbackId);

	BrokerModel fetchBrokerForTrade(Long tradeId);

	void validateAndAdjustLcDate(UpdateStartedLcRequest lcRequest, ProformaDetailModel proformaDetail);

	void validateAndAdjustLcDate(UpdateAcceptedLcRequest lcRequest, ProformaDetailModel proformaDetail);

	void populateLcModel(LcModel lcModel, UpdateStartedLcRequest lcRequest,
	                     ProformaDetailModel proformaDetail, ProformaMasterModel proformaMaster,
	                     TradingBankModel tradingBank, IssuingBankModel issuingBank, Date expireDate);

	void updateTradingBankIfPresent(LcModel lc, UpdateAcceptedLcRequest request);

	void updateIssuingBankIfPresent(LcModel lc, UpdateAcceptedLcRequest request);

	void updateLcDetailsIfPresent(LcModel lc, UpdateAcceptedLcRequest request);

	void validateDispatchFileRequirement(LcModel lcModel, String dispatchFileId);


	LcBrokerEmailRequest buildLcBrokerEmailRequest(ProformaDetailModel detail, BrokerModel broker);

	void markAllLcsAsReckoning(Long proformaMasterId);

	void sendLcBrokerReckoningEmail(LcBrokerEmailRequest lcBrokerEmailRequest, String emailContent);

	void cancelLcModel(LcModel model, LcCancelRequest request);

	String buildCancellationRecord(LcCancelRequest request);

	void appendCancellationRecord(LcModel model, String cancellationRecord);
}
