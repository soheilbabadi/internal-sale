package com.nicico.internal.sales.wf.dto.request;

import com.nicico.internal.sales.wf.dto.AuthorityTypeKeyValue;
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
public class RoleAccessRequest implements Serializable {
	@Serial
	private static final long serialVersionUID = 1801711825772737601L;
	@Schema(description = "شناسه نقش", example = "1", name = "roleId")
	private Long roleId;
	@Schema(description = "حق دسترسی بصورت کلید مقدار. کلید برای نام فرایند و مقدار برای نوع دسترسی", example = "<aa351-ac8c2-a9ec1,START_PROCESS>", name = "authorityTypeKey")
	private List<AuthorityTypeKeyValue> authorityTypeKey;
}
