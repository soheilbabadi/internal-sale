package com.nicico.internal.sales.gaam.mapper;

import com.nicico.internal.sales.gaam.dto.GaamReportDto.Create;
import com.nicico.internal.sales.gaam.dto.GaamReportDto.Info;
import com.nicico.internal.sales.gaam.model.GaamReportModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:36+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class GaamReportMapperImpl implements GaamReportMapper {

    @Override
    public Info toDTO(GaamReportModel model) {
        if ( model == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( model.getId() );
        info.setIssuerBankName( model.getIssuerBankName() );
        info.setBranchCode( model.getBranchCode() );
        info.setBranchName( model.getBranchName() );
        info.setPaymentCity( model.getPaymentCity() );
        info.setAgentBankName( model.getAgentBankName() );
        info.setNosaCode( model.getNosaCode() );
        info.setSepamCode( model.getSepamCode() );
        info.setTreasuryId( model.getTreasuryId() );
        info.setIssueDate( model.getIssueDate() );
        info.setDueDate( model.getDueDate() );
        info.setContractNo( model.getContractNo() );
        info.setTradeId( model.getTradeId() );
        info.setProformaMasterId( model.getProformaMasterId() );
        info.setBillStatus( model.getBillStatus() );
        info.setProcessId( model.getProcessId() );
        info.setAcknowledgment( model.getAcknowledgment() );
        info.setReckoningSend( model.isReckoningSend() );
        info.setReckoningSendDate( model.getReckoningSendDate() );
        info.setPmsBillId( model.getPmsBillId() );
        info.setCancelDate( model.getCancelDate() );
        info.setCancellationReason( model.getCancellationReason() );
        info.setBuyerName( model.getBuyerName() );
        info.setBuyerNationalCode( model.getBuyerNationalCode() );
        info.setCommodityCode( model.getCommodityCode() );
        info.setPaymentCode( model.getPaymentCode() );
        info.setCustomerName( model.getCustomerName() );
        info.setCustomerNationalCode( model.getCustomerNationalCode() );
        info.setGoodName( model.getGoodName() );
        info.setTotalFinalAmount( model.getTotalFinalAmount() );
        info.setTotalCashAmount( model.getTotalCashAmount() );
        info.setTotalCreditAmount( model.getTotalCreditAmount() );
        info.setProformaStatus( model.getProformaStatus() );
        info.setBrokerId( model.getBrokerId() );
        info.setBrokerName( model.getBrokerName() );
        info.setBrokerNationalCode( model.getBrokerNationalCode() );
        info.setTotalQuantity( model.getTotalQuantity() );
        info.setOfferDescription( model.getOfferDescription() );
        info.setImeCommoditySymbol( model.getImeCommoditySymbol() );
        info.setGoodId( model.getGoodId() );
        info.setContractDate( model.getContractDate() );
        info.setTradeContractDate( model.getTradeContractDate() );
        info.setPerformaNo( model.getPerformaNo() );
        info.setPerformaDate( model.getPerformaDate() );
        info.setExtraBillFileId( model.getExtraBillFileId() );
        info.setDispatchAttachmentId( model.getDispatchAttachmentId() );

        return info;
    }

    @Override
    public GaamReportModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        GaamReportModel gaamReportModel = new GaamReportModel();

        gaamReportModel.setId( request.getId() );
        gaamReportModel.setIssuerBankName( request.getIssuerBankName() );
        gaamReportModel.setBranchCode( request.getBranchCode() );
        gaamReportModel.setBranchName( request.getBranchName() );
        gaamReportModel.setPaymentCity( request.getPaymentCity() );
        gaamReportModel.setAgentBankName( request.getAgentBankName() );
        gaamReportModel.setNosaCode( request.getNosaCode() );
        gaamReportModel.setSepamCode( request.getSepamCode() );
        gaamReportModel.setTreasuryId( request.getTreasuryId() );
        gaamReportModel.setIssueDate( request.getIssueDate() );
        gaamReportModel.setDueDate( request.getDueDate() );
        gaamReportModel.setContractNo( request.getContractNo() );
        gaamReportModel.setTradeId( request.getTradeId() );
        gaamReportModel.setProformaMasterId( request.getProformaMasterId() );
        gaamReportModel.setBillStatus( request.getBillStatus() );
        gaamReportModel.setProcessId( request.getProcessId() );
        gaamReportModel.setAcknowledgment( request.getAcknowledgment() );
        gaamReportModel.setReckoningSend( request.isReckoningSend() );
        gaamReportModel.setReckoningSendDate( request.getReckoningSendDate() );
        gaamReportModel.setPmsBillId( request.getPmsBillId() );
        gaamReportModel.setCancelDate( request.getCancelDate() );
        gaamReportModel.setCancellationReason( request.getCancellationReason() );
        gaamReportModel.setBuyerName( request.getBuyerName() );
        gaamReportModel.setBuyerNationalCode( request.getBuyerNationalCode() );
        gaamReportModel.setCommodityCode( request.getCommodityCode() );
        gaamReportModel.setTradeContractDate( request.getTradeContractDate() );
        gaamReportModel.setContractDate( request.getContractDate() );
        gaamReportModel.setPaymentCode( request.getPaymentCode() );
        gaamReportModel.setCustomerName( request.getCustomerName() );
        gaamReportModel.setCustomerNationalCode( request.getCustomerNationalCode() );
        gaamReportModel.setGoodName( request.getGoodName() );
        gaamReportModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        gaamReportModel.setTotalCashAmount( request.getTotalCashAmount() );
        gaamReportModel.setTotalCreditAmount( request.getTotalCreditAmount() );
        gaamReportModel.setProformaStatus( request.getProformaStatus() );
        gaamReportModel.setBrokerId( request.getBrokerId() );
        gaamReportModel.setBrokerName( request.getBrokerName() );
        gaamReportModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        gaamReportModel.setTotalQuantity( request.getTotalQuantity() );
        gaamReportModel.setOfferDescription( request.getOfferDescription() );
        gaamReportModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        gaamReportModel.setGoodId( request.getGoodId() );
        gaamReportModel.setPerformaNo( request.getPerformaNo() );
        gaamReportModel.setPerformaDate( request.getPerformaDate() );
        gaamReportModel.setExtraBillFileId( request.getExtraBillFileId() );
        gaamReportModel.setDispatchAttachmentId( request.getDispatchAttachmentId() );

        return gaamReportModel;
    }
}
