package com.nicico.internal.sales.deposit.dto;

import com.nicico.internal.sales.deposit.dto.CashDepositDto.Create;
import com.nicico.internal.sales.deposit.dto.CashDepositDto.Info;
import com.nicico.internal.sales.deposit.model.CashDepositModel;
import com.nicico.internal.sales.deposit.model.CashDepositModel.CashDepositModelBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class CashDepositMapperImpl implements CashDepositMapper {

    @Override
    public Info toDTO(CashDepositModel model) {
        if ( model == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( model.getId() );
        info.setNosaCode( model.getNosaCode() );
        info.setIssueDate( model.getIssueDate() );
        info.setProformaDetailId( model.getProformaDetailId() );
        info.setProformaMasterId( model.getProformaMasterId() );
        info.setContractNo( model.getContractNo() );
        info.setTradeId( model.getTradeId() );
        info.setAcknowledgment( model.getAcknowledgment() );
        info.setReckoningSend( model.isReckoningSend() );
        info.setReckoningSendDate( model.getReckoningSendDate() );
        info.setDepositSlipWithTax( model.getDepositSlipWithTax() );
        info.setDepositSlipWithoutTax( model.getDepositSlipWithoutTax() );
        info.setDepositSlipType( model.getDepositSlipType() );

        return info;
    }

    @Override
    public CashDepositModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        CashDepositModelBuilder cashDepositModel = CashDepositModel.builder();

        cashDepositModel.id( request.getId() );
        cashDepositModel.nosaCode( request.getNosaCode() );
        cashDepositModel.issueDate( request.getIssueDate() );
        cashDepositModel.proformaDetailId( request.getProformaDetailId() );
        cashDepositModel.proformaMasterId( request.getProformaMasterId() );
        cashDepositModel.contractNo( request.getContractNo() );
        cashDepositModel.tradeId( request.getTradeId() );
        cashDepositModel.acknowledgment( request.getAcknowledgment() );
        cashDepositModel.reckoningSendDate( request.getReckoningSendDate() );
        cashDepositModel.depositSlipWithTax( request.getDepositSlipWithTax() );
        cashDepositModel.depositSlipWithoutTax( request.getDepositSlipWithoutTax() );
        cashDepositModel.depositSlipType( request.getDepositSlipType() );

        return cashDepositModel.build();
    }
}
