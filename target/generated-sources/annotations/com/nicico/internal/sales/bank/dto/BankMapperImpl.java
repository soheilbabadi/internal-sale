package com.nicico.internal.sales.bank.dto;

import com.nicico.internal.sales.bank.dto.TradingBankDto.Create;
import com.nicico.internal.sales.bank.dto.TradingBankDto.Info;
import com.nicico.internal.sales.bank.model.TradingBankModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class BankMapperImpl implements BankMapper {

    @Override
    public Info toDTO(TradingBankModel model) {
        if ( model == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( model.getId() );
        info.setBankTitle( model.getBankTitle() );
        info.setBankBranchTitle( model.getBankBranchTitle() );
        info.setBranchCode( model.getBranchCode() );
        info.setAccountNumber( model.getAccountNumber() );
        info.setIban( model.getIban() );
        info.setCreatedDate( model.getCreatedDate() );
        info.setLastModifiedDate( model.getLastModifiedDate() );
        info.setCreatedBy( model.getCreatedBy() );
        info.setLastModifiedBy( model.getLastModifiedBy() );
        info.setComment( model.getComment() );
        info.setDescription( model.getDescription() );
        info.setVersion( model.getVersion() );

        return info;
    }

    @Override
    public TradingBankModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        TradingBankModel tradingBankModel = new TradingBankModel();

        tradingBankModel.setId( request.getId() );
        tradingBankModel.setBankTitle( request.getBankTitle() );
        tradingBankModel.setBankBranchTitle( request.getBankBranchTitle() );
        tradingBankModel.setBranchCode( request.getBranchCode() );
        tradingBankModel.setAccountNumber( request.getAccountNumber() );
        tradingBankModel.setIban( request.getIban() );

        return tradingBankModel;
    }
}
