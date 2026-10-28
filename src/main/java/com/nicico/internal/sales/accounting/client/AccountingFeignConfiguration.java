package com.nicico.internal.sales.accounting.client;

import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AccountingFeignConfiguration {

	@Bean
	public RequestInterceptor accountingFeignRequestInterceptor(PasswordGrantAuthenticator authenticator) {
		return new AccountingFeignRequestInterceptor(authenticator);
	}

	@Bean
	public ErrorDecoder accountingFeignErrorDecoder(PasswordGrantAuthenticator authenticator) {
		return new AccountingFeignErrorDecoder(authenticator);
	}

	/**
	 * Configures a Feign Retryer:
	 * - period: initial backoff interval (500ms)
	 * - maxPeriod: maximum backoff interval (2000ms)
	 * - maxAttempts: 3 (1 initial call + up to 2 retries on timeout or 401 re-auth)
	 */
	@Bean
	public Retryer accountingFeignRetryer() {
		return new Retryer.Default(500, TimeUnit.SECONDS.toMillis(2), 3);
	}

	@Bean
	public Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
}
