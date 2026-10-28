package com.nicico.internal.sales.crm.repository;

import com.nicico.internal.sales.crm.model.GaamWithProformaView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GaamViewRepository extends JpaRepository<GaamWithProformaView, Long>, JpaSpecificationExecutor<GaamWithProformaView> {

	List<GaamWithProformaView> findAllByNationalCodeInOrderByContractDateDesc(List<String> filteredNationalCodes);
}
