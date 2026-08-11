package com.nicico.internal.sales.wf.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.*;

import javax.persistence.*;
import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "T_INS_WF_USER", uniqueConstraints = {@UniqueConstraint(columnNames = {"C_USER_NAME"})})
public class WorkflowUserModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -546732783107470908L;
	@Id
	@Column(name = "ID", nullable = false)
	private Long id;
	@Column(name = "C_USER_NAME", nullable = false, length = 100, unique = true)
	private String username;
	@Column(name = "C_NATIONAL_CODE", nullable = false, length = 10, unique = true)
	private String nationalCode;
	@Column(name = "C_FULL_NAME", nullable = false, length = 100, unique = true)
	private String fullName;
}
