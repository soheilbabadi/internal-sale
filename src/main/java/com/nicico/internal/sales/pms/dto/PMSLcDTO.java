package com.nicico.internal.sales.pms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nicico.internal.sales.pms.enums.PMSLcMarkEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder(builderMethodName = "lBuilder", buildMethodName = "lBuild")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PMSLcDTO {
	//        @NotNull(message = "شماره پيش فاکتور نمي تواند خالي باشد.")
	private String prefactorId;
	@NotBlank(message = "خريدار نمي تواند خالي باشد.")
	private String customerId;
	@NotBlank(message = "بانک گشايش کننده نمي تواند خالي باشد.")
	private String bankLCId;
	@NotBlank(message = "بانک معامله کننده نمي تواند خالي باشد.")
	private String bankLCMoamelehId;
	@NotNull(message = "محصول نمي تواند خالي باشد.")
	private Long goodsId;
	@NotBlank(message = "تاريخ صدور نمي تواند خالي باشد.")
	private String issueDate;
	@NotBlank(message = "تاريخ سررسيد نمي تواند خالي باشد.")
	private String expiryDate;
	@NotNull(message = "مقدار نمي تواند خالي باشد.")
	private BigDecimal amount;
	@NotNull(message = "مبلغ تضميني نمي تواند خالي باشد.")
	private BigDecimal price;
	@NotBlank(message = "شماره اعتباري اسنادي LC نمي تواند خالي باشد.")
	private String lcNumber;
	@NotNull(message = "نوع LC نمي تواند خالي باشد.")
	private Integer type;
	@NotNull(message = "وضعيت LC نمي تواند خالي باشد.")
	private Integer state;
	@NotNull(message = "مدل سند نمي تواند خالي باشد.")
	private PMSLcMarkEnum mark;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder(builderMethodName = "lBuilder", buildMethodName = "lBuild")
	public static class Create extends PMSLcDTO {
		@NotBlank
		private String user;
		@NotBlank
		private String pass;
		@NotNull(message = "نام کاربري خالی مجاز نیست")
		private String username;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder(builderMethodName = "lBuilder", buildMethodName = "lBuild")
	public static class Update extends PMSLcDTO.Create {
		@NotBlank
		private String id;
	}


	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder(builderMethodName = "lBuilder", buildMethodName = "lBuild")
	public static class Info extends PMSLcDTO {
		@NotBlank
		private String id;
		private String user;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder(builderMethodName = "lBuilder", buildMethodName = "lBuild")
	public static class PerGoodItemResponse {
		private Long lcId;
		private Info pmsLcInfo;
	}

	@SuperBuilder(builderMethodName = "lBuilder", buildMethodName = "lBuild")
	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class PMSResponse {
		private Long masterId;
		private List<PerGoodItemResponse> pmsLcResponseLis;
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
