package com.nicico.internal.sales.accounting.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AccountingSecurityConfiguration {

	@Value("${spring.security.oauth2.client.provider.oserver.token-uri:http://devapp01.icico.net.ir/oauth/token}")
	private String accessTokenUri;

	@Value("${spring.security.oauth2.client.registration.oserver.client-id:${spring.application.name}}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.oserver.client-secret:password}")
	private String clientSecret;

	@Value("${spring.application.name:internal-sales}")
	private String appName;

	@Value("${nicico.security.sysPassword}")
	private String sysPassword;

	/**
	 * Creates the PasswordGrantAuthenticator bean for system user authentication.
	 */
	@Bean
	public PasswordGrantAuthenticator passwordGrantAuthenticator() {
		return new PasswordGrantAuthenticator(
				accessTokenUri,
				clientId,
				clientSecret,
				String.format("sys_%s", appName),
				sysPassword
		);
	}
}
