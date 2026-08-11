package com.nicico.internal.sales.wf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthorityTypeKeyValue implements Serializable {
	@Serial
	private static final long serialVersionUID = -9158296363786509238L;
	@NotBlank
	private String processId;
	@NotBlank
	private String authorityType;
}
