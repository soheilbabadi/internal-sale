package com.nicico.internal.sales.goods.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodBucketRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = -1688443961620890577L;

	@Schema(name = "goodId", description = "شناسه کالا", example = "1")
	@NotNull(message = "شناسه کالا الزامی است.")
	@Positive(message = "شناسه کالا باید بزرگتر از صفر باشد.")
	private Long goodId;

	@Schema(name = "startDate", description = "تاریخ شروع", example = "2023-01-01")
	@NotNull(message = "تاریخ شروع الزامی است.")
	private Date startDate;

	@Schema(name = "packagingSize", description = "ظرفیت بسته بندی هر واحد", example = "500")
	@NotNull(message = "ظرفیت بسته بندی الزامی است.")
	@DecimalMin(value = "0", inclusive = false, message = "ظرفیت بسته بندی باید بزرگتر از صفر باشد."
	)
	@Digits(integer = 18, fraction = 0, message = "ظرفیت بسته بندی باید یک عدد صحیح باشد.")
	private BigDecimal packagingSize;

	@Schema(name = "packingName", description = "نام بسته بندی", example = "بشکه")
	@NotBlank(message = "نام بسته بندی الزامی است.")
	@Size(max = 100, message = "نام بسته بندی نباید بیشتر از 100 کاراکتر باشد.")
	private String packingName;

	@Schema(name = "cashPercentage", description = "درصد نقدی", example = "53.25")
	@NotNull(message = "درصد نقدی الزامی است.")
	@DecimalMin(value = "0", message = "درصد نقدی نمی تواند کمتر از صفر باشد.")
	@DecimalMax(value = "100", message = "درصد نقدی نمی تواند بیشتر از 100 باشد.")
	@Digits(integer = 3, fraction = 2, message = "درصد نقدی حداکثر می تواند 2 رقم اعشار داشته باشد.")
	private BigDecimal cashPercentage;

	@Schema(name = "commission", description = "درصد کمیسیون مس", example = "6")
	@DecimalMin(value = "0", message = "درصد کمیسیون نمی تواند کمتر از صفر باشد.")
	@DecimalMax(value = "100", message = "درصد کمیسیون نمی تواند بیشتر از 100 باشد.")
	private Double commission;

	@Schema(name = "divisibilityCheck", description = "قابلیت تقسیم - حداقل مقداری که می توان فروخت", example = "1")
	@NotNull(message = "حداقل مقدار فروش الزامی است.")
	@DecimalMin(value = "0", inclusive = false, message = "حداقل مقدار فروش باید بزرگتر از صفر باشد.")
	@Digits(integer = 18, fraction = 0, message = "حداقل مقدار فروش باید یک عدد صحیح باشد.")
	private BigDecimal divisibilityCheck;
}