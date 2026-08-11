package com.nicico.internal.sales.ime.broker.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Subselect("select broker.* from TBL_IME_PS_BROKERS broker ")
@Immutable
@Setter
@Getter
@Audited(targetAuditMode = NOT_AUDITED)
public class IMEBrokerModel implements Serializable {
	@Id
	private Long pk;
	@Column(name = "id")
	private Integer brokerId;
	@Column(name = "PERSIAN_NAME")
	private String persianName;
	@Column(name = "NATIONAL_ID")
	private String nationalId;
	@Column(name = "SPOT_ID")
	private Integer spotId;
	@Column(name = "DERIVATIVES_ID")
	private Integer derivativesId;
	@Column(name = "DESCRIPTION")
	private String description;
}
