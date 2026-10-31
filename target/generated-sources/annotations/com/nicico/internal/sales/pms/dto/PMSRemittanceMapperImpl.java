package com.nicico.internal.sales.pms.dto;

import com.nicico.internal.sales.pms.dto.PMSRemittanceDTO.Create;
import com.nicico.internal.sales.pms.dto.PMSRemittanceDTO.Create.CreateBuilder;
import com.nicico.internal.sales.pms.dto.PMSRemittanceDTO.Nullables;
import com.nicico.internal.sales.pms.dto.PMSRemittanceDTO.Nullables.NullablesBuilder;
import com.nicico.internal.sales.pms.dto.PMSRemittanceDTO.Update;
import com.nicico.internal.sales.pms.dto.PMSRemittanceDTO.Update.UpdateBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class PMSRemittanceMapperImpl implements PMSRemittanceMapper {

    @Override
    public Nullables toNullablesDTO(PMSRemittanceDTO pmsRemittanceDTO) {
        if ( pmsRemittanceDTO == null ) {
            return null;
        }

        NullablesBuilder<?, ?> nullables = Nullables.builderr();

        nullables.pmsBuyerId( pmsRemittanceDTO.getPmsBuyerId() );
        nullables.pmsGoodId( pmsRemittanceDTO.getPmsGoodId() );
        nullables.pmsGoodUnit( pmsRemittanceDTO.getPmsGoodUnit() );
        nullables.name( pmsRemittanceDTO.getName() );
        nullables.issueDate( pmsRemittanceDTO.getIssueDate() );
        nullables.amount( pmsRemittanceDTO.getAmount() );

        return nullables.buildd();
    }

    @Override
    public Create toCreateDTO(Nullables pmsRemittance, String user, String pass, String username) {
        if ( pmsRemittance == null && user == null && pass == null && username == null ) {
            return null;
        }

        CreateBuilder<?, ?> create = Create.builderr();

        if ( pmsRemittance != null ) {
            create.pmsBuyerId( pmsRemittance.getPmsBuyerId() );
            create.pmsGoodId( pmsRemittance.getPmsGoodId() );
            create.pmsGoodUnit( pmsRemittance.getPmsGoodUnit() );
            create.name( pmsRemittance.getName() );
            create.issueDate( pmsRemittance.getIssueDate() );
            create.amount( pmsRemittance.getAmount() );
            create.processId( pmsRemittance.getProcessId() );
            create.stcProcess( pmsRemittance.getStcProcess() );
            create.sellerId( pmsRemittance.getSellerId() );
            create.loadId( pmsRemittance.getLoadId() );
            create.from( pmsRemittance.getFrom() );
            create.to( pmsRemittance.getTo() );
            create.remittanceStatus( pmsRemittance.getRemittanceStatus() );
            create.unLoadId( pmsRemittance.getUnLoadId() );
            create.validUntil( pmsRemittance.getValidUntil() );
            create.isFinal( pmsRemittance.getIsFinal() );
            create.buyDate( pmsRemittance.getBuyDate() );
            create.lastPayDate( pmsRemittance.getLastPayDate() );
            create.description( pmsRemittance.getDescription() );
            create.contractorId( pmsRemittance.getContractorId() );
            create.code( pmsRemittance.getCode() );
            create.status( pmsRemittance.getStatus() );
            create.price( pmsRemittance.getPrice() );
            create.contractCode( pmsRemittance.getContractCode() );
            create.contractDescription( pmsRemittance.getContractDescription() );
            create.sanadCode( pmsRemittance.getSanadCode() );
            create.contractDate( pmsRemittance.getContractDate() );
            create.contractValidDate( pmsRemittance.getContractValidDate() );
            create.payFeshNumber( pmsRemittance.getPayFeshNumber() );
            create.payFeshDate( pmsRemittance.getPayFeshDate() );
            create.payBankCode( pmsRemittance.getPayBankCode() );
            create.ldFesh( pmsRemittance.getLdFesh() );
            create.ldFeshDate( pmsRemittance.getLdFeshDate() );
            create.ldBankCode( pmsRemittance.getLdBankCode() );
            create.pdBankCode( pmsRemittance.getPdBankCode() );
            create.pdFeshDate( pmsRemittance.getPdFeshDate() );
            create.pdFesh( pmsRemittance.getPdFesh() );
            create.payType( pmsRemittance.getPayType() );
            create.paymankarKh( pmsRemittance.getPaymankarKh() );
            create.finalDate( pmsRemittance.getFinalDate() );
            create.finalUser( pmsRemittance.getFinalUser() );
            create.invoiceState( pmsRemittance.getInvoiceState() );
            create.financeCustomerId( pmsRemittance.getFinanceCustomerId() );
            create.cashPercent( pmsRemittance.getCashPercent() );
            create.creditPercent( pmsRemittance.getCreditPercent() );
            create.cashUnitPrice( pmsRemittance.getCashUnitPrice() );
            create.creditUnitPrice( pmsRemittance.getCreditUnitPrice() );
            create.lcId( pmsRemittance.getLcId() );
            create.prefactorId( pmsRemittance.getPrefactorId() );
            create.pollutionCashPercent( pmsRemittance.getPollutionCashPercent() );
            create.pollutionCreditPercent( pmsRemittance.getPollutionCreditPercent() );
            create.taxCashPercent( pmsRemittance.getTaxCashPercent() );
            create.taxCreditPercent( pmsRemittance.getTaxCreditPercent() );
            create.brokerId( pmsRemittance.getBrokerId() );
            create.lotNumber( pmsRemittance.getLotNumber() );
        }
        if ( user != null ) {
            create.user( user );
        }
        if ( pass != null ) {
            create.pass( pass );
        }
        if ( username != null ) {
            create.username( username );
        }

        return create.buildd();
    }

    @Override
    public Update toUpdateDTO(Nullables pmsRemittance, String user, String pass, String username) {
        if ( pmsRemittance == null && user == null && pass == null && username == null ) {
            return null;
        }

        UpdateBuilder<?, ?> update = Update.builderr();

        if ( pmsRemittance != null ) {
            update.pmsBuyerId( pmsRemittance.getPmsBuyerId() );
            update.pmsGoodId( pmsRemittance.getPmsGoodId() );
            update.pmsGoodUnit( pmsRemittance.getPmsGoodUnit() );
            update.name( pmsRemittance.getName() );
            update.issueDate( pmsRemittance.getIssueDate() );
            update.amount( pmsRemittance.getAmount() );
            update.processId( pmsRemittance.getProcessId() );
            update.stcProcess( pmsRemittance.getStcProcess() );
            update.sellerId( pmsRemittance.getSellerId() );
            update.loadId( pmsRemittance.getLoadId() );
            update.from( pmsRemittance.getFrom() );
            update.to( pmsRemittance.getTo() );
            update.remittanceStatus( pmsRemittance.getRemittanceStatus() );
            update.unLoadId( pmsRemittance.getUnLoadId() );
            update.validUntil( pmsRemittance.getValidUntil() );
            update.isFinal( pmsRemittance.getIsFinal() );
            update.buyDate( pmsRemittance.getBuyDate() );
            update.lastPayDate( pmsRemittance.getLastPayDate() );
            update.description( pmsRemittance.getDescription() );
            update.contractorId( pmsRemittance.getContractorId() );
            update.code( pmsRemittance.getCode() );
            update.status( pmsRemittance.getStatus() );
            update.price( pmsRemittance.getPrice() );
            update.contractCode( pmsRemittance.getContractCode() );
            update.contractDescription( pmsRemittance.getContractDescription() );
            update.sanadCode( pmsRemittance.getSanadCode() );
            update.contractDate( pmsRemittance.getContractDate() );
            update.contractValidDate( pmsRemittance.getContractValidDate() );
            update.payFeshNumber( pmsRemittance.getPayFeshNumber() );
            update.payFeshDate( pmsRemittance.getPayFeshDate() );
            update.payBankCode( pmsRemittance.getPayBankCode() );
            update.ldFesh( pmsRemittance.getLdFesh() );
            update.ldFeshDate( pmsRemittance.getLdFeshDate() );
            update.ldBankCode( pmsRemittance.getLdBankCode() );
            update.pdBankCode( pmsRemittance.getPdBankCode() );
            update.pdFeshDate( pmsRemittance.getPdFeshDate() );
            update.pdFesh( pmsRemittance.getPdFesh() );
            update.payType( pmsRemittance.getPayType() );
            update.paymankarKh( pmsRemittance.getPaymankarKh() );
            update.finalDate( pmsRemittance.getFinalDate() );
            update.finalUser( pmsRemittance.getFinalUser() );
            update.invoiceState( pmsRemittance.getInvoiceState() );
            update.financeCustomerId( pmsRemittance.getFinanceCustomerId() );
            update.cashPercent( pmsRemittance.getCashPercent() );
            update.creditPercent( pmsRemittance.getCreditPercent() );
            update.cashUnitPrice( pmsRemittance.getCashUnitPrice() );
            update.creditUnitPrice( pmsRemittance.getCreditUnitPrice() );
            update.lcId( pmsRemittance.getLcId() );
            update.prefactorId( pmsRemittance.getPrefactorId() );
            update.pollutionCashPercent( pmsRemittance.getPollutionCashPercent() );
            update.pollutionCreditPercent( pmsRemittance.getPollutionCreditPercent() );
            update.taxCashPercent( pmsRemittance.getTaxCashPercent() );
            update.taxCreditPercent( pmsRemittance.getTaxCreditPercent() );
            update.brokerId( pmsRemittance.getBrokerId() );
            update.lotNumber( pmsRemittance.getLotNumber() );
        }
        if ( user != null ) {
            update.user( user );
        }
        if ( pass != null ) {
            update.pass( pass );
        }
        if ( username != null ) {
            update.username( username );
        }

        return update.buildd();
    }
}
