package com.nicico.internal.sales.crm.dto;

import com.nicico.internal.sales.crm.dto.LcWithProformaDto.Create;
import com.nicico.internal.sales.crm.dto.LcWithProformaDto.Info;
import com.nicico.internal.sales.crm.model.LcWithProformaView;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class LcViewMapperImpl implements LcViewMapper {

    @Override
    public Info toDTO(LcWithProformaView request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setLcId( request.getLcId() );
        info.setPerformaNo( request.getPerformaNo() );
        info.setPerformaDate( request.getPerformaDate() );
        info.setContractNo( request.getContractNo() );
        info.setLcNo( request.getLcNo() );
        info.setLcDate( request.getLcDate() );
        info.setTradingBankId( request.getTradingBankId() );
        info.setTradingBankTitle( request.getTradingBankTitle() );
        info.setTradingBankBranchTitle( request.getTradingBankBranchTitle() );
        info.setCreditExpirePeriod( request.getCreditExpirePeriod() );
        info.setPaymentDeferral( request.getPaymentDeferral() );
        info.setDeadlineDays( request.getDeadlineDays() );
        info.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        info.setLcExpiryDate( request.getLcExpiryDate() );
        info.setSettlementDueDate( request.getSettlementDueDate() );
        info.setIssuerBankId( request.getIssuerBankId() );
        info.setIssuerBankName( request.getIssuerBankName() );
        info.setIssuerBankBranchName( request.getIssuerBankBranchName() );
        info.setIssuerBankCode( request.getIssuerBankCode() );
        info.setLcAttachmentId( request.getLcAttachmentId() );
        info.setDispatchAttachmentId( request.getDispatchAttachmentId() );
        info.setProformaFileId( request.getProformaFileId() );
        info.setProformaMasterId( request.getProformaMasterId() );
        info.setProformaDetailId( request.getProformaDetailId() );
        info.setLcInstanceId( request.getLcInstanceId() );
        info.setNotificationDocumentId( request.getNotificationDocumentId() );
        info.setAcknowledgment( request.getAcknowledgment() );
        info.setIsReckoningSend( request.getIsReckoningSend() );
        info.setReckoningSendDate( request.getReckoningSendDate() );
        info.setCancelDate( request.getCancelDate() );
        info.setLcCancellationReason( request.getLcCancellationReason() );
        info.setContractDate( request.getContractDate() );
        info.setBrokerId( request.getBrokerId() );
        info.setBrokerName( request.getBrokerName() );
        info.setTotalQuantity( request.getTotalQuantity() );
        info.setTotalFinalAmount( request.getTotalFinalAmount() );
        info.setOfferDescription( request.getOfferDescription() );
        info.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        info.setGoodId( request.getGoodId() );
        info.setGoodName( request.getGoodName() );
        info.setCashPercentage( request.getCashPercentage() );
        info.setCreditPercentage( request.getCreditPercentage() );
        info.setTotalVatAmount( request.getTotalVatAmount() );
        info.setTotalCashAmount( request.getTotalCashAmount() );
        info.setTotalCreditAmount( request.getTotalCreditAmount() );
        info.setCustomerId( request.getCustomerId() );
        info.setCustomerName( request.getCustomerName() );
        info.setNationalCode( request.getNationalCode() );
        info.setPhone( request.getPhone() );
        info.setEconomicCode( request.getEconomicCode() );
        info.setRegisterNumber( request.getRegisterNumber() );
        info.setPostCode( request.getPostCode() );
        info.setAddress( request.getAddress() );
        info.setTradeId( request.getTradeId() );
        info.setPerformaDetailId( request.getPerformaDetailId() );
        info.setJalaaliYear( request.getJalaaliYear() );
        info.setFinalPrice( request.getFinalPrice() );
        info.setPerformaDetailDate( request.getPerformaDetailDate() );
        info.setSaleType( request.getSaleType() );
        info.setTotalAmount( request.getTotalAmount() );
        info.setStorageDeadline( request.getStorageDeadline() );
        info.setOrderDate( request.getOrderDate() );
        info.setStorageCost( request.getStorageCost() );

        return info;
    }

    @Override
    public LcWithProformaView fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        LcWithProformaView lcWithProformaView = new LcWithProformaView();

        lcWithProformaView.setLcId( request.getLcId() );
        lcWithProformaView.setPerformaNo( request.getPerformaNo() );
        lcWithProformaView.setPerformaDate( request.getPerformaDate() );
        lcWithProformaView.setContractNo( request.getContractNo() );
        lcWithProformaView.setLcNo( request.getLcNo() );
        lcWithProformaView.setLcDate( request.getLcDate() );
        lcWithProformaView.setTradingBankId( request.getTradingBankId() );
        lcWithProformaView.setTradingBankTitle( request.getTradingBankTitle() );
        lcWithProformaView.setTradingBankBranchTitle( request.getTradingBankBranchTitle() );
        lcWithProformaView.setCreditExpirePeriod( request.getCreditExpirePeriod() );
        lcWithProformaView.setPaymentDeferral( request.getPaymentDeferral() );
        lcWithProformaView.setDeadlineDays( request.getDeadlineDays() );
        lcWithProformaView.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        lcWithProformaView.setLcExpiryDate( request.getLcExpiryDate() );
        lcWithProformaView.setSettlementDueDate( request.getSettlementDueDate() );
        lcWithProformaView.setIssuerBankId( request.getIssuerBankId() );
        lcWithProformaView.setIssuerBankName( request.getIssuerBankName() );
        lcWithProformaView.setIssuerBankBranchName( request.getIssuerBankBranchName() );
        lcWithProformaView.setIssuerBankCode( request.getIssuerBankCode() );
        lcWithProformaView.setLcAttachmentId( request.getLcAttachmentId() );
        lcWithProformaView.setDispatchAttachmentId( request.getDispatchAttachmentId() );
        lcWithProformaView.setProformaFileId( request.getProformaFileId() );
        lcWithProformaView.setProformaMasterId( request.getProformaMasterId() );
        lcWithProformaView.setProformaDetailId( request.getProformaDetailId() );
        lcWithProformaView.setLcInstanceId( request.getLcInstanceId() );
        lcWithProformaView.setNotificationDocumentId( request.getNotificationDocumentId() );
        lcWithProformaView.setAcknowledgment( request.getAcknowledgment() );
        lcWithProformaView.setIsReckoningSend( request.getIsReckoningSend() );
        lcWithProformaView.setReckoningSendDate( request.getReckoningSendDate() );
        lcWithProformaView.setCancelDate( request.getCancelDate() );
        lcWithProformaView.setLcCancellationReason( request.getLcCancellationReason() );
        lcWithProformaView.setContractDate( request.getContractDate() );
        lcWithProformaView.setBrokerId( request.getBrokerId() );
        lcWithProformaView.setBrokerName( request.getBrokerName() );
        lcWithProformaView.setTotalQuantity( request.getTotalQuantity() );
        lcWithProformaView.setTotalFinalAmount( request.getTotalFinalAmount() );
        lcWithProformaView.setOfferDescription( request.getOfferDescription() );
        lcWithProformaView.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        lcWithProformaView.setGoodId( request.getGoodId() );
        lcWithProformaView.setGoodName( request.getGoodName() );
        lcWithProformaView.setCashPercentage( request.getCashPercentage() );
        lcWithProformaView.setCreditPercentage( request.getCreditPercentage() );
        lcWithProformaView.setTotalVatAmount( request.getTotalVatAmount() );
        lcWithProformaView.setTotalCashAmount( request.getTotalCashAmount() );
        lcWithProformaView.setTotalCreditAmount( request.getTotalCreditAmount() );
        lcWithProformaView.setCustomerId( request.getCustomerId() );
        lcWithProformaView.setCustomerName( request.getCustomerName() );
        lcWithProformaView.setNationalCode( request.getNationalCode() );
        lcWithProformaView.setPhone( request.getPhone() );
        lcWithProformaView.setEconomicCode( request.getEconomicCode() );
        lcWithProformaView.setRegisterNumber( request.getRegisterNumber() );
        lcWithProformaView.setPostCode( request.getPostCode() );
        lcWithProformaView.setAddress( request.getAddress() );
        lcWithProformaView.setTradeId( request.getTradeId() );
        lcWithProformaView.setPerformaDetailId( request.getPerformaDetailId() );
        lcWithProformaView.setJalaaliYear( request.getJalaaliYear() );
        lcWithProformaView.setFinalPrice( request.getFinalPrice() );
        lcWithProformaView.setPerformaDetailDate( request.getPerformaDetailDate() );
        lcWithProformaView.setSaleType( request.getSaleType() );
        lcWithProformaView.setTotalAmount( request.getTotalAmount() );
        lcWithProformaView.setStorageDeadline( request.getStorageDeadline() );
        lcWithProformaView.setOrderDate( request.getOrderDate() );
        lcWithProformaView.setStorageCost( request.getStorageCost() );

        return lcWithProformaView;
    }
}
