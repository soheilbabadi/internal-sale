package com.nicico.internal.sales.pms.dto;

import com.nicico.internal.sales.pms.dto.PMSGoodsDTO.Info;
import com.nicico.internal.sales.pms.model.PMSGoodsModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class PMSGoodsMapperImpl implements PMSGoodsMapper {

    @Override
    public Info toDTO(PMSGoodsModel imeTrade) {
        if ( imeTrade == null ) {
            return null;
        }

        Date createdDate = null;
        Date lastModifiedDate = null;
        String createdBy = null;
        String lastModifiedBy = null;
        String comment = null;

        Info info = new Info( createdDate, lastModifiedDate, createdBy, lastModifiedBy, comment );

        info.setCode( imeTrade.getCode() );
        info.setName( imeTrade.getName() );
        info.setStock( imeTrade.getStock() );
        info.setPackingCode( imeTrade.getPackingCode() );
        info.setNosaCode( imeTrade.getNosaCode() );
        info.setCostCenterCode( imeTrade.getCostCenterCode() );
        info.setNosaAlayandeghiCode( imeTrade.getNosaAlayandeghiCode() );
        info.setGroupGssId( imeTrade.getGroupGssId() );

        return info;
    }

    @Override
    public List<Info> toDTOS(List<PMSGoodsModel> imeTrade) {
        if ( imeTrade == null ) {
            return null;
        }

        List<Info> list = new ArrayList<Info>( imeTrade.size() );
        for ( PMSGoodsModel pMSGoodsModel : imeTrade ) {
            list.add( toDTO( pMSGoodsModel ) );
        }

        return list;
    }
}
