package com.nicico.internal.sales.wf.dto.mapper;

import com.nicico.internal.sales.wf.dto.WorkflowDto.Create;
import com.nicico.internal.sales.wf.dto.WorkflowDto.Info;
import com.nicico.internal.sales.wf.model.WorkflowModel;
import com.nicico.internal.sales.wf.model.WorkflowModel.WorkflowModelBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class WorkflowMapperImpl implements WorkflowMapper {

    @Override
    public Info toDTO(WorkflowModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setProcessTitle( request.getProcessTitle() );
        info.setProcessLocalTitle( request.getProcessLocalTitle() );
        info.setProcessVersion( request.getProcessVersion() );
        info.setTenantId( request.getTenantId() );
        info.setDefinitionKey( request.getDefinitionKey() );
        info.setCreatedDate( request.getCreatedDate() );
        info.setLastModifiedDate( request.getLastModifiedDate() );
        info.setCreatedBy( request.getCreatedBy() );
        info.setLastModifiedBy( request.getLastModifiedBy() );
        info.setComment( request.getComment() );

        return info;
    }

    @Override
    public WorkflowModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        WorkflowModelBuilder workflowModel = WorkflowModel.builder();

        workflowModel.id( request.getId() );
        workflowModel.processTitle( request.getProcessTitle() );
        workflowModel.processLocalTitle( request.getProcessLocalTitle() );
        workflowModel.processVersion( request.getProcessVersion() );
        workflowModel.tenantId( request.getTenantId() );
        workflowModel.definitionKey( request.getDefinitionKey() );

        return workflowModel.build();
    }
}
