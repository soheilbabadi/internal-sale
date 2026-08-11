package com.nicico.internal.sales.pms.repository;

import com.nicico.internal.sales.pms.model.PmsRemmitanceModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PmsRemittanceRepository extends JpaRepository<PmsRemmitanceModel, String> {
}
