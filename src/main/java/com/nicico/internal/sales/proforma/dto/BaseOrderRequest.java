package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public abstract class BaseOrderRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = 8178744733558050537L;
	Long contractNo;
	@Schema(description = "شناسه آگهی عرضه", name = "tradeId", example = "12")
	private Long tradeId;
	@Schema(description = "وزن کل پیش فاکتور", name = "totalWeight", example = "330000")
	private BigDecimal totalWeight;
	private List<BigDecimal> parts;
	private ProformaIssueType proformaIssueType;
	private Integer deadlineDays;
	private Date orderDate;
//	private int gamCertificateCount;

}