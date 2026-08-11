package com.nicico.internal.sales.ins.InsPmsImeMapping;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "T_INS_PMS_IME_MAPPING")
@Audited
public class InsPmsImeMappingModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -5477610593547189980L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_PMS_IME_MAPPING")
	@SequenceGenerator(name = "SEQ_INS_PMS_IME_MAPPING", sequenceName = "SEQ_INS_PMS_IME_MAPPING", allocationSize = 1)
	private Long id;
	@Column(name = "C_MAPPING_FOR_OBJECT", nullable = false)
	@Enumerated(EnumType.STRING)
	private InsPmsImeMappingTypeEnum mappingFor;
	@Column(name = "N_INS_ID")
	private Long insId;
	@Column(name = "C_INS_ID")
	private String insCode;
	@Column(name = "C_INS_NAME")
	private String insName;
	@Column(name = "B_IS_INS_PK_TYPE_NUMBER", nullable = false, columnDefinition = "NUMBER(1) DEFAULT 1")
	private boolean isInsTypeNumber = true;

	@Column(name = "N_PMS_ID")
	private Long pmsId;
	@Column(name = "C_PMS_ID")
	private String pmsCode;
	@Column(name = "C_PMS_NAME")
	private String pmsName;
	@Column(name = "B_IS_PMS_PK_TYPE_NUMBER", nullable = false, columnDefinition = "NUMBER(1) DEFAULT 0")
	private boolean isPmsTypeNumber = false;

	@Column(name = "N_IME_ID")
	private Long imeId;
	@Column(name = "C_IME_ID")
	private String imeCode;
	@Column(name = "C_IME_NAME")
	private String imeName;
	@Column(name = "B_IS_IME_PK_TYPE_NUMBER", nullable = false, columnDefinition = "NUMBER(1) DEFAULT 1")
	private boolean isImeTypeNumber = true;

	@Column(name = "C_INS_GET_BY_ID_SERVICE_PACKAGE_NAME")
	private String insGetByIDServicePackageName;
	@Column(name = "C_INS_GET_BY_ID_SERVICE_METHOD_NAME")
	private String insGetByIDServiceMethodName;
}