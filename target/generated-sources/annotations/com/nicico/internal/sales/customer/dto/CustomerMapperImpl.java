package com.nicico.internal.sales.customer.dto;

import com.nicico.internal.sales.customer.dto.CustomerDTO.Create;
import com.nicico.internal.sales.customer.dto.CustomerDTO.Info;
import com.nicico.internal.sales.customer.model.CustomerModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public Info toDTO(CustomerModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setName( request.getName() );
        info.setNameEn( request.getNameEn() );
        info.setNationalCode( request.getNationalCode() );
        info.setPmsCustomerCode( request.getPmsCustomerCode() );
        info.setImeCustomerId( request.getImeCustomerId() );
        info.setPhone( request.getPhone() );
        info.setEconomicCode( request.getEconomicCode() );
        info.setRegisterNumber( request.getRegisterNumber() );
        info.setPostCode( request.getPostCode() );
        info.setAddress( request.getAddress() );
        info.setEmail( request.getEmail() );
        info.setMobile( request.getMobile() );
        info.setCoordinator( request.getCoordinator() );
        info.setCeoName( request.getCeoName() );
        info.setCeoPhone( request.getCeoPhone() );
        info.setTradeRoleCode( request.getTradeRoleCode() );
        info.setNosaCode( request.getNosaCode() );
        info.setCreatedDate( request.getCreatedDate() );
        info.setLastModifiedDate( request.getLastModifiedDate() );
        info.setCreatedBy( request.getCreatedBy() );
        info.setLastModifiedBy( request.getLastModifiedBy() );
        info.setComment( request.getComment() );

        return info;
    }

    @Override
    public CustomerModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        CustomerModel customerModel = new CustomerModel();

        customerModel.setId( request.getId() );
        customerModel.setName( request.getName() );
        customerModel.setNameEn( request.getNameEn() );
        customerModel.setNationalCode( request.getNationalCode() );
        customerModel.setPmsCustomerCode( request.getPmsCustomerCode() );
        customerModel.setImeCustomerId( request.getImeCustomerId() );
        customerModel.setPhone( request.getPhone() );
        customerModel.setEconomicCode( request.getEconomicCode() );
        customerModel.setRegisterNumber( request.getRegisterNumber() );
        customerModel.setPostCode( request.getPostCode() );
        customerModel.setEmail( request.getEmail() );
        customerModel.setMobile( request.getMobile() );
        customerModel.setCoordinator( request.getCoordinator() );
        customerModel.setAddress( request.getAddress() );
        customerModel.setCeoName( request.getCeoName() );
        customerModel.setCeoPhone( request.getCeoPhone() );
        customerModel.setTradeRoleCode( request.getTradeRoleCode() );
        customerModel.setNosaCode( request.getNosaCode() );

        return customerModel;
    }
}
