package com.nicico.internal.sales.goods.dto;

import com.nicico.internal.sales.goods.dto.GoodsDTO.Create;
import com.nicico.internal.sales.goods.dto.GoodsDTO.Info;
import com.nicico.internal.sales.goods.model.GoodsModel;
import com.nicico.internal.sales.goods.model.GoodsModel.GoodsModelBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class GoodsMapperImpl implements GoodsMapper {

    @Override
    public Info toDTO(GoodsModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setDescription( request.getDescription() );
        info.setId( request.getId() );
        info.setName( request.getName() );
        info.setNameEn( request.getNameEn() );
        info.setPmsGoodsId( request.getPmsGoodsId() );
        info.setImeCommodityId( request.getImeCommodityId() );
        info.setImeCommodityParentId( request.getImeCommodityParentId() );
        info.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        info.setCreatedDate( request.getCreatedDate() );
        info.setLastModifiedDate( request.getLastModifiedDate() );
        info.setCreatedBy( request.getCreatedBy() );
        info.setLastModifiedBy( request.getLastModifiedBy() );
        info.setComment( request.getComment() );

        return info;
    }

    @Override
    public GoodsModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        GoodsModelBuilder goodsModel = GoodsModel.builder();

        goodsModel.id( request.getId() );
        goodsModel.name( request.getName() );
        goodsModel.nameEn( request.getNameEn() );
        goodsModel.pmsGoodsId( request.getPmsGoodsId() );
        goodsModel.imeCommodityId( request.getImeCommodityId() );
        goodsModel.imeCommodityParentId( request.getImeCommodityParentId() );
        goodsModel.imeCommoditySymbol( request.getImeCommoditySymbol() );

        return goodsModel.build();
    }
}
