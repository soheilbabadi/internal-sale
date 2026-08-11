package com.nicico.internal.sales.pms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nicico.internal.sales.pms.enums.HavStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder(builderMethodName = "builderr", buildMethodName = "buildd")
public class PMSRemittanceDTO {

	/**
	 * Without default Values
	 */

	@JsonProperty("buyerId")
	@Schema(description = "خریدار")
	@NotBlank(message = "خريدار بايد انتخاب شود")
	private String pmsBuyerId;

	@JsonProperty("productId")
	@Schema(description = "کد کالا در لجستیک")
	@NotNull(message = "کالا بايد وارد شود.")
	private Long pmsGoodId;

	@NotNull(message = "واحد بسته بندي بايد وارد شود.")
	@JsonProperty("productUnit")
	@Schema(description = "واحد بسته بندی")
	private Long pmsGoodUnit;

	// input data
	@NotBlank(message = "موضوع بايد وارد شود.")
	@Schema(description = "موضوع حواله")
	private String name;
	@NotBlank(message = "تاريخ صدور حواله بايد وارد شود.")
	private String issueDate;
	@NotNull(message = "مقدار بايد وارد شود.")
	private BigDecimal amount;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Accessors(chain = true)
	@SuperBuilder(builderMethodName = "builderr", buildMethodName = "buildd")
	public static class Nullables extends PMSRemittanceDTO {
		/**
		 * With default Values
		 */
		@NotNull(message = "نوع حواله نبايد خالي باشد.")
		@JsonProperty("processId")
		@Schema(description = "[{\"id\":\"281\",\"title\":\"فعال\"},{\"id\":\"282\",\"title\":\"غيرفعال\"}]"
				, defaultValue = "282")
		@Builder.Default
		private Integer processId = 42;
		@NotNull(message = "گروه حواله بايد مشخص شود.")
		@JsonProperty("havalehGroupId")
		@Schema(description = "احتمالا گروه حواله پیش فرض 42")
		@Builder.Default
		private Integer stcProcess = 42;

		@Schema(description = "فروشنده پیش فرض شرکت ملی صنایع مس ایران", defaultValue = "1-1060")
		@NotBlank(message = "فروشنده بايد انتخاب شود.")
		@Builder.Default
		private String sellerId = "1-1060";
		@NotNull(message = "محل بارگيري بايد وارد شود.")
		@JsonProperty("loadId")
		@Schema(description = "محل بارگیری")
		@Builder.Default
		private Long loadId = 1000L; // PMSRemittanceLoadEnum

		@NotNull(message = "صادر کننده حواله بايد وارد شود.")
		@Schema(description = "صادر کننده پیش فرض فروش داخلی", defaultValue = "1061")
		@JsonProperty("havalehFrom")
		@Builder.Default
		private Long from = 1061L;
		@NotNull(message = "حواله وارده بايد وارد شود.")
		@JsonProperty("havalehTo")
		@Schema(description = "وارده پیش فرض اداره هماهنگی فروش", defaultValue = "1040")
		@Builder.Default
		private Long to = 1040L;
		@NotNull(message = "وضعيت حواله بايد وارد شود.")
		@JsonProperty("havalehStatus")
		@Builder.Default
		private HavStatusEnum remittanceStatus = HavStatusEnum.TANZIM_HAVALEH;
		/**
		 * Nullables
		 */
		private Integer unLoadId;
		private String validUntil;
		private Boolean isFinal;

		private String buyDate;
		private String lastPayDate;
		private String description;
		@Schema(description = "پیمان کار")
		private String contractorId;
		private String code;
		private String status;
		private BigDecimal price;
		private String contractCode;
		private String contractDescription;
		private String sanadCode;
		private String contractDate;
		private String contractValidDate;
		private String payFeshNumber;
		private String payFeshDate;
		private Integer payBankCode;
		private String ldFesh;
		private String ldFeshDate;
		private Integer ldBankCode;
		private Integer pdBankCode;
		private String pdFeshDate;
		private String pdFesh;
		private String payType;
		private String paymankarKh;
		private String finalDate;
		private String finalUser;
		private Integer invoiceState;
		private BigDecimal financeCustomerId;
		private Integer cashPercent;
		private Integer creditPercent;
		private BigDecimal cashUnitPrice;
		private BigDecimal creditUnitPrice;
		private String lcId;
		private String prefactorId;
		private Integer pollutionCashPercent;
		private Integer pollutionCreditPercent;
		private Integer taxCashPercent;
		private Integer taxCreditPercent;
		private String brokerId;
		private String lotNumber;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Accessors(chain = true)
	@SuperBuilder(builderMethodName = "builderr", buildMethodName = "buildd")
	public static class Create extends PMSRemittanceDTO.Nullables {
		@NotBlank(message = "نام کاربري خالی مجاز نیست")
		private String user;
		@NotBlank(message = "رمز عبور خالی مجاز نیست")
		private String pass;
		@NotBlank(message = "مقدار خالی مجاز نیست")
		private String username;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Accessors(chain = true)
	@SuperBuilder(builderMethodName = "builderr", buildMethodName = "buildd")
	public static class Update extends PMSRemittanceDTO.Create {
		private String id;
		private Integer version;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@NoArgsConstructor
	@Accessors(chain = true)
	public static class Info extends PMSRemittanceDTO.Nullables {
		@Schema(description = "ایجاد شده توسط")
		private String createdBy;
		@Schema(description = "بروزرسانی شده توسط")
		private String updatedBy;
		@Schema(description = "تاریخ ایجاد")
		private String createdAt;
		@Schema(description = "تاریخ بروزرسانی")
		private String updatedAt;
		@Schema(description = "آخرین زمان بروزرسانی")
		private String lastUpdateTime;
		@Schema(description = "زمان ایجاد")
		private String createTime;
		@Schema(description = "رزرو")
		private String reserve;
	}

	@Data
	@NoArgsConstructor
	@Accessors(chain = true)
	@SuperBuilder(builderMethodName = "builderr", buildMethodName = "buildd")
	public static class Response {
		private Long remittanceMasterId;
		private PMSRemittanceDTO.Info pmsRemittance;
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
