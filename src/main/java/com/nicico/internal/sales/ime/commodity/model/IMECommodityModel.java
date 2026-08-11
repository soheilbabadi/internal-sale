package com.nicico.internal.sales.ime.commodity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Data
@Table(name = "TBL_IME_PS_COMMODITIES")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class IMECommodityModel implements Serializable {
	@Serial
	private static final long serialVersionUID = 8098972445659490650L;
	@Id
	@Column(name = "PK")
	private Long id;
	@Column(name = "ID")
	private Long commodityId;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "PERSIAN_NAME")
	private String persianName;
	@Column(name = "PARENT_ID")
	private Long parentId;
	@Column(name = "SYMBOL")
	private String symbol;
	@Column(name = "C_CREATED_BY")
	private String createdBy;
	@Column(name = "C_LAST_MODIFIED_BY")
	private String lastModifiedBy;
	@Column(name = "COMMENT")
	private String comment;
	@Column(name = "CLOB_OPTIONS")
	private String clobOptions;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RESPONSE_DATE")
	private Date responseDate;
	@Column(name = "MESSAGE_TEXT")
	private String messageText;
}