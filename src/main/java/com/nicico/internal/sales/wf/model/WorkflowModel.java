package com.nicico.internal.sales.wf.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "T_INS_WORKFLOW")
public class WorkflowModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -6867625358198560130L;
	@Id
	@Column(name = "ID", length = 100)
	private String id;
	@Column(name = "C_PROCESS_TITLE", length = 100, nullable = false)
	private String processTitle;
	@Column(name = "C_PROCESS_LOCAL_TITLE", length = 100, nullable = false)
	private String processLocalTitle;
	@Column(name = "N_PROCESS_VERSION")
	private Integer processVersion;
	@Column(name = "C_TENANT_ID", length = 100, nullable = false)
	private String tenantId;
	@Column(name = "C_PROCESS_DEFINITION_KEY", length = 100, nullable = false)
	private String definitionKey;
}
