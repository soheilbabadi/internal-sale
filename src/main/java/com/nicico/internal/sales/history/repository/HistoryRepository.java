package com.nicico.internal.sales.history.repository;

import com.nicico.internal.sales.history.model.HistoryExtractMasterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryExtractMasterModel, Long>, JpaSpecificationExecutor<HistoryExtractMasterModel> {

	Optional<HistoryExtractMasterModel> findFirstByIdOrderByMasterIdDesc(Long aLong);

	List<HistoryExtractMasterModel> findAllByBuyerNationalCodeIn(List<String> buyerNationalCodes);

}
