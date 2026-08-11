package com.nicico.internal.sales.wf.repository;

import com.nicico.internal.sales.wf.model.WorkflowModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowRepository extends JpaRepository<WorkflowModel, String>, JpaSpecificationExecutor<WorkflowModel> {
	Optional<WorkflowModel> findByTenantId(String tenantId);

	Optional<WorkflowModel> findByDefinitionKey(String definitionKey);

	Optional<WorkflowModel> findByProcessTitleIgnoreCase(String processTitle);
}
