package com.nicico.internal.sales.extrabill.repository;

import com.nicico.internal.sales.extrabill.model.ProformaBankBillModel;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtraBillRepository extends JpaRepository<ProformaBankBillModel, Long>, JpaSpecificationExecutor<ProformaBankBillModel> {
	List<ProformaBankBillModel> findAllByProformaMasterId(Long masterId);

	List<ProformaBankBillModel> findAllByWorkflowApproveStatusIn(List<WorkflowApproveStatus> statuses);

	ProformaBankBillModel findByProcessId(String processId);
}
