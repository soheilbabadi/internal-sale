package com.nicico.internal.sales.notification.service;

import com.nicico.internal.sales.notification.dto.EmailRequest;

public interface MailService {

	void sendMail(EmailRequest emailRequest, String pathOfPdf);

	void sendMail(EmailRequest emailRequest);
}
