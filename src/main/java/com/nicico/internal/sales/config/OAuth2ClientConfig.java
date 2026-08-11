package com.nicico.internal.sales.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@ConditionalOnClass(OAuth2AuthorizedClientService.class)
public class OAuth2ClientConfig {

	@Bean
	@ConditionalOnMissingBean(OAuth2AuthorizedClientService.class)
	public OAuth2AuthorizedClientService oauth2AuthorizedClientService(
			ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider) {

		ClientRegistrationRepository clientRegistrationRepository = clientRegistrationRepositoryProvider.getIfAvailable();
		if (clientRegistrationRepository != null) {
			return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
		}

		// Fallback for environments where client registrations are intentionally disabled.
		return new OAuth2AuthorizedClientService() {
			@Override
			public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
				return null;
			}

			@Override
			public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
				// no-op
			}

			@Override
			public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
				// no-op
			}
		};
	}
}
