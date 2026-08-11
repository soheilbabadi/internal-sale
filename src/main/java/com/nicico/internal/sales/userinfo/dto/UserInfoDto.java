package com.nicico.internal.sales.userinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserInfoDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 270277806407949279L;
	private Long userId;
	private String firstName;
	private String lastName;
	private String nationalCode;
	private String username;
	private Set<String> authority;
}
