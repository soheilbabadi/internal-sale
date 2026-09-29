package com.nicico.internal.sales.gaam.dto;

import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GaamCancelRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = 7459496575730589771L;
	@Schema(description = "شناسه برات")
	private long Id;

	@Schema(description = "دلیل ابطال")
	@NotEmpty
	@Enumerated(EnumType.STRING)
	private LcCancellationReason cancellationReason;

	@Schema(description = "توضیحات")
	private String description = "برات ابطال شد";
}