package com.nicico.internal.sales.crm.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.crm.dto.CrmApprovedCompanyDto;
import com.nicico.internal.sales.crm.dto.LcWithProformaDto;
import com.nicico.internal.sales.history.dto.HistoryExtractMasterDto;
import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.proforma.dto.ProformaMasterDTO;
import com.nicico.internal.sales.proforma.dto.ProformaResponseDto;
import com.nicico.internal.sales.remittance.dto.RemittanceMasterDto;

import java.util.List;

public interface CrmHistoryService {
	List<CrmApprovedCompanyDto> getApprovedCompanyRequests();

	SearchDTO.SearchRs<HistoryExtractMasterDto.Info> getFilteredHistory();

	SearchDTO.SearchRs<ProformaMasterDTO.Info> getProformaList();

	List<LcDto.Info> getAllLcDataByProformaMasterId(Long proformaMasterId);

	ProformaResponseDto getProformaDetailById(long id);

	SearchDTO.SearchRs<LcWithProformaDto.Info> searchLc(SearchDTO.SearchRq request);

	SearchDTO.SearchRs<ProformaMasterDTO.Info> searchProforma(SearchDTO.SearchRq request);

	SearchDTO.SearchRs<RemittanceMasterDto.Info> searchRemittance(SearchDTO.SearchRq request);

	RemittanceMasterDto.Info getRemittanceDetailById(Long remittanceId);

	List<RemittanceMasterDto.Info> getAllByProformaMasterId(Long proformaMasterId);

	byte[] exportRemittanceDoc(long remittanceId);

	byte[] exportRemittancePdf(long remittanceId);

	byte[] exportProformaDoc(long proformaDetailId);

	byte[] exportProformaPdf(long proformaDetailId);
}
