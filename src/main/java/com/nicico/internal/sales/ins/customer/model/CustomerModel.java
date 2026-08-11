package com.nicico.internal.sales.ins.customer.model;

import com.nicico.internal.sales.config.BaseClassModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serial;

@Entity
@Data
@Table(name = "T_INS_CUSTOMER")
@Audited
public class CustomerModel extends BaseClassModel {
	@Serial
	private static final long serialVersionUID = -5477610593547189980L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INS_CUSTOMER")
	@SequenceGenerator(name = "SEQ_INS_CUSTOMER", sequenceName = "SEQ_INS_CUSTOMER", allocationSize = 1)
	private Long id;
	@NotBlank
	@Column(nullable = false, name = "C_NAME", length = 100)
	private String name;
	@Column(name = "C_NAME_EN", length = 100)
	private String nameEn;
	@NotBlank
	@Column(name = "C_NATIONAL_CODE", unique = true, nullable = false, length = 15)
	private String nationalCode;
	@Column(name = "C_PMS_CUSTOMER_CODE")
	private String pmsCustomerCode;
	@Column(name = "N_IME_CUSTOMER_CODE")
	private Long imeCustomerId;
	@Column(name = "C_PHONE")
	private String phone;
	@Column(name = "C_ECONOMIC_CODE", length = 50)
	private String economicCode;
	@Column(name = "C_REGISTER_NUMBER", length = 50)
	private String registerNumber;
	@Column(name = "C_POST_CODE", length = 10)
	private String postCode;
	@Email(message = "email should be valid")
	@Column(name = "C_EMAIL", length = 100)
	private String email;
	//@Pattern(regexp = "^09\\d{9}$", message = "mobile number must start with 09 and be 11 digits long")
	@Column(name = "C_MOBILE", length = 11)
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
	@Schema(description = "کد نقش تاجر در سامانه جامع تجارت")
	@Column(name = "C_TRADE_ROLE_CODE", length = 50)
	private String tradeRoleCode;

}