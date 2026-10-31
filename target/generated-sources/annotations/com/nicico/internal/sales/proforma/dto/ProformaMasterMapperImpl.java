package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.dto.ProformaMasterDTO.Create;
import com.nicico.internal.sales.proforma.dto.ProformaMasterDTO.Info;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel.ProformaMasterModelBuilder;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class ProformaMasterMapperImpl implements ProformaMasterMapper {

    @Override
    public Info toDTO(ProformaMasterModel request) {
        if ( request == null ) {
            return null;
        }

        Date createdDate = null;
        Date lastModifiedDate = null;
        String createdBy = null;
        String lastModifiedBy = null;
        String comment = null;

        createdDate = request.getCreatedDate();
        lastModifiedDate = request.getLastModifiedDate();
        createdBy = request.getCreatedBy();
        lastModifiedBy = request.getLastModifiedBy();
        comment = request.getComment();

        Info info = new Info( createdDate, lastModifiedDate, createdBy, lastModifiedBy, comment );

        info.setId( request.getId() );
        info.setProcessId( request.getProcessId() );
        info.setReversalProcessId( request.getReversalProcessId() );
        info.setContractNo( request.getContractNo() );
        info.setPaymentCode( request.getPaymentCode() );
        info.setCashPercentage( request.getCashPercentage() );
        if ( request.getCommissionPercentage() != null ) {
            info.setCommissionPercentage( BigDecimal.valueOf( request.getCommissionPercentage() ) );
        }
        info.setDeadlineDays( request.getDeadlineDays() );
        info.setCreditPercentage( request.getCreditPercentage() );
        info.setTotalVatAmount( request.getTotalVatAmount() );
        info.setTotalFinalAmount( request.getTotalFinalAmount() );
        info.setTotalCashAmount( request.getTotalCashAmount() );
        info.setTotalCreditAmount( request.getTotalCreditAmount() );
        info.setTotalQuantity( request.getTotalQuantity() );
        info.setCustomerId( request.getCustomerId() );
        info.setCustomerName( request.getCustomerName() );
        info.setNationalCode( request.getNationalCode() );
        info.setPhone( request.getPhone() );
        info.setEconomicCode( request.getEconomicCode() );
        info.setRegisterNumber( request.getRegisterNumber() );
        info.setPostCode( request.getPostCode() );
        info.setAddress( request.getAddress() );
        info.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        info.setProformaIssueType( request.getProformaIssueType() );
        info.setGoodId( request.getGoodId() );
        info.setGoodName( request.getGoodName() );
        info.setIsProcessFinal( request.getIsProcessFinal() );
        info.setIsReversalProcessFinal( request.getIsReversalProcessFinal() );
        info.setContractDate( request.getContractDate() );
        info.setBrokerId( request.getBrokerId() );
        info.setBrokerName( request.getBrokerName() );
        info.setBrokerNationalCode( request.getBrokerNationalCode() );
        info.setOfferDescription( request.getOfferDescription() );
        info.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        info.setSettlementType( request.getSettlementType() );
        info.setTradeId( request.getTradeId() );
        info.setDepositSlipWithTax( request.getDepositSlipWithTax() );
        info.setDepositSlipWithoutTax( request.getDepositSlipWithoutTax() );
        info.setDepositSlipType( request.getDepositSlipType() );

        return info;
    }

    @Override
    public ProformaMasterModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        ProformaMasterModelBuilder proformaMasterModel = ProformaMasterModel.builder();

        proformaMasterModel.id( request.getId() );
        proformaMasterModel.processId( request.getProcessId() );
        proformaMasterModel.reversalProcessId( request.getReversalProcessId() );
        proformaMasterModel.contractNo( request.getContractNo() );
        proformaMasterModel.paymentCode( request.getPaymentCode() );
        proformaMasterModel.cashPercentage( request.getCashPercentage() );
        if ( request.getCommissionPercentage() != null ) {
            proformaMasterModel.commissionPercentage( request.getCommissionPercentage().doubleValue() );
        }
        proformaMasterModel.deadlineDays( request.getDeadlineDays() );
        proformaMasterModel.creditPercentage( request.getCreditPercentage() );
        proformaMasterModel.totalVatAmount( request.getTotalVatAmount() );
        proformaMasterModel.totalFinalAmount( request.getTotalFinalAmount() );
        proformaMasterModel.totalCashAmount( request.getTotalCashAmount() );
        proformaMasterModel.totalCreditAmount( request.getTotalCreditAmount() );
        proformaMasterModel.totalQuantity( request.getTotalQuantity() );
        proformaMasterModel.customerId( request.getCustomerId() );
        proformaMasterModel.customerName( request.getCustomerName() );
        proformaMasterModel.nationalCode( request.getNationalCode() );
        proformaMasterModel.phone( request.getPhone() );
        proformaMasterModel.economicCode( request.getEconomicCode() );
        proformaMasterModel.registerNumber( request.getRegisterNumber() );
        proformaMasterModel.postCode( request.getPostCode() );
        proformaMasterModel.address( request.getAddress() );
        proformaMasterModel.workflowApproveStatus( request.getWorkflowApproveStatus() );
        proformaMasterModel.proformaIssueType( request.getProformaIssueType() );
        proformaMasterModel.goodId( request.getGoodId() );
        proformaMasterModel.goodName( request.getGoodName() );
        proformaMasterModel.isProcessFinal( request.getIsProcessFinal() );
        proformaMasterModel.isReversalProcessFinal( request.getIsReversalProcessFinal() );
        proformaMasterModel.contractDate( request.getContractDate() );
        proformaMasterModel.tradeId( request.getTradeId() );
        proformaMasterModel.brokerId( request.getBrokerId() );
        proformaMasterModel.brokerName( request.getBrokerName() );
        proformaMasterModel.brokerNationalCode( request.getBrokerNationalCode() );
        proformaMasterModel.offerDescription( request.getOfferDescription() );
        proformaMasterModel.imeCommoditySymbol( request.getImeCommoditySymbol() );
        proformaMasterModel.settlementType( request.getSettlementType() );
        proformaMasterModel.depositSlipWithTax( request.getDepositSlipWithTax() );
        proformaMasterModel.depositSlipWithoutTax( request.getDepositSlipWithoutTax() );
        proformaMasterModel.depositSlipType( request.getDepositSlipType() );

        return proformaMasterModel.build();
    }
}
