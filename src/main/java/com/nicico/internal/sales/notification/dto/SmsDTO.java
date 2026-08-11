package com.nicico.internal.sales.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@Builder(builderMethodName = "builderr", buildMethodName = "buildd")
public class SmsDTO {
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Accessors(chain = true)
	@Builder(builderMethodName = "builderr", buildMethodName = "buildd")
	public static class SMSServicePattern {
		private String pid;
		private List<String> to;
		private SMSServicePattern.Params params;

		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		@Accessors(chain = true)
		@Builder(builderMethodName = "builderr", buildMethodName = "buildd")
		public static class Params {
			@JsonProperty("company_name")
			String companyName;
			@JsonProperty("date")
			String date_;
			@JsonProperty("contract_number")
			String contractNumber;
		}
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Accessors(chain = true)
	@Builder(builderMethodName = "builderr", buildMethodName = "buildd")
	public static class ResponseReceiver {
		String number;
		String trackingNumber;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Accessors(chain = true)
	@Builder(builderMethodName = "builderr", buildMethodName = "buildd")
	public static class Response {
		String status;
		String message;
		List<ResponseReceiver> receivers;
	}
}
