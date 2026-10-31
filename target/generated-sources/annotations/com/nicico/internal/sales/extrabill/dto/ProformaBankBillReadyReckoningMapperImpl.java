package com.nicico.internal.sales.extrabill.dto;

import com.nicico.internal.sales.extrabill.dto.ProformaBankBillReportDto.Create;
import com.nicico.internal.sales.extrabill.dto.ProformaBankBillReportDto.Info;
import com.nicico.internal.sales.extrabill.model.ExtraBillReadyReckoningModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class ProformaBankBillReadyReckoningMapperImpl implements ProformaBankBillReadyReckoningMapper {

    @Override
    public Info toDTO(ExtraBillReadyReckoningModel model) {
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
        info.setPerformaNo( model.getPerformaNo() );
        info.setPerformaDate( model.getPerformaDate() );

        return info;
    }

    @Override
    public ExtraBillReadyReckoningModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        ExtraBillReadyReckoningModel extraBillReadyReckoningModel = new ExtraBillReadyReckoningModel();

        extraBillReadyReckoningModel.setId( request.getId() );
        extraBillReadyReckoningModel.setIssuerBankName( request.getIssuerBankName() );
        extraBillReadyReckoningModel.setBranchCode( request.getBranchCode() );
        extraBillReadyReckoningModel.setBranchName( request.getBranchName() );
        extraBillReadyReckoningModel.setPaymentCity( request.getPaymentCity() );
        extraBillReadyReckoningModel.setAgentBankName( request.getAgentBankName() );
        extraBillReadyReckoningModel.setNosaCode( request.getNosaCode() );
        extraBillReadyReckoningModel.setSepamCode( request.getSepamCode() );
        extraBillReadyReckoningModel.setTreasuryId( request.getTreasuryId() );
        extraBillReadyReckoningModel.setIssueDate( request.getIssueDate() );
        extraBillReadyReckoningModel.setDueDate( request.getDueDate() );
        extraBillReadyReckoningModel.setContractNo( request.getContractNo() );
        extraBillReadyReckoningModel.setTradeId( request.getTradeId() );
        extraBillReadyReckoningModel.setProformaMasterId( request.getProformaMasterId() );
        extraBillReadyReckoningModel.setBillStatus( request.getBillStatus() );
        extraBillReadyReckoningModel.setProcessId( request.getProcessId() );
        extraBillReadyReckoningModel.setAcknowledgment( request.getAcknowledgment() );
        extraBillReadyReckoningModel.setReckoningSend( request.isReckoningSend() );
        extraBillReadyReckoningModel.setReckoningSendDate( request.getReckoningSendDate() );
        extraBillReadyReckoningModel.setPmsBillId( request.getPmsBillId() );
        extraBillReadyReckoningModel.setCancelDate( request.getCancelDate() );
        extraBillReadyReckoningModel.setCancellationReason( request.getCancellationReason() );
        extraBillReadyReckoningModel.setBuyerName( request.getBuyerName() );
        extraBillReadyReckoningModel.setBuyerNationalCode( request.getBuyerNationalCode() );
        extraBillReadyReckoningModel.setCommodityCode( request.getCommodityCode() );
        extraBillReadyReckoningModel.setPaymentCode( request.getPaymentCode() );
        extraBillReadyReckoningModel.setCustomerName( request.getCustomerName() );
        extraBillReadyReckoningModel.setCustomerNationalCode( request.getCustomerNationalCode() );
        extraBillReadyReckoningModel.setGoodName( request.getGoodName() );
        extraBillReadyReckoningModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        extraBillReadyReckoningModel.setTotalCashAmount( request.getTotalCashAmount() );
        extraBillReadyReckoningModel.setTotalCreditAmount( request.getTotalCreditAmount() );
        extraBillReadyReckoningModel.setProformaStatus( request.getProformaStatus() );
        extraBillReadyReckoningModel.setBrokerId( request.getBrokerId() );
        extraBillReadyReckoningModel.setBrokerName( request.getBrokerName() );
        extraBillReadyReckoningModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        extraBillReadyReckoningModel.setTotalQuantity( request.getTotalQuantity() );
        extraBillReadyReckoningModel.setOfferDescription( request.getOfferDescription() );
        extraBillReadyReckoningModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        extraBillReadyReckoningModel.setGoodId( request.getGoodId() );
        extraBillReadyReckoningModel.setContractDate( request.getContractDate() );
        extraBillReadyReckoningModel.setPerformaNo( request.getPerformaNo() );
        extraBillReadyReckoningModel.setPerformaDate( request.getPerformaDate() );

        return extraBillReadyReckoningModel;
    }
}
