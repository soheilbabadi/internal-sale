package com.nicico.internal.sales.loading.repository;

import com.nicico.internal.sales.loading.model.LoadingPlaceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadingPlaceRepository extends JpaRepository<LoadingPlaceModel, Long>, JpaSpecificationExecutor<LoadingPlaceModel> {
	java.util.Optional<LoadingPlaceModel> findByPlaceValue(String placeValue);

	java.util.Optional<LoadingPlaceModel> findByPlaceTitle(String placeTitle);
}
