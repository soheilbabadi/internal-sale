package com.nicico.internal.sales.crm.repository;

import com.nicico.internal.sales.crm.model.LcWithProformaView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LcViewRepository extends JpaRepository<LcWithProformaView, Long>, JpaSpecificationExecutor<LcWithProformaView> {

	Page<LcWithProformaView> findAllByNationalCodeIn(List<String> nationalCodes, Pageable pageable);

	List<LcWithProformaView> findAllByNationalCodeInOrderByContractDateDesc(List<String> filteredNationalCodes);
}
