package com.nicico.internal.sales.accounting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CriteriaParamDTO implements Serializable {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private Integer startRow;
	private Integer endRow;
	private String operator;
	private List<Criteria> criteria = new ArrayList<>();
	private String sortBy;
	private Boolean distinct;
	private String _constructor = "AdvancedCriteria";
	private String textMatchStyle;
	private String componentId;
	private String dataSource;
	private String _isc_metaDataPrefix;
	private String _isc_dataFormat;
	private String operationType;
	private String operationId;

	public CriteriaParamDTO(Integer startRow, Integer endRow) {
		this.startRow = startRow;
		this.endRow = endRow;
	}

	public CriteriaParamDTO addCriteria(Criteria c) {
		if (this.criteria == null) {
			this.criteria = new ArrayList<>();
		}
		this.criteria.add(c);
		return this;
	}

	public CriteriaParamDTO setOperator(String operator) {
		this.operator = operator;
		return this;
	}

	public CriteriaParamDTO setConstructor(String constructor) {
		this._constructor = constructor;
		return this;
	}

	public CriteriaParamDTO setIscMetaDataPrefix(String iscMetaDataPrefix) {
		this._isc_metaDataPrefix = iscMetaDataPrefix;
		return this;
	}
	public CriteriaParamDTO setIscDataFormat(String iscDataFormat) {
		this._isc_dataFormat = iscDataFormat;
		return this;
	}

	public static CriteriaParamDTO of(Integer startRow, Integer endRow) {
		return new CriteriaParamDTO(startRow, endRow);
	}

	public static CriteriaParamDTO of(Integer startRow, Integer endRow, Criteria... criteria) {
		CriteriaParamDTO dto = new CriteriaParamDTO(startRow, endRow);
		if (criteria != null) {
			dto.setCriteria(new ArrayList<>(Arrays.asList(criteria)));
		}
		return dto;
	}

	public Map<String, String> toQueryMap() {
		Map<String, String> map = new LinkedHashMap<>();
		if (operator != null) map.put("operator", operator);
		if (_constructor != null) map.put("_constructor", _constructor);
		if (startRow != null) map.put("_startRow", String.valueOf(startRow));
		if (endRow != null) map.put("_endRow", String.valueOf(endRow));
		if (sortBy != null) map.put("_sortBy", sortBy);
		if (textMatchStyle != null) map.put("textMatchStyle", textMatchStyle);
		if (componentId != null) map.put("componentId", componentId);
		if (dataSource != null) map.put("dataSource", dataSource);
		if (operationType != null) map.put("operationType", operationType);
		if (operationId != null) map.put("operationId", operationId);
		if (_isc_metaDataPrefix != null) map.put("isc_metaDataPrefix", _isc_metaDataPrefix);
		if (_isc_dataFormat != null) map.put("isc_dataFormat", _isc_dataFormat);

		if (criteria != null && !criteria.isEmpty()) {
			try {
				if (criteria.size() == 1) {
					map.put("criteria", OBJECT_MAPPER.writeValueAsString(criteria.get(0)));
				} else {
					map.put("criteria", OBJECT_MAPPER.writeValueAsString(criteria));
				}
			} catch (JsonProcessingException e) {
				map.put("criteria", criteria.toString());
			}
		}
		return map;
	}
}
