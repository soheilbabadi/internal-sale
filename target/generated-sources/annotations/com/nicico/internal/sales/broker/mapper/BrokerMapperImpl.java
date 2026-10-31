package com.nicico.internal.sales.broker.mapper;

import com.nicico.internal.sales.broker.dto.BrokerDto.Create;
import com.nicico.internal.sales.broker.dto.BrokerDto.Info;
import com.nicico.internal.sales.broker.model.BrokerModel;
import com.nicico.internal.sales.broker.model.BrokerModel.BrokerModelBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class BrokerMapperImpl implements BrokerMapper {

    @Override
    public Info toDTO(BrokerModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setName( request.getName() );
        info.setNationalCode( request.getNationalCode() );
        info.setPhone( request.getPhone() );
        info.setEconomicCode( request.getEconomicCode() );
        info.setPostCode( request.getPostCode() );
        info.setEmail( request.getEmail() );
        info.setMobile( request.getMobile() );
        info.setCoordinator( request.getCoordinator() );
        info.setAddress( request.getAddress() );
        info.setCeoName( request.getCeoName() );
        info.setCeoPhone( request.getCeoPhone() );
        info.setCreatedDate( request.getCreatedDate() );
        info.setLastModifiedDate( request.getLastModifiedDate() );
        info.setCreatedBy( request.getCreatedBy() );
        info.setLastModifiedBy( request.getLastModifiedBy() );
        info.setComment( request.getComment() );

        return info;
    }

    @Override
    public BrokerModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        BrokerModelBuilder brokerModel = BrokerModel.builder();

        brokerModel.id( request.getId() );
        brokerModel.name( request.getName() );
        brokerModel.nationalCode( request.getNationalCode() );
        brokerModel.phone( request.getPhone() );
        brokerModel.economicCode( request.getEconomicCode() );
        brokerModel.postCode( request.getPostCode() );
        brokerModel.email( request.getEmail() );
        brokerModel.mobile( request.getMobile() );
        brokerModel.coordinator( request.getCoordinator() );
        brokerModel.address( request.getAddress() );
        brokerModel.ceoName( request.getCeoName() );
        brokerModel.ceoPhone( request.getCeoPhone() );

        return brokerModel.build();
    }
}
