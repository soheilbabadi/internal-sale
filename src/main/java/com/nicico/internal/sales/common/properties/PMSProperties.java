package com.nicico.internal.sales.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("pms")
public class PMSProperties {
	private PMSProperties.PreFactor preFactor;
	private PMSProperties.Lc lc;
	private PMSProperties.Remittance remittance;
	private PMSProperties.Bank bank;
	private Customer customer;

	@Data
	public static class PreFactor {
		private String url;
		private String user;
		private String pass;
		private String defaultPmsUser;
	}

	@Data
	public static class Lc {
		private String url;
		private String defaultPmsUser;
	}

	@Data
	public static class Bank {
		private String url;
	}

	@Data
	public static class Customer {
		private String url;
	}

	@Data
	public static class Remittance {
		private String url;
		private String titleBase;
	}
}
