package com.nicico.internal.sales.config.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Value("${spring.security.oauth2.client.registration.oserver.client-id}")
	private String clientId;
	@Value(("${spring.security.oauth2.client.registration.oserver.client-secret}"))
	private String clientSecret;
	@Value("${nicico.swagger}")
	private String accessTokenUri;

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build().securityContexts(Collections.singletonList(securityContext())).securitySchemes(List.of(securitySchema())).apiInfo(getApiInfo());
	}

	private OAuth securitySchema() {
		List<AuthorizationScope> authorizationScopeList = newArrayList();
		authorizationScopeList.add(new AuthorizationScope("user_info", ""));
		List<GrantType> grantTypes = newArrayList();
		GrantType passwordCredentialsGrant = new ResourceOwnerPasswordCredentialsGrant(accessTokenUri);
		grantTypes.add(passwordCredentialsGrant);
		return new OAuth("oauth2", authorizationScopeList, grantTypes);
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).build();
	}

	private List<SecurityReference> defaultAuth() {
		final AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = new AuthorizationScope("user_info", "");
		return Collections.singletonList(new SecurityReference("oauth2", authorizationScopes));
	}

	@Bean
	public SecurityConfiguration security() {
		return SecurityConfigurationBuilder.builder().clientId(clientId).clientSecret(clientSecret).realm("").appName("").scopeSeparator("").build();
	}

	private ApiInfo getApiInfo() {
		return new ApiInfo("NICICO", "NICICO API for Sales", "1.0", null, new Contact("Admin", "https://nicico.ir", "admin@nicico.com"), null, null, Collections.emptyList());
	}
}
