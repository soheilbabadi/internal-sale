package com.nicico.internal.sales.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DatabaseConfig {
	// Getters and setters
	private String url;
	private String username;
	private String password;
	private String driverClassName;

}