package com.nicico.internal.sales.accounting.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@Slf4j
@RequiredArgsConstructor
public class AccountingFeignRequestInterceptor implements RequestInterceptor {

	private final PasswordGrantAuthenticator authenticator;

	@Override
	public void apply(RequestTemplate template) {
		String token = authenticator.getValidToken();
		template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
	}
}
