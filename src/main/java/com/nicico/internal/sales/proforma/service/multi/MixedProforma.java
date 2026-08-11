package com.nicico.internal.sales.proforma.service.multi;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.proforma.dto.MixedProformaRequest;
import com.nicico.internal.sales.proforma.service.ProformaValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MixedProforma {

	private static final String MSG_INVALID_DATA = "اطلاعات پیش فاکتور نادرست است";
	private final ProformaValidationService proformaValidationService;


	public String issueMix(MixedProformaRequest mixedProformaRequest) {
		var errors = proformaValidationService.validateMixedProforma(mixedProformaRequest);
		if (!errors.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(MSG_INVALID_DATA, errors);
		}

		return "";
	}
}
