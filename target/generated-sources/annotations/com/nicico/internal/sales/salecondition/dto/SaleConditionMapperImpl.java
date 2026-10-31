package com.nicico.internal.sales.salecondition.dto;

import com.nicico.internal.sales.salecondition.dto.SaleConditionDto.Create;
import com.nicico.internal.sales.salecondition.dto.SaleConditionDto.Info;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel;
import com.nicico.internal.sales.salecondition.model.SaleConditionModel.SaleConditionModelBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class SaleConditionMapperImpl implements SaleConditionMapper {

    @Override
    public Info toDTO(SaleConditionModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setStartDate( request.getStartDate() );
        info.setExpireDate( request.getExpireDate() );
        info.setStorageDeadline( request.getStorageDeadline() );
        info.setStorageCost( request.getStorageCost() );
        info.setCreditExpirePeriod( request.getCreditExpirePeriod() );
        info.setShippingDeadline( request.getShippingDeadline() );
        info.setPaymentDeferral( request.getPaymentDeferral() );
        info.setGoodId( request.getGoodId() );
        info.setGoodName( request.getGoodName() );
        info.setImeCommodityId( request.getImeCommodityId() );
        info.setImeCommoditySymbol( request.getImeCommoditySymbol() );
        info.setExtraBillOfExchangePercent( request.getExtraBillOfExchangePercent() );
        info.setExtraGamCertificatePercent( request.getExtraGamCertificatePercent() );

        return info;
    }

    @Override
    public SaleConditionModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        SaleConditionModelBuilder saleConditionModel = SaleConditionModel.builder();

        saleConditionModel.id( request.getId() );
        saleConditionModel.startDate( request.getStartDate() );
        saleConditionModel.expireDate( request.getExpireDate() );
        saleConditionModel.storageDeadline( request.getStorageDeadline() );
        saleConditionModel.storageCost( request.getStorageCost() );
        saleConditionModel.creditExpirePeriod( request.getCreditExpirePeriod() );
        saleConditionModel.shippingDeadline( request.getShippingDeadline() );
        saleConditionModel.paymentDeferral( request.getPaymentDeferral() );
        saleConditionModel.goodId( request.getGoodId() );
        saleConditionModel.goodName( request.getGoodName() );
        saleConditionModel.imeCommodityId( request.getImeCommodityId() );
        saleConditionModel.imeCommoditySymbol( request.getImeCommoditySymbol() );
        saleConditionModel.extraBillOfExchangePercent( request.getExtraBillOfExchangePercent() );
        saleConditionModel.extraGamCertificatePercent( request.getExtraGamCertificatePercent() );

        return saleConditionModel.build();
    }
}
