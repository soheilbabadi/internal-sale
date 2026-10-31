package com.nicico.internal.sales.pricing.dto;

import com.nicico.internal.sales.pricing.dto.PricingCommodityDto.Create;
import com.nicico.internal.sales.pricing.dto.PricingCommodityDto.Info;
import com.nicico.internal.sales.pricing.model.PricingCommodityModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class PricingCommodityMapperImpl implements PricingCommodityMapper {

    @Override
    public Info toDTO(PricingCommodityModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setDayOfWeek( request.getDayOfWeek() );
        info.setPersianShortDate( request.getPersianShortDate() );
        info.setShortDate( request.getShortDate() );
        info.setUsdPerMtCu( request.getUsdPerMtCu() );
        info.setUsdPerOunceGold( request.getUsdPerOunceGold() );
        info.setUsdPerOunceSilver( request.getUsdPerOunceSilver() );
        info.setSeleniumBid( request.getSeleniumBid() );
        info.setSeleniumAsk( request.getSeleniumAsk() );
        info.setMolybdenumLow( request.getMolybdenumLow() );
        info.setMolybdenumHigh( request.getMolybdenumHigh() );
        info.setPlatinumAm( request.getPlatinumAm() );
        info.setPlatinumPm( request.getPlatinumPm() );
        info.setPalladiumAm( request.getPalladiumAm() );
        info.setPalladiumPm( request.getPalladiumPm() );
        info.setMolybdenumAm( request.getMolybdenumAm() );
        info.setMolybdenumPm( request.getMolybdenumPm() );

        return info;
    }

    @Override
    public PricingCommodityModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        PricingCommodityModel pricingCommodityModel = new PricingCommodityModel();

        pricingCommodityModel.setId( request.getId() );
        pricingCommodityModel.setDayOfWeek( request.getDayOfWeek() );
        pricingCommodityModel.setPersianShortDate( request.getPersianShortDate() );
        pricingCommodityModel.setShortDate( request.getShortDate() );
        pricingCommodityModel.setUsdPerMtCu( request.getUsdPerMtCu() );
        pricingCommodityModel.setUsdPerOunceGold( request.getUsdPerOunceGold() );
        pricingCommodityModel.setUsdPerOunceSilver( request.getUsdPerOunceSilver() );
        pricingCommodityModel.setSeleniumBid( request.getSeleniumBid() );
        pricingCommodityModel.setSeleniumAsk( request.getSeleniumAsk() );
        pricingCommodityModel.setMolybdenumLow( request.getMolybdenumLow() );
        pricingCommodityModel.setMolybdenumHigh( request.getMolybdenumHigh() );
        pricingCommodityModel.setPlatinumAm( request.getPlatinumAm() );
        pricingCommodityModel.setPlatinumPm( request.getPlatinumPm() );
        pricingCommodityModel.setPalladiumAm( request.getPalladiumAm() );
        pricingCommodityModel.setPalladiumPm( request.getPalladiumPm() );
        pricingCommodityModel.setMolybdenumAm( request.getMolybdenumAm() );
        pricingCommodityModel.setMolybdenumPm( request.getMolybdenumPm() );

        return pricingCommodityModel;
    }
}
