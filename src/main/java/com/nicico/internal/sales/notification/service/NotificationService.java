package com.nicico.internal.sales.notification.service;

import com.nicico.internal.sales.lc.dto.request.LcBrokerEmailRequest;

public interface NotificationService {
	void sendEmailWithProformaAttachment(Long proformaMasterId);

	void retrySendEmailWithProformaAttachment(Long proformaMasterId);

	void sendEmailWithEditedRemittanceAttachment(Long remittanceId);

	void sendEmailForLcBroker(LcBrokerEmailRequest lcBrokerEmailRequest, String emailContent);


}
