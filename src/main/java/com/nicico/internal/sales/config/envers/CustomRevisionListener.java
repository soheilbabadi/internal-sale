package com.nicico.internal.sales.config.envers;

import com.nicico.copper.core.SecurityUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.RevisionListener;

@Getter
@Setter
@Slf4j
public class CustomRevisionListener implements RevisionListener {
	@Override
	public void newRevision(Object revisionEntity) {
		RevInfo rev = (RevInfo) revisionEntity;
		try {
			rev.setUserName(SecurityUtil.getUsername() + "-" + SecurityUtil.getFullName());
		} catch (Exception e) {
			log.error(e.getMessage());
			rev.setUserName("unknown-rabbit-maybe");
		}
	}
}
