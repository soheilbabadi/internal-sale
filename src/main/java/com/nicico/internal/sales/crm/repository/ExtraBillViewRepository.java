package com.nicico.internal.sales.crm.repository;

import com.nicico.internal.sales.crm.model.ExtraBillWithProformaView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtraBillViewRepository extends JpaRepository<ExtraBillWithProformaView, Long>, JpaSpecificationExecutor<ExtraBillWithProformaView> {

	List<ExtraBillWithProformaView> findAllByNationalCodeInOrderByContractDateDesc(List<String> filteredNationalCodes);
}
