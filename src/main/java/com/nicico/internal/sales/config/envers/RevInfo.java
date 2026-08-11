package com.nicico.internal.sales.config.envers;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;

@Setter
@Getter
@Entity
@RevisionEntity(CustomRevisionListener.class)
@Table(name = "REVINFO")
public class RevInfo {
	@Id
	@Column(name = "REV")
	@GeneratedValue
	@RevisionNumber
	@Expose
	private long rev;
	@Column(name = "REVTSTMP")
	@RevisionTimestamp
	@Expose
	private long timestamp;
	@Column(name = "USERNAME")
	@Expose
	private String userName;
}