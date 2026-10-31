package com.nicico.internal.sales.loading.dto;

import com.nicico.internal.sales.loading.dto.LoadingExtractDto.Create;
import com.nicico.internal.sales.loading.dto.LoadingExtractDto.Info;
import com.nicico.internal.sales.loading.model.LoadingExtractModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class LoadingExtractMapperImpl implements LoadingExtractMapper {

    @Override
    public Info toDTO(LoadingExtractModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setGoodId( request.getGoodId() );
        info.setGoodName( request.getGoodName() );
        info.setGoodDescription( request.getGoodDescription() );
        info.setLoadingPlaceId( request.getLoadingPlaceId() );
        info.setLoadingPlaceTitle( request.getLoadingPlaceTitle() );

        return info;
    }

    @Override
    public LoadingExtractModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        LoadingExtractModel loadingExtractModel = new LoadingExtractModel();

        loadingExtractModel.setId( request.getId() );
        loadingExtractModel.setGoodId( request.getGoodId() );
        loadingExtractModel.setGoodName( request.getGoodName() );
        loadingExtractModel.setGoodDescription( request.getGoodDescription() );
        loadingExtractModel.setLoadingPlaceId( request.getLoadingPlaceId() );
        loadingExtractModel.setLoadingPlaceTitle( request.getLoadingPlaceTitle() );

        return loadingExtractModel;
    }
}
