package com.nicico.internal.sales.wf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupTaskActionDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -1851178431121730726L;

	private List<String> taskId;
	private String comment;
}
