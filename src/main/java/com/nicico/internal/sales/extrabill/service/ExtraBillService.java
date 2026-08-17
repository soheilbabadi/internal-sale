package com.nicico.internal.sales.extrabill.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.extrabill.dto.ProformaBankBillDto;
import com.nicico.internal.sales.extrabill.dto.ProformaBankBillFileUpdateDto;
import com.nicico.internal.sales.extrabill.dto.ProformaBankBillReportDto;
import com.nicico.internal.sales.extrabill.dto.ProformaBankBillRequest;

import java.util.List;

public interface ExtraBillService {


	SearchDTO.SearchRs<ProformaBankBillDto.Info> search(SearchDTO.SearchRq request);


	SearchDTO.SearchRs<ProformaBankBillReportDto.Info> searchReport(SearchDTO.SearchRq request);

	ProformaBankBillDto.Info save(ProformaBankBillRequest extraBillIssue);

	List<ProformaBankBillDto.Info> getByMasterId(Long proformaMasterId);

	ProformaBankBillDto.Info updateBillFiles(ProformaBankBillFileUpdateDto updateDto);
}
