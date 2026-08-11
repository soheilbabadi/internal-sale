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
//@Schema(description = "کنسانتره مس")
//@Audited
//@EqualsAndHashCode(callSuper = true)
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
//@Accessors(chain = true)
//@Table(name = "T_INS_PRICE_COPPER_CONCENTRATE")
//public class CopperConcentrateModel extends BaseClassModel {
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
//    @Schema(description = "عیار نقره (ppm)")
//    @Column(name = "N_AG_PPM")
//    private BigDecimal ag;
//
//    @Schema(description = "عیار طلا (ppm)")
//    @Column(name = "N_AU_PPM")
//    private BigDecimal au;
//
//    @Schema(description = "درصد رطوبت (H₂O)")
//    @Column(name = "N_H2O_PERCENT")
//    private BigDecimal h2o;
//
//    @Schema(description = "درصد مس (Cu)")
//    @Column(name = "N_CU_PERCENT")
//    private BigDecimal cu;
//
//    @Schema(description = "وزن ناخالص مرطوب (کیلوگرم)")
//    @Column(name = "N_GROSS_WET_WEIGHT")
//    private BigDecimal grossWetWeight;
//
//    @Schema(description = "وزن خالص خشک (کیلوگرم)")
//    @Column(name = "N_NET_DRY_WEIGHT")
//    private BigDecimal netDryWeight;
//
//    @Schema(description = "هزینه ذوب آوری")
//    @Column(name = "N_SMELTING_COST")
//    private BigDecimal smeltingCost;
//
//    @Schema(description = "هزینه پالایش")
//    @Column(name = "N_REFINING_COST")
//    private BigDecimal refiningCost;
//}
