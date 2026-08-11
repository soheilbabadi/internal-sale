package com.nicico.internal.sales.wf.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
	private long remittanceMasterId;
	private String contractDate;
	private Date remittanceDate;
	private String remittanceNumber;
	private long goodId;
	private String goodName;
	private String customerName;
	private String contractNo;
	private String issuerName;
	private long issuerId;

	@Schema(name = "settlementType", description = "نوع تسویه")
	private String settlementType;
	private String proformaType;
	private Date proformaIssueDate;
	private String proformaNo;
	private String tradingBank;
	private String lcNo;
	private String issuerBank;
	private Date lcDate;

}
