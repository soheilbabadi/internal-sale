package com.nicico.internal.sales.goods.special.dto;

import com.nicico.internal.sales.goods.special.dto.PreciousMetalDto.Create;
import com.nicico.internal.sales.goods.special.dto.PreciousMetalDto.Info;
import com.nicico.internal.sales.goods.special.model.PreciousMetalModel;
import com.nicico.internal.sales.goods.special.model.PreciousMetalModel.PreciousMetalModelBuilder;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class PreciousMetalMapperImpl implements PreciousMetalMapper {

    @Override
    public Info toDTO(PreciousMetalModel request) {
        if ( request == null ) {
            return null;
        }

        Date createdDate = null;
        Date lastModifiedDate = null;
        String createdBy = null;
        String lastModifiedBy = null;
        String comment = null;

        createdDate = request.getCreatedDate();
        lastModifiedDate = request.getLastModifiedDate();
        createdBy = request.getCreatedBy();
        lastModifiedBy = request.getLastModifiedBy();
        comment = request.getComment();

        Info info = new Info( createdDate, lastModifiedDate, createdBy, lastModifiedBy, comment );

        info.setId( request.getId() );
        info.setName( request.getName() );
        info.setImeCommodityId( request.getImeCommodityId() );
        info.setImeCommoditySymbol( request.getImeCommoditySymbol() );

        return info;
    }

    @Override
    public PreciousMetalModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        PreciousMetalModelBuilder preciousMetalModel = PreciousMetalModel.builder();

        preciousMetalModel.id( request.getId() );
        preciousMetalModel.name( request.getName() );
        preciousMetalModel.imeCommodityId( request.getImeCommodityId() );
        preciousMetalModel.imeCommoditySymbol( request.getImeCommoditySymbol() );

        return preciousMetalModel.build();
    }
}
