package com.nicico.internal.sales.extrabill.dto;

import com.nicico.internal.sales.extrabill.dto.ExtraBillIssueProviderDto.Create;
import com.nicico.internal.sales.extrabill.dto.ExtraBillIssueProviderDto.Info;
import com.nicico.internal.sales.extrabill.model.ExtraBillIssueProviderModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class ExtraBillIssueMapperImpl implements ExtraBillIssueMapper {

    @Override
    public Info toDTO(ExtraBillIssueProviderModel model) {
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

        return info;
    }

    @Override
    public ExtraBillIssueProviderModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        ExtraBillIssueProviderModel extraBillIssueProviderModel = new ExtraBillIssueProviderModel();

        extraBillIssueProviderModel.setId( request.getId() );
        extraBillIssueProviderModel.setMasterId( request.getMasterId() );
        extraBillIssueProviderModel.setProformaNo( request.getProformaNo() );
        extraBillIssueProviderModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        extraBillIssueProviderModel.setPerformaDate( request.getPerformaDate() );
        extraBillIssueProviderModel.setContractDate( request.getContractDate() );
        extraBillIssueProviderModel.setContractNo( request.getContractNo() );
        extraBillIssueProviderModel.setNationalCode( request.getNationalCode() );
        extraBillIssueProviderModel.setPaymentCode( request.getPaymentCode() );
        extraBillIssueProviderModel.setCustomerId( request.getCustomerId() );
        extraBillIssueProviderModel.setCustomerName( request.getCustomerName() );
        extraBillIssueProviderModel.setEconomicCode( request.getEconomicCode() );
        extraBillIssueProviderModel.setGoodId( request.getGoodId() );
        extraBillIssueProviderModel.setGoodName( request.getGoodName() );
        extraBillIssueProviderModel.setCreditPercentage( request.getCreditPercentage() );
        extraBillIssueProviderModel.setCashPercentage( request.getCashPercentage() );
        extraBillIssueProviderModel.setTotalCashAmount( request.getTotalCashAmount() );
        extraBillIssueProviderModel.setTotalCreditAmount( request.getTotalCreditAmount() );
        extraBillIssueProviderModel.setTotalVatAmount( request.getTotalVatAmount() );
        extraBillIssueProviderModel.setTotalQuantity( request.getTotalQuantity() );
        extraBillIssueProviderModel.setTradeId( request.getTradeId() );
        extraBillIssueProviderModel.setProformaIssueType( request.getProformaIssueType() );
        extraBillIssueProviderModel.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        extraBillIssueProviderModel.setBrokerId( request.getBrokerId() );
        extraBillIssueProviderModel.setBrokerName( request.getBrokerName() );
        extraBillIssueProviderModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        extraBillIssueProviderModel.setOfferDescription( request.getOfferDescription() );
        extraBillIssueProviderModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );

        return extraBillIssueProviderModel;
    }
}
