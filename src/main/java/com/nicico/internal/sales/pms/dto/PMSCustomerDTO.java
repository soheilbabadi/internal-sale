package com.nicico.internal.sales.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
public class PMSCustomerDTO {
	private String id;
	private String name;
	private String address;
	private String description;
	private String phone;
	private String economicCode;
	private String registerNumber;
	private String postCode;
	private String telex;
	private String fax;
	private String nosaCode;
	private String nosaCreditCode;
	private String depCode;

	@AllArgsConstructor
	@Getter
	@Setter
	public static class Info extends PMSCustomerDTO {
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}
}
