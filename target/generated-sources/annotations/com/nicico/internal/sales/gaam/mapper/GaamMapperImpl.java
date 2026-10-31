package com.nicico.internal.sales.gaam.mapper;

import com.nicico.internal.sales.gaam.dto.GaamDto.Create;
import com.nicico.internal.sales.gaam.dto.GaamDto.Info;
import com.nicico.internal.sales.gaam.model.GaamModel;
import com.nicico.internal.sales.gaam.model.GaamModel.GaamModelBuilder;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class GaamMapperImpl implements GaamMapper {

    @Override
    public Info toDTO(GaamModel model) {
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
    public GaamModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        GaamModelBuilder gaamModel = GaamModel.builder();

        gaamModel.id( request.getId() );
        gaamModel.issuerBankName( request.getIssuerBankName() );
        gaamModel.issuerBankId( request.getIssuerBankId() );
        gaamModel.branchCode( request.getBranchCode() );
        gaamModel.branchName( request.getBranchName() );
        gaamModel.paymentCity( request.getPaymentCity() );
        gaamModel.agentBankName( request.getAgentBankName() );
        gaamModel.agentBankId( request.getAgentBankId() );
        gaamModel.extraBillFileId( request.getExtraBillFileId() );
        gaamModel.dispatchAttachmentId( request.getDispatchAttachmentId() );
        gaamModel.nosaCode( request.getNosaCode() );
        gaamModel.sepamCode( request.getSepamCode() );
        gaamModel.treasuryId( request.getTreasuryId() );
        gaamModel.issueDate( request.getIssueDate() );
        gaamModel.dueDate( request.getDueDate() );
        gaamModel.proformaDetailId( request.getProformaDetailId() );
        gaamModel.proformaMasterId( request.getProformaMasterId() );
        gaamModel.workflowApproveStatus( request.getWorkflowApproveStatus() );
        gaamModel.processId( request.getProcessId() );
        gaamModel.reversalProcessId( request.getReversalProcessId() );
        gaamModel.contractNo( request.getContractNo() );
        gaamModel.tradeId( request.getTradeId() );
        gaamModel.acknowledgment( request.getAcknowledgment() );
        gaamModel.reckoningSendDate( request.getReckoningSendDate() );
        gaamModel.pmsBillId( request.getPmsBillId() );
        gaamModel.cancelDate( request.getCancelDate() );
        gaamModel.cancellationReason( request.getCancellationReason() );

        return gaamModel.build();
    }
}
