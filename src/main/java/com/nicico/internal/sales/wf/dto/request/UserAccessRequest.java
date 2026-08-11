package com.nicico.internal.sales.wf.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAccessRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = -8993368008019648788L;
	@Schema(description = "شناسه کاربر", example = "1")
	private Long userId;
	@Schema(description = "لیست شناسه های نقش", example = "[1,2,3]")
	private List<Long> roleId;
}
