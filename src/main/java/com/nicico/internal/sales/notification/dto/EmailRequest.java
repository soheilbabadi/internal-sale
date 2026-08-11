package com.nicico.internal.sales.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequest {
	private String toRecipients;
	private String subject;
	private String bccRecipients;
	private String content;
}