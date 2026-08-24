package com.nicico.internal.sales.config;

import com.fgostar.fms.sdk.FmsFileService;
import com.fgostar.fms.sdk.factory.FmsApiClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FmsConfig {

	@Value("${nicico.fms.base-url}")
	private String fmsBaseUrl;

	@Bean(destroyMethod = "close")
	public FmsApiClientFactory fmsApiClientFactory() {
		return FmsApiClientFactory.builder(fmsBaseUrl).build();
	}

	@Bean
	public FmsFileService fmsFileService(FmsApiClientFactory factory) {
		return factory.fileService();
	}
}
