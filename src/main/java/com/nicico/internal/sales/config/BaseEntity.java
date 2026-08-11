package com.nicico.internal.sales.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity<ID extends Serializable> implements Serializable {
	public static final String DEFAULT_SEQ_GEN = "DEFAULT_SEQ_GEN";

	@Serial
	private static final long serialVersionUID = 5420748073454814337L;
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator = DEFAULT_SEQ_GEN)
	protected ID id;
}