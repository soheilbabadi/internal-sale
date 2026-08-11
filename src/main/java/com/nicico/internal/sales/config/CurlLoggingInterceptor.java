package com.nicico.internal.sales.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Interceptor برای لاگ کردن درخواست های HTTP به صورت دستور cURL
 */
@Slf4j
public class CurlLoggingInterceptor implements ClientHttpRequestInterceptor {
	@NotNull
	@Override
	public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body, ClientHttpRequestExecution execution) throws IOException {
		logRequestAsCurl(request, body);
		return execution.execute(request, body);
	}

	private void logRequestAsCurl(HttpRequest request, byte[] body) {
		StringBuilder curlCommand = new StringBuilder("curl -X ");
		// متد HTTP
		curlCommand.append(request.getMethod()).append(" ");
		// URL
		curlCommand.append("'").append(request.getURI()).append("' ");
		// Headers
		request.getHeaders().forEach((headerName, headerValues) -> headerValues.forEach(headerValue -> curlCommand.append("-H '")
				.append(headerName)
				.append(": ")
				.append(headerValue)
				.append("' ")));
		// Body (اگر وجود داشته باشد)
		if (body.length > 0) {
			String bodyString = new String(body, StandardCharsets.UTF_8);
			curlCommand.append("-d '").append(bodyString.replace("'", "\\'")).append("' ");
		}
		log.info("cURL Command: {}", curlCommand.toString().trim());
	}
}