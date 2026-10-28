package com.nicico.internal.sales.accounting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDto implements Serializable {

	private Long id;
	private String name;
	private String nameFA;
	private String nameEN;
	private String nationalcode;
	private Boolean type;
	private Boolean status;
	private Boolean isForeign;
	private Integer stateId;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Create implements Serializable {
		private String name;
		private String nameFA;
		private String nameEN;
		private String nationalcode;
		private Boolean type;
		private Boolean status;
		private Boolean isForeign;
		private Integer stateId;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Info implements Serializable {
		private Long id;
		private String name;
		private String nationalcode;
	}
}
