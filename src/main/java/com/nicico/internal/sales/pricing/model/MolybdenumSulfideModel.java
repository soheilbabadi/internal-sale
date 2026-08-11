//package com.nicico.internal.sales.pricing.model;
//
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
//@Schema(description = "سولفید مولیبدن")
//@Audited
//@EqualsAndHashCode(callSuper = true)
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
//@Accessors(chain = true)
//@Table(name = "T_INS_PRICE_MOLYBDENUM_SULFIDE")
//public class MolybdenumSulfideModel extends BaseClassModel {
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
//    @Schema(description = "شماره لات")
//    @Column(name = "C_LOT_NUMBER")
//    private String lotNumber;
//
//    @Schema(description = "درصد تخفیف")
//    @Column(name = "N_DISCOUNT")
//    private BigDecimal discount;
//
//    @Schema(description = "درصد رطوبت (H₂O)")
//    @Column(name = "N_H2O_PERCENT")
//    private BigDecimal h2o;
//
//    @Schema(description = "درصد روغن")
//    @Column(name = "N_OIL_PERCENT")
//    private BigDecimal oilPercent;
//
//    @Schema(description = "درصد مس (Cu)")
//    @Column(name = "N_CU_PERCENT")
//    private BigDecimal cu;
//
//    @Schema(description = "درصد مولیبدن")
//    @Column(name = "N_MOLYBDENUM_PERCENT")
//    private BigDecimal molybdenumPercent;
//
//    @Schema(description = "قیمت بشکه")
//    @Column(name = "N_BARREL_PRICE")
//    private BigDecimal barrelPrice;
//}
