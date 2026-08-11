package com.nicico.internal.sales.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "message-service")
public class MessageServiceProperties {
	private Sms sms;

	@Data
	public static class Sms {
		private Pattern pattern;
	}

	@Data
	public static class Pattern {
		private Url url;
		private Patterns patterns;
	}

	@Data
	public static class Url {
		private String nimad;
	}

	@Data
	public static class Patterns {
		private String preFactorEmailed;
	}
}