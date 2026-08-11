//package com.nicico.internal.sales.pricing.service;
//
//
//import com.nicico.internal.sales.pricing.model.AnodeSlimeModel;
//import com.nicico.internal.sales.pricing.model.CommodityModel;
//import com.nicico.internal.sales.pricing.model.CurrencyModel;
//import com.nicico.internal.sales.pricing.model.PricingFormulaModel;
//import com.nicico.internal.sales.pricing.repository.PricingFormulaRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.Map;
//import java.util.Optional;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class PricingCalculationServiceImpl implements PricingCalculationService {
//
//    private final SpelPricingEngine pricingEngine;
//    private final PricingFormulaRepository formulaRepository;
//    private final CommodityService commodityService;
//    private final CurrencyService currencyService;
//
//
//    @Transactional
//    @Override
//    public BigDecimal calculateAnodeSlimePrice(AnodeSlimeModel slime,
//                                               boolean withSelenium) {
//
//        CommodityModel commodity = commodityService.getLatestCommodity();
//        CurrencyModel currency = currencyService.getLatestCurrencyRate();
//
//        PricingContext context = new PricingContext()
//                .setUsdToIrr(currency.getusdIrrSell())
//                .setGoldPrice(commodity.getUsdPerOunceGold())
//                .setSilverPrice(commodity.getUsdPerOunceSilver())
//                .setLmeCopperPrice(commodity.getUsdPerMtCu())
//                .setSeleniumPrice(commodity.getSeleniumAsk())
//                .setPlatinumPrice(commodity.getPlatinumAm())
//                .setPalladiumPrice(commodity.getPalladiumAm())
//                .setAuDecimal(slime.getAu())
//                .setAgDecimal(slime.getAg())
//                .setCuDecimal(slime.getCu())
//                .setSeDecimal(slime.getSe())
//                .setPtDecimal(slime.getPt())
//                .setPdDecimal(slime.getPd());
//
//        String formulaKey = withSelenium ? "ANODE_SLIME_WITH_SE" : "ANODE_SLIME_WITHOUT_SE";
//
//        Optional<PricingFormulaModel> customFormula =
//                formulaRepository.findByCodeAndActiveTrue(formulaKey);
//
//        if (customFormula.isPresent()) {
//            return pricingEngine.calculate(customFormula.get().getSpelExpression(), context, true);
//        } else {
//            return pricingEngine.calculate(formulaKey, context);
//        }
//    }
//
//    @Override
//    public BigDecimal calculateWithCustomFormula(String formula,
//                                                 Map<String, Object> parameters) {
//
//        PricingContext context = new PricingContext();
//
//        parameters.forEach((key, value) -> {
//            switch (key) {
//                case "usdToIrr" -> context.setUsdToIrr((BigDecimal) value);
//                case "cuDecimal" -> context.setCuDecimal((BigDecimal) value);
//            }
//        });
//
//        return pricingEngine.calculate(formula, context, false);
//    }
//
//}
