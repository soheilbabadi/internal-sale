package com.nicico.internal.sales.customer.dto;

import com.nicico.internal.sales.customer.dto.CustomerContactDto.Create;
import com.nicico.internal.sales.customer.model.CustomerContactModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class CustomerContactMapperImpl implements CustomerContactMapper {

    @Override
    public Create toDTO(CustomerContactModel request) {
        if ( request == null ) {
            return null;
        }

        Create create = new Create();

        create.setId( request.getId() );
        create.setPhone( request.getPhone() );
        create.setPostCode( request.getPostCode() );
        create.setEmail( request.getEmail() );
        create.setMobile( request.getMobile() );
        create.setCoordinator( request.getCoordinator() );
        create.setAddress( request.getAddress() );
        create.setCustomerId( request.getCustomerId() );
        create.setValid( request.isValid() );
        create.setDefault( request.isDefault() );

        return create;
    }

    @Override
    public CustomerContactModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        CustomerContactModel customerContactModel = new CustomerContactModel();

        customerContactModel.setId( request.getId() );
        customerContactModel.setPhone( request.getPhone() );
        customerContactModel.setPostCode( request.getPostCode() );
        customerContactModel.setEmail( request.getEmail() );
        customerContactModel.setMobile( request.getMobile() );
        customerContactModel.setCoordinator( request.getCoordinator() );
        customerContactModel.setAddress( request.getAddress() );
        customerContactModel.setCustomerId( request.getCustomerId() );
        customerContactModel.setValid( request.isValid() );
        customerContactModel.setDefault( request.isDefault() );

        return customerContactModel;
    }
}
