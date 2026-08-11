package com.nicico.internal.sales.proforma.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.proforma.dto.*;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProformaService {
	ProformaMasterModel createProformaMaster(PerfomaCreateRequest requestDto);

	SearchDTO.SearchRs<ProformaMasterDTO.Info> search(SearchDTO.SearchRq request);

	SearchDTO.SearchRs<ProformaMasterDTO.Info> getByNationalCode(List<String> nationalCodes);


	List<ProformaMasterDTO.Info> getFailedProforma(Pageable pageable, Sort sort);

	void delete(long performaId);

	String create(PerfomaCreateRequest requestDto);

	boolean isPreciousMetal(String paymentCode);

	List<String> getEditedProformaNo(Long masterId);

	boolean canStartReversal(Long masterId);

	ProformaResponseDto getDetailById(long id);

	ProformaResponseDto getActiveProformaById(long id);

	ProformaResponseDto getDetailByInstanceId(String instanceId);

	String createReversal(PerformerCreateRevealRequest requestDto);

	List<String> getCanceledProformaNo(Long masterId);

	List<ProformaDetailDto.Info> getCanceledByContractNo(Long contractNo);

	List<ProformaMasterDTO.Info> getCancelable();

	String getLotNumberByTradeId(Long tradeId);


}
