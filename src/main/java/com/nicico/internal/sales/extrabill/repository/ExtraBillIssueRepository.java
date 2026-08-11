package com.nicico.internal.sales.extrabill.repository;

import com.nicico.internal.sales.extrabill.model.ExtraBillIssueProviderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtraBillIssueRepository extends JpaRepository<ExtraBillIssueProviderModel, Long>, JpaSpecificationExecutor<ExtraBillIssueProviderModel> {
	List<ExtraBillIssueProviderModel> findByMasterId(Long masterId);
}
