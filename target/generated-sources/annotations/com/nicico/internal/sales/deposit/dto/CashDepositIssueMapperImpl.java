package com.nicico.internal.sales.deposit.dto;

import com.nicico.internal.sales.deposit.dto.CashDepositIssueHistoryDto.Create;
import com.nicico.internal.sales.deposit.dto.CashDepositIssueHistoryDto.Info;
import com.nicico.internal.sales.deposit.model.CashDepositIssueHistoryModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class CashDepositIssueMapperImpl implements CashDepositIssueMapper {

    @Override
    public Info toDTO(CashDepositIssueHistoryModel model) {
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
        info.setDepositSlipWithTax( model.getDepositSlipWithTax() );
        info.setDepositSlipWithoutTax( model.getDepositSlipWithoutTax() );
        info.setDepositSlipType( model.getDepositSlipType() );

        return info;
    }

    @Override
    public CashDepositIssueHistoryModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        CashDepositIssueHistoryModel cashDepositIssueHistoryModel = new CashDepositIssueHistoryModel();

        cashDepositIssueHistoryModel.setId( request.getId() );
        cashDepositIssueHistoryModel.setMasterId( request.getMasterId() );
        cashDepositIssueHistoryModel.setProformaNo( request.getProformaNo() );
        cashDepositIssueHistoryModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        cashDepositIssueHistoryModel.setPerformaDate( request.getPerformaDate() );
        cashDepositIssueHistoryModel.setContractDate( request.getContractDate() );
        cashDepositIssueHistoryModel.setContractNo( request.getContractNo() );
        cashDepositIssueHistoryModel.setNationalCode( request.getNationalCode() );
        cashDepositIssueHistoryModel.setPaymentCode( request.getPaymentCode() );
        cashDepositIssueHistoryModel.setCustomerId( request.getCustomerId() );
        cashDepositIssueHistoryModel.setCustomerName( request.getCustomerName() );
        cashDepositIssueHistoryModel.setEconomicCode( request.getEconomicCode() );
        cashDepositIssueHistoryModel.setGoodId( request.getGoodId() );
        cashDepositIssueHistoryModel.setGoodName( request.getGoodName() );
        cashDepositIssueHistoryModel.setCreditPercentage( request.getCreditPercentage() );
        cashDepositIssueHistoryModel.setCashPercentage( request.getCashPercentage() );
        cashDepositIssueHistoryModel.setTotalCashAmount( request.getTotalCashAmount() );
        cashDepositIssueHistoryModel.setTotalCreditAmount( request.getTotalCreditAmount() );
        cashDepositIssueHistoryModel.setTotalVatAmount( request.getTotalVatAmount() );
        cashDepositIssueHistoryModel.setTotalQuantity( request.getTotalQuantity() );
        cashDepositIssueHistoryModel.setTradeId( request.getTradeId() );
        cashDepositIssueHistoryModel.setProformaIssueType( request.getProformaIssueType() );
        cashDepositIssueHistoryModel.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        cashDepositIssueHistoryModel.setBrokerId( request.getBrokerId() );
        cashDepositIssueHistoryModel.setBrokerName( request.getBrokerName() );
        cashDepositIssueHistoryModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        cashDepositIssueHistoryModel.setOfferDescription( request.getOfferDescription() );
        cashDepositIssueHistoryModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        cashDepositIssueHistoryModel.setDepositSlipWithTax( request.getDepositSlipWithTax() );
        cashDepositIssueHistoryModel.setDepositSlipWithoutTax( request.getDepositSlipWithoutTax() );
        cashDepositIssueHistoryModel.setDepositSlipType( request.getDepositSlipType() );

        return cashDepositIssueHistoryModel;
    }
}
