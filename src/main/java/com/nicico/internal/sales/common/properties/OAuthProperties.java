package com.nicico.internal.sales.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "ui")
public class OAuthProperties {
	private String redirectAddress;
	private String landingAddress;
}
