package com.nicico.internal.sales.wf.repository;

import com.nicico.internal.sales.wf.model.ProcessUserAccessModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessUserAccessRepository extends JpaRepository<ProcessUserAccessModel, Long>, JpaSpecificationExecutor<ProcessUserAccessModel> {
	List<ProcessUserAccessModel> findAllByUsername(String username);

	List<ProcessUserAccessModel> findAllByProcessTitle(String processTitle);

	Optional<ProcessUserAccessModel> findByProcessIdAndUserIdAndProcessVariable(String processId, Long userId, String processVariable);

	void deleteAllByProcessTitle(String processTitle);

	boolean existsByProcessIdAndProcessVariableAndUserId(String processId, String processVariable, Long userId);

	boolean existsByProcessTitleAndUserId(String processTitle, Long userId);
}
