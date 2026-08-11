package com.nicico.internal.sales.wf.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Audited
@Table(name = "T_INS_PROCESS_USER_ACCESS")
public class ProcessUserAccessModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = 1264024630987938008L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_PROCESS_USER_ACCESS")
	@SequenceGenerator(name = "SEQ_INS_PROCESS_USER_ACCESS", sequenceName = "SEQ_INS_PROCESS_USER_ACCESS", allocationSize = 1)
	@Column(name = "ID", nullable = false)
	private Long id;
	@Column(name = "C_PROCESS_ID", length = 50, nullable = false)
	private String processId;
	@Column(name = "C_PROCESS_TITLE", length = 100, nullable = false)
	private String processTitle;
	@Column(name = "C_PROCESS_LOCAL_TITLE", length = 200, nullable = false)
	private String processLocalTitle;
	@Column(name = "C_PROCESS_VARIABLE", length = 200, nullable = false)
	private String processVariable;
	@Column(name = "C_PROCESS_VARIABLE_TITLE", length = 200, nullable = false)
	private String processVariableTitle;
	@Column(name = "N_USER_ID", nullable = false)
	private Long userId;
	@Column(name = "C_USERNAME", length = 100, nullable = false)
	private String username;
	@Column(name = "C_FULL_NAME", length = 200, nullable = false)
	private String fullName;
	@Column(name = "C_NATIONAL_CODE", length = 10, nullable = false)
	private String nationalCode;
}
