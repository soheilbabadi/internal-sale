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
public class SearchInDetailsRequest implements Serializable {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private String operator = "and";
	private String _constructor = "AdvancedCriteria";
	private List<Criteria> criteria = new ArrayList<>();
	private String operationType = "fetch";
	private Integer startRow = 0;
	private Integer endRow = 400;
	private String sortBy = "detailNumber";
	private String orderBy;
	private String textMatchStyle = "substring";
	private String componentId = "isc_ListGrid_5";
	private String dataSource = "isc_MyRestDataSource_66";
	private String _isc_dataFormat = "json";
	private String _isc_metaDataPrefix = "";
	private String operationId;

	public static SearchInDetailsRequest of(Criteria... criteria) {
		SearchInDetailsRequest req = new SearchInDetailsRequest();
		if (criteria != null) {
			req.setCriteria(new ArrayList<>(Arrays.asList(criteria)));
		}
		return req;
	}

	public Map<String, String> toFieldMap() {
		Map<String, String> map = new LinkedHashMap<>();
		if (operator != null) map.put("operator", operator);
		if (_constructor != null) map.put("_constructor", _constructor);

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

		if (operationType != null) map.put("operationType", operationType);
		if (startRow != null) map.put("startRow", String.valueOf(startRow));
		if (endRow != null) map.put("endRow", String.valueOf(endRow));
		if (sortBy != null) map.put("sortBy", sortBy);
		if (orderBy != null) map.put("orderBy", orderBy);
		if (textMatchStyle != null) map.put("textMatchStyle", textMatchStyle);
		if (componentId != null) map.put("componentId", componentId);
		if (dataSource != null) map.put("dataSource", dataSource);
		if (_isc_metaDataPrefix != null) map.put("isc_metaDataPrefix", _isc_metaDataPrefix);
		if (_isc_dataFormat != null) map.put("isc_dataFormat", _isc_dataFormat);
		if (operationId != null) map.put("operationId", operationId);

		return map;
	}
}
