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
//@Schema(description = "اکسید مولیبدن")
//@Audited
//@EqualsAndHashCode(callSuper = true)
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
//@Accessors(chain = true)
//@Table(name = "T_INS_PRICE_MOLYBDENUM_OXIDE")
//public class MolybdenumOxideModel extends BaseClassModel {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRICE_FORMULA")
//    @SequenceGenerator(name = "SEQ_PRICE_FORMULA", sequenceName = "SEQ_PRICE_FORMULA")
//    private Long id;
//
//    @Schema(description = "تاریخ عرضه")
//    @Column(name = "D_OFFER_DATE")
//    private Date offerDate;
//
//    @Schema(description = "شماره اندیکاتور کنترل کیفی")
//    @Column(name = "C_QC_INDICATOR")
//    private String qcIndicator;
//
//    @Schema(description = "شماره لات")
//    @Column(name = "C_LOT_NUMBER")
//    private String lotNumber;
//
//    @Schema(description = "درصد مولیبدن")
//    @Column(name = "N_MOLYBDENUM_PERCENT")
//    private BigDecimal molybdenumPercent;
//
//    @Schema(description = "ضریب تعدیل")
//    @Column(name = "N_ADJUSTMENT_FACTOR")
//    private BigDecimal adjustmentFactor;
//}
