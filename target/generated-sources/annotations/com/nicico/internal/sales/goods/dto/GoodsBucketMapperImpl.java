package com.nicico.internal.sales.goods.dto;

import com.nicico.internal.sales.goods.dto.GoodBucketDto.Create;
import com.nicico.internal.sales.goods.dto.GoodBucketDto.Info;
import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.model.GoodsBucketModel.GoodsBucketModelBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class GoodsBucketMapperImpl implements GoodsBucketMapper {

    @Override
    public Info toDTO(GoodsBucketModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        if ( request.getId() != null ) {
            info.setId( request.getId() );
        }
        if ( request.getGoodId() != null ) {
            info.setGoodId( request.getGoodId() );
        }
        info.setImeCommodityId( request.getImeCommodityId() );
        info.setGoodName( request.getGoodName() );
        info.setStartDate( request.getStartDate() );
        info.setExpireDate( request.getExpireDate() );
        info.setPackagingSize( request.getPackagingSize() );
        info.setPackingId( request.getPackingId() );
        info.setPackingName( request.getPackingName() );
        info.setCashPercentage( request.getCashPercentage() );
        info.setCommission( request.getCommission() );
        info.setDivisibilityCheck( request.getDivisibilityCheck() );
        info.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        info.setCreatedDate( request.getCreatedDate() );
        info.setLastModifiedDate( request.getLastModifiedDate() );
        info.setCreatedBy( request.getCreatedBy() );
        info.setLastModifiedBy( request.getLastModifiedBy() );
        info.setComment( request.getComment() );

        return info;
    }

    @Override
    public GoodsBucketModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        GoodsBucketModelBuilder goodsBucketModel = GoodsBucketModel.builder();

        goodsBucketModel.id( request.getId() );
        goodsBucketModel.goodId( request.getGoodId() );
        goodsBucketModel.imeCommodityId( request.getImeCommodityId() );
        goodsBucketModel.goodName( request.getGoodName() );
        goodsBucketModel.imeCommoditySymbol( request.getImeCommoditySymbol() );
        goodsBucketModel.packagingSize( request.getPackagingSize() );
        goodsBucketModel.packingName( request.getPackingName() );
        goodsBucketModel.packingId( request.getPackingId() );
        goodsBucketModel.cashPercentage( request.getCashPercentage() );
        goodsBucketModel.commission( request.getCommission() );
        goodsBucketModel.startDate( request.getStartDate() );
        goodsBucketModel.expireDate( request.getExpireDate() );
        goodsBucketModel.divisibilityCheck( request.getDivisibilityCheck() );

        return goodsBucketModel.build();
    }
}
