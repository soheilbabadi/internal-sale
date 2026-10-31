package com.nicico.internal.sales.remittance.mapper;

import com.nicico.internal.sales.remittance.dto.RemittanceProformaDataProviderDto.Create;
import com.nicico.internal.sales.remittance.dto.RemittanceProformaDataProviderDto.Info;
import com.nicico.internal.sales.remittance.model.RemittanceProformaDataProviderModel;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class RemittanceProformaDataProviderMapperImpl implements RemittanceProformaDataProviderMapper {

    @Override
    public Info toDTO(RemittanceProformaDataProviderModel model) {
        if ( model == null ) {
            return null;
        }

        Date createdDate = null;
        Date lastModifiedDate = null;
        String createdBy = null;
        String lastModifiedBy = null;
        String comment = null;

        Info info = new Info( createdDate, lastModifiedDate, createdBy, lastModifiedBy, comment );

        info.setId( model.getId() );
        info.setPaymentCode( model.getPaymentCode() );
        info.setCustomerId( model.getCustomerId() );
        info.setCustomerName( model.getCustomerName() );
        info.setEconomicCode( model.getEconomicCode() );
        info.setNationalCode( model.getNationalCode() );
        info.setGoodId( model.getGoodId() );
        info.setGoodName( model.getGoodName() );
        info.setContractDate( model.getContractDate() );
        info.setContractNo( model.getContractNo() );
        info.setBuyerBrokerId( model.getBuyerBrokerId() );
        info.setBuyerBrokerName( model.getBuyerBrokerName() );
        info.setSellerBrokerId( model.getSellerBrokerId() );
        info.setSellerBrokerName( model.getSellerBrokerName() );
        info.setUnitCount( model.getUnitCount() );
        info.setUnitPrice( model.getUnitPrice() );
        info.setCashPercentage( model.getCashPercentage() );
        info.setCreditPercentage( model.getCreditPercentage() );
        info.setCreditUnitPrice( model.getCreditUnitPrice() );
        info.setCreditAmount( model.getCreditAmount() );
        info.setCashAmount( model.getCashAmount() );
        info.setFinalAmount( model.getFinalAmount() );
        info.setSettlementType( model.getSettlementType() );
        info.setSettlementTypeDesc( model.getSettlementTypeDesc() );
        info.setProformaMasterId( model.getProformaMasterId() );
        info.setProformaIssueType( model.getProformaIssueType() );
        info.setProformaNo( model.getProformaNo() );
        info.setProformaDate( model.getProformaDate() );
        info.setContractTypeCode( model.getContractTypeCode() );
        info.setContractTypeDescription( model.getContractTypeDescription() );
        info.setSettlementDate( model.getSettlementDate() );
        info.setDeliveryDate( model.getDeliveryDate() );
        info.setLcExpiryDate( model.getLcExpiryDate() );
        info.setSettlementDueDate( model.getSettlementDueDate() );
        info.setIssuerBankId( model.getIssuerBankId() );
        info.setIssuerBankName( model.getIssuerBankName() );
        info.setIssuerBankBranchName( model.getIssuerBankBranchName() );
        info.setIssuerBankBranchCode( model.getIssuerBankBranchCode() );
        info.setTradingBankName( model.getTradingBankName() );
        info.setTradingBankBranchName( model.getTradingBankBranchName() );
        info.setProformaDetailId( model.getProformaDetailId() );
        info.setLcId( model.getLcId() );
        info.setLcNo( model.getLcNo() );
        info.setLcDate( model.getLcDate() );
        info.setDelayPenalty( model.isDelayPenalty() );
        info.setWorkflowApproveStatus( model.getWorkflowApproveStatus() );
        info.setTotalQuantity( model.getTotalQuantity() );
        info.setOfferDescription( model.getOfferDescription() );
        info.setLotNumber( model.getLotNumber() );

        return info;
    }

    @Override
    public RemittanceProformaDataProviderModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        RemittanceProformaDataProviderModel remittanceProformaDataProviderModel = new RemittanceProformaDataProviderModel();

        remittanceProformaDataProviderModel.setId( request.getId() );
        remittanceProformaDataProviderModel.setPaymentCode( request.getPaymentCode() );
        remittanceProformaDataProviderModel.setCustomerId( request.getCustomerId() );
        remittanceProformaDataProviderModel.setCustomerName( request.getCustomerName() );
        remittanceProformaDataProviderModel.setEconomicCode( request.getEconomicCode() );
        remittanceProformaDataProviderModel.setNationalCode( request.getNationalCode() );
        remittanceProformaDataProviderModel.setGoodId( request.getGoodId() );
        remittanceProformaDataProviderModel.setGoodName( request.getGoodName() );
        remittanceProformaDataProviderModel.setContractDate( request.getContractDate() );
        remittanceProformaDataProviderModel.setContractNo( request.getContractNo() );
        remittanceProformaDataProviderModel.setBuyerBrokerId( request.getBuyerBrokerId() );
        remittanceProformaDataProviderModel.setBuyerBrokerName( request.getBuyerBrokerName() );
        remittanceProformaDataProviderModel.setSellerBrokerId( request.getSellerBrokerId() );
        remittanceProformaDataProviderModel.setSellerBrokerName( request.getSellerBrokerName() );
        remittanceProformaDataProviderModel.setUnitCount( request.getUnitCount() );
        remittanceProformaDataProviderModel.setUnitPrice( request.getUnitPrice() );
        remittanceProformaDataProviderModel.setCashPercentage( request.getCashPercentage() );
        remittanceProformaDataProviderModel.setCreditPercentage( request.getCreditPercentage() );
        remittanceProformaDataProviderModel.setCreditUnitPrice( request.getCreditUnitPrice() );
        remittanceProformaDataProviderModel.setCreditAmount( request.getCreditAmount() );
        remittanceProformaDataProviderModel.setCashAmount( request.getCashAmount() );
        remittanceProformaDataProviderModel.setFinalAmount( request.getFinalAmount() );
        remittanceProformaDataProviderModel.setSettlementType( request.getSettlementType() );
        remittanceProformaDataProviderModel.setSettlementTypeDesc( request.getSettlementTypeDesc() );
        remittanceProformaDataProviderModel.setProformaMasterId( request.getProformaMasterId() );
        remittanceProformaDataProviderModel.setProformaIssueType( request.getProformaIssueType() );
        remittanceProformaDataProviderModel.setProformaNo( request.getProformaNo() );
        remittanceProformaDataProviderModel.setProformaDate( request.getProformaDate() );
        remittanceProformaDataProviderModel.setContractTypeCode( request.getContractTypeCode() );
        remittanceProformaDataProviderModel.setContractTypeDescription( request.getContractTypeDescription() );
        remittanceProformaDataProviderModel.setSettlementDate( request.getSettlementDate() );
        remittanceProformaDataProviderModel.setDeliveryDate( request.getDeliveryDate() );
        remittanceProformaDataProviderModel.setLcExpiryDate( request.getLcExpiryDate() );
        remittanceProformaDataProviderModel.setSettlementDueDate( request.getSettlementDueDate() );
        remittanceProformaDataProviderModel.setIssuerBankId( request.getIssuerBankId() );
        remittanceProformaDataProviderModel.setIssuerBankName( request.getIssuerBankName() );
        remittanceProformaDataProviderModel.setIssuerBankBranchName( request.getIssuerBankBranchName() );
        remittanceProformaDataProviderModel.setIssuerBankBranchCode( request.getIssuerBankBranchCode() );
        remittanceProformaDataProviderModel.setTradingBankName( request.getTradingBankName() );
        remittanceProformaDataProviderModel.setTradingBankBranchName( request.getTradingBankBranchName() );
        remittanceProformaDataProviderModel.setProformaDetailId( request.getProformaDetailId() );
        remittanceProformaDataProviderModel.setLcId( request.getLcId() );
        remittanceProformaDataProviderModel.setLcNo( request.getLcNo() );
        remittanceProformaDataProviderModel.setLcDate( request.getLcDate() );
        remittanceProformaDataProviderModel.setDelayPenalty( request.isDelayPenalty() );
        remittanceProformaDataProviderModel.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        remittanceProformaDataProviderModel.setTotalQuantity( request.getTotalQuantity() );
        remittanceProformaDataProviderModel.setOfferDescription( request.getOfferDescription() );
        remittanceProformaDataProviderModel.setLotNumber( request.getLotNumber() );

        return remittanceProformaDataProviderModel;
    }
}
