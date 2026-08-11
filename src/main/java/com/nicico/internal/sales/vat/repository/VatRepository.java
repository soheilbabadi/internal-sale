package com.nicico.internal.sales.vat.repository;

import com.nicico.internal.sales.vat.model.TaxVatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VatRepository extends JpaRepository<TaxVatModel, Long>, JpaSpecificationExecutor<TaxVatModel> {
	Optional<TaxVatModel> findByJalaliYear(Integer jalaliYear);
}
