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
/// / خواندن اطلاعات توزین از لجستیک (Detail view by HAVCODE)
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//
//public class BillingWeighingDetailExtractDto implements Serializable {
//
//    @Serial
//    private static final long serialVersionUID = -2269528829524435713L;
//
//    private Long id;
//
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
//    private Integer weighingCount;
//
//    private String packName;
//
//    private String goodName;
//
//    private String issueDate;
//
//    private boolean isFinal;
//
//
//    private String contractNo;
//
//    private String remittanceDate;
//
//    @EqualsAndHashCode(callSuper = true)
//    @Data
//    @ApiModel("RemittanceWeighingDetailDto.Create")
//    @NoArgsConstructor
//    public static class Create extends BillingWeighingDetailExtractDto {
//        @Serial
//        private static final long serialVersionUID = -7747104093940814509L;
//    }
//
//    @EqualsAndHashCode(callSuper = true)
//    @Data
//    @ApiModel("RemittanceWeighingDetailDto.Info")
//    @NoArgsConstructor
//    public static class Info extends BillingWeighingDetailExtractDto {
//        @Serial
//        private static final long serialVersionUID = -6955842500879602565L;
//        private Date createdDate;
//        private Date lastModifiedDate;
//        private String createdBy;
//        private String lastModifiedBy;
//        private String comment;
//    }
//}