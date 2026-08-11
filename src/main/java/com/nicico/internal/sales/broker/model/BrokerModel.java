package com.nicico.internal.sales.broker.model;

import com.nicico.internal.sales.config.BaseClassModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import java.io.Serial;

@Entity
@Data
@Table(name = "T_INS_BROKER")
@Builder
@Audited
@AllArgsConstructor
@NoArgsConstructor
public class BrokerModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -552535385170580386L;
	@Id
	private Long id;
	@Column(nullable = false, name = "C_NAME", unique = true)
	private String name;
	@Column(name = "C_NATIONAL_CODE", unique = true, nullable = false, length = 15)
	private String nationalCode;

	@Column(name = "C_PHONE", length = 50)
	private String phone;
	@Column(name = "C_ECONOMIC_CODE", length = 50)
	private String economicCode;
	@Column(name = "C_POST_CODE", length = 10)
	private String postCode;
	@Email(message = "email should be valid")
	@Column(name = "C_EMAIL", length = 100)
	private String email;
	//@Pattern(regexp = "^09\\d{9}$", message = "mobile number must start with 09 and be 11 digits long")
	@Column(name = "C_MOBILE", length = 50)
	private String mobile;
	@Schema(description = "نام مسئول", example = "محمدرضا")
	@Column(name = "C_COORDINATOR", length = 100)
	private String coordinator;
	@Column(name = "C_ADDRESS", length = 4000)
	private String address;
	@Schema(description = "نام مدیر عامل")
	@Column(name = "C_CEO_NAME", length = 100)
	private String ceoName;
	@Schema(description = "شماره تماس مدیر عامل")
	@Column(name = "C_CEO_PHONE", length = 20)
	private String ceoPhone;

}
