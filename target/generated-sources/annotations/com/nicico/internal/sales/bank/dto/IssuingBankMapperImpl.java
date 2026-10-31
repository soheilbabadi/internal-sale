package com.nicico.internal.sales.bank.dto;

import com.nicico.internal.sales.bank.dto.IssuingBankDto.Create;
import com.nicico.internal.sales.bank.dto.IssuingBankDto.Info;
import com.nicico.internal.sales.bank.model.IssuingBankModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class IssuingBankMapperImpl implements IssuingBankMapper {

    @Override
    public Info toDTO(IssuingBankModel model) {
        if ( model == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( model.getId() );
        info.setBankName( model.getBankName() );
        info.setBranchName( model.getBranchName() );
        info.setBranchCode( model.getBranchCode() );
        info.setProvince( model.getProvince() );
        info.setCity( model.getCity() );
        info.setBankCode( model.getBankCode() );
        info.setBaseNosaCode( model.getBaseNosaCode() );

        return info;
    }

    @Override
    public IssuingBankModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        IssuingBankModel issuingBankModel = new IssuingBankModel();

        issuingBankModel.setId( request.getId() );
        issuingBankModel.setBankName( request.getBankName() );
        issuingBankModel.setBranchName( request.getBranchName() );
        issuingBankModel.setBranchCode( request.getBranchCode() );
        issuingBankModel.setProvince( request.getProvince() );
        issuingBankModel.setCity( request.getCity() );
        issuingBankModel.setBankCode( request.getBankCode() );
        issuingBankModel.setBaseNosaCode( request.getBaseNosaCode() );

        return issuingBankModel;
    }
}
