package com.nicico.internal.sales.gaam.mapper;

import com.nicico.internal.sales.gaam.dto.GaamReportDto.Create;
import com.nicico.internal.sales.gaam.dto.GaamReportDto.Info;
import com.nicico.internal.sales.gaam.model.GaamlReadyReckoningModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class GaamRevokingMapperImpl implements GaamRevokingMapper {

    @Override
    public Info toDTO(GaamlReadyReckoningModel model) {
        if ( model == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( model.getId() );
        info.setIssuerBankName( model.getIssuerBankName() );
        info.setBranchCode( model.getBranchCode() );
        info.setBranchName( model.getBranchName() );
        info.setPaymentCity( model.getPaymentCity() );
        info.setAgentBankName( model.getAgentBankName() );
        info.setNosaCode( model.getNosaCode() );
        info.setSepamCode( model.getSepamCode() );
        info.setTreasuryId( model.getTreasuryId() );
        info.setIssueDate( model.getIssueDate() );
        info.setDueDate( model.getDueDate() );
        info.setContractNo( model.getContractNo() );
        info.setTradeId( model.getTradeId() );
        info.setProformaMasterId( model.getProformaMasterId() );
        info.setBillStatus( model.getBillStatus() );
        info.setProcessId( model.getProcessId() );
        info.setAcknowledgment( model.getAcknowledgment() );
        info.setReckoningSend( model.isReckoningSend() );
        info.setReckoningSendDate( model.getReckoningSendDate() );
        info.setPmsBillId( model.getPmsBillId() );
        info.setCancelDate( model.getCancelDate() );
        info.setCancellationReason( model.getCancellationReason() );
        info.setBuyerName( model.getBuyerName() );
        info.setBuyerNationalCode( model.getBuyerNationalCode() );
        info.setCommodityCode( model.getCommodityCode() );
        info.setPaymentCode( model.getPaymentCode() );
        info.setCustomerName( model.getCustomerName() );
        info.setCustomerNationalCode( model.getCustomerNationalCode() );
        info.setGoodName( model.getGoodName() );
        info.setTotalFinalAmount( model.getTotalFinalAmount() );
        info.setTotalCashAmount( model.getTotalCashAmount() );
        info.setTotalCreditAmount( model.getTotalCreditAmount() );
        info.setProformaStatus( model.getProformaStatus() );
        info.setBrokerId( model.getBrokerId() );
        info.setBrokerName( model.getBrokerName() );
        info.setBrokerNationalCode( model.getBrokerNationalCode() );
        info.setTotalQuantity( model.getTotalQuantity() );
        info.setOfferDescription( model.getOfferDescription() );
        info.setImeCommoditySymbol( model.getImeCommoditySymbol() );
        info.setGoodId( model.getGoodId() );
        info.setContractDate( model.getContractDate() );
        info.setTradeContractDate( model.getTradeContractDate() );
        info.setPerformaNo( model.getPerformaNo() );
        info.setPerformaDate( model.getPerformaDate() );

        return info;
    }

    @Override
    public GaamlReadyReckoningModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        GaamlReadyReckoningModel gaamlReadyReckoningModel = new GaamlReadyReckoningModel();

        gaamlReadyReckoningModel.setId( request.getId() );
        gaamlReadyReckoningModel.setIssuerBankName( request.getIssuerBankName() );
        gaamlReadyReckoningModel.setBranchCode( request.getBranchCode() );
        gaamlReadyReckoningModel.setBranchName( request.getBranchName() );
        gaamlReadyReckoningModel.setPaymentCity( request.getPaymentCity() );
        gaamlReadyReckoningModel.setAgentBankName( request.getAgentBankName() );
        gaamlReadyReckoningModel.setNosaCode( request.getNosaCode() );
        gaamlReadyReckoningModel.setSepamCode( request.getSepamCode() );
        gaamlReadyReckoningModel.setTreasuryId( request.getTreasuryId() );
        gaamlReadyReckoningModel.setIssueDate( request.getIssueDate() );
        gaamlReadyReckoningModel.setDueDate( request.getDueDate() );
        gaamlReadyReckoningModel.setContractNo( request.getContractNo() );
        gaamlReadyReckoningModel.setTradeId( request.getTradeId() );
        gaamlReadyReckoningModel.setProformaMasterId( request.getProformaMasterId() );
        gaamlReadyReckoningModel.setBillStatus( request.getBillStatus() );
        gaamlReadyReckoningModel.setProcessId( request.getProcessId() );
        gaamlReadyReckoningModel.setAcknowledgment( request.getAcknowledgment() );
        gaamlReadyReckoningModel.setReckoningSend( request.isReckoningSend() );
        gaamlReadyReckoningModel.setReckoningSendDate( request.getReckoningSendDate() );
        gaamlReadyReckoningModel.setPmsBillId( request.getPmsBillId() );
        gaamlReadyReckoningModel.setCancelDate( request.getCancelDate() );
        gaamlReadyReckoningModel.setCancellationReason( request.getCancellationReason() );
        gaamlReadyReckoningModel.setBuyerName( request.getBuyerName() );
        gaamlReadyReckoningModel.setBuyerNationalCode( request.getBuyerNationalCode() );
        gaamlReadyReckoningModel.setCommodityCode( request.getCommodityCode() );
        gaamlReadyReckoningModel.setPaymentCode( request.getPaymentCode() );
        gaamlReadyReckoningModel.setCustomerName( request.getCustomerName() );
        gaamlReadyReckoningModel.setCustomerNationalCode( request.getCustomerNationalCode() );
        gaamlReadyReckoningModel.setGoodName( request.getGoodName() );
        gaamlReadyReckoningModel.setTotalFinalAmount( request.getTotalFinalAmount() );
        gaamlReadyReckoningModel.setTotalCashAmount( request.getTotalCashAmount() );
        gaamlReadyReckoningModel.setTotalCreditAmount( request.getTotalCreditAmount() );
        gaamlReadyReckoningModel.setProformaStatus( request.getProformaStatus() );
        gaamlReadyReckoningModel.setBrokerId( request.getBrokerId() );
        gaamlReadyReckoningModel.setBrokerName( request.getBrokerName() );
        gaamlReadyReckoningModel.setBrokerNationalCode( request.getBrokerNationalCode() );
        gaamlReadyReckoningModel.setTotalQuantity( request.getTotalQuantity() );
        gaamlReadyReckoningModel.setOfferDescription( request.getOfferDescription() );
        gaamlReadyReckoningModel.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        gaamlReadyReckoningModel.setGoodId( request.getGoodId() );
        gaamlReadyReckoningModel.setContractDate( request.getContractDate() );
        gaamlReadyReckoningModel.setTradeContractDate( request.getTradeContractDate() );
        gaamlReadyReckoningModel.setPerformaNo( request.getPerformaNo() );
        gaamlReadyReckoningModel.setPerformaDate( request.getPerformaDate() );

        return gaamlReadyReckoningModel;
    }
}
