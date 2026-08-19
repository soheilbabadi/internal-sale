package com.nicico.internal.sales.extrabill.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.extrabill.dto.*;

import java.util.List;

public interface ExtraBillService {


	SearchDTO.SearchRs<ProformaBankBillDto.Info> search(SearchDTO.SearchRq request);


	SearchDTO.SearchRs<ProformaBankBillReportDto.Info> searchReport(SearchDTO.SearchRq request);

	ProformaBankBillDto.Info save(ProformaBankBillRequest extraBillIssue);

	List<ProformaBankBillDto.Info> getByMasterId(Long proformaMasterId);

	ProformaBankBillDto.Info updateBillFiles(ProformaBankBillFileUpdateDto updateDto);

	void sendReckoningEmail(Long extraBillId);

	ProformaBankBillDto.Info updateExtraBill(UpdateExtraBillRequest updateExtraBillRequest);

	List<ProformaBankBillAuditDto> getAuditHistory(Long extraBillId);
}
