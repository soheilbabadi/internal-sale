package com.nicico.internal.sales.notification.controller;

import com.nicico.internal.sales.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ins/loading/issue-placemail-notification")
public class MailNotificationController {

	private final NotificationService notificationService;

	@GetMapping("/send-proforma-email/{proformaMasterId}")
	public ResponseEntity<?> sendEmailWithProformaAttachment(@PathVariable Long proformaMasterId) {

		notificationService.sendEmailWithProformaAttachment(proformaMasterId);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	//	@PreAuthorize("@secUtil.hasAuthority('C_INS_SEND_NOTIFICATION') and @secUtil.hasAuthority('C_INS_PROFORMA')")
	@GetMapping("/retry-proforma-email/{proformaMasterId}")
	public ResponseEntity<?> retrySendProformaAttachment(@PathVariable Long proformaMasterId) {

		notificationService.retrySendEmailWithProformaAttachment(proformaMasterId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/send-remittance-email/{remittanceId}")
	public ResponseEntity<?> sendEmailWithRemittanceAttachment(@PathVariable Long remittanceId) {

		notificationService.sendEmailWithEditedRemittanceAttachment(remittanceId);
		return new ResponseEntity<>(HttpStatus.OK);

	}
}
