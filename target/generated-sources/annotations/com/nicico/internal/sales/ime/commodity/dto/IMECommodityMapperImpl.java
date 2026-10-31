package com.nicico.internal.sales.ime.commodity.dto;

import com.nicico.internal.sales.ime.commodity.dto.IMECommodityDTO.Info;
import com.nicico.internal.sales.ime.commodity.model.IMECommodityModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class IMECommodityMapperImpl implements IMECommodityMapper {

    @Override
    public Info toDTO(IMECommodityModel imeTrade) {
        if ( imeTrade == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( imeTrade.getId() );
        info.setCommodityId( imeTrade.getCommodityId() );
        info.setDescription( imeTrade.getDescription() );
        info.setPersianName( imeTrade.getPersianName() );
        info.setParentId( imeTrade.getParentId() );
        info.setSymbol( imeTrade.getSymbol() );
        info.setClobOptions( imeTrade.getClobOptions() );
        info.setResponseDate( imeTrade.getResponseDate() );
        info.setMessageText( imeTrade.getMessageText() );
        info.setCreatedBy( imeTrade.getCreatedBy() );
        info.setLastModifiedBy( imeTrade.getLastModifiedBy() );
        info.setComment( imeTrade.getComment() );

        return info;
    }
}
