package com.nicico.internal.sales.lc.dto;

import com.nicico.internal.sales.lc.dto.LcIssueProviderDto.Create;
import com.nicico.internal.sales.lc.dto.LcIssueProviderDto.Info;
import com.nicico.internal.sales.lc.model.LcIssueProviderModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class LcIssueMapperImpl implements LcIssueMapper {

    @Override
    public Info toDTO(LcIssueProviderModel model) {
        if ( model == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( model.getId() );
        info.setMasterId( model.getMasterId() );
        info.setPerformaNo( model.getPerformaNo() );
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
        info.setReckoningSendDate( model.getReckoningSendDate() );
        info.setOfferDescription( model.getOfferDescription() );
        info.setImeCommoditySymbol( model.getImeCommoditySymbol() );

        return info;
    }

    @Override
    public LcIssueProviderModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        LcIssueProviderModel lcIssueProviderModel = new LcIssueProviderModel();

        lcIssueProviderModel.setId( request.getId() );
        lcIssueProviderModel.setMasterId( request.getMasterId() );
        lcIssueProviderModel.setPerformaNo( request.getPerformaNo() );
        lcIssueProviderModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        lcIssueProviderModel.setPerformaDate( request.getPerformaDate() );
        lcIssueProviderModel.setContractDate( request.getContractDate() );
        lcIssueProviderModel.setContractNo( request.getContractNo() );
        lcIssueProviderModel.setNationalCode( request.getNationalCode() );
        lcIssueProviderModel.setPaymentCode( request.getPaymentCode() );
        lcIssueProviderModel.setCustomerId( request.getCustomerId() );
        lcIssueProviderModel.setCustomerName( request.getCustomerName() );
        lcIssueProviderModel.setEconomicCode( request.getEconomicCode() );
        lcIssueProviderModel.setGoodId( request.getGoodId() );
        lcIssueProviderModel.setGoodName( request.getGoodName() );
        lcIssueProviderModel.setCreditPercentage( request.getCreditPercentage() );
        lcIssueProviderModel.setCashPercentage( request.getCashPercentage() );
        lcIssueProviderModel.setTotalCashAmount( request.getTotalCashAmount() );
        lcIssueProviderModel.setTotalCreditAmount( request.getTotalCreditAmount() );
        lcIssueProviderModel.setTotalVatAmount( request.getTotalVatAmount() );
        lcIssueProviderModel.setTotalQuantity( request.getTotalQuantity() );
        lcIssueProviderModel.setTradeId( request.getTradeId() );
        lcIssueProviderModel.setProformaIssueType( request.getProformaIssueType() );
        lcIssueProviderModel.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        lcIssueProviderModel.setBrokerId( request.getBrokerId() );
        lcIssueProviderModel.setBrokerName( request.getBrokerName() );
        lcIssueProviderModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        lcIssueProviderModel.setOfferDescription( request.getOfferDescription() );
        lcIssueProviderModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        lcIssueProviderModel.setReckoningSendDate( request.getReckoningSendDate() );

        return lcIssueProviderModel;
    }
}
