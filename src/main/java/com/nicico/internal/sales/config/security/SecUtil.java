package com.nicico.internal.sales.config.security;

import com.nicico.copper.core.SecurityUtil;
import org.springframework.stereotype.Service;

@Service
public class SecUtil {
	public boolean hasAuthority(String authority) {
		return SecurityUtil.isAdmin() || SecurityUtil.hasAuthority(authority);
	}
}
