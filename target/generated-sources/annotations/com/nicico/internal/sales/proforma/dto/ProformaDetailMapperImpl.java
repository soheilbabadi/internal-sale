package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.dto.ProformaDetailDto.Create;
import com.nicico.internal.sales.proforma.dto.ProformaDetailDto.Info;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel.ProformaDetailModelBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class ProformaDetailMapperImpl implements ProformaDetailMapper {

    @Override
    public Info toDTO(ProformaDetailModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setJalaaliYear( request.getJalaaliYear() );
        info.setFinalPrice( request.getFinalPrice() );
        info.setPerformaNo( request.getPerformaNo() );
        info.setPerformaDate( request.getPerformaDate() );
        info.setSaleType( request.getSaleType() );
        info.setSettlementType( request.getSettlementType() );
        info.setVatAmount( request.getVatAmount() );
        info.setTotalAmount( request.getTotalAmount() );
        info.setStorageDeadline( request.getStorageDeadline() );
        info.setStorageCost( request.getStorageCost() );
        info.setCreditExpirePeriod( request.getCreditExpirePeriod() );
        info.setShippingDeadline( request.getShippingDeadline() );
        info.setPaymentDeferral( request.getPaymentDeferral() );
        info.setDeadlineDays( request.getDeadlineDays() );
        info.setProformaIssueType( request.getProformaIssueType() );
        info.setProformaReversalStatus( request.getProformaReversalStatus() );
        info.setOrderDate( request.getOrderDate() );
        info.setContractDate( request.getContractDate() );
        info.setPmsId( request.getPmsId() );
        info.setGamCertificateCount( request.getGamCertificateCount() );
        info.setExtraBillOfExchangeAmount( request.getExtraBillOfExchangeAmount() );
        info.setExtraBillOfPercent( request.getExtraBillOfPercent() );
        info.setProformaFileId( request.getProformaFileId() );

        return info;
    }

    @Override
    public ProformaDetailModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        ProformaDetailModelBuilder proformaDetailModel = ProformaDetailModel.builder();

        proformaDetailModel.id( request.getId() );
        proformaDetailModel.jalaaliYear( request.getJalaaliYear() );
        proformaDetailModel.finalPrice( request.getFinalPrice() );
        proformaDetailModel.performaNo( request.getPerformaNo() );
        proformaDetailModel.performaDate( request.getPerformaDate() );
        proformaDetailModel.saleType( request.getSaleType() );
        proformaDetailModel.settlementType( request.getSettlementType() );
        proformaDetailModel.pmsId( request.getPmsId() );
        proformaDetailModel.vatAmount( request.getVatAmount() );
        proformaDetailModel.totalAmount( request.getTotalAmount() );
        proformaDetailModel.storageDeadline( request.getStorageDeadline() );
        proformaDetailModel.storageCost( request.getStorageCost() );
        proformaDetailModel.creditExpirePeriod( request.getCreditExpirePeriod() );
        proformaDetailModel.shippingDeadline( request.getShippingDeadline() );
        proformaDetailModel.paymentDeferral( request.getPaymentDeferral() );
        proformaDetailModel.deadlineDays( request.getDeadlineDays() );
        proformaDetailModel.proformaIssueType( request.getProformaIssueType() );
        proformaDetailModel.proformaReversalStatus( request.getProformaReversalStatus() );
        proformaDetailModel.orderDate( request.getOrderDate() );
        proformaDetailModel.contractDate( request.getContractDate() );
        proformaDetailModel.gamCertificateCount( request.getGamCertificateCount() );
        proformaDetailModel.extraBillOfExchangeAmount( request.getExtraBillOfExchangeAmount() );
        proformaDetailModel.extraBillOfPercent( request.getExtraBillOfPercent() );
        proformaDetailModel.proformaFileId( request.getProformaFileId() );

        return proformaDetailModel.build();
    }
}
