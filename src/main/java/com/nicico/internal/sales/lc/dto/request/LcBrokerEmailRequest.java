package com.nicico.internal.sales.lc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LcBrokerEmailRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = -1797638256762810523L;
	private long contractNo;
	private String contractDate;
	private long quantity;
	private String goodName;
	private String customerName;
	private String brokerName;
	private String brokerEmail;
}
