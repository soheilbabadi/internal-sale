package com.nicico.internal.sales.pms.repository;

import com.nicico.internal.sales.pms.model.InsPmsImeMappingModel;
import com.nicico.internal.sales.pms.enums.InsPmsImeMappingTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InsPmsImeMappingRepository extends JpaRepository<InsPmsImeMappingModel, Long> {
	Optional<InsPmsImeMappingModel> findFirstByImeIdAndMappingFor(Long imeId, InsPmsImeMappingTypeEnum mappingType);
}
