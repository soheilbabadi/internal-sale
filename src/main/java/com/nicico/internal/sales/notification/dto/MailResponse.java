package com.nicico.internal.sales.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MailResponse implements Serializable {
	private int status;
	private String message;
	private String trackingId;
	private String errors;
}
