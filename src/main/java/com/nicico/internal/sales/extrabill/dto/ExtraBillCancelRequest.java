package com.nicico.internal.sales.extrabill.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtraBillCancelRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = 7459496575730589771L;
	@Schema(description = "شناسه برات")
	private long Id;

	@Schema(description = "دلیل ابطال")
	@NotEmpty
	private String cancellationReason;

	@Schema(description = "توضیحات")
	private String description = "برات ابطال شد";
}