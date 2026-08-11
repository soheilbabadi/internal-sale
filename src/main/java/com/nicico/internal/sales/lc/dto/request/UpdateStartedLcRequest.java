package com.nicico.internal.sales.lc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStartedLcRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = -3691068735820393927L;

	private Long proformaId;
	private String lcNo;
	private Date lcDate;
	private Date settlementDueDate;
	private Long issuerBankId;
	private Long tradingBankId;
	private String lcAttachmentId;
	private Boolean requireDispatchFile;
}
