package com.nicico.internal.sales.lc.dto;

import com.nicico.internal.sales.lc.dto.LcDto.Create;
import com.nicico.internal.sales.lc.dto.LcDto.Info;
import com.nicico.internal.sales.lc.model.LcModel;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class LcMapperImpl implements LcMapper {

    @Override
    public Info toDTO(LcModel model) {
        if ( model == null ) {
            return null;
        }

        Date createdDate = null;
        Date lastModifiedDate = null;
        String createdBy = null;
        String lastModifiedBy = null;
        String comment = null;

        createdDate = model.getCreatedDate();
        lastModifiedDate = model.getLastModifiedDate();
        createdBy = model.getCreatedBy();
        lastModifiedBy = model.getLastModifiedBy();
        comment = model.getComment();

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
    public LcModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        LcModel lcModel = new LcModel();

        lcModel.setId( request.getId() );
        lcModel.setPerformaNo( request.getPerformaNo() );
        lcModel.setPerformaDate( request.getPerformaDate() );
        lcModel.setContractDate( request.getContractDate() );
        lcModel.setContractNo( request.getContractNo() );
        lcModel.setPaymentCode( request.getPaymentCode() );
        lcModel.setLcNo( request.getLcNo() );
        lcModel.setLcDate( request.getLcDate() );
        lcModel.setTradingBankId( request.getTradingBankId() );
        lcModel.setTradingBankTitle( request.getTradingBankTitle() );
        lcModel.setTradingBankBranchTitle( request.getTradingBankBranchTitle() );
        lcModel.setCreditExpirePeriod( request.getCreditExpirePeriod() );
        lcModel.setPaymentDeferral( request.getPaymentDeferral() );
        lcModel.setDeadlineDays( request.getDeadlineDays() );
        lcModel.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        lcModel.setAcknowledgment( request.getAcknowledgment() );
        lcModel.setReckoningSend( request.isReckoningSend() );
        lcModel.setReckoningSendDate( request.getReckoningSendDate() );
        lcModel.setProcessId( request.getProcessId() );
        lcModel.setLcExpiryDate( request.getLcExpiryDate() );
        lcModel.setSettlementDueDate( request.getSettlementDueDate() );
        lcModel.setIssuerBankId( request.getIssuerBankId() );
        lcModel.setIssuerBankName( request.getIssuerBankName() );
        lcModel.setIssuerBankBranchName( request.getIssuerBankBranchName() );
        lcModel.setIssuerBankBranchCode( request.getIssuerBankBranchCode() );
        lcModel.setLcAttachmentId( request.getLcAttachmentId() );
        lcModel.setDispatchAttachmentId( request.getDispatchAttachmentId() );
        lcModel.setProformaFileId( request.getProformaFileId() );
        lcModel.setProformaMasterId( request.getProformaMasterId() );
        lcModel.setProformaDetailId( request.getProformaDetailId() );
        lcModel.setLcInstanceId( request.getLcInstanceId() );
        lcModel.setRequireDispatchFile( request.getRequireDispatchFile() );
        lcModel.setNotificationDocumentId( request.getNotificationDocumentId() );
        lcModel.setNosaCode( request.getNosaCode() );
        lcModel.setPmsLcId( request.getPmsLcId() );
        lcModel.setCancelDate( request.getCancelDate() );
        lcModel.setLcCancellationReason( request.getLcCancellationReason() );
        lcModel.setBrokerId( request.getBrokerId() );
        lcModel.setBrokerName( request.getBrokerName() );
        lcModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        lcModel.setTotalQuantity( request.getTotalQuantity() );
        lcModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        lcModel.setOfferDescription( request.getOfferDescription() );
        lcModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        lcModel.setCustomerId( request.getCustomerId() );
        lcModel.setCustomerName( request.getCustomerName() );
        lcModel.setGoodId( request.getGoodId() );
        lcModel.setGoodName( request.getGoodName() );

        return lcModel;
    }
}
