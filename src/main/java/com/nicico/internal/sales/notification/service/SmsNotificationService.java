package com.nicico.internal.sales.notification.service;

import com.nicico.internal.sales.notification.dto.SmsDTO;

import java.io.IOException;

public interface SmsNotificationService {
	SmsDTO.Response preFactorEmailedSMSNotification(Long proformaMasterId) throws IOException;
}
