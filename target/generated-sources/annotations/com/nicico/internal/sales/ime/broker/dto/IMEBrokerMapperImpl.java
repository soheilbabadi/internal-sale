package com.nicico.internal.sales.ime.broker.dto;

import com.nicico.internal.sales.ime.broker.dto.IMEBrokerDTO.Info;
import com.nicico.internal.sales.ime.broker.model.IMEBrokerModel;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class IMEBrokerMapperImpl implements IMEBrokerMapper {

    @Override
    public Info toDTO(IMEBrokerModel imeTrade) {
        if ( imeTrade == null ) {
            return null;
        }

        Date createdDate = null;
        Date lastModifiedDate = null;
        String createdBy = null;
        String lastModifiedBy = null;
        String comment = null;

        Info info = new Info( createdDate, lastModifiedDate, createdBy, lastModifiedBy, comment );

        info.setPk( imeTrade.getPk() );
        info.setBrokerId( imeTrade.getBrokerId() );
        info.setPersianName( imeTrade.getPersianName() );
        info.setNationalId( imeTrade.getNationalId() );
        info.setSpotId( imeTrade.getSpotId() );
        info.setDerivativesId( imeTrade.getDerivativesId() );
        info.setDescription( imeTrade.getDescription() );

        return info;
    }
}
