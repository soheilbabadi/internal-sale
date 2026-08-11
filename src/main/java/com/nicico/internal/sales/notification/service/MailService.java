package com.nicico.internal.sales.notification.service;

import com.nicico.internal.sales.notification.dto.EmailRequest;

import java.net.http.HttpResponse;

public interface MailService {

	HttpResponse<String> sendMail(EmailRequest emailRequest, String pathOfPdf);

	HttpResponse<String> sendMail(EmailRequest emailRequest);
}
