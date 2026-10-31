package com.nicico.internal.sales.pms.dto;

import com.nicico.internal.sales.pms.dto.PMSCustomerDTO.Info;
import com.nicico.internal.sales.pms.model.PMSCustomerModel;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class PMSCustomerMapperImpl implements PMSCustomerMapper {

    @Override
    public Info toDTO(PMSCustomerModel imeTrade) {
        if ( imeTrade == null ) {
            return null;
        }

        Date createdDate = null;
        Date lastModifiedDate = null;
        String createdBy = null;
        String lastModifiedBy = null;
        String comment = null;

        Info info = new Info( createdDate, lastModifiedDate, createdBy, lastModifiedBy, comment );

        info.setId( imeTrade.getId() );
        info.setName( imeTrade.getName() );
        info.setAddress( imeTrade.getAddress() );
        info.setDescription( imeTrade.getDescription() );
        info.setPhone( imeTrade.getPhone() );
        info.setEconomicCode( imeTrade.getEconomicCode() );
        info.setRegisterNumber( imeTrade.getRegisterNumber() );
        info.setPostCode( imeTrade.getPostCode() );
        info.setTelex( imeTrade.getTelex() );
        info.setFax( imeTrade.getFax() );
        info.setNosaCode( imeTrade.getNosaCode() );
        info.setNosaCreditCode( imeTrade.getNosaCreditCode() );
        info.setDepCode( imeTrade.getDepCode() );

        return info;
    }
}
