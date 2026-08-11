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
//@Schema(description = "سرباره گرانوله")
//@Audited
//@EqualsAndHashCode(callSuper = true)
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
//@Accessors(chain = true)
//@Table(name = "T_INS_PRICE_GRANULATED_SLAG")
//public class GranulatedSlagModel extends BaseClassModel {
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
//    @Column(name = "C_LOADING_LOCATION")
//    private String loadingLocation;
//
//    @Schema(description = "شماره اندیکاتور کنترل کیفی")
//    @Column(name = "C_QC_INDICATOR")
//    private String qcIndicator;
//
//    @Schema(description = "درصد رطوبت (H₂O)")
//    @Column(name = "N_H2O_PERCENT")
//    private BigDecimal h2o;
//
//    @Schema(description = "درصد مس (Cu)")
//    @Column(name = "N_CU_PERCENT")
//    private BigDecimal cu;
//
//    @Schema(description = "ضریب تعدیل")
//    @Column(name = "N_ADJUSTMENT_FACTOR")
//    private BigDecimal adjustmentFactor;
//}
