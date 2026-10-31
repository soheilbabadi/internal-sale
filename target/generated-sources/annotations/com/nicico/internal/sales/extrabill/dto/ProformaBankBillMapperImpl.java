package com.nicico.internal.sales.extrabill.dto;

import com.nicico.internal.sales.extrabill.dto.ProformaBankBillDto.Create;
import com.nicico.internal.sales.extrabill.dto.ProformaBankBillDto.Info;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel.ExtraBankBillModelBuilder;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class ProformaBankBillMapperImpl implements ProformaBankBillMapper {

    @Override
    public Info toDTO(ExtraBankBillModel model) {
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

        info.setId( model.getId() );
        info.setProformaDetailId( model.getProformaDetailId() );
        info.setProformaMasterId( model.getProformaMasterId() );
        info.setTradeId( model.getTradeId() );
        info.setContractNo( model.getContractNo() );
        info.setIssuerBankId( model.getIssuerBankId() );
        info.setIssuerBankName( model.getIssuerBankName() );
        info.setBranchCode( model.getBranchCode() );
        info.setBranchName( model.getBranchName() );
        info.setPaymentCity( model.getPaymentCity() );
        info.setAgentBankName( model.getAgentBankName() );
        info.setAgentBankId( model.getAgentBankId() );
        info.setNosaCode( model.getNosaCode() );
        info.setSepamCode( model.getSepamCode() );
        info.setTreasuryId( model.getTreasuryId() );
        info.setIssueDate( model.getIssueDate() );
        info.setDueDate( model.getDueDate() );
        info.setExtraBillFileId( model.getExtraBillFileId() );
        info.setDispatchAttachmentId( model.getDispatchAttachmentId() );
        info.setWorkflowApproveStatus( model.getWorkflowApproveStatus() );
        info.setProcessId( model.getProcessId() );
        info.setReversalProcessId( model.getReversalProcessId() );
        info.setPmsBillId( model.getPmsBillId() );
        info.setAcknowledgment( model.getAcknowledgment() );
        info.setReckoningSend( model.isReckoningSend() );
        info.setReckoningSendDate( model.getReckoningSendDate() );
        info.setCancelDate( model.getCancelDate() );
        info.setCancellationReason( model.getCancellationReason() );

        return info;
    }

    @Override
    public ExtraBankBillModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        ExtraBankBillModelBuilder extraBankBillModel = ExtraBankBillModel.builder();

        extraBankBillModel.id( request.getId() );
        extraBankBillModel.issuerBankName( request.getIssuerBankName() );
        extraBankBillModel.issuerBankId( request.getIssuerBankId() );
        extraBankBillModel.branchCode( request.getBranchCode() );
        extraBankBillModel.branchName( request.getBranchName() );
        extraBankBillModel.paymentCity( request.getPaymentCity() );
        extraBankBillModel.agentBankName( request.getAgentBankName() );
        extraBankBillModel.agentBankId( request.getAgentBankId() );
        extraBankBillModel.extraBillFileId( request.getExtraBillFileId() );
        extraBankBillModel.dispatchAttachmentId( request.getDispatchAttachmentId() );
        extraBankBillModel.nosaCode( request.getNosaCode() );
        extraBankBillModel.sepamCode( request.getSepamCode() );
        extraBankBillModel.treasuryId( request.getTreasuryId() );
        extraBankBillModel.issueDate( request.getIssueDate() );
        extraBankBillModel.dueDate( request.getDueDate() );
        extraBankBillModel.proformaDetailId( request.getProformaDetailId() );
        extraBankBillModel.proformaMasterId( request.getProformaMasterId() );
        extraBankBillModel.workflowApproveStatus( request.getWorkflowApproveStatus() );
        extraBankBillModel.processId( request.getProcessId() );
        extraBankBillModel.reversalProcessId( request.getReversalProcessId() );
        extraBankBillModel.contractNo( request.getContractNo() );
        extraBankBillModel.tradeId( request.getTradeId() );
        extraBankBillModel.acknowledgment( request.getAcknowledgment() );
        extraBankBillModel.reckoningSendDate( request.getReckoningSendDate() );
        extraBankBillModel.pmsBillId( request.getPmsBillId() );
        extraBankBillModel.cancelDate( request.getCancelDate() );
        extraBankBillModel.cancellationReason( request.getCancellationReason() );

        return extraBankBillModel.build();
    }
}
