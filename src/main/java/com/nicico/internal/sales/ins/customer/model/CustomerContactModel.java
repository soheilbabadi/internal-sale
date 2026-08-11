package com.nicico.internal.sales.ins.customer.model;

import com.nicico.internal.sales.config.BaseClassModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serial;

@Audited
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "T_INS_CUSTOMER_CONTACT")
public class CustomerContactModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -5477610593547189980L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_CUSTOMER_CONTACT")
	@SequenceGenerator(name = "SEQ_INS_CUSTOMER_CONTACT", sequenceName = "SEQ_INS_CUSTOMER_CONTACT", allocationSize = 1)
	@Column(name = "ID")
	private Long id;
	@Column(name = "C_PHONE")
	private String phone;
	@Column(name = "C_POST_CODE", length = 10)
	private String postCode;
	@Email(message = "email should be valid")
	@Column(name = "C_EMAIL", length = 100)
	private String email;
	//@Pattern(regexp = "^09\\d{9}$", message = "mobile number must start with 09 and be 11 digits long")
	@Column(name = "C_MOBILE", length = 11)
	private String mobile;
	@Column(name = "C_COORDINATOR", length = 100)
	private String coordinator;
	@Column(name = "C_ADDRESS", length = 4000)
	private String address;
	@Column(name = "N_CUSTOMER_ID")
	private Long customerId;
	@Column(name = "IS_VALID")
	private boolean isValid;
	@Column(name = "IS_DEFAULT")
	private boolean isDefault;

}