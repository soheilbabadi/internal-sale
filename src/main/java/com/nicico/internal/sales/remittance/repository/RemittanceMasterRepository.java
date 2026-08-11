package com.nicico.internal.sales.remittance.repository;

import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemittanceMasterRepository
		extends JpaRepository<RemittanceMasterModel, Long>, JpaSpecificationExecutor<RemittanceMasterModel> {
	Optional<RemittanceMasterModel> findFirstByPaymentCodeOrderByIdDesc(String paymentCode);

	Optional<RemittanceMasterModel> findByProcessId(String processInstanceId);

	@Query("SELECT r FROM RemittanceMasterModel r WHERE r.nationalCode IN :nationalCodes")
	List<RemittanceMasterModel> findAllByNationalCodeIn(@Param("nationalCodes") List<String> nationalCodes);

	List<RemittanceMasterModel> findAllByWorkflowApproveStatusIn(List<WorkflowApproveStatus> statuses);

	List<RemittanceMasterModel> findAllByContractNoAndPaymentCodeAndCustomerId(String contractNo, String paymentCode,
	                                                                           Long customerId);

	List<RemittanceMasterModel> findAllByProformaMasterId(Long proformaMasterId);

	List<RemittanceMasterModel> findAllByProformaNo(String proformaNo);

	List<RemittanceMasterModel> findAllByTradeId(Long tradeId);

}
