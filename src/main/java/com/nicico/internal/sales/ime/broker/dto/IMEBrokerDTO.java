package com.nicico.internal.sales.ime.broker.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class IMEBrokerDTO {
	private Long pk;
	private Integer brokerId;
	private String persianName;
	private String nationalId;
	private Integer spotId;
	private Integer derivativesId;
	private String description;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@ApiModel("IMEBrokerDTO.Info")
	public static class Info extends IMEBrokerDTO {
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}
}
