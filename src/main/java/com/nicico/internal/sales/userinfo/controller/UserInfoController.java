package com.nicico.internal.sales.userinfo.controller;

import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.userinfo.dto.UserInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
@RestController
@RequestMapping("/api/v1/ins/user-info")
public class UserInfoController {
	@GetMapping
	public ResponseEntity<?> getCurrentUserInfo() {
		UserInfoDto userInfoDto = new UserInfoDto();
		userInfoDto.setAuthority(SecurityUtil.getAuthorities());
		userInfoDto.setUsername(SecurityUtil.getUsername());
		userInfoDto.setFirstName(SecurityUtil.getFirstName());
		userInfoDto.setUserId(SecurityUtil.getUserId());
		userInfoDto.setLastName(SecurityUtil.getLastName());
		userInfoDto.setNationalCode(SecurityUtil.getNationalCode());
		return ResponseEntity.ok(userInfoDto);
	}
}
