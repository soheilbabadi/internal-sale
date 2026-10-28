package com.nicico.internal.sales.accounting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Criteria implements Serializable {

	private String fieldName;
	private String operator;
	private Object value;
	private Object start;
	private Object end;
	private String _constructor;
	private List<Criteria> criteria;

	public Criteria(String fieldName, String operator, Object value) {
		this.fieldName = fieldName;
		this.operator = operator;
		this.value = value;
	}

	public Criteria(String fieldName, EOperator operator, Object value) {
		this(fieldName, operator != null ? operator.name() : null, value);
	}

	public Criteria(String operator) {
		this.operator = operator;
	}

	public Criteria(EOperator operator) {
		this.operator = operator != null ? operator.name() : null;
	}

	public static Criteria equals(String fieldName, Object value) {
		return new Criteria(fieldName, EOperator.equals, value);
	}

	public static Criteria contains(String fieldName, Object value) {
		return new Criteria(fieldName, EOperator.iContains, value);
	}

	public static Criteria startsWith(String fieldName, Object value) {
		return new Criteria(fieldName, EOperator.startsWith, value);
	}

	public static Criteria and(Criteria... criteria) {
		Criteria c = new Criteria("and");
		c.setCriteria(List.of(criteria));
		return c;
	}

	public static Criteria or(Criteria... criteria) {
		Criteria c = new Criteria("or");
		c.setCriteria(List.of(criteria));
		return c;
	}

	public Criteria setConstructor(String constructor) {
		this._constructor = constructor;
		return this;
	}

	public String getConstructor() {
		return _constructor;
	}
}
