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
public class PMSCreateCustomerDto {
	@NotEmpty
	private String name;

	private String address;

	private String description;

	private String phone;

	private String economyNumber;
	@NotEmpty
	private String nationalCode;

	private String postalCode;


	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@SuperBuilder(builderMethodName = "builderr", buildMethodName = "buildd")
	public static class Create extends PMSCreateCustomerDto {
		@NotEmpty
		private String user;
		@NotEmpty
		private String pass;
	}

	@EqualsAndHashCode(callSuper = true)
	@AllArgsConstructor
	@Data
	@NoArgsConstructor
	public static class Info extends PMSCreateCustomerDto {
		String id;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder(builderMethodName = "builderr", buildMethodName = "buildd")
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

