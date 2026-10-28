package com.nicico.internal.sales.gaam.repository;

import com.nicico.internal.sales.gaam.model.GaamlReadyReckoningModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GaamReadyReckoningRepository extends JpaRepository<GaamlReadyReckoningModel, Long>, JpaSpecificationExecutor<GaamlReadyReckoningModel> {
}
