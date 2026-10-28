package com.nicico.internal.sales.wf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemittanceVariablesInput implements Serializable {
	@Serial
	private static final long serialVersionUID = 9114643488025535161L;
	private Long remittanceMasterId;
	private String remittanceNumber;
	private Date remittanceDate;
	private Long issuerId;
	private String issuerName;
	private String settlementType;
	private String proformaType;
	private String proformaNo;
	private Date proformaIssueDate;
	private String tradingBank;
	private String issuerBank;
	private String lcNo;
	private Date lcDate;
	private String contractDate;
	private Long goodId;
	private String goodName;
	private String customerName;
	private String contractNo;
}
