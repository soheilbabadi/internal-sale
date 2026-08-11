package com.nicico.internal.sales.wf.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkflowDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -2424261071181268584L;
	private String id;
	private String processTitle;
	private String processLocalTitle;
	private Integer processVersion;
	private String tenantId;
	private String definitionKey;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("WorkflowDto.Info")
	@NoArgsConstructor
	public static class Info extends WorkflowDto {
		@Serial
		private static final long serialVersionUID = 5418300751327197189L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@ApiModel("WorkflowDto.Create")
	@NoArgsConstructor
	public static class Create extends WorkflowDto {
		@Serial
		private static final long serialVersionUID = 187504434788468059L;
	}
}
