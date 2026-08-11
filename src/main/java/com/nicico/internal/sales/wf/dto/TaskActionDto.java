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
public class TaskActionDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -1851178431121730726L;
	@NotBlank
	private String taskId;
	private String comment;
	private Boolean approve;
}
