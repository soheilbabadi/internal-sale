package com.nicico.internal.sales.bill.dto;

import com.nicico.internal.sales.bill.dto.BillingTradeExtractDto.Create;
import com.nicico.internal.sales.bill.dto.BillingTradeExtractDto.Info;
import com.nicico.internal.sales.bill.model.BillingTradeExtractModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class BillingTradeExtractMapperImpl implements BillingTradeExtractMapper {

    @Override
    public Info toDTO(BillingTradeExtractModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setDescription( request.getDescription() );
        info.setRemittanceNo( request.getRemittanceNo() );
        info.setRemittanceDate( request.getRemittanceDate() );
        info.setValidityDate( request.getValidityDate() );
        info.setCustomerId( request.getCustomerId() );
        info.setCustomerName( request.getCustomerName() );
        info.setEconomicCode( request.getEconomicCode() );
        info.setNationalCode( request.getNationalCode() );
        info.setTargetAddress( request.getTargetAddress() );
        info.setLoadingPortId( request.getLoadingPortId() );
        info.setLoadingPort( request.getLoadingPort() );
        info.setGoodId( request.getGoodId() );
        info.setGoodName( request.getGoodName() );
        info.setPackingName( request.getPackingName() );
        info.setPackingId( request.getPackingId() );
        info.setLotNumber( request.getLotNumber() );
        info.setContractDate( request.getContractDate() );
        info.setContractNo( request.getContractNo() );
        info.setSellerBrokerName( request.getSellerBrokerName() );
        info.setBuyerBrokerName( request.getBuyerBrokerName() );
        info.setCashPercentage( request.getCashPercentage() );
        info.setCreditPercentage( request.getCreditPercentage() );
        info.setRemittanceQuantity( request.getRemittanceQuantity() );
        info.setRemittanceQuantityCash( request.getRemittanceQuantityCash() );
        info.setRemittanceQuantityCredit( request.getRemittanceQuantityCredit() );
        info.setRemittanceUnitPriceCash( request.getRemittanceUnitPriceCash() );
        info.setRemittanceUnitPriceCredit( request.getRemittanceUnitPriceCredit() );
        info.setLcExpiryDate( request.getLcExpiryDate() );
        info.setSettlementDueDate( request.getSettlementDueDate() );
        info.setIssuerBankId( request.getIssuerBankId() );
        info.setIssuerBankName( request.getIssuerBankName() );
        info.setIssuerBankBranchName( request.getIssuerBankBranchName() );
        info.setIssuerBankCode( request.getIssuerBankCode() );
        info.setLcNo( request.getLcNo() );
        info.setIssuePlaceId( request.getIssuePlaceId() );
        info.setIssuePlace( request.getIssuePlace() );
        info.setProformaNo( request.getProformaNo() );
        info.setProformaDate( request.getProformaDate() );
        info.setProformaIssueType( request.getProformaIssueType() );
        info.setIssueSourceType( request.getIssueSourceType() );
        info.setIssuerName( request.getIssuerName() );
        info.setPaymentCode( request.getPaymentCode() );
        info.setIsDelayPenalty( request.getIsDelayPenalty() );
        info.setTradingBankId( request.getTradingBankId() );
        info.setTradingBankTitle( request.getTradingBankTitle() );
        info.setTradingBranchTitle( request.getTradingBranchTitle() );
        info.setPmsId( request.getPmsId() );
        info.setTaxAmount( request.getTaxAmount() );
        info.setSettlementType( request.getSettlementType() );
        info.setSettlementTypeDesc( request.getSettlementTypeDesc() );
        info.setBrokerId( request.getBrokerId() );
        info.setBrokerName( request.getBrokerName() );
        info.setTotalQuantity( request.getTotalQuantity() );
        info.setTotalFinalAmount( request.getTotalFinalAmount() );
        info.setOfferDescription( request.getOfferDescription() );
        info.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        info.setJalaliYear( request.getJalaliYear() );
        info.setEmissionTax( request.getEmissionTax() );
        info.setStorageDeadline( request.getStorageDeadline() );
        info.setStorageCost( request.getStorageCost() );
        info.setCreditExpirePeriod( request.getCreditExpirePeriod() );
        info.setShippingDeadline( request.getShippingDeadline() );
        info.setPaymentDeferral( request.getPaymentDeferral() );
        info.setCustomerNosaCode( request.getCustomerNosaCode() );

        return info;
    }

    @Override
    public BillingTradeExtractModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        BillingTradeExtractModel billingTradeExtractModel = new BillingTradeExtractModel();

        billingTradeExtractModel.setId( request.getId() );
        billingTradeExtractModel.setDescription( request.getDescription() );
        billingTradeExtractModel.setRemittanceNo( request.getRemittanceNo() );
        billingTradeExtractModel.setRemittanceDate( request.getRemittanceDate() );
        billingTradeExtractModel.setValidityDate( request.getValidityDate() );
        billingTradeExtractModel.setCustomerId( request.getCustomerId() );
        billingTradeExtractModel.setCustomerName( request.getCustomerName() );
        billingTradeExtractModel.setEconomicCode( request.getEconomicCode() );
        billingTradeExtractModel.setNationalCode( request.getNationalCode() );
        billingTradeExtractModel.setTargetAddress( request.getTargetAddress() );
        billingTradeExtractModel.setLoadingPortId( request.getLoadingPortId() );
        billingTradeExtractModel.setLoadingPort( request.getLoadingPort() );
        billingTradeExtractModel.setGoodId( request.getGoodId() );
        billingTradeExtractModel.setGoodName( request.getGoodName() );
        billingTradeExtractModel.setPackingName( request.getPackingName() );
        billingTradeExtractModel.setPackingId( request.getPackingId() );
        billingTradeExtractModel.setLotNumber( request.getLotNumber() );
        billingTradeExtractModel.setContractDate( request.getContractDate() );
        billingTradeExtractModel.setContractNo( request.getContractNo() );
        billingTradeExtractModel.setSellerBrokerName( request.getSellerBrokerName() );
        billingTradeExtractModel.setBuyerBrokerName( request.getBuyerBrokerName() );
        billingTradeExtractModel.setCashPercentage( request.getCashPercentage() );
        billingTradeExtractModel.setCreditPercentage( request.getCreditPercentage() );
        billingTradeExtractModel.setRemittanceQuantity( request.getRemittanceQuantity() );
        billingTradeExtractModel.setRemittanceQuantityCash( request.getRemittanceQuantityCash() );
        billingTradeExtractModel.setRemittanceQuantityCredit( request.getRemittanceQuantityCredit() );
        billingTradeExtractModel.setRemittanceUnitPriceCash( request.getRemittanceUnitPriceCash() );
        billingTradeExtractModel.setRemittanceUnitPriceCredit( request.getRemittanceUnitPriceCredit() );
        billingTradeExtractModel.setLcExpiryDate( request.getLcExpiryDate() );
        billingTradeExtractModel.setSettlementDueDate( request.getSettlementDueDate() );
        billingTradeExtractModel.setIssuerBankId( request.getIssuerBankId() );
        billingTradeExtractModel.setIssuerBankName( request.getIssuerBankName() );
        billingTradeExtractModel.setIssuerBankBranchName( request.getIssuerBankBranchName() );
        billingTradeExtractModel.setIssuerBankCode( request.getIssuerBankCode() );
        billingTradeExtractModel.setLcNo( request.getLcNo() );
        billingTradeExtractModel.setIssuePlaceId( request.getIssuePlaceId() );
        billingTradeExtractModel.setIssuePlace( request.getIssuePlace() );
        billingTradeExtractModel.setProformaNo( request.getProformaNo() );
        billingTradeExtractModel.setProformaDate( request.getProformaDate() );
        billingTradeExtractModel.setProformaIssueType( request.getProformaIssueType() );
        billingTradeExtractModel.setIssueSourceType( request.getIssueSourceType() );
        billingTradeExtractModel.setIssuerName( request.getIssuerName() );
        billingTradeExtractModel.setPaymentCode( request.getPaymentCode() );
        billingTradeExtractModel.setIsDelayPenalty( request.getIsDelayPenalty() );
        billingTradeExtractModel.setTradingBankId( request.getTradingBankId() );
        billingTradeExtractModel.setTradingBankTitle( request.getTradingBankTitle() );
        billingTradeExtractModel.setTradingBranchTitle( request.getTradingBranchTitle() );
        billingTradeExtractModel.setPmsId( request.getPmsId() );
        billingTradeExtractModel.setTaxAmount( request.getTaxAmount() );
        billingTradeExtractModel.setSettlementType( request.getSettlementType() );
        billingTradeExtractModel.setSettlementTypeDesc( request.getSettlementTypeDesc() );
        billingTradeExtractModel.setBrokerId( request.getBrokerId() );
        billingTradeExtractModel.setBrokerName( request.getBrokerName() );
        billingTradeExtractModel.setTotalQuantity( request.getTotalQuantity() );
        billingTradeExtractModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        billingTradeExtractModel.setOfferDescription( request.getOfferDescription() );
        billingTradeExtractModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        billingTradeExtractModel.setJalaliYear( request.getJalaliYear() );
        billingTradeExtractModel.setEmissionTax( request.getEmissionTax() );
        billingTradeExtractModel.setStorageDeadline( request.getStorageDeadline() );
        billingTradeExtractModel.setStorageCost( request.getStorageCost() );
        billingTradeExtractModel.setCreditExpirePeriod( request.getCreditExpirePeriod() );
        billingTradeExtractModel.setShippingDeadline( request.getShippingDeadline() );
        billingTradeExtractModel.setPaymentDeferral( request.getPaymentDeferral() );
        billingTradeExtractModel.setCustomerNosaCode( request.getCustomerNosaCode() );

        return billingTradeExtractModel;
    }
}
