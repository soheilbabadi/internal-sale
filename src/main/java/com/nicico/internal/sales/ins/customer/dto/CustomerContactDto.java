package com.nicico.internal.sales.ins.customer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerContactDto implements Serializable {
	@Serial
	private static final long serialVersionUID = -5004064808385095447L;
	@Schema(description = "شناسه", example = "1")
	private Long id;
	@Schema(description = "شماره تماس", example = "021-12345678")
	private String phone;
	@Pattern(regexp = "^\\d{10}$", message = "کد پستی باید ده رقم باشد")
	@Schema(description = "کد پستی", example = "1234567890")
	private String postCode;
	@Schema(description = "ایمیل", example = "sb@gmail.com")
	@Email(message = "ایمیل معتبر نیست")
	private String email;
	@Schema(description = "شماره موبایل", example = "09123456789")
	@Pattern(regexp = "^09\\d{9}$", message = "شماره موبایل معتبر نیست")
	private String mobile;
	@Schema(description = "نام مسئول", example = "محمدرضا")
	private String coordinator;
	@Schema(description = "آدرس", example = "تهران، خیابان ولیعصر")
	private String address;
	@Schema(description = "شناسه مشتری", example = "1324576")
	private Long customerId;
	@Schema(description = "وضعیت", example = "true")
	private boolean isValid;
	@Schema(description = "پیش فرض", example = "true")
	private boolean isDefault;

	@EqualsAndHashCode(callSuper = true)
	@Data
	@NoArgsConstructor
	@ApiModel("CustomerContactDto.Create")
	public static class Create extends CustomerContactDto {
		@Serial
		private static final long serialVersionUID = 9130204397605661901L;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	@NoArgsConstructor
	@ApiModel("CustomerContactDto.Info")
	public static class Info extends CustomerContactDto {
		@Serial
		private static final long serialVersionUID = 5366637728002836316L;
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}
}
