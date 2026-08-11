package com.nicico.internal.sales.ime.commodity.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Data
@NoArgsConstructor
public class IMECommodityDTO {
	private Long id;
	private Long commodityId;
	private String description;
	private String persianName;
	private Long parentId;
	private String symbol;
	private String createdBy;
	private String lastModifiedBy;
	private String comment;
	private String clobOptions; // Consider using a specific type like Clob or Text
	private Date responseDate;
	private String messageText;

	@Getter
	@Setter
	@ApiModel("IMECommodityDTO.Info")
	public static class Info extends IMECommodityDTO {
		private Date createdDate;
		private Date lastModifiedDate;
		private String createdBy;
		private String lastModifiedBy;
		private String comment;
	}
}
