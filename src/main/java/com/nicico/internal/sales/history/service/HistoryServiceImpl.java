package com.nicico.internal.sales.history.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.history.dto.HistoryDetailResponse;
import com.nicico.internal.sales.history.dto.HistoryExtractMapper;
import com.nicico.internal.sales.history.dto.HistoryExtractMasterDto;
import com.nicico.internal.sales.history.model.HistoryExtractMasterModel;
import com.nicico.internal.sales.history.repository.HistoryRepository;
import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.lc.dto.LcMapper;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.dto.ProformaDetailMapper;
import com.nicico.internal.sales.proforma.dto.ProformaResponseDto;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.proforma.service.ProformaService;
import com.nicico.internal.sales.remittance.dto.RemittanceGoodItemDto;
import com.nicico.internal.sales.remittance.dto.RemittanceMasterDto;
import com.nicico.internal.sales.remittance.dto.RemittanceMasterMapper;
import com.nicico.internal.sales.remittance.mapper.RemittanceGoodItemMapper;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

	private static final String MSG_HISTORY_NOT_FOUND = "آیتمی با شناسه %d یافت نشد";
	private static final String MSG_ERROR_FETCHING_DETAILS = "خطا در دریافت جزئیات فروش : %s";

	private final HistoryRepository historyRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final LcRepository lcRepository;
	private final RemittanceMasterRepository remittanceMasterRepository;

	private final ProformaDetailMapper proformaDetailMapper;
	private final RemittanceMasterMapper remittanceMasterMapper;
	private final LcMapper lcMapper;
	private final RemittanceGoodItemMapper remittanceGoodItemMapper;
	private final HistoryExtractMapper historyExtractMapper;
	private final ProformaService proformaService;

	@Override
	public HistoryDetailResponse getHistoryDetails(Long historyId) {

		var historyRecord = findHistoryRecord(historyId);
		var proformaMaster = fetchProformaMaster(historyRecord.getMasterId());
		var remittanceData = fetchRemittanceData(historyRecord.getRemittanceId());
		ProformaResponseDto prformaResponse = proformaService.getDetailById(historyRecord.getMasterId());


		return new HistoryDetailResponse(
				prformaResponse,
				fetchLcData(proformaMaster),
				remittanceData.masterDto(),
				remittanceData.goodItemDtoList()
		);
	}

	@Override
	public SearchDTO.SearchRs<HistoryExtractMasterDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(historyRepository, request, historyExtractMapper::toDTO);
	}

	@Override
	public HistoryExtractMasterDto.Info findById(Long historyId) {
		return historyExtractMapper.toDTO(findHistoryRecord(historyId));
	}


	private HistoryExtractMasterModel findHistoryRecord(Long historyId) {
		return historyRepository.findFirstByIdOrderByMasterIdDesc(historyId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						String.format(MSG_HISTORY_NOT_FOUND, historyId)));
	}

	private Optional<ProformaMasterModel> fetchProformaMaster(Long masterId) {
		if (isInvalidId(masterId)) {
			log.debug("Invalid proforma master ID: {}", masterId);
			return Optional.empty();
		}
		var proforma = proformaMasterRepository.findById(masterId);
		if (proforma.isEmpty()) {
			log.debug("No proforma master found for ID: {}", masterId);
		}
		return proforma;
	}

	private List<LcDto> fetchLcData(Optional<ProformaMasterModel> proformaMaster) {
		return proformaMaster
				.map(master -> {
					var lcList = lcRepository.findAllByProformaMasterId(master.getId());
					if (lcList.isEmpty()) {
						log.debug("No LC records found for proforma master ID: {}", master.getId());
						return Collections.<LcDto>emptyList();
					}
					log.debug("Found {} LC records for proforma master ID: {}", lcList.size(), master.getId());
					return lcList.stream()
							.<LcDto>map(lcMapper::toDTO)
							.toList();
				})
				.orElse(Collections.emptyList());
	}

	private RemittanceData fetchRemittanceData(Long remittanceId) {
		if (isInvalidId(remittanceId)) {
			log.debug("Invalid remittance ID: {}", remittanceId);
			return RemittanceData.empty();
		}

		return remittanceMasterRepository.findById(remittanceId)
				.map(remittance -> {
					log.debug("Fetched remittance data for remittance ID: {}", remittanceId);
					var goodItems = Optional.ofNullable(remittance.getRemittanceGoodItemModels())
							.orElse(Collections.emptyList())
							.stream()
							.<RemittanceGoodItemDto>map(remittanceGoodItemMapper::toDTO)
							.toList();
					return new RemittanceData(remittanceMasterMapper.toDTO(remittance), goodItems);
				})
				.orElseGet(() -> {
					log.debug("No remittance found for ID: {}", remittanceId);
					return RemittanceData.empty();
				});
	}


	private boolean isInvalidId(Long id) {
		return id == null || id <= 0;
	}

	private record RemittanceData(RemittanceMasterDto.Info masterDto, List<RemittanceGoodItemDto> goodItemDtoList) {
		static RemittanceData empty() {
			return new RemittanceData(null, Collections.emptyList());
		}
	}

}
