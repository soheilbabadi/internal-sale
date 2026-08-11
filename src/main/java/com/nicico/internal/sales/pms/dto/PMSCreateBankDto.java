package com.nicico.internal.sales.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder(builderMethodName = "builderr", buildMethodName = "buildd")
public class PMSCreateBankDto {
	@NotEmpty
	private String baseBankId;
	@NotEmpty
	private String branchCode;
	@NotEmpty
	private String branchDescription;
	private Long accountingId;

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@SuperBuilder(builderMethodName = "builderr", buildMethodName = "buildd")
	public static class Create extends PMSCreateBankDto {
		@NotEmpty
		private String user;
		@NotEmpty
		private String pass;
	}

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@NoArgsConstructor
	public static class Info extends PMSCreateBankDto {
		String id;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RabbitListenerRequestDTO {
		String url;
		Long id;
		Create request;
		String responseRoutingKey;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RabbitListenerResponseDTO {
		RabbitListenerRequestDTO request;
		Info response;
	}
}

