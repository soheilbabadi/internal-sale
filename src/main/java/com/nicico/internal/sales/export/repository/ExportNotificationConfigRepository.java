package com.nicico.internal.sales.export.repository;


import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.model.ExportNotificationConfigModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExportNotificationConfigRepository extends JpaRepository<ExportNotificationConfigModel, Long>, JpaSpecificationExecutor<ExportNotificationConfigModel> {

	Optional<ExportNotificationConfigModel> findByEntityType(EntityTypeEnum entityType);

}