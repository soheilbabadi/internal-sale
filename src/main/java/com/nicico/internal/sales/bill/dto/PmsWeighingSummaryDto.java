//package com.nicico.internal.sales.bill.dto;
//
//import io.swagger.annotations.ApiModel;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//
//import java.io.Serial;
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.Date;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//
//public class PmsWeighingSummaryDto implements Serializable {
//
//    @Serial
//    private static final long serialVersionUID = -2269528829524435712L;
//
//    private Long id;
//
//    private String remittanceNo;
//
//    private Long customerId;
//
//    private String customerName;
//
//    private String economicCode;
//
//    private String nationalCode;
//
//    private String loadingPort;
//
//    private String contractDate;
//
//    private BigDecimal remittanceQuantity;
//
//    private BigDecimal totalRealWeight;
//
//    private BigDecimal weighingCount;
//
//    private String packName;
//
//    private String goodName;
//
//    private String issueDate;
//
//    private boolean isFinal;
//
//    private BigDecimal weightDifference;
//
//    private String contractNo;
//
//    private String remittanceDate;
//
//    @EqualsAndHashCode(callSuper = true)
//    @Data
//    @ApiModel("RemittanceWeighingSummaryDto.Create")
//    @NoArgsConstructor
//    public static class Create extends PmsWeighingSummaryDto {
//        @Serial
//        private static final long serialVersionUID = -7747104093940814509L;
//    }
//
//    @EqualsAndHashCode(callSuper = true)
//    @Data
//    @ApiModel("RemittanceWeighingSummaryDto.Info")
//    @NoArgsConstructor
//    public static class Info extends PmsWeighingSummaryDto {
//        @Serial
//        private static final long serialVersionUID = -6955842500879602565L;
//        private Date createdDate;
//        private Date lastModifiedDate;
//        private String createdBy;
//        private String lastModifiedBy;
//        private String comment;
//    }
//}
