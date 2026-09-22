package com.nicico.internal.sales.accounting.dto;

import com.fgostar.accounting.sdk.dto.DetailDto;
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
@Schema(description = "Response object for financial instrument sequence generation and detail creation")
public class FinancialInstrumentCreateResponseDto implements Serializable {

	@Schema(description = "Whether the detail was created successfully", example = "true")
	private boolean success;

	@Schema(description = "Created detail full code", example = "108/1803010001")
	private String code;

	@Schema(description = "Calculated sequential number", example = "1")
	private Long detailNumber;

	@Schema(description = "Submission ID associated with Accounting tracking", example = "123456")
	private String submissionId;

	@Schema(description = "Detail account details")
	private DetailDto detail;
}
