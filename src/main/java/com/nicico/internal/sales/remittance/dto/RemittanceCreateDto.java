package com.nicico.internal.sales.remittance.dto;

import com.nicico.internal.sales.remittance.enums.RemittanceSourceType;
import com.nicico.internal.sales.util.ValidDateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ValidDateRange(startField = "remittanceDate", endField = "validityDate", message = "تاریخ اعتبار باید برابر یا بعد از تاریخ صدور حواله باشد")
public class RemittanceCreateDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 788276480203056653L;
	@Schema(name = "loadingPortId", description = "شناسه مجل بارگیری")
	@Positive(message = "شناسه محل بارگیری باید مثبت باشد")
	private Long loadingPortId;
	@Schema(description = "شناسه آگهی عرضه", name = "tradeId", example = "12")
	@NotNull(message = "شناسه معامله الزامی است")
	private long tradeId;
	@Schema(name = "issuePlaceId", description = "محل صدور")
	@NotNull(message = "محل صدور الزامی است")
	@Positive(message = "شناسه محل صدور باید مثبت باشد")
	private Long issuePlaceId;
	@Schema(name = "netWeight", description = "وزن خالص خشک")
	private Double netWeight = 0.0;

	@Schema(name = "sourceType", description = "نوع حواله", example = "PROFORMA", required = true)
	@NotNull(message = "نوع حواله الزامی است")
	private RemittanceSourceType sourceType;
}
