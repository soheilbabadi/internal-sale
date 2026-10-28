package com.nicico.internal.sales.accounting.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailDto implements Serializable {

	private Long id;
	private String code;
	private String detailName;
	private String note;
	private Integer level;
	private Long childrenDigitCount;
	private Long detailNumber;
	private Boolean dashChildrenCode;
	private Integer version;
	private String blocked;
	private Integer attachmentCount;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Accessors(chain = true)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class DetailInfoDto extends DetailDto {
		private DetailDto parentDetail;
		private Long parentDetailId;
		private Set<DetailTypeDto> detailTypes = new HashSet<>();
		private ContactTuple contact;
		private Long contactId;
		private String detailNameLatin;
		private String detBlockDesc;
		private String detBlockDate;
		private String detBlockReplace;
		private String sourceKind;
		private String standardDesc;
		private Long financialStructureId;
		private Boolean branchFree;
		private DepartmentDto department;
		private Long departmentId;
		private GoodsUnitTuple goodsUnit;
		private Long goodsUnitId;
		private Long currencyUnitId;
		private CurrencyUnitTuple currencyUnit;
		private String unitState;
		private String currencyState;
		private String actionControl;
		private Boolean isFolder;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class ContactTuple implements Serializable {
		private Long id;
		private String name;
		private String nationalcode;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class DepartmentDto implements Serializable {
		private Long id;
		private String name;
		private String code;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class DetailTypeDto implements Serializable {
		private Long id;
		private String name;
		private String code;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class GoodsUnitTuple implements Serializable {
		private Long id;
		private String name;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class CurrencyUnitTuple implements Serializable {
		private Long id;
		private String name;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class WithParent implements Serializable {
		private Long id;
		private String code;
		private String detailName;
		private DetailDto parentDetail;
	}
}
