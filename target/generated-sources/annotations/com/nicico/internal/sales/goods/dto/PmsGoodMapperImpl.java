package com.nicico.internal.sales.goods.dto;

import com.nicico.internal.sales.goods.dto.PmsMappingDto.Create;
import com.nicico.internal.sales.goods.dto.PmsMappingDto.Info;
import com.nicico.internal.sales.goods.model.PmsMappingModel;
import com.nicico.internal.sales.goods.model.PmsMappingModel.PmsMappingModelBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class PmsGoodMapperImpl implements PmsGoodMapper {

    @Override
    public Info toDTO(PmsMappingModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setPmsId( request.getPmsId() );
        info.setGoodName( request.getGoodName() );
        info.setPmsName( request.getPmsName() );

        return info;
    }

    @Override
    public PmsMappingModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        PmsMappingModelBuilder pmsMappingModel = PmsMappingModel.builder();

        pmsMappingModel.pmsId( request.getPmsId() );
        pmsMappingModel.goodName( request.getGoodName() );
        pmsMappingModel.pmsName( request.getPmsName() );

        return pmsMappingModel.build();
    }
}
