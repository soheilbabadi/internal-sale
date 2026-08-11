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
//public class BillingTradeExtractMasterDto implements Serializable {
//
//    @Serial
//    private static final long serialVersionUID = -8364682081445078704L;
//
//
//    private Long id;
//
//    private BigDecimal tradeId;
//    private BigDecimal commodityCode;
//
//    private String contractDate;
//
//    private Date remittanceDate;
//
//    private String validityDate;
//
//    private BigDecimal customerId;
//
//    private String customerName;
//
//    private String economicCode;
//
//    private String nationalCode;
//
//    private Long loadingPortId;
//
//    private String loadingPort;
//    private Long goodId;
//
//    private String goodName;
//
//    private String packingName;
//
//    private String lotNumber;
//
//    private Date contractDateMaster;
//
//    private String contractNo;
//
//    private String sellerBrokerName;
//
//    private Double cashPercentage;
//
//    private Double creditPercentage;
//
//    private BigDecimal remittanceQuantity;
//
//    private BigDecimal remittanceQuantityCash;
//
//    private BigDecimal remittanceQuantityCredit;
//
//    private BigDecimal remittanceUnitPriceCash;
//
//    private BigDecimal remittanceUnitPriceCredit;
//
//    private Date lcExpiryDate;
//
//    private Long issuerBankId;
//
//    private String settlementTypeDesc;
//
//    private String offerDescription;
//
//
//    @EqualsAndHashCode(callSuper = true)
//    @Data
//    @ApiModel("RemittanceTradeExtractDto.Create")
//    @NoArgsConstructor
//    public static class Create extends BillingTradeExtractMasterDto {
//        @Serial
//        private static final long serialVersionUID = -7747104093940814509L;
//    }
//
//    @EqualsAndHashCode(callSuper = true)
//    @Data
//    @ApiModel("RemittanceTradeExtractDto.Info")
//    @NoArgsConstructor
//    public static class Info extends BillingTradeExtractMasterDto {
//        @Serial
//        private static final long serialVersionUID = -6955842500879602565L;
//        private Date createdDate;
//        private Date lastModifiedDate;
//        private String createdBy;
//        private String lastModifiedBy;
//        private String comment;
//    }
//}
