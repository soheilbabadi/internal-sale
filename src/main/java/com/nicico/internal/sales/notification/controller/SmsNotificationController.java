package com.nicico.internal.sales.notification.controller;

import com.nicico.internal.sales.notification.dto.SmsDTO;
import com.nicico.internal.sales.notification.service.SmsNotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/ins/loading/sms-notification")
@AllArgsConstructor
@Slf4j
class SmsNotificationController {
	private final SmsNotificationService smsNotificationService;

	@GetMapping("/pre-factor-emailed/{proformaMasterId}")
	@PreAuthorize("@secUtil.hasAuthority('SMS_NOTIFICATION')")
	public ResponseEntity<SmsDTO.Response> sendPrefactorEmailSentSmsNotification(
			@PathVariable Long proformaMasterId
	) throws IOException {
		log.info("Sending notification sms for prefactor email sent with master id {}", proformaMasterId);
		return ResponseEntity.ok(smsNotificationService.preFactorEmailedSMSNotification(proformaMasterId));
	}
}
