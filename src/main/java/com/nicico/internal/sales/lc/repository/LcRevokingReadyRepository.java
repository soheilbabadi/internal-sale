package com.nicico.internal.sales.lc.repository;

import com.nicico.internal.sales.lc.model.LcRevokingReadyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LcRevokingReadyRepository extends JpaRepository<LcRevokingReadyModel, Long>, JpaSpecificationExecutor<LcRevokingReadyModel> {

}
