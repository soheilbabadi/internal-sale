package com.nicico.internal.sales.trade.dto;

import com.nicico.internal.sales.trade.dto.TradeExtractDto.Create;
import com.nicico.internal.sales.trade.dto.TradeExtractDto.Info;
import com.nicico.internal.sales.trade.model.TradeExtractModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class TradeExtractMapperImpl implements TradeExtractMapper {

    @Override
    public Info toDTO(TradeExtractModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setBuyerName( request.getBuyerName() );
        info.setBuyerNationalCode( request.getBuyerNationalCode() );
        info.setCommodityCode( request.getCommodityCode() );
        info.setCommoditySymbol( request.getCommoditySymbol() );
        info.setCommodityName( request.getCommodityName() );
        info.setCashPercentage( request.getCashPercentage() );
        info.setCreditPercentage( request.getCreditPercentage() );
        info.setCommission( request.getCommission() );
        info.setContractDate( request.getContractDate() );
        info.setContractNo( request.getContractNo() );
        info.setPaymentCode( request.getPaymentCode() );
        info.setSettlementType( request.getSettlementType() );
        info.setSettlementTypeDesc( request.getSettlementTypeDesc() );
        info.setUnitCount( request.getUnitCount() );
        info.setUnitPrice( request.getUnitPrice() );
        info.setCreditUnitPrice( request.getCreditUnitPrice() );
        info.setVatCoefficient( request.getVatCoefficient() );
        info.setVatAmount( request.getVatAmount() );
        info.setCreditAmount( request.getCreditAmount() );
        info.setCashAmount( request.getCashAmount() );
        info.setFinalAmount( request.getFinalAmount() );
        info.setPreInvoiceStatus( request.getPreInvoiceStatus() );
        info.setLcStatus( request.getLcStatus() );
        if ( request.getMasterId() != null ) {
            info.setMasterId( request.getMasterId() );
        }
        info.setOfferDescription( request.getOfferDescription() );
        info.setSettlementDate( request.getSettlementDate() );

        return info;
    }

    @Override
    public TradeExtractModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        TradeExtractModel tradeExtractModel = new TradeExtractModel();

        tradeExtractModel.setId( request.getId() );
        tradeExtractModel.setBuyerName( request.getBuyerName() );
        tradeExtractModel.setBuyerNationalCode( request.getBuyerNationalCode() );
        tradeExtractModel.setCommodityCode( request.getCommodityCode() );
        tradeExtractModel.setCommoditySymbol( request.getCommoditySymbol() );
        tradeExtractModel.setCommodityName( request.getCommodityName() );
        tradeExtractModel.setCashPercentage( request.getCashPercentage() );
        tradeExtractModel.setCreditPercentage( request.getCreditPercentage() );
        tradeExtractModel.setCommission( request.getCommission() );
        tradeExtractModel.setContractDate( request.getContractDate() );
        tradeExtractModel.setContractNo( request.getContractNo() );
        tradeExtractModel.setPaymentCode( request.getPaymentCode() );
        tradeExtractModel.setSettlementType( request.getSettlementType() );
        tradeExtractModel.setSettlementTypeDesc( request.getSettlementTypeDesc() );
        tradeExtractModel.setUnitCount( request.getUnitCount() );
        tradeExtractModel.setUnitPrice( request.getUnitPrice() );
        tradeExtractModel.setCreditUnitPrice( request.getCreditUnitPrice() );
        tradeExtractModel.setVatCoefficient( request.getVatCoefficient() );
        tradeExtractModel.setVatAmount( request.getVatAmount() );
        tradeExtractModel.setCreditAmount( request.getCreditAmount() );
        tradeExtractModel.setCashAmount( request.getCashAmount() );
        tradeExtractModel.setFinalAmount( request.getFinalAmount() );
        tradeExtractModel.setPreInvoiceStatus( request.getPreInvoiceStatus() );
        tradeExtractModel.setLcStatus( request.getLcStatus() );
        tradeExtractModel.setMasterId( request.getMasterId() );
        tradeExtractModel.setOfferDescription( request.getOfferDescription() );
        tradeExtractModel.setSettlementDate( request.getSettlementDate() );

        return tradeExtractModel;
    }
}
