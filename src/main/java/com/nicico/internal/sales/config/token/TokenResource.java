package com.nicico.internal.sales.config.token;

import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.common.properties.OAuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Controller
public class TokenResource {
	private final OAuthProperties oAuthProperties;
	private final TokenStore tokenStore;

	@RequestMapping("/")
	public ResponseEntity<HttpStatus> redirectToHomePage(HttpServletRequest request, HttpServletResponse httpServletResponse) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String accessToken;
		if (authentication.getDetails() instanceof WebAuthenticationDetails) {
			accessToken = ((DefaultOAuth2User) authentication.getPrincipal()).getAttributes().get("AccessToken").toString();
		} else {
			accessToken = ((OAuth2AuthenticationDetails) authentication.getDetails()).getTokenValue();
		}
		if (tokenStore.readAccessToken(accessToken) == null) {
			SecurityContextHolder.clearContext();
			httpServletResponse.setHeader(HttpHeaders.LOCATION, "/");
		} else {
			httpServletResponse.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
			httpServletResponse.setHeader(HttpHeaders.LOCATION, oAuthProperties.getRedirectAddress() + "?token=" + accessToken + "&" + "landingAddress=" + oAuthProperties.getLandingAddress() + "&" + "userId=" + SecurityUtil.getUserId());
		}
		return new ResponseEntity<>(HttpStatus.MOVED_PERMANENTLY);
	}
}