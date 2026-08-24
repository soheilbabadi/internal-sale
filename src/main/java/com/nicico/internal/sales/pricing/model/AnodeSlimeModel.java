//package com.nicico.internal.sales.pricing.model;
//
//import com.nicico.internal.sales.config.BaseClassModel;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.*;
//import lombok.experimental.Accessors;
//import org.hibernate.envers.Audited;
//
//import javax.persistence.*;
//import java.math.BigDecimal;
//import java.util.Date;
//
//@Schema(description = "کنسانتره فلزات گرانبها")
//@Audited
//@EqualsAndHashCode(callSuper = true)
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
//@Accessors(chain = true)
//@Table(name = "T_INS_PRICE_ANODE_SLIME")
//public class AnodeSlimeModel extends BaseClassModel {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRICE_FORMULA")
//    @SequenceGenerator(name = "SEQ_PRICE_FORMULA", sequenceName = "SEQ_PRICE_FORMULA")
//    private Long id;
//
//
//    @Schema(description = "تاریخ عرضه")
//    @Column(name = "D_OFFER_DATE")
//    private Date offerDate;
//
//    @Schema(name = "commodityCode", description = "کد کالا", example = "123456")
//    @Column(name = "N_COMMODITY_CODE")
//    private Long commodityCode;
//
//
//    @Schema(name = "commoditySymbol", description = "نماد کالا", example = "ANODE_SLIME")
//    @Column(name = "C_COMMODITY_SYMBOL")
//    private String commoditySymbol;
//
//    @Schema(description = "نام کالا")
//    @Column(name = "C_COMMODITY_NAME")
//    private String commodityName;
//
//    @Schema(description = "محل بارگیری")
//    @Column(name = "N_LOADING_PLACE_ID")
//    private Long loadingPlaceId;
//
//    @Schema(description = "محل بارگیری")
//    @Column(name = "C_LOADING_PLACE")
//    private String loadingPlace;
//
//    @Schema(description = "شماره اندیکاتور کنترل کیفی")
//    @Column(name = "C_QC_INDICATOR")
//    private String qcIndicator;
//
//    @Schema(description = "شماره بشکه")
//    @Column(name = "C_BARREL_NO")
//    private String barrelNo;
//
//    @Schema(description = "درصد نقره (Ag)")
//    @Column(name = "N_AG_PERCENT")
//    private BigDecimal ag;
//
//    @Schema(description = "درصد طلا (Au)")
//    @Column(name = "N_AU_PERCENT")
//    private BigDecimal au;
//
//    @Schema(description = "درصد رطوبت (H₂O)")
//    @Column(name = "N_H2O_PERCENT")
//    private BigDecimal h2o;
//
//    @Schema(description = "درصد سلنیوم (Se)")
//    @Column(name = "N_SE_PERCENT")
//    private BigDecimal se;
//
//    @Schema(description = "درصد مس (Cu)")
//    @Column(name = "N_CU_PERCENT")
//    private BigDecimal cu;
//
//    @Schema(description = "پلاتین (ppm)")
//    @Column(name = "N_PT_PPM")
//    private BigDecimal pt;
//
//    @Schema(description = "پالادیوم (ppm)")
//    @Column(name = "N_PD_PPM")
//    private BigDecimal pd;
//
//    @Schema(description = "قیمت بشکه")
//    @Column(name = "N_BARREL_PRICE")
//    private BigDecimal barrelPrice;
//
//    @Schema(description = "وزن ناخالص مرطوب (کیلوگرم)")
//    @Column(name = "N_GROSS_WET_WEIGHT")
//    private BigDecimal grossWetWeight;
//
//    @Schema(description = "وزن خالص مرطوب (کیلوگرم)")
//    @Column(name = "N_NET_WET_WEIGHT")
//    private BigDecimal netWetWeight;
//
//    @Schema(description = "وزن خالص خشک (کیلوگرم)")
//    @Column(name = "N_NET_DRY_WEIGHT")
//    private BigDecimal netDryWeight;
//}
