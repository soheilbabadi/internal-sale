package com.nicico.internal.sales.pms.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Immutable
@Subselect("""
		SELECT * FROM PMS.CUSTOMER1TBL@DBL_DS2_COPLINK
		""")
@Setter
@Getter
//@Table(name = "MVW_DBLINK_CUSTOMER1TBL")
@Audited(targetAuditMode = NOT_AUDITED)
public class PMSCustomerModel {

	@Id
	@Column(name = "CUST_ID")
	private String id;

	@Column(name = "CUST_NAME")
	private String name;

	@Column(name = "CUST_ADDRESS")
	private String address;

	@Column(name = "CUST_SHARH")
	private String description;

	@Column(name = "CUST_TEL")
	private String phone;

	@Column(name = "CUST_EGHTESADNUMBER")
	private String economicCode;

	@Column(name = "CUST_SABTNUMBER")
	private String registerNumber;

	@Column(name = "CUST_POSTCODE")
	private String postCode;

	@Column(name = "CUST_TELEX")
	private String telex;

	@Column(name = "CUST_FAX")
	private String fax;

	@Column(name = "CUST_CODENOSA")
	private String nosaCode;

	@Column(name = "CUST_CODEETEBARENOSA")
	private String nosaCreditCode;

	@Column(name = "CUST_CODEMARKAZHAZ")
	private String depCode;
}