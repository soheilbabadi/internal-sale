package com.nicico.internal.sales.remittance.dto;

import com.nicico.internal.sales.remittance.dto.RemittanceMasterDto.Create;
import com.nicico.internal.sales.remittance.dto.RemittanceMasterDto.Info;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class RemittanceMasterMapperImpl implements RemittanceMasterMapper {

    @Override
    public Info toDTO(RemittanceMasterModel model) {
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
        info.setRemittanceNumber( model.getRemittanceNumber() );
        info.setRemittanceDate( model.getRemittanceDate() );
        info.setValidityDate( model.getValidityDate() );
        info.setCustomerId( model.getCustomerId() );
        info.setCustomerName( model.getCustomerName() );
        info.setEconomicCode( model.getEconomicCode() );
        info.setNationalCode( model.getNationalCode() );
        info.setTargetAddress( model.getTargetAddress() );
        info.setLoadingPortId( model.getLoadingPortId() );
        info.setLoadingPort( model.getLoadingPort() );
        info.setGoodId( model.getGoodId() );
        info.setGoodName( model.getGoodName() );
        info.setPackingName( model.getPackingName() );
        info.setPackingId( model.getPackingId() );
        info.setLotNumber( model.getLotNumber() );
        info.setContractDate( model.getContractDate() );
        info.setContractDateJalali( model.getContractDateJalali() );
        info.setContractNo( model.getContractNo() );
        info.setSellerBrokerName( model.getSellerBrokerName() );
        info.setBuyerBrokerName( model.getBuyerBrokerName() );
        info.setCashPercentage( model.getCashPercentage() );
        info.setCreditPercentage( model.getCreditPercentage() );
        info.setRemittanceQuantity( model.getRemittanceQuantity() );
        info.setRemittanceQuantityCash( model.getRemittanceQuantityCash() );
        info.setRemittanceQuantityCredit( model.getRemittanceQuantityCredit() );
        info.setRemittanceUnitPriceCash( model.getRemittanceUnitPriceCash() );
        info.setRemittanceUnitPriceCredit( model.getRemittanceUnitPriceCredit() );
        info.setTaxAmount( model.getTaxAmount() );
        info.setLcExpiryDate( model.getLcExpiryDate() );
        info.setSettlementDueDate( model.getSettlementDueDate() );
        info.setTradingBankId( model.getTradingBankId() );
        info.setTradingBankTitle( model.getTradingBankTitle() );
        info.setTradingBankBranchTitle( model.getTradingBankBranchTitle() );
        info.setIssuerBankId( model.getIssuerBankId() );
        info.setIssuerBankName( model.getIssuerBankName() );
        info.setIssuerBankBranchName( model.getIssuerBankBranchName() );
        info.setIssuerBankBranchCode( model.getIssuerBankBranchCode() );
        info.setRemittanceFileId( model.getRemittanceFileId() );
        info.setProcessId( model.getProcessId() );
        info.setWorkflowApproveStatus( model.getWorkflowApproveStatus() );
        info.setProformaMasterId( model.getProformaMasterId() );
        info.setProformaDetailId( model.getProformaDetailId() );
        info.setLcId( model.getLcId() );
        info.setLcNo( model.getLcNo() );
        info.setIssuePlaceId( model.getIssuePlaceId() );
        info.setIssuePlace( model.getIssuePlace() );
        info.setProformaNo( model.getProformaNo() );
        info.setProformaDate( model.getProformaDate() );
        info.setProformaIssueType( model.getProformaIssueType() );
        info.setIssueSourceType( model.getIssueSourceType() );
        info.setProcessFinal( model.isProcessFinal() );
        info.setPaymentCode( model.getPaymentCode() );
        info.setTradeId( model.getTradeId() );
        info.setDelayPenalty( model.isDelayPenalty() );
        info.setSettlementType( model.getSettlementType() );
        info.setSettlementTypeDesc( model.getSettlementTypeDesc() );
        info.setPmsId( model.getPmsId() );
        info.setBrokerId( model.getBrokerId() );
        info.setBrokerName( model.getBrokerName() );
        info.setBrokerNationalCode( model.getBrokerNationalCode() );
        info.setTotalQuantity( model.getTotalQuantity() );
        info.setTotalFinalAmount( model.getTotalFinalAmount() );
        info.setOfferDescription( model.getOfferDescription() );
        info.setImeCommoditySymbol( model.getImeCommoditySymbol() );

        return info;
    }

    @Override
    public RemittanceMasterModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        RemittanceMasterModel remittanceMasterModel = new RemittanceMasterModel();

        remittanceMasterModel.setId( request.getId() );
        remittanceMasterModel.setRemittanceNumber( request.getRemittanceNumber() );
        remittanceMasterModel.setRemittanceDate( request.getRemittanceDate() );
        remittanceMasterModel.setValidityDate( request.getValidityDate() );
        if ( request.getCustomerId() != null ) {
            remittanceMasterModel.setCustomerId( request.getCustomerId() );
        }
        remittanceMasterModel.setCustomerName( request.getCustomerName() );
        remittanceMasterModel.setEconomicCode( request.getEconomicCode() );
        remittanceMasterModel.setNationalCode( request.getNationalCode() );
        remittanceMasterModel.setTargetAddress( request.getTargetAddress() );
        remittanceMasterModel.setLoadingPortId( request.getLoadingPortId() );
        remittanceMasterModel.setLoadingPort( request.getLoadingPort() );
        remittanceMasterModel.setGoodId( request.getGoodId() );
        remittanceMasterModel.setGoodName( request.getGoodName() );
        remittanceMasterModel.setPackingName( request.getPackingName() );
        remittanceMasterModel.setPackingId( request.getPackingId() );
        remittanceMasterModel.setLotNumber( request.getLotNumber() );
        remittanceMasterModel.setContractDate( request.getContractDate() );
        remittanceMasterModel.setContractDateJalali( request.getContractDateJalali() );
        remittanceMasterModel.setContractNo( request.getContractNo() );
        remittanceMasterModel.setSellerBrokerName( request.getSellerBrokerName() );
        remittanceMasterModel.setBuyerBrokerName( request.getBuyerBrokerName() );
        remittanceMasterModel.setCashPercentage( request.getCashPercentage() );
        remittanceMasterModel.setCreditPercentage( request.getCreditPercentage() );
        remittanceMasterModel.setRemittanceQuantity( request.getRemittanceQuantity() );
        remittanceMasterModel.setRemittanceQuantityCash( request.getRemittanceQuantityCash() );
        remittanceMasterModel.setRemittanceQuantityCredit( request.getRemittanceQuantityCredit() );
        remittanceMasterModel.setRemittanceUnitPriceCash( request.getRemittanceUnitPriceCash() );
        remittanceMasterModel.setRemittanceUnitPriceCredit( request.getRemittanceUnitPriceCredit() );
        remittanceMasterModel.setTaxAmount( request.getTaxAmount() );
        remittanceMasterModel.setRemittanceFileId( request.getRemittanceFileId() );
        remittanceMasterModel.setProcessId( request.getProcessId() );
        remittanceMasterModel.setWorkflowApproveStatus( request.getWorkflowApproveStatus() );
        remittanceMasterModel.setProformaMasterId( request.getProformaMasterId() );
        remittanceMasterModel.setProformaDetailId( request.getProformaDetailId() );
        remittanceMasterModel.setIssuePlaceId( request.getIssuePlaceId() );
        remittanceMasterModel.setIssuePlace( request.getIssuePlace() );
        remittanceMasterModel.setIssueSourceType( request.getIssueSourceType() );
        remittanceMasterModel.setProcessFinal( request.isProcessFinal() );
        remittanceMasterModel.setPaymentCode( request.getPaymentCode() );
        remittanceMasterModel.setTradeId( request.getTradeId() );
        remittanceMasterModel.setDelayPenalty( request.isDelayPenalty() );
        remittanceMasterModel.setPmsId( request.getPmsId() );
        remittanceMasterModel.setSettlementType( request.getSettlementType() );
        remittanceMasterModel.setSettlementTypeDesc( request.getSettlementTypeDesc() );
        remittanceMasterModel.setBrokerId( request.getBrokerId() );
        remittanceMasterModel.setBrokerName( request.getBrokerName() );
        remittanceMasterModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        remittanceMasterModel.setTotalQuantity( request.getTotalQuantity() );
        remittanceMasterModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        remittanceMasterModel.setOfferDescription( request.getOfferDescription() );
        remittanceMasterModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        remittanceMasterModel.setProformaNo( request.getProformaNo() );
        remittanceMasterModel.setProformaDate( request.getProformaDate() );
        remittanceMasterModel.setProformaIssueType( request.getProformaIssueType() );
        remittanceMasterModel.setLcExpiryDate( request.getLcExpiryDate() );
        remittanceMasterModel.setSettlementDueDate( request.getSettlementDueDate() );
        remittanceMasterModel.setTradingBankId( request.getTradingBankId() );
        remittanceMasterModel.setTradingBankTitle( request.getTradingBankTitle() );
        remittanceMasterModel.setTradingBankBranchTitle( request.getTradingBankBranchTitle() );
        remittanceMasterModel.setIssuerBankId( request.getIssuerBankId() );
        remittanceMasterModel.setIssuerBankName( request.getIssuerBankName() );
        remittanceMasterModel.setIssuerBankBranchName( request.getIssuerBankBranchName() );
        remittanceMasterModel.setIssuerBankBranchCode( request.getIssuerBankBranchCode() );
        remittanceMasterModel.setLcId( request.getLcId() );
        remittanceMasterModel.setLcNo( request.getLcNo() );

        return remittanceMasterModel;
    }
}
