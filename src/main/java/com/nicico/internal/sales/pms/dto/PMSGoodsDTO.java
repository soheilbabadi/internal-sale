package com.nicico.internal.sales.pms.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
public class PMSGoodsDTO {
	private String code;
	private String name;
	private String stock;
	private String packingCode;
	private String nosaCode;
	private String costCenterCode;
	private String nosaAlayandeghiCode;
	private Integer groupGssId;

	@AllArgsConstructor
	@Getter
	@Setter
	public static class Info extends PMSGoodsDTO {
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}
}
