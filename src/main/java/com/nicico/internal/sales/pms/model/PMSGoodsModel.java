package com.nicico.internal.sales.pms.model;

import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Table(name = "MVW_DBLINK_GOODS1TBL")
@Immutable
@Data
@Audited(targetAuditMode = NOT_AUDITED)
public class PMSGoodsModel implements Serializable {
	@Id
	@Column(name = "GDSCODE")
	private String code;
	@Column(name = "GDSNAME")
	private String name;
	@Column(name = "GDSSTOCK")
	private String stock;
	@Column(name = "PACKINGCODE")
	private String packingCode;
	@Column(name = "CODENOSA")
	private String nosaCode;
	@Column(name = "CODEMARKAZHAZINEH")
	private String costCenterCode;
	@Column(name = "CODENOSAALAYANDEGI")
	private String nosaAlayandeghiCode;
	@Column(name = "GROUPGSSID")
	private Integer groupGssId;
}
