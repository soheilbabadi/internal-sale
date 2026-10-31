package com.nicico.internal.sales.remittance.mapper;

import com.nicico.internal.sales.remittance.dto.RemittanceGoodItemDto.Create;
import com.nicico.internal.sales.remittance.dto.RemittanceGoodItemDto.Info;
import com.nicico.internal.sales.remittance.model.RemittanceGoodItemModel;
import com.nicico.internal.sales.remittance.model.RemittanceGoodItemModel.RemittanceGoodItemModelBuilder;
import java.util.Date;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class RemittanceGoodItemMapperImpl implements RemittanceGoodItemMapper {

    @Override
    public Info toDTO(RemittanceGoodItemModel model) {
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

        info.setDescription( model.getDescription() );
        info.setVersion( model.getVersion() );
        info.setId( model.getId() );
        info.setGoodId( model.getGoodId() );
        info.setGoodName( model.getGoodName() );
        info.setUnitId( model.getUnitId() );
        info.setQuantity( model.getQuantity() );
        info.setCreditAmount( model.getCreditAmount() );
        info.setCashAmount( model.getCashAmount() );
        info.setTotalAmount( model.getTotalAmount() );
        info.setVatAmount( model.getVatAmount() );
        info.setUnitPriceCredit( model.getUnitPriceCredit() );
        info.setUnitPriceCash( model.getUnitPriceCash() );
        info.setNetQuantity( model.getNetQuantity() );
        info.setFinalAmount( model.getFinalAmount() );
        info.setUnitPrice( model.getUnitPrice() );
        info.setCreditQuantity( model.getCreditQuantity() );
        info.setLotNumber( model.getLotNumber() );
        info.setCreditPercentage( model.getCreditPercentage() );

        return info;
    }

    @Override
    public RemittanceGoodItemModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        RemittanceGoodItemModelBuilder remittanceGoodItemModel = RemittanceGoodItemModel.builder();

        remittanceGoodItemModel.id( request.getId() );
        remittanceGoodItemModel.goodId( request.getGoodId() );
        remittanceGoodItemModel.goodName( request.getGoodName() );
        remittanceGoodItemModel.unitId( request.getUnitId() );
        remittanceGoodItemModel.quantity( request.getQuantity() );
        remittanceGoodItemModel.creditAmount( request.getCreditAmount() );
        remittanceGoodItemModel.cashAmount( request.getCashAmount() );
        remittanceGoodItemModel.totalAmount( request.getTotalAmount() );
        remittanceGoodItemModel.vatAmount( request.getVatAmount() );
        remittanceGoodItemModel.unitPriceCredit( request.getUnitPriceCredit() );
        remittanceGoodItemModel.unitPriceCash( request.getUnitPriceCash() );
        remittanceGoodItemModel.netQuantity( request.getNetQuantity() );
        remittanceGoodItemModel.finalAmount( request.getFinalAmount() );
        remittanceGoodItemModel.unitPrice( request.getUnitPrice() );
        remittanceGoodItemModel.creditQuantity( request.getCreditQuantity() );
        remittanceGoodItemModel.lotNumber( request.getLotNumber() );
        remittanceGoodItemModel.creditPercentage( request.getCreditPercentage() );

        return remittanceGoodItemModel.build();
    }
}
