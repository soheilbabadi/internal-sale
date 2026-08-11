package com.nicico.internal.sales.config.security;

import com.nicico.copper.common.config.CORSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
	@Bean
	public FilterRegistrationBean<CORSFilter> corsFilterRegistration(CORSFilter corsFilter) {
		FilterRegistrationBean<CORSFilter> registrationBean = new FilterRegistrationBean<>(corsFilter);
		registrationBean.addUrlPatterns("/api/v1/ins/performa/*");
		registrationBean.setEnabled(false);
		return registrationBean;
	}
}