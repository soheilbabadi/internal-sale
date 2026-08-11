package com.nicico.internal.sales.loading.repository;

import com.nicico.internal.sales.loading.model.IssuePlaceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IssuePlaceRepository extends JpaRepository<IssuePlaceModel, Long>, JpaSpecificationExecutor<IssuePlaceModel> {
}
