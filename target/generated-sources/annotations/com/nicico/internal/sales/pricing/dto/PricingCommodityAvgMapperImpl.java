package com.nicico.internal.sales.pricing.dto;

import com.nicico.internal.sales.pricing.dto.PricingCommodityWithAvgDto.Create;
import com.nicico.internal.sales.pricing.dto.PricingCommodityWithAvgDto.Info;
import com.nicico.internal.sales.pricing.model.PricingCommodityWithAvgModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class PricingCommodityAvgMapperImpl implements PricingCommodityAvgMapper {

    @Override
    public Info toDTO(PricingCommodityWithAvgModel request) {
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
        info.setSeleniumAvg( request.getSeleniumAvg() );
        info.setMolybdenumAvg( request.getMolybdenumAvg() );
        info.setMolybdenumAmPmAvg( request.getMolybdenumAmPmAvg() );
        info.setPlatinumAvg( request.getPlatinumAvg() );
        info.setPalladiumAvg( request.getPalladiumAvg() );
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
    public PricingCommodityWithAvgModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        PricingCommodityWithAvgModel pricingCommodityWithAvgModel = new PricingCommodityWithAvgModel();

        pricingCommodityWithAvgModel.setId( request.getId() );
        pricingCommodityWithAvgModel.setDayOfWeek( request.getDayOfWeek() );
        pricingCommodityWithAvgModel.setPersianShortDate( request.getPersianShortDate() );
        pricingCommodityWithAvgModel.setShortDate( request.getShortDate() );
        pricingCommodityWithAvgModel.setUsdPerMtCu( request.getUsdPerMtCu() );
        pricingCommodityWithAvgModel.setUsdPerOunceGold( request.getUsdPerOunceGold() );
        pricingCommodityWithAvgModel.setUsdPerOunceSilver( request.getUsdPerOunceSilver() );
        pricingCommodityWithAvgModel.setSeleniumAvg( request.getSeleniumAvg() );
        pricingCommodityWithAvgModel.setMolybdenumAvg( request.getMolybdenumAvg() );
        pricingCommodityWithAvgModel.setMolybdenumAmPmAvg( request.getMolybdenumAmPmAvg() );
        pricingCommodityWithAvgModel.setPlatinumAvg( request.getPlatinumAvg() );
        pricingCommodityWithAvgModel.setPalladiumAvg( request.getPalladiumAvg() );
        pricingCommodityWithAvgModel.setSeleniumBid( request.getSeleniumBid() );
        pricingCommodityWithAvgModel.setSeleniumAsk( request.getSeleniumAsk() );
        pricingCommodityWithAvgModel.setMolybdenumLow( request.getMolybdenumLow() );
        pricingCommodityWithAvgModel.setMolybdenumHigh( request.getMolybdenumHigh() );
        pricingCommodityWithAvgModel.setPlatinumAm( request.getPlatinumAm() );
        pricingCommodityWithAvgModel.setPlatinumPm( request.getPlatinumPm() );
        pricingCommodityWithAvgModel.setPalladiumAm( request.getPalladiumAm() );
        pricingCommodityWithAvgModel.setPalladiumPm( request.getPalladiumPm() );
        pricingCommodityWithAvgModel.setMolybdenumAm( request.getMolybdenumAm() );
        pricingCommodityWithAvgModel.setMolybdenumPm( request.getMolybdenumPm() );

        return pricingCommodityWithAvgModel;
    }
}
