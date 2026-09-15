package com.nicico.internal.sales.accounting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating an accounting detail account for a legal entity / company")
public class CreateCompanyDetailDto implements Serializable {

    @Schema(description = "Company national ID (شناسه ملی - 11 digits)", example = "10100000000", required = true)
    private String nationalId;

    @Schema(description = "Company name", example = "شرکت صنایع مس ایران", required = true)
    private String companyName;

    @Schema(description = "English/Latin company name", example = "National Iranian Copper Industries Co.")
    private String detailNameLatin;


    @Schema(description = "Note / Description", example = "تفصیلی شخص حقوقی")
    private String note;
}
