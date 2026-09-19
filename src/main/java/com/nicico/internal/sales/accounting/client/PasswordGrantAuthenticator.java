package com.nicico.internal.sales.accounting.client;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.IOException;

@Slf4j
public class PasswordGrantAuthenticator implements Authenticator {

	private final OAuth2RestTemplate oAuth2RestTemplate;
	private volatile OAuth2AccessToken token;

	public PasswordGrantAuthenticator(
			String accessTokenUri,
			String clientId,
			String clientSecret,
			String username,
			String password
	) {
		ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
		resourceDetails.setGrantType("password");
		resourceDetails.setAccessTokenUri(accessTokenUri);
		resourceDetails.setClientId(clientId);
		resourceDetails.setClientSecret(clientSecret);
		resourceDetails.setUsername(username);
		resourceDetails.setPassword(password);

		this.oAuth2RestTemplate = new OAuth2RestTemplate(resourceDetails);
		try {
			this.token = oAuth2RestTemplate.getAccessToken();
			log.info("## PasswordGrantAuthenticator: Initial system token acquired for user {}.", username);
		} catch (Exception e) {
			log.warn("## PasswordGrantAuthenticator: Initial token acquisition deferred: {}", e.getMessage());
		}
	}

	public PasswordGrantAuthenticator(OAuth2RestTemplate oAuth2RestTemplate) {
		this.oAuth2RestTemplate = oAuth2RestTemplate;
	}

	/**
	 * Returns the current valid token, fetching a new one if necessary.
	 * This method is thread-safe.
	 */
	public synchronized String getValidToken() {
		try {
			this.token = oAuth2RestTemplate.getAccessToken();
			return this.token.getValue();
		} catch (Exception e) {
			log.error("Could not get a valid token from the authentication server.", e);
			throw new InternalSaleCustomException.ApplicationServerException(
					"accounting.system.token.acquire.failed: " + e.getMessage());
		}
	}

	@Nullable
	@Override
	public Request authenticate(Route route, Response response) throws IOException {
		log.warn("API request to {} failed with 401. Attempting to re-authenticate.", response.request().url());

		String currentTokenValue = (this.token != null) ? this.token.getValue() : "";
		String failedRequestToken = response.request().header("Authorization");

		synchronized (this) {
			if (failedRequestToken != null && !failedRequestToken.equals("Bearer " + currentTokenValue)) {
				log.info("Token was already refreshed by another thread. Retrying request.");
				return response.request().newBuilder()
						.header("Authorization", "Bearer " + this.token.getValue())
						.build();
			}

			log.info("Forcing re-authentication for system user.");
			if (oAuth2RestTemplate.getOAuth2ClientContext() != null) {
				oAuth2RestTemplate.getOAuth2ClientContext().setAccessToken(null);
			}
			try {
				String newToken = getValidToken();
				log.info("Successfully re-authenticated. Retrying the failed API request.");
				return response.request().newBuilder()
						.header("Authorization", "Bearer " + newToken)
						.build();
			} catch (Exception e) {
				log.error("!!! Critical: Failed to re-authenticate system user after 401. Cannot retry request.", e);
				return null;
			}
		}
	}
}
