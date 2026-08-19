package com.nicico.internal.sales.extrabill.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateExtraBillRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "شناسه برات", example = "1")
    private Long id;

    // ==================== فیلدهای بانکی ====================
    @Schema(description = "شناسه بانک صادر کننده برات")
    private Long issuerBankId;

    @Schema(description = "شناسه بانک عامل", example = "2011")
    private Long agentBankId;

    // ==================== فیلدهای برات الکترونیک ====================

    @Schema(description = "کد تفصیلی حسابداری", example = "123-456-789")
    private String nosaCode;

    @Schema(description = "کد سپام (شماره برات)", example = "1234-5678-9012-3456")
    private String sepamCode;

    @Schema(description = "شناسه خزانه داری کل کشور", example = "1234-5678-9012-3456")
    private String treasuryId;

    @Schema(description = "تاریخ صدور برات", example = "1404-01-15")
    private Date issueDate;

    @Schema(description = "تاریخ سررسید برات", example = "1404-03-15")
    private Date dueDate;

}
