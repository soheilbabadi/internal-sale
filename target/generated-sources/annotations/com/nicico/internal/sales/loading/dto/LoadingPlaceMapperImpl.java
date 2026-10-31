package com.nicico.internal.sales.loading.dto;

import com.nicico.internal.sales.loading.dto.LoadingPlaceDto.Create;
import com.nicico.internal.sales.loading.dto.LoadingPlaceDto.Info;
import com.nicico.internal.sales.loading.model.LoadingPlaceModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class LoadingPlaceMapperImpl implements LoadingPlaceMapper {

    @Override
    public Info toDTO(LoadingPlaceModel request) {
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
    public LoadingPlaceModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        LoadingPlaceModel loadingPlaceModel = new LoadingPlaceModel();

        loadingPlaceModel.setId( request.getId() );
        loadingPlaceModel.setPlaceTitle( request.getPlaceTitle() );
        loadingPlaceModel.setPlaceValue( request.getPlaceValue() );

        return loadingPlaceModel;
    }
}
