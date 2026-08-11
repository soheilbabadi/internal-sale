package com.nicico.internal.sales.lc.repository;

import com.nicico.internal.sales.lc.model.LcIssueProviderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LcIssueRepository extends JpaRepository<LcIssueProviderModel, Long>, JpaSpecificationExecutor<LcIssueProviderModel> {
	List<LcIssueProviderModel> findByMasterId(Long masterId);
}
