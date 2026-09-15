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
@Schema(description = "DTO for creating an accounting detail account for a natural person")
public class CreatePersonDetailDto implements Serializable {

    @Schema(description = "National code (10 digits)", example = "0012345678")
    private String nationalCode;

    @Schema(description = "Full name", example = "علی محمدی")
    private String fullName;
}

