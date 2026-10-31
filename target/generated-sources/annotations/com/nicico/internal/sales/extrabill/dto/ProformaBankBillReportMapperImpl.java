package com.nicico.internal.sales.extrabill.dto;

import com.nicico.internal.sales.extrabill.dto.ProformaBankBillReportDto.Create;
import com.nicico.internal.sales.extrabill.dto.ProformaBankBillReportDto.Info;
import com.nicico.internal.sales.extrabill.model.ProformaBankBillReportModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class ProformaBankBillReportMapperImpl implements ProformaBankBillReportMapper {

    @Override
    public Info toDTO(ProformaBankBillReportModel model) {
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
    public ProformaBankBillReportModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        ProformaBankBillReportModel proformaBankBillReportModel = new ProformaBankBillReportModel();

        proformaBankBillReportModel.setId( request.getId() );
        proformaBankBillReportModel.setIssuerBankName( request.getIssuerBankName() );
        proformaBankBillReportModel.setBranchCode( request.getBranchCode() );
        proformaBankBillReportModel.setBranchName( request.getBranchName() );
        proformaBankBillReportModel.setPaymentCity( request.getPaymentCity() );
        proformaBankBillReportModel.setAgentBankName( request.getAgentBankName() );
        proformaBankBillReportModel.setNosaCode( request.getNosaCode() );
        proformaBankBillReportModel.setSepamCode( request.getSepamCode() );
        proformaBankBillReportModel.setTreasuryId( request.getTreasuryId() );
        proformaBankBillReportModel.setIssueDate( request.getIssueDate() );
        proformaBankBillReportModel.setDueDate( request.getDueDate() );
        proformaBankBillReportModel.setContractNo( request.getContractNo() );
        proformaBankBillReportModel.setTradeId( request.getTradeId() );
        proformaBankBillReportModel.setProformaMasterId( request.getProformaMasterId() );
        proformaBankBillReportModel.setBillStatus( request.getBillStatus() );
        proformaBankBillReportModel.setProcessId( request.getProcessId() );
        proformaBankBillReportModel.setAcknowledgment( request.getAcknowledgment() );
        proformaBankBillReportModel.setReckoningSend( request.isReckoningSend() );
        proformaBankBillReportModel.setReckoningSendDate( request.getReckoningSendDate() );
        proformaBankBillReportModel.setPmsBillId( request.getPmsBillId() );
        proformaBankBillReportModel.setCancelDate( request.getCancelDate() );
        proformaBankBillReportModel.setCancellationReason( request.getCancellationReason() );
        proformaBankBillReportModel.setBuyerName( request.getBuyerName() );
        proformaBankBillReportModel.setBuyerNationalCode( request.getBuyerNationalCode() );
        proformaBankBillReportModel.setCommodityCode( request.getCommodityCode() );
        proformaBankBillReportModel.setTradeContractDate( request.getTradeContractDate() );
        proformaBankBillReportModel.setContractDate( request.getContractDate() );
        proformaBankBillReportModel.setPaymentCode( request.getPaymentCode() );
        proformaBankBillReportModel.setCustomerName( request.getCustomerName() );
        proformaBankBillReportModel.setCustomerNationalCode( request.getCustomerNationalCode() );
        proformaBankBillReportModel.setGoodName( request.getGoodName() );
        proformaBankBillReportModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        proformaBankBillReportModel.setTotalCashAmount( request.getTotalCashAmount() );
        proformaBankBillReportModel.setTotalCreditAmount( request.getTotalCreditAmount() );
        proformaBankBillReportModel.setProformaStatus( request.getProformaStatus() );
        proformaBankBillReportModel.setBrokerId( request.getBrokerId() );
        proformaBankBillReportModel.setBrokerName( request.getBrokerName() );
        proformaBankBillReportModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        proformaBankBillReportModel.setTotalQuantity( request.getTotalQuantity() );
        proformaBankBillReportModel.setOfferDescription( request.getOfferDescription() );
        proformaBankBillReportModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        proformaBankBillReportModel.setGoodId( request.getGoodId() );
        proformaBankBillReportModel.setPerformaNo( request.getPerformaNo() );
        proformaBankBillReportModel.setPerformaDate( request.getPerformaDate() );
        proformaBankBillReportModel.setExtraBillFileId( request.getExtraBillFileId() );
        proformaBankBillReportModel.setDispatchAttachmentId( request.getDispatchAttachmentId() );

        return proformaBankBillReportModel;
    }
}
