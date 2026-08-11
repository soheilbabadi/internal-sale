package com.nicico.internal.sales.loading.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Data
@Audited(targetAuditMode = NOT_AUDITED)
@Subselect("""
		   SELECT 
		  tinlg.ID AS ID,
		  tinlg.N_GOOD_ID AS N_GOOD_ID,
		  tig.C_NAME AS C_GOOD_NAME,
		  tig.C_DESCRIPTION AS C_GOOD_DESCRIPTION,
		  tinlg.N_LOADING_PLACE_ID AS N_LOADING_PLACE_ID,
		  tilp.C_PLACE_TITLE AS C_LOADING_PLACE_TITLE
		FROM 
		  T_INS_LOADING_GOODS tinlg
		INNER JOIN 
		  T_INS_GOODS tig ON tinlg.N_GOOD_ID = tig.ID
		INNER JOIN 
		  T_INS_LOADING_PLACE tilp ON tinlg.N_LOADING_PLACE_ID = tilp.ID
		""")
public class LoadingExtractModel implements Serializable {
	@Id
	private Long id;
	@Column(name = "N_GOOD_ID")
	private Long goodId;
	@Column(name = "C_GOOD_NAME")
	private String goodName;
	@Column(name = "C_GOOD_DESCRIPTION")
	private String goodDescription;
	@Column(name = "N_LOADING_PLACE_ID")
	private Long loadingPlaceId;
	@Column(name = "C_LOADING_PLACE_TITLE")
	private String loadingPlaceTitle;
}
