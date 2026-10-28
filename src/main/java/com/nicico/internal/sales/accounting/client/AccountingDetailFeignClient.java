package com.nicico.internal.sales.accounting.client;

import com.nicico.internal.sales.accounting.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
		name = "accountingClient",
		url = "${accounting.api.base-url:http://devapp01.icico.net.ir/accounting/}",
		configuration = AccountingFeignConfiguration.class
)
public interface AccountingDetailFeignClient {

	@PostMapping(value = "rest/detail/saveDetail", consumes = MediaType.APPLICATION_JSON_VALUE)
	String saveDetail(
			@RequestParam(value = "submissionId", required = false) String submissionId,
			@RequestBody DetailDto.DetailInfoDto detailInfo
	);

	@GetMapping(value = "rest/detail/detailGridFetch")
	TotalResponse<DetailDto> getDetailGridList(@SpringQueryMap Map<String, String> queryParams);

	@PostMapping(
			value = "rest/detail/searchInDetails",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	TotalResponse<DetailDto> searchInDetails(@RequestBody Map<String, ?> formParams);

	@PostMapping(value = "rest/contact/addAndUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
	String addAndUpdateContact(@RequestBody ContactDto.Create contactDto);
}
