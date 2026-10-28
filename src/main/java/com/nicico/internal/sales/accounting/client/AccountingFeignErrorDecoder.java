package com.nicico.internal.sales.accounting.client;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class AccountingFeignErrorDecoder implements ErrorDecoder {

	private final PasswordGrantAuthenticator authenticator;
	private final ErrorDecoder defaultDecoder = new Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		byte[] bodyBytes = new byte[0];
		String errorBody = "";
		if (response.body() != null) {
			try {
				bodyBytes = IOUtils.toByteArray(response.body().asInputStream());
				errorBody = new String(bodyBytes, StandardCharsets.UTF_8);
			} catch (IOException e) {
				errorBody = "[Failed to read error body: " + e.getMessage() + "]";
			}
		}

		Response responseWithBody = response.toBuilder().body(bodyBytes).build();

		int status = response.status();
		log.error("Feign call '{}' returned HTTP status {}: {}", methodKey, status, errorBody);

		if (status == 401) {
			log.warn("Received 401 Unauthorized for '{}'. Refreshing token and scheduling retry.", methodKey);
			try {
				authenticator.refreshToken();
				// Throwing RetryableException causes Feign's Retryer to retry this request with the new token
				return new RetryableException(
						response.status(),
						"Unauthorized 401 - token refreshed, retrying: " + errorBody,
						response.request().httpMethod(),
						null,
						response.request()
				);
			} catch (Exception e) {
				log.error("Failed to refresh token after 401 error: {}", e.getMessage(), e);
				return new InternalSaleCustomException.ApplicationServerException(
						"Failed to refresh accounting authentication token: " + e.getMessage());
			}
		}

		if (status == 400 || status == 422) {
			return new InternalSaleCustomException.ValidationException(
					"Accounting service validation failed: " + errorBody);
		} else if (status == 404) {
			return new InternalSaleCustomException.ResourceNotFoundException(
					"Accounting resource not found: " + errorBody);
		} else if (status >= 500) {
			return new InternalSaleCustomException.ApplicationServerException(
					"Accounting API server error [" + status + "]: " + errorBody);
		}

		return defaultDecoder.decode(methodKey, responseWithBody);
	}
}
