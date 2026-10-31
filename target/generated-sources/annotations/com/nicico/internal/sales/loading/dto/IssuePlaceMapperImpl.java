package com.nicico.internal.sales.loading.dto;

import com.nicico.internal.sales.loading.dto.IssuePlaceDto.Create;
import com.nicico.internal.sales.loading.dto.IssuePlaceDto.Info;
import com.nicico.internal.sales.loading.model.IssuePlaceModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class IssuePlaceMapperImpl implements IssuePlaceMapper {

    @Override
    public Info toDTO(IssuePlaceModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setPlaceTitle( request.getPlaceTitle() );
        info.setPlaceValue( request.getPlaceValue() );

        return info;
    }

    @Override
    public IssuePlaceModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        IssuePlaceModel issuePlaceModel = new IssuePlaceModel();

        issuePlaceModel.setId( request.getId() );
        issuePlaceModel.setPlaceTitle( request.getPlaceTitle() );
        issuePlaceModel.setPlaceValue( request.getPlaceValue() );

        return issuePlaceModel;
    }
}
