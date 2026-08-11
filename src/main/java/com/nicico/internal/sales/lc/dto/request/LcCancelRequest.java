package com.nicico.internal.sales.lc.dto.request;

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
public class LcCancelRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = 7459496575730589771L;
	@Schema(description = "شناسه اعتبار اسنادی")
	private long lcId;

	@Schema(description = "دلیل ابطال")
	@NotEmpty
	private String cancellationReason;

	@Schema(description = "توضیحات")
	private String description = "اعتبار اسنادی ابطال شد";
}