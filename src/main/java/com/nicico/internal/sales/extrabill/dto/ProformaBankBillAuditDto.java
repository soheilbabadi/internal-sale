package com.nicico.internal.sales.extrabill.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProformaBankBillAuditDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rev;
    private Long id;
    private String dCreatedDate;
    private String dLastModifiedDate;
    private String cCreatedBy;
    private String cLastModifiedBy;
    private String cComment;
    private String cDescription;
    private Integer revtype;
    
    // Bank Fields
    private String nIssuerBankId;
    private String cIssuerBankName;
    private String cIssuerBankBranchName;
    private String cIssuerBankCode;
    private String nAgentBankId;
    private String cAgentBankName;
    
    // Electronic Bill Fields
    private String cNosaCode;
    private String cSepamCode;
    private String cTreasuryId;
    private String dIssueDate;
    private String dDueDate;
    
    // File & Process Fields
    private String cExtraBillFileId;
    private String cDispatchFileId;
    private String cWorkflowApproveStatus;
    private String cProcessId;
    private String cReversalProcessId;
    
    // Relation Fields
    private String nContractNo;
    private String fTradeId;
    private String nProformaMasterId;
    private String cProformaInstanceId;
    
    // Status & Dates
    private String cAcknowledgment;
    private String isReckoningSend;
    private String dReckoningSendDate;
    private String cCancellationReason;
    private String dCancelDate;
    private String cPmsBillId;
}
