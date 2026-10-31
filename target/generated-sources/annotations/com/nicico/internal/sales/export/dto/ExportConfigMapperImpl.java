package com.nicico.internal.sales.export.dto;

import com.nicico.internal.sales.export.dto.ExportNotificationConfigDto.Create;
import com.nicico.internal.sales.export.dto.ExportNotificationConfigDto.Info;
import com.nicico.internal.sales.export.model.ExportNotificationConfigModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class ExportConfigMapperImpl implements ExportConfigMapper {

    @Override
    public Info toDTO(ExportNotificationConfigModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setEntityType( request.getEntityType() );
        info.setSendEmail( request.getSendEmail() );
        info.setSendSms( request.getSendSms() );
        info.setSendPms( request.getSendPms() );
        info.setCreatedDate( request.getCreatedDate() );
        info.setLastModifiedDate( request.getLastModifiedDate() );
        info.setCreatedBy( request.getCreatedBy() );
        info.setLastModifiedBy( request.getLastModifiedBy() );
        info.setComment( request.getComment() );

        return info;
    }

    @Override
    public ExportNotificationConfigModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        ExportNotificationConfigModel exportNotificationConfigModel = new ExportNotificationConfigModel();

        exportNotificationConfigModel.setId( request.getId() );
        exportNotificationConfigModel.setEntityType( request.getEntityType() );
        exportNotificationConfigModel.setSendEmail( request.getSendEmail() );
        exportNotificationConfigModel.setSendSms( request.getSendSms() );
        exportNotificationConfigModel.setSendPms( request.getSendPms() );

        return exportNotificationConfigModel;
    }
}
