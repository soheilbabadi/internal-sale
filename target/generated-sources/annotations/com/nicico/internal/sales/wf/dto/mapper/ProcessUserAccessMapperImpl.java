package com.nicico.internal.sales.wf.dto.mapper;

import com.nicico.internal.sales.wf.dto.ProcessUserAccessDto.Create;
import com.nicico.internal.sales.wf.dto.ProcessUserAccessDto.Info;
import com.nicico.internal.sales.wf.model.ProcessUserAccessModel;
import com.nicico.internal.sales.wf.model.ProcessUserAccessModel.ProcessUserAccessModelBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class ProcessUserAccessMapperImpl implements ProcessUserAccessMapper {

    @Override
    public Info toDTO(ProcessUserAccessModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setUserId( request.getUserId() );
        info.setProcessId( request.getProcessId() );
        info.setProcessTitle( request.getProcessTitle() );
        info.setProcessLocalTitle( request.getProcessLocalTitle() );
        info.setProcessVariable( request.getProcessVariable() );
        info.setProcessVariableTitle( request.getProcessVariableTitle() );
        info.setUsername( request.getUsername() );
        info.setFullName( request.getFullName() );
        info.setNationalCode( request.getNationalCode() );
        info.setCreatedDate( request.getCreatedDate() );
        info.setLastModifiedDate( request.getLastModifiedDate() );
        info.setCreatedBy( request.getCreatedBy() );
        info.setLastModifiedBy( request.getLastModifiedBy() );
        info.setComment( request.getComment() );

        return info;
    }

    @Override
    public ProcessUserAccessModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        ProcessUserAccessModelBuilder processUserAccessModel = ProcessUserAccessModel.builder();

        processUserAccessModel.id( request.getId() );
        processUserAccessModel.processId( request.getProcessId() );
        processUserAccessModel.processTitle( request.getProcessTitle() );
        processUserAccessModel.processLocalTitle( request.getProcessLocalTitle() );
        processUserAccessModel.processVariable( request.getProcessVariable() );
        processUserAccessModel.processVariableTitle( request.getProcessVariableTitle() );
        processUserAccessModel.userId( request.getUserId() );
        processUserAccessModel.username( request.getUsername() );
        processUserAccessModel.fullName( request.getFullName() );
        processUserAccessModel.nationalCode( request.getNationalCode() );

        return processUserAccessModel.build();
    }
}
