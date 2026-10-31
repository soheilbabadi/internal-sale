package com.nicico.internal.sales.lc.dto;

import com.nicico.internal.sales.lc.dto.LcDto.Create;
import com.nicico.internal.sales.lc.dto.LcDto.Info;
import com.nicico.internal.sales.lc.model.LcRevokingReadyModel;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class LcRevokingReadyMapperImpl implements LcRevokingReadyMapper {

    @Override
    public Info toDTO(LcRevokingReadyModel model) {
        if ( model == null ) {
            return null;
        }

        Date createdDate = null;
        Date lastModifiedDate = null;
        String createdBy = null;
        String lastModifiedBy = null;
        String comment = null;

        Info info = new Info( createdDate, lastModifiedDate, createdBy, lastModifiedBy, comment );

        if ( model.getId() != null ) {
            info.setId( model.getId() );
        }
        info.setPerformaNo( model.getPerformaNo() );
        info.setPerformaDate( model.getPerformaDate() );
        info.setContractNo( model.getContractNo() );
        info.setContractDate( model.getContractDate() );
        info.setLcNo( model.getLcNo() );
        info.setLcDate( model.getLcDate() );
        info.setTradingBankId( model.getTradingBankId() );
        info.setTradingBankTitle( model.getTradingBankTitle() );
        info.setTradingBankBranchTitle( model.getTradingBankBranchTitle() );
        info.setCreditExpirePeriod( model.getCreditExpirePeriod() );
        info.setPaymentDeferral( model.getPaymentDeferral() );
        info.setDeadlineDays( model.getDeadlineDays() );
        info.setWorkflowApproveStatus( model.getWorkflowApproveStatus() );
        info.setProcessId( model.getProcessId() );
        info.setLcExpiryDate( model.getLcExpiryDate() );
        info.setSettlementDueDate( model.getSettlementDueDate() );
        info.setIssuerBankId( model.getIssuerBankId() );
        info.setIssuerBankName( model.getIssuerBankName() );
        info.setIssuerBankBranchName( model.getIssuerBankBranchName() );
        info.setIssuerBankBranchCode( model.getIssuerBankBranchCode() );
        info.setProformaMasterId( model.getProformaMasterId() );
        info.setProformaDetailId( model.getProformaDetailId() );
        info.setLcInstanceId( model.getLcInstanceId() );
        info.setLcAttachmentId( model.getLcAttachmentId() );
        info.setDispatchAttachmentId( model.getDispatchAttachmentId() );
        info.setProformaFileId( model.getProformaFileId() );
        info.setRequireDispatchFile( model.getRequireDispatchFile() );
        info.setNotificationDocumentId( model.getNotificationDocumentId() );
        info.setNosaCode( model.getNosaCode() );
        info.setPaymentCode( model.getPaymentCode() );
        info.setAcknowledgment( model.getAcknowledgment() );
        info.setReckoningSend( model.isReckoningSend() );
        info.setReckoningSendDate( model.getReckoningSendDate() );
        info.setCancelDate( model.getCancelDate() );
        info.setLcCancellationReason( model.getLcCancellationReason() );
        info.setBrokerId( model.getBrokerId() );
        info.setBrokerName( model.getBrokerName() );
        info.setBrokerNationalCode( model.getBrokerNationalCode() );
        info.setTotalQuantity( model.getTotalQuantity() );
        info.setTotalFinalAmount( model.getTotalFinalAmount() );
        info.setOfferDescription( model.getOfferDescription() );
        info.setImeCommoditySymbol( model.getImeCommoditySymbol() );
        info.setPmsLcId( model.getPmsLcId() );
        info.setGoodId( model.getGoodId() );
        info.setGoodName( model.getGoodName() );
        info.setCustomerId( model.getCustomerId() );
        info.setCustomerName( model.getCustomerName() );

        return info;
    }

    @Override
    public LcRevokingReadyModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        LcRevokingReadyModel lcRevokingReadyModel = new LcRevokingReadyModel();

        lcRevokingReadyModel.setId( request.getId() );
        lcRevokingReadyModel.setPerformaNo( request.getPerformaNo() );
        lcRevokingReadyModel.setPerformaDate( request.getPerformaDate() );
        lcRevokingReadyModel.setContractDate( request.getContractDate() );
        lcRevokingReadyModel.setContractNo( request.getContractNo() );
        lcRevokingReadyModel.setPaymentCode( request.getPaymentCode() );
        lcRevokingReadyModel.setLcNo( request.getLcNo() );
        lcRevokingReadyModel.setLcDate( request.getLcDate() );
        lcRevokingReadyModel.setTradingBankId( request.getTradingBankId() );
        lcRevokingReadyModel.setTradingBankTitle( request.getTradingBankTitle() );
        lcRevokingReadyModel.setTradingBankBranchTitle( request.getTradingBankBranchTitle() );
        lcRevokingReadyModel.setCreditExpirePeriod( request.getCreditExpirePeriod() );
        lcRevokingReadyModel.setPaymentDeferral( request.getPaymentDeferral() );
        lcRevokingReadyModel.setDeadlineDays( request.getDeadlineDays() );
        lcRevokingReadyModel.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        lcRevokingReadyModel.setAcknowledgment( request.getAcknowledgment() );
        lcRevokingReadyModel.setReckoningSend( request.isReckoningSend() );
        lcRevokingReadyModel.setReckoningSendDate( request.getReckoningSendDate() );
        lcRevokingReadyModel.setProcessId( request.getProcessId() );
        lcRevokingReadyModel.setLcExpiryDate( request.getLcExpiryDate() );
        lcRevokingReadyModel.setSettlementDueDate( request.getSettlementDueDate() );
        lcRevokingReadyModel.setIssuerBankId( request.getIssuerBankId() );
        lcRevokingReadyModel.setIssuerBankName( request.getIssuerBankName() );
        lcRevokingReadyModel.setIssuerBankBranchName( request.getIssuerBankBranchName() );
        lcRevokingReadyModel.setIssuerBankBranchCode( request.getIssuerBankBranchCode() );
        lcRevokingReadyModel.setLcAttachmentId( request.getLcAttachmentId() );
        lcRevokingReadyModel.setDispatchAttachmentId( request.getDispatchAttachmentId() );
        lcRevokingReadyModel.setProformaFileId( request.getProformaFileId() );
        lcRevokingReadyModel.setProformaMasterId( request.getProformaMasterId() );
        lcRevokingReadyModel.setProformaDetailId( request.getProformaDetailId() );
        lcRevokingReadyModel.setLcInstanceId( request.getLcInstanceId() );
        lcRevokingReadyModel.setRequireDispatchFile( request.getRequireDispatchFile() );
        lcRevokingReadyModel.setNotificationDocumentId( request.getNotificationDocumentId() );
        lcRevokingReadyModel.setNosaCode( request.getNosaCode() );
        lcRevokingReadyModel.setPmsLcId( request.getPmsLcId() );
        lcRevokingReadyModel.setCancelDate( request.getCancelDate() );
        lcRevokingReadyModel.setLcCancellationReason( request.getLcCancellationReason() );
        lcRevokingReadyModel.setBrokerId( request.getBrokerId() );
        lcRevokingReadyModel.setBrokerName( request.getBrokerName() );
        lcRevokingReadyModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        lcRevokingReadyModel.setTotalQuantity( request.getTotalQuantity() );
        lcRevokingReadyModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        lcRevokingReadyModel.setOfferDescription( request.getOfferDescription() );
        lcRevokingReadyModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        lcRevokingReadyModel.setCustomerId( request.getCustomerId() );
        lcRevokingReadyModel.setCustomerName( request.getCustomerName() );
        lcRevokingReadyModel.setGoodId( request.getGoodId() );
        lcRevokingReadyModel.setGoodName( request.getGoodName() );

        return lcRevokingReadyModel;
    }
}
