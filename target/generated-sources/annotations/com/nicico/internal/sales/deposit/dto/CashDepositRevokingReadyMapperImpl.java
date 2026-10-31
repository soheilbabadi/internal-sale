package com.nicico.internal.sales.deposit.dto;

import com.nicico.internal.sales.deposit.dto.CashDepositReadyReckoningDto.Create;
import com.nicico.internal.sales.deposit.dto.CashDepositReadyReckoningDto.Info;
import com.nicico.internal.sales.deposit.model.CashDepositReadyReckoningModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class CashDepositRevokingReadyMapperImpl implements CashDepositRevokingReadyMapper {

    @Override
    public Info toDTO(CashDepositReadyReckoningModel model) {
        if ( model == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( model.getId() );
        info.setMasterId( model.getMasterId() );
        info.setProformaNo( model.getProformaNo() );
        info.setTotalFinalAmount( model.getTotalFinalAmount() );
        info.setPerformaDate( model.getPerformaDate() );
        info.setContractDate( model.getContractDate() );
        info.setContractNo( model.getContractNo() );
        info.setNationalCode( model.getNationalCode() );
        info.setPaymentCode( model.getPaymentCode() );
        info.setCustomerId( model.getCustomerId() );
        info.setCustomerName( model.getCustomerName() );
        info.setEconomicCode( model.getEconomicCode() );
        info.setGoodId( model.getGoodId() );
        info.setGoodName( model.getGoodName() );
        info.setCreditPercentage( model.getCreditPercentage() );
        info.setCashPercentage( model.getCashPercentage() );
        info.setTotalCashAmount( model.getTotalCashAmount() );
        info.setTotalCreditAmount( model.getTotalCreditAmount() );
        info.setTotalVatAmount( model.getTotalVatAmount() );
        info.setTotalQuantity( model.getTotalQuantity() );
        info.setTradeId( model.getTradeId() );
        info.setProformaIssueType( model.getProformaIssueType() );
        info.setWorkflowApproveStatus( model.getWorkflowApproveStatus() );
        info.setBrokerId( model.getBrokerId() );
        info.setBrokerName( model.getBrokerName() );
        info.setBrokerNationalCode( model.getBrokerNationalCode() );
        info.setOfferDescription( model.getOfferDescription() );
        info.setImeCommoditySymbol( model.getImeCommoditySymbol() );
        info.setReckoningSend( model.isReckoningSend() );
        info.setReckoningSendDate( model.getReckoningSendDate() );

        return info;
    }

    @Override
    public CashDepositReadyReckoningModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        CashDepositReadyReckoningModel cashDepositReadyReckoningModel = new CashDepositReadyReckoningModel();

        cashDepositReadyReckoningModel.setId( request.getId() );
        cashDepositReadyReckoningModel.setMasterId( request.getMasterId() );
        cashDepositReadyReckoningModel.setProformaNo( request.getProformaNo() );
        cashDepositReadyReckoningModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        cashDepositReadyReckoningModel.setPerformaDate( request.getPerformaDate() );
        cashDepositReadyReckoningModel.setContractDate( request.getContractDate() );
        cashDepositReadyReckoningModel.setContractNo( request.getContractNo() );
        cashDepositReadyReckoningModel.setNationalCode( request.getNationalCode() );
        cashDepositReadyReckoningModel.setPaymentCode( request.getPaymentCode() );
        cashDepositReadyReckoningModel.setCustomerId( request.getCustomerId() );
        cashDepositReadyReckoningModel.setCustomerName( request.getCustomerName() );
        cashDepositReadyReckoningModel.setEconomicCode( request.getEconomicCode() );
        cashDepositReadyReckoningModel.setGoodId( request.getGoodId() );
        cashDepositReadyReckoningModel.setGoodName( request.getGoodName() );
        cashDepositReadyReckoningModel.setCreditPercentage( request.getCreditPercentage() );
        cashDepositReadyReckoningModel.setCashPercentage( request.getCashPercentage() );
        cashDepositReadyReckoningModel.setTotalCashAmount( request.getTotalCashAmount() );
        cashDepositReadyReckoningModel.setTotalCreditAmount( request.getTotalCreditAmount() );
        cashDepositReadyReckoningModel.setTotalVatAmount( request.getTotalVatAmount() );
        cashDepositReadyReckoningModel.setTotalQuantity( request.getTotalQuantity() );
        cashDepositReadyReckoningModel.setTradeId( request.getTradeId() );
        cashDepositReadyReckoningModel.setProformaIssueType( request.getProformaIssueType() );
        cashDepositReadyReckoningModel.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        cashDepositReadyReckoningModel.setBrokerId( request.getBrokerId() );
        cashDepositReadyReckoningModel.setBrokerName( request.getBrokerName() );
        cashDepositReadyReckoningModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        cashDepositReadyReckoningModel.setOfferDescription( request.getOfferDescription() );
        cashDepositReadyReckoningModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        cashDepositReadyReckoningModel.setReckoningSend( request.isReckoningSend() );
        cashDepositReadyReckoningModel.setReckoningSendDate( request.getReckoningSendDate() );

        return cashDepositReadyReckoningModel;
    }
}
