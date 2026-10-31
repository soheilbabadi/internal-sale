package com.nicico.internal.sales.trade.dto;

import com.nicico.internal.sales.trade.dto.TradeExtractDto.Create;
import com.nicico.internal.sales.trade.dto.TradeExtractDto.Info;
import com.nicico.internal.sales.trade.model.TradeExtractStartProformaModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class TradeExtractStartProformaMapperImpl implements TradeExtractStartProformaMapper {

    @Override
    public Info toDTO(TradeExtractStartProformaModel request) {
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

        return info;
    }

    @Override
    public TradeExtractStartProformaModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        TradeExtractStartProformaModel tradeExtractStartProformaModel = new TradeExtractStartProformaModel();

        tradeExtractStartProformaModel.setId( request.getId() );
        tradeExtractStartProformaModel.setBuyerName( request.getBuyerName() );
        tradeExtractStartProformaModel.setBuyerNationalCode( request.getBuyerNationalCode() );
        tradeExtractStartProformaModel.setCommodityCode( request.getCommodityCode() );
        tradeExtractStartProformaModel.setCommoditySymbol( request.getCommoditySymbol() );
        tradeExtractStartProformaModel.setCommodityName( request.getCommodityName() );
        tradeExtractStartProformaModel.setCashPercentage( request.getCashPercentage() );
        tradeExtractStartProformaModel.setCreditPercentage( request.getCreditPercentage() );
        tradeExtractStartProformaModel.setCommission( request.getCommission() );
        tradeExtractStartProformaModel.setContractDate( request.getContractDate() );
        tradeExtractStartProformaModel.setContractNo( request.getContractNo() );
        tradeExtractStartProformaModel.setPaymentCode( request.getPaymentCode() );
        tradeExtractStartProformaModel.setSettlementType( request.getSettlementType() );
        tradeExtractStartProformaModel.setSettlementTypeDesc( request.getSettlementTypeDesc() );
        tradeExtractStartProformaModel.setUnitCount( request.getUnitCount() );
        tradeExtractStartProformaModel.setUnitPrice( request.getUnitPrice() );
        tradeExtractStartProformaModel.setCreditUnitPrice( request.getCreditUnitPrice() );
        tradeExtractStartProformaModel.setVatCoefficient( request.getVatCoefficient() );
        tradeExtractStartProformaModel.setVatAmount( request.getVatAmount() );
        tradeExtractStartProformaModel.setCreditAmount( request.getCreditAmount() );
        tradeExtractStartProformaModel.setCashAmount( request.getCashAmount() );
        tradeExtractStartProformaModel.setFinalAmount( request.getFinalAmount() );
        tradeExtractStartProformaModel.setPreInvoiceStatus( request.getPreInvoiceStatus() );
        tradeExtractStartProformaModel.setLcStatus( request.getLcStatus() );
        tradeExtractStartProformaModel.setMasterId( request.getMasterId() );
        tradeExtractStartProformaModel.setOfferDescription( request.getOfferDescription() );

        return tradeExtractStartProformaModel;
    }
}
