package com.nicico.internal.sales.gaam.mapper;

import com.nicico.internal.sales.gaam.dto.GaamIssueProviderDto.Create;
import com.nicico.internal.sales.gaam.dto.GaamIssueProviderDto.Info;
import com.nicico.internal.sales.gaam.model.GaamIssueProviderModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class GaamIssueMapperImpl implements GaamIssueMapper {

    @Override
    public Info toDTO(GaamIssueProviderModel model) {
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
    public GaamIssueProviderModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        GaamIssueProviderModel gaamIssueProviderModel = new GaamIssueProviderModel();

        gaamIssueProviderModel.setId( request.getId() );
        gaamIssueProviderModel.setMasterId( request.getMasterId() );
        gaamIssueProviderModel.setProformaNo( request.getProformaNo() );
        gaamIssueProviderModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        gaamIssueProviderModel.setPerformaDate( request.getPerformaDate() );
        gaamIssueProviderModel.setContractDate( request.getContractDate() );
        gaamIssueProviderModel.setContractNo( request.getContractNo() );
        gaamIssueProviderModel.setNationalCode( request.getNationalCode() );
        gaamIssueProviderModel.setPaymentCode( request.getPaymentCode() );
        gaamIssueProviderModel.setCustomerId( request.getCustomerId() );
        gaamIssueProviderModel.setCustomerName( request.getCustomerName() );
        gaamIssueProviderModel.setEconomicCode( request.getEconomicCode() );
        gaamIssueProviderModel.setGoodId( request.getGoodId() );
        gaamIssueProviderModel.setGoodName( request.getGoodName() );
        gaamIssueProviderModel.setCreditPercentage( request.getCreditPercentage() );
        gaamIssueProviderModel.setCashPercentage( request.getCashPercentage() );
        gaamIssueProviderModel.setTotalCashAmount( request.getTotalCashAmount() );
        gaamIssueProviderModel.setTotalCreditAmount( request.getTotalCreditAmount() );
        gaamIssueProviderModel.setTotalVatAmount( request.getTotalVatAmount() );
        gaamIssueProviderModel.setTotalQuantity( request.getTotalQuantity() );
        gaamIssueProviderModel.setTradeId( request.getTradeId() );
        gaamIssueProviderModel.setProformaIssueType( request.getProformaIssueType() );
        gaamIssueProviderModel.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        gaamIssueProviderModel.setBrokerId( request.getBrokerId() );
        gaamIssueProviderModel.setBrokerName( request.getBrokerName() );
        gaamIssueProviderModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        gaamIssueProviderModel.setOfferDescription( request.getOfferDescription() );
        gaamIssueProviderModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );

        return gaamIssueProviderModel;
    }
}
