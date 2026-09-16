package com.nicico.internal.sales.notification.service;

import com.nicico.internal.sales.lc.dto.request.BrokerEmailRequest;

public interface NotificationService {
	void sendEmailWithProformaAttachment(Long proformaMasterId);

	void retrySendEmailWithProformaAttachment(Long proformaMasterId);

	void sendEmailWithEditedRemittanceAttachment(Long remittanceId);

	void sendEmailForLcBroker(BrokerEmailRequest brokerEmailRequest, String emailContent);


}
