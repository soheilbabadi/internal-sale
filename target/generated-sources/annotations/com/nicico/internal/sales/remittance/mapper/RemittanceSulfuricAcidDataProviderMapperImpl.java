package com.nicico.internal.sales.remittance.mapper;

import com.nicico.internal.sales.remittance.dto.RemittanceTradeDataProviderDto.Create;
import com.nicico.internal.sales.remittance.dto.RemittanceTradeDataProviderDto.Info;
import com.nicico.internal.sales.remittance.model.RemittanceSulfuricAcidDataProviderModel;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class RemittanceSulfuricAcidDataProviderMapperImpl implements RemittanceSulfuricAcidDataProviderMapper {

    @Override
    public Info toDTO(RemittanceSulfuricAcidDataProviderModel model) {
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
    public RemittanceSulfuricAcidDataProviderModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        RemittanceSulfuricAcidDataProviderModel remittanceSulfuricAcidDataProviderModel = new RemittanceSulfuricAcidDataProviderModel();

        remittanceSulfuricAcidDataProviderModel.setId( request.getId() );
        remittanceSulfuricAcidDataProviderModel.setPaymentCode( request.getPaymentCode() );
        remittanceSulfuricAcidDataProviderModel.setCustomerId( request.getCustomerId() );
        remittanceSulfuricAcidDataProviderModel.setCustomerName( request.getCustomerName() );
        remittanceSulfuricAcidDataProviderModel.setEconomicCode( request.getEconomicCode() );
        remittanceSulfuricAcidDataProviderModel.setNationalCode( request.getNationalCode() );
        remittanceSulfuricAcidDataProviderModel.setGoodId( request.getGoodId() );
        remittanceSulfuricAcidDataProviderModel.setGoodName( request.getGoodName() );
        remittanceSulfuricAcidDataProviderModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        remittanceSulfuricAcidDataProviderModel.setContractDate( request.getContractDate() );
        remittanceSulfuricAcidDataProviderModel.setContractNo( request.getContractNo() );
        remittanceSulfuricAcidDataProviderModel.setBuyerBrokerId( request.getBuyerBrokerId() );
        remittanceSulfuricAcidDataProviderModel.setBuyerBrokerName( request.getBuyerBrokerName() );
        remittanceSulfuricAcidDataProviderModel.setSellerBrokerId( request.getSellerBrokerId() );
        remittanceSulfuricAcidDataProviderModel.setSellerBrokerName( request.getSellerBrokerName() );
        remittanceSulfuricAcidDataProviderModel.setUnitCount( request.getUnitCount() );
        remittanceSulfuricAcidDataProviderModel.setUnitPrice( request.getUnitPrice() );
        remittanceSulfuricAcidDataProviderModel.setCommission( request.getCommission() );
        remittanceSulfuricAcidDataProviderModel.setFinalAmount( request.getFinalAmount() );
        remittanceSulfuricAcidDataProviderModel.setSettlementType( request.getSettlementType() );
        remittanceSulfuricAcidDataProviderModel.setSettlementTypeDesc( request.getSettlementTypeDesc() );
        remittanceSulfuricAcidDataProviderModel.setContractTypeCode( request.getContractTypeCode() );
        remittanceSulfuricAcidDataProviderModel.setContractTypeDescription( request.getContractTypeDescription() );
        remittanceSulfuricAcidDataProviderModel.setSettlementDate( request.getSettlementDate() );
        remittanceSulfuricAcidDataProviderModel.setDeliveryDate( request.getDeliveryDate() );
        remittanceSulfuricAcidDataProviderModel.setDelayPenalty( request.isDelayPenalty() );
        remittanceSulfuricAcidDataProviderModel.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        remittanceSulfuricAcidDataProviderModel.setOfferDescription( request.getOfferDescription() );
        remittanceSulfuricAcidDataProviderModel.setLotNumber( request.getLotNumber() );

        return remittanceSulfuricAcidDataProviderModel;
    }
}
