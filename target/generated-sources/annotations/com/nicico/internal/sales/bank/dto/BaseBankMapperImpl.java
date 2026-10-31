package com.nicico.internal.sales.bank.dto;

import com.nicico.internal.sales.bank.dto.BaseBankDto.Create;
import com.nicico.internal.sales.bank.dto.BaseBankDto.Info;
import com.nicico.internal.sales.bank.model.BaseBankModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class BaseBankMapperImpl implements BaseBankMapper {

    @Override
    public Info toDTO(BaseBankModel model) {
        if ( model == null ) {
            return null;
        }

        Info info = new Info();

        info.setCreatedDate( model.getCreatedDate() );
        info.setLastModifiedDate( model.getLastModifiedDate() );
        info.setCreatedBy( model.getCreatedBy() );
        info.setLastModifiedBy( model.getLastModifiedBy() );
        info.setComment( model.getComment() );
        info.setDescription( model.getDescription() );
        info.setVersion( model.getVersion() );
        info.setId( model.getId() );
        info.setBankCode( model.getBankCode() );
        info.setBankTitle( model.getBankTitle() );
        info.setBaseNosaCode( model.getBaseNosaCode() );
        info.setNationalCode( model.getNationalCode() );

        return info;
    }

    @Override
    public BaseBankModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        BaseBankModel baseBankModel = new BaseBankModel();

        baseBankModel.setCreatedDate( request.getCreatedDate() );
        baseBankModel.setLastModifiedDate( request.getLastModifiedDate() );
        baseBankModel.setCreatedBy( request.getCreatedBy() );
        baseBankModel.setLastModifiedBy( request.getLastModifiedBy() );
        baseBankModel.setComment( request.getComment() );
        baseBankModel.setDescription( request.getDescription() );
        baseBankModel.setVersion( request.getVersion() );
        baseBankModel.setId( request.getId() );
        baseBankModel.setBankCode( request.getBankCode() );
        baseBankModel.setBankTitle( request.getBankTitle() );
        baseBankModel.setBaseNosaCode( request.getBaseNosaCode() );
        baseBankModel.setNationalCode( request.getNationalCode() );

        return baseBankModel;
    }
}
