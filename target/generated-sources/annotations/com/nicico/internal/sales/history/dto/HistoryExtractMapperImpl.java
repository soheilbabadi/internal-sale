package com.nicico.internal.sales.history.dto;

import com.nicico.internal.sales.history.dto.HistoryExtractMasterDto.Create;
import com.nicico.internal.sales.history.dto.HistoryExtractMasterDto.Info;
import com.nicico.internal.sales.history.model.HistoryExtractMasterModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class HistoryExtractMapperImpl implements HistoryExtractMapper {

    @Override
    public Info toDTO(HistoryExtractMasterModel request) {
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
        info.setOfferDescription( request.getOfferDescription() );
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
        info.setPreinvoiceStatus( request.getPreinvoiceStatus() );
        info.setLcStatus( request.getLcStatus() );
        info.setRemittanceStatus( request.getRemittanceStatus() );
        info.setMasterId( request.getMasterId() );
        info.setRemittanceId( request.getRemittanceId() );
        info.setPerformaNo( request.getPerformaNo() );
        info.setPerformaDate( request.getPerformaDate() );
        info.setLcNo( request.getLcNo() );
        info.setLcDate( request.getLcDate() );
        info.setRemittanceDate( request.getRemittanceDate() );
        info.setRemittanceNo( request.getRemittanceNo() );
        info.setRemittancePmsId( request.getRemittancePmsId() );
        info.setLcPmsId( request.getLcPmsId() );
        info.setAcknowledgment( request.getAcknowledgment() );

        return info;
    }

    @Override
    public HistoryExtractMasterModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        HistoryExtractMasterModel historyExtractMasterModel = new HistoryExtractMasterModel();

        historyExtractMasterModel.setId( request.getId() );
        historyExtractMasterModel.setBuyerName( request.getBuyerName() );
        historyExtractMasterModel.setBuyerNationalCode( request.getBuyerNationalCode() );
        historyExtractMasterModel.setCommodityCode( request.getCommodityCode() );
        historyExtractMasterModel.setCommoditySymbol( request.getCommoditySymbol() );
        historyExtractMasterModel.setCommodityName( request.getCommodityName() );
        historyExtractMasterModel.setOfferDescription( request.getOfferDescription() );
        historyExtractMasterModel.setCashPercentage( request.getCashPercentage() );
        historyExtractMasterModel.setCreditPercentage( request.getCreditPercentage() );
        historyExtractMasterModel.setCommission( request.getCommission() );
        historyExtractMasterModel.setContractDate( request.getContractDate() );
        historyExtractMasterModel.setContractNo( request.getContractNo() );
        historyExtractMasterModel.setPaymentCode( request.getPaymentCode() );
        historyExtractMasterModel.setSettlementType( request.getSettlementType() );
        historyExtractMasterModel.setSettlementTypeDesc( request.getSettlementTypeDesc() );
        historyExtractMasterModel.setUnitCount( request.getUnitCount() );
        historyExtractMasterModel.setUnitPrice( request.getUnitPrice() );
        historyExtractMasterModel.setCreditUnitPrice( request.getCreditUnitPrice() );
        historyExtractMasterModel.setVatCoefficient( request.getVatCoefficient() );
        historyExtractMasterModel.setVatAmount( request.getVatAmount() );
        historyExtractMasterModel.setCreditAmount( request.getCreditAmount() );
        historyExtractMasterModel.setCashAmount( request.getCashAmount() );
        historyExtractMasterModel.setFinalAmount( request.getFinalAmount() );
        historyExtractMasterModel.setPreinvoiceStatus( request.getPreinvoiceStatus() );
        historyExtractMasterModel.setLcStatus( request.getLcStatus() );
        historyExtractMasterModel.setRemittanceStatus( request.getRemittanceStatus() );
        historyExtractMasterModel.setMasterId( request.getMasterId() );
        historyExtractMasterModel.setRemittanceId( request.getRemittanceId() );
        historyExtractMasterModel.setPerformaNo( request.getPerformaNo() );
        historyExtractMasterModel.setPerformaDate( request.getPerformaDate() );
        historyExtractMasterModel.setLcNo( request.getLcNo() );
        historyExtractMasterModel.setLcDate( request.getLcDate() );
        historyExtractMasterModel.setRemittanceDate( request.getRemittanceDate() );
        historyExtractMasterModel.setRemittanceNo( request.getRemittanceNo() );
        historyExtractMasterModel.setRemittancePmsId( request.getRemittancePmsId() );
        historyExtractMasterModel.setLcPmsId( request.getLcPmsId() );
        historyExtractMasterModel.setAcknowledgment( request.getAcknowledgment() );

        return historyExtractMasterModel;
    }
}
