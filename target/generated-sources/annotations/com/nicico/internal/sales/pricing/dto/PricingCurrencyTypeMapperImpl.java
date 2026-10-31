package com.nicico.internal.sales.pricing.dto;

import com.nicico.internal.sales.pricing.dto.PricingCurrencyTypeDto.Create;
import com.nicico.internal.sales.pricing.dto.PricingCurrencyTypeDto.Info;
import com.nicico.internal.sales.pricing.model.PricingCurrencyTypeModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class PricingCurrencyTypeMapperImpl implements PricingCurrencyTypeMapper {

    @Override
    public Info toDTO(PricingCurrencyTypeModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setRateType( request.getRateType() );

        return info;
    }

    @Override
    public PricingCurrencyTypeModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        PricingCurrencyTypeModel pricingCurrencyTypeModel = new PricingCurrencyTypeModel();

        pricingCurrencyTypeModel.setId( request.getId() );
        pricingCurrencyTypeModel.setRateType( request.getRateType() );

        return pricingCurrencyTypeModel;
    }
}
