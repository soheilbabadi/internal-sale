package com.nicico.internal.sales.bank.dto;

import com.nicico.internal.sales.bank.dto.IssuingBankWithPmsIdDto.Info;
import com.nicico.internal.sales.bank.model.IssuingBankWithPmsIdView;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class IssuingBankWithPmsIdMapperImpl implements IssuingBankWithPmsIdMapper {

    @Override
    public Info toDTO(IssuingBankWithPmsIdView model) {
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
        info.setPmsLcBankId( model.getPmsLcBankId() );

        return info;
    }
}
