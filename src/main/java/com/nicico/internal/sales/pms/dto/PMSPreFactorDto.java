package com.nicico.internal.sales.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(builderMethodName = "lBuilder", buildMethodName = "lBuild")
public class PMSPreFactorDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -6359293904161363769L;

	@NotNull
	private Long goodsId;
	@NotBlank
	private String customerId;
	// Note: Column name is in Finglish (Persian transliteration) as defined by the original API
	@NotNull
	private BigDecimal meghdar;
	@NotBlank
	private String priceUnit;

	// Note: Column name is in Finglish (Persian transliteration) as defined by the original API
	// Expected format: Shamsi (Jalali) date in yyyy/MM/dd format (e.g., 1403/07/13)
	@NotBlank
	private String sodorDate;
	@NotBlank
	private String username;

	@NotBlank
	private String letterNumber;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@SuperBuilder(builderMethodName = "lBuilder", buildMethodName = "lBuild")
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Create extends PMSPreFactorDto {
		private String user;
		private String pass;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@SuperBuilder(builderMethodName = "lBuilder", buildMethodName = "lBuild")
	public static class Update extends PMSPreFactorDto.Create {
		private String id;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@SuperBuilder(builderMethodName = "lBuilder", buildMethodName = "lBuild")
	public static class Info extends PMSPreFactorDto {
		private String id;
		private String avarez;
		private String mabnayfrosh;
		private String lcid;
		private String state;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResponseForProformaMasterIdDto {
		Long proformaMasterId;
		List<Info> info;
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
