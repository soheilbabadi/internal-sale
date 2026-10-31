package com.nicico.internal.sales.pricing.dto;

import com.nicico.internal.sales.pricing.dto.PricingCurrencyDto.Create;
import com.nicico.internal.sales.pricing.dto.PricingCurrencyDto.Info;
import com.nicico.internal.sales.pricing.model.PricingCurrencyModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class PricingCurrencyMapperImpl implements PricingCurrencyMapper {

    @Override
    public Info toDTO(PricingCurrencyModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setPersianShortDate( request.getPersianShortDate() );
        info.setShortDate( request.getShortDate() );
        info.setDayOfWeek( request.getDayOfWeek() );
        info.setUsdIrrBuy( request.getUsdIrrBuy() );
        info.setUsdIrrSell( request.getUsdIrrSell() );
        info.setEurIrrBuy( request.getEurIrrBuy() );
        info.setEurIrrSell( request.getEurIrrSell() );

        return info;
    }

    @Override
    public PricingCurrencyModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        PricingCurrencyModel pricingCurrencyModel = new PricingCurrencyModel();

        pricingCurrencyModel.setId( request.getId() );
        pricingCurrencyModel.setPersianShortDate( request.getPersianShortDate() );
        pricingCurrencyModel.setShortDate( request.getShortDate() );
        pricingCurrencyModel.setDayOfWeek( request.getDayOfWeek() );
        pricingCurrencyModel.setUsdIrrBuy( request.getUsdIrrBuy() );
        pricingCurrencyModel.setUsdIrrSell( request.getUsdIrrSell() );
        pricingCurrencyModel.setEurIrrBuy( request.getEurIrrBuy() );
        pricingCurrencyModel.setEurIrrSell( request.getEurIrrSell() );

        return pricingCurrencyModel;
    }
}
