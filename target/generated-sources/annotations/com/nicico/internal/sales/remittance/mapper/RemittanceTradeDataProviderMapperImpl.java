package com.nicico.internal.sales.remittance.mapper;

import com.nicico.internal.sales.remittance.dto.RemittanceTradeDataProviderDto.Create;
import com.nicico.internal.sales.remittance.dto.RemittanceTradeDataProviderDto.Info;
import com.nicico.internal.sales.remittance.model.RemittanceTradeDataProviderModel;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class RemittanceTradeDataProviderMapperImpl implements RemittanceTradeDataProviderMapper {

    @Override
    public Info toDTO(RemittanceTradeDataProviderModel model) {
        if ( model == null ) {
            return null;
        }

        Date createdDate = null;
        Date lastModifiedDate = null;
        String createdBy = null;
        String lastModifiedBy = null;
        String comment = null;

        Info info = new Info( createdDate, lastModifiedDate, createdBy, lastModifiedBy, comment );

        info.setId( model.getId() );
        info.setPaymentCode( model.getPaymentCode() );
        info.setCustomerId( model.getCustomerId() );
        info.setCustomerName( model.getCustomerName() );
        info.setEconomicCode( model.getEconomicCode() );
        info.setNationalCode( model.getNationalCode() );
        info.setGoodId( model.getGoodId() );
        info.setGoodName( model.getGoodName() );
        info.setContractDate( model.getContractDate() );
        info.setContractNo( model.getContractNo() );
        info.setBuyerBrokerId( model.getBuyerBrokerId() );
        info.setBuyerBrokerName( model.getBuyerBrokerName() );
        info.setSellerBrokerId( model.getSellerBrokerId() );
        info.setSellerBrokerName( model.getSellerBrokerName() );
        info.setUnitCount( model.getUnitCount() );
        info.setUnitPrice( model.getUnitPrice() );
        info.setFinalAmount( model.getFinalAmount() );
        info.setSettlementType( model.getSettlementType() );
        info.setSettlementTypeDesc( model.getSettlementTypeDesc() );
        info.setContractTypeCode( model.getContractTypeCode() );
        info.setContractTypeDescription( model.getContractTypeDescription() );
        info.setSettlementDate( model.getSettlementDate() );
        info.setDeliveryDate( model.getDeliveryDate() );
        info.setDelayPenalty( model.isDelayPenalty() );
        info.setCommission( model.getCommission() );
        info.setImeCommoditySymbol( model.getImeCommoditySymbol() );
        info.setWorkflowApproveStatus( model.getWorkflowApproveStatus() );
        info.setOfferDescription( model.getOfferDescription() );
        info.setLotNumber( model.getLotNumber() );

        return info;
    }

    @Override
    public RemittanceTradeDataProviderModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        RemittanceTradeDataProviderModel remittanceTradeDataProviderModel = new RemittanceTradeDataProviderModel();

        remittanceTradeDataProviderModel.setId( request.getId() );
        remittanceTradeDataProviderModel.setPaymentCode( request.getPaymentCode() );
        remittanceTradeDataProviderModel.setCustomerId( request.getCustomerId() );
        remittanceTradeDataProviderModel.setCustomerName( request.getCustomerName() );
        remittanceTradeDataProviderModel.setEconomicCode( request.getEconomicCode() );
        remittanceTradeDataProviderModel.setNationalCode( request.getNationalCode() );
        remittanceTradeDataProviderModel.setGoodId( request.getGoodId() );
        remittanceTradeDataProviderModel.setGoodName( request.getGoodName() );
        remittanceTradeDataProviderModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        remittanceTradeDataProviderModel.setContractDate( request.getContractDate() );
        remittanceTradeDataProviderModel.setContractNo( request.getContractNo() );
        remittanceTradeDataProviderModel.setBuyerBrokerId( request.getBuyerBrokerId() );
        remittanceTradeDataProviderModel.setBuyerBrokerName( request.getBuyerBrokerName() );
        remittanceTradeDataProviderModel.setSellerBrokerId( request.getSellerBrokerId() );
        remittanceTradeDataProviderModel.setSellerBrokerName( request.getSellerBrokerName() );
        remittanceTradeDataProviderModel.setUnitCount( request.getUnitCount() );
        remittanceTradeDataProviderModel.setUnitPrice( request.getUnitPrice() );
        remittanceTradeDataProviderModel.setCommission( request.getCommission() );
        remittanceTradeDataProviderModel.setFinalAmount( request.getFinalAmount() );
        remittanceTradeDataProviderModel.setSettlementType( request.getSettlementType() );
        remittanceTradeDataProviderModel.setSettlementTypeDesc( request.getSettlementTypeDesc() );
        remittanceTradeDataProviderModel.setContractTypeCode( request.getContractTypeCode() );
        remittanceTradeDataProviderModel.setContractTypeDescription( request.getContractTypeDescription() );
        remittanceTradeDataProviderModel.setSettlementDate( request.getSettlementDate() );
        remittanceTradeDataProviderModel.setDeliveryDate( request.getDeliveryDate() );
        remittanceTradeDataProviderModel.setDelayPenalty( request.isDelayPenalty() );
        remittanceTradeDataProviderModel.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        remittanceTradeDataProviderModel.setOfferDescription( request.getOfferDescription() );
        remittanceTradeDataProviderModel.setLotNumber( request.getLotNumber() );

        return remittanceTradeDataProviderModel;
    }
}
