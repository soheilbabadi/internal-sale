package com.nicico.internal.sales.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@Audited
public abstract class BaseClassModel implements Serializable {
	@Serial
	private static final long serialVersionUID = 8869654958678966384L;
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	@Column(name = "d_created_date", nullable = false, updatable = false)
	private Date createdDate;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "d_last_modified_date")
	@org.hibernate.annotations.UpdateTimestamp
	@LastModifiedDate
	private Date lastModifiedDate;
	@Column(name = "c_created_by", length = 250, nullable = false, updatable = false)
	@CreatedBy
	private String createdBy;
	@LastModifiedBy
	@Column(name = "c_last_modified_by", length = 250)
	private String lastModifiedBy;
	@Column(name = "c_comment", length = 4000)
	private String comment;
	@Column(name = "c_description", length = 4000)
	private String description;
	@Version
	@NotAudited
	@Column(name = "n_version", nullable = false)
	private Integer version;
}
