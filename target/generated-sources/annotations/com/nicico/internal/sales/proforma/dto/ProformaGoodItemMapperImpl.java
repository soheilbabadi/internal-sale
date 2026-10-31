package com.nicico.internal.sales.proforma.dto;

import com.nicico.internal.sales.proforma.dto.ProformaGoodItemDto.Create;
import com.nicico.internal.sales.proforma.dto.ProformaGoodItemDto.Info;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel.ProformaGoodItemModelBuilder;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class ProformaGoodItemMapperImpl implements ProformaGoodItemMapper {

    @Override
    public Info toDTO(ProformaGoodItemModel request) {
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
        if ( request.getGoodId() != null ) {
            info.setGoodId( request.getGoodId() );
        }
        info.setGoodName( request.getGoodName() );
        if ( request.getUnitId() != null ) {
            info.setUnitId( request.getUnitId() );
        }
        info.setCashAmount( request.getCashAmount() );
        info.setTotalAmount( request.getTotalAmount() );
        info.setVatPercent( request.getVatPercent() );
        info.setVatAmount( request.getVatAmount() );
        info.setFinalAmount( request.getFinalAmount() );
        info.setLotNumber( request.getLotNumber() );
        info.setUnitPrice( request.getUnitPrice() );
        info.setQuantity( request.getQuantity() );
        info.setCreditAmount( request.getCreditAmount() );
        info.setVatCashAmount( request.getVatCashAmount() );
        info.setVatCreditAmount( request.getVatCreditAmount() );
        info.setUnitPriceCredit( request.getUnitPriceCredit() );
        info.setUnitPriceCash( request.getUnitPriceCash() );
        info.setInterestPercent( request.getInterestPercent() );
        info.setNetQuantity( request.getNetQuantity() );
        info.setCreditQuantity( request.getCreditQuantity() );
        info.setCreditPercentage( request.getCreditPercentage() );
        info.setProformaDetailId( request.getProformaDetailId() );

        return info;
    }

    @Override
    public ProformaGoodItemModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        ProformaGoodItemModelBuilder proformaGoodItemModel = ProformaGoodItemModel.builder();

        proformaGoodItemModel.id( request.getId() );
        proformaGoodItemModel.goodId( request.getGoodId() );
        proformaGoodItemModel.goodName( request.getGoodName() );
        proformaGoodItemModel.unitId( request.getUnitId() );
        proformaGoodItemModel.quantity( request.getQuantity() );
        proformaGoodItemModel.creditAmount( request.getCreditAmount() );
        proformaGoodItemModel.cashAmount( request.getCashAmount() );
        proformaGoodItemModel.totalAmount( request.getTotalAmount() );
        proformaGoodItemModel.vatPercent( request.getVatPercent() );
        proformaGoodItemModel.vatAmount( request.getVatAmount() );
        proformaGoodItemModel.vatCashAmount( request.getVatCashAmount() );
        proformaGoodItemModel.vatCreditAmount( request.getVatCreditAmount() );
        proformaGoodItemModel.unitPriceCredit( request.getUnitPriceCredit() );
        proformaGoodItemModel.unitPriceCash( request.getUnitPriceCash() );
        proformaGoodItemModel.interestPercent( request.getInterestPercent() );
        proformaGoodItemModel.netQuantity( request.getNetQuantity() );
        proformaGoodItemModel.finalAmount( request.getFinalAmount() );
        proformaGoodItemModel.unitPrice( request.getUnitPrice() );
        proformaGoodItemModel.creditQuantity( request.getCreditQuantity() );
        proformaGoodItemModel.lotNumber( request.getLotNumber() );
        proformaGoodItemModel.creditPercentage( request.getCreditPercentage() );
        proformaGoodItemModel.proformaDetailId( request.getProformaDetailId() );

        return proformaGoodItemModel.build();
    }
}
