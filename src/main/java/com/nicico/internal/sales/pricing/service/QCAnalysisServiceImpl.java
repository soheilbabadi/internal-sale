package com.nicico.internal.sales.pricing.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.pricing.dto.QCAnalysisItemDto;
import com.nicico.internal.sales.pricing.dto.QCAnalysisMapper;
import com.nicico.internal.sales.pricing.dto.QCAnalysisMasterDto;
import com.nicico.internal.sales.pricing.enums.QCAnalysisStage;
import com.nicico.internal.sales.pricing.enums.QCAnalysisStatus;
import com.nicico.internal.sales.pricing.model.QCAnalysisItemModel;
import com.nicico.internal.sales.pricing.model.QCAnalysisMasterModel;
import com.nicico.internal.sales.pricing.repository.QCAnalysisItemRepository;
import com.nicico.internal.sales.pricing.repository.QCAnalysisMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QCAnalysisServiceImpl implements QCAnalysisService {

	private static final BigDecimal TWO = BigDecimal.valueOf(2);

	private final QCAnalysisMasterRepository masterRepository;
	private final QCAnalysisItemRepository itemRepository;
	private final QCAnalysisMapper mapper;

	@Override
	@Transactional
	public QCAnalysisMasterDto.Info save(QCAnalysisMasterDto.SaveRequest request) {
		validateRequest(request);

		QCAnalysisMasterModel masterModel;
		if (request.getId() != null) {
			masterModel = masterRepository.findById(request.getId())
					.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("نامه آنالیز مورد نظر یافت نشد"));

			if (masterRepository.existsByParentAnalysisId(request.getId())) {
				throw new InternalSaleCustomException.ValidationException("ویرایش نامه",
						Collections.singletonList("این نامه در مراحل بعدی ارجاع داده شده و قابل ویرایش نیست"));
			}

			mapper.updateModelFromRequest(request, masterModel);
		} else {
			masterModel = mapper.toModel(request);
		}

		// Handle parent reference logic & auto compute averages
		if (request.getParentAnalysisId() != null) {
			QCAnalysisMasterModel parent = masterRepository.findById(request.getParentAnalysisId())
					.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("نامه والد یافت نشد"));

			masterModel.setRefIndicatorNo(parent.getQcIndicatorNo());

			// If Correction stage, supersede the previous post-offer record
			if (request.getAnalysisStage() == QCAnalysisStage.CORRECTION) {
				parent.setStatus(QCAnalysisStatus.SUPERSEDED);
				masterRepository.save(parent);
			}

			// Auto compute element averages based on parent items
			if (request.getItems() != null && !request.getItems().isEmpty()) {
				computeAverages(request, parent);
			}
		}

		QCAnalysisMasterModel savedMaster = masterRepository.save(masterModel);

		// Synchronize detail items (if provided)
		if (request.getItems() != null && !request.getItems().isEmpty()) {
			List<QCAnalysisItemModel> itemModels = new ArrayList<>();
			int rowIndex = 1;
			for (QCAnalysisItemDto itemDto : request.getItems()) {
				QCAnalysisItemModel itemModel = mapper.toItemModel(itemDto);
				itemModel.setAnalysisMasterId(savedMaster.getId());
				itemModel.setRowNo(rowIndex++);

				// Calculate barrel count and display text if barrels are defined
				if (itemModel.getFromBarrelNo() != null && itemModel.getToBarrelNo() != null) {
					long count = itemModel.getToBarrelNo() - itemModel.getFromBarrelNo() + 1;
					itemModel.setTotalBarrelsCount((int) count);
				} else if (itemModel.getFromBarrelNo() != null) {
					itemModel.setTotalBarrelsCount(1);
				}

				itemModels.add(itemModel);
			}

			itemRepository.deleteByAnalysisMasterId(savedMaster.getId());
			List<QCAnalysisItemModel> savedItems = itemRepository.saveAll(itemModels);
			if (savedMaster.getItems() == null) {
				savedMaster.setItems(new ArrayList<>());
			} else {
				savedMaster.getItems().clear();
			}
			savedMaster.getItems().addAll(savedItems);
		} else if (request.getId() != null && request.getItems() != null) {
			itemRepository.deleteByAnalysisMasterId(savedMaster.getId());
			if (savedMaster.getItems() != null) {
				savedMaster.getItems().clear();
			}
		}

		return mapper.toDto(savedMaster);
	}

	@Override
	@Transactional(readOnly = true)
	public SearchDTO.SearchRs<QCAnalysisMasterDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(masterRepository, request, mapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public QCAnalysisMasterDto.Info findById(Long id) {
		QCAnalysisMasterModel master = masterRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("نامه آنالیز یافت نشد"));
		List<QCAnalysisItemModel> items = itemRepository.findByAnalysisMasterIdOrderByRowNoAsc(id);
		master.setItems(items);
		return mapper.toDto(master);
	}

	@Override
	@Transactional(readOnly = true)
	public QCAnalysisMasterDto.Info findByIndicatorNo(String indicatorNo) {
		QCAnalysisMasterModel master = masterRepository.findByQcIndicatorNo(indicatorNo)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("نامه‌ای با این شماره اندیکاتور یافت نشد"));
		List<QCAnalysisItemModel> items = itemRepository.findByAnalysisMasterIdOrderByRowNoAsc(master.getId());
		master.setItems(items);
		return mapper.toDto(master);
	}

	@Override
	@Transactional(readOnly = true)
	public List<QCAnalysisMasterDto.ParentOption> getParentOptions(Long commodityId, QCAnalysisStage stage) {
		QCAnalysisStage parentStage;
		if (stage == QCAnalysisStage.POST_OFFER) {
			parentStage = QCAnalysisStage.PRE_OFFER;
		} else if (stage == QCAnalysisStage.CORRECTION) {
			parentStage = QCAnalysisStage.POST_OFFER;
		} else {
			return Collections.emptyList();
		}

		List<QCAnalysisMasterModel> masters = masterRepository.findByCommodityIdAndAnalysisStageOrderByIdDesc(commodityId, parentStage);
		return mapper.toParentOptionList(masters);
	}

	@Override
	@Transactional(readOnly = true)
	public List<QCAnalysisItemDto> getParentItems(Long parentMasterId) {
		List<QCAnalysisItemModel> items = itemRepository.findByAnalysisMasterIdOrderByRowNoAsc(parentMasterId);
		return mapper.toItemDtoList(items);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		QCAnalysisMasterModel master = masterRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("نامه آنالیز یافت نشد"));

		if (masterRepository.existsByParentAnalysisId(id)) {
			throw new InternalSaleCustomException.ValidationException("حذف آنالیز",
					Collections.singletonList("این نامه در مراحل بعدی استفاده شده و امکان حذف آن وجود ندارد. ابتدا رکوردهای وابسته را حذف کنید."));
		}

		itemRepository.deleteByAnalysisMasterId(id);
		masterRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public QCAnalysisItemDto findItemById(Long itemId) {
		QCAnalysisItemModel item = itemRepository.findById(itemId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("آیتم آنالیز یافت نشد"));
		return mapper.toItemDto(item);
	}

	@Override
	@Transactional
	public QCAnalysisItemDto saveItem(Long masterId, QCAnalysisItemDto itemDto) {
		QCAnalysisMasterModel master = masterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("نامه مستر یافت نشد"));

		QCAnalysisItemModel itemModel = mapper.toItemModel(itemDto);
		itemModel.setAnalysisMasterId(master.getId());

		if (itemModel.getRowNo() == null) {
			List<QCAnalysisItemModel> existingItems = itemRepository.findByAnalysisMasterIdOrderByRowNoAsc(masterId);
			itemModel.setRowNo(existingItems.size() + 1);
		}

		if (itemModel.getFromBarrelNo() != null && itemModel.getToBarrelNo() != null) {
			long count = itemModel.getToBarrelNo() - itemModel.getFromBarrelNo() + 1;
			itemModel.setTotalBarrelsCount((int) count);
		} else if (itemModel.getFromBarrelNo() != null) {
			itemModel.setTotalBarrelsCount(1);
		}

		QCAnalysisItemModel saved = itemRepository.save(itemModel);
		return mapper.toItemDto(saved);
	}

	@Override
	@Transactional
	public QCAnalysisItemDto updateItem(Long itemId, QCAnalysisItemDto itemDto) {
		QCAnalysisItemModel itemModel = itemRepository.findById(itemId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("آیتم آنالیز یافت نشد"));

		if (itemDto.getRowNo() != null) {
			itemModel.setRowNo(itemDto.getRowNo());
		}

		mapper.updateItemModelFromDto(itemDto, itemModel);

		if (itemModel.getFromBarrelNo() != null && itemModel.getToBarrelNo() != null) {
			long count = itemModel.getToBarrelNo() - itemModel.getFromBarrelNo() + 1;
			itemModel.setTotalBarrelsCount((int) count);
		} else if (itemModel.getFromBarrelNo() != null) {
			itemModel.setTotalBarrelsCount(1);
		}

		QCAnalysisItemModel updated = itemRepository.save(itemModel);
		return mapper.toItemDto(updated);
	}

	@Override
	@Transactional
	public void deleteItem(Long itemId) {
		if (!itemRepository.existsById(itemId)) {
			throw new InternalSaleCustomException.ResourceNotFoundException("آیتم آنالیز یافت نشد");
		}
		itemRepository.deleteById(itemId);
	}

	private void validateRequest(QCAnalysisMasterDto.SaveRequest request) {
		if (request.getId() == null) {
			if (masterRepository.existsByCommodityIdAndQcIndicatorNo(request.getCommodityId(), request.getQcIndicatorNo())) {
				throw new InternalSaleCustomException.ValidationException("اعتبارسنجی اندیکاتور",
						Collections.singletonList("این شماره اندیکاتور قبلاً برای این کالا ثبت شده است"));
			}
		} else {
			if (masterRepository.existsByCommodityIdAndQcIndicatorNoAndIdNot(request.getCommodityId(), request.getQcIndicatorNo(), request.getId())) {
				throw new InternalSaleCustomException.ValidationException("اعتبارسنجی اندیکاتور",
						Collections.singletonList("این شماره اندیکاتور قبلاً برای این کالا ثبت شده است"));
			}
		}

		if (request.getAnalysisStage() != QCAnalysisStage.PRE_OFFER && request.getParentAnalysisId() == null) {
			throw new InternalSaleCustomException.ValidationException("اعتبارسنجی نامه مرجع",
					Collections.singletonList("انتخاب نامه مرجع برای آنالیز بعد عرضه یا اصلاحی الزامی است"));
		}
	}

	private void computeAverages(QCAnalysisMasterDto.SaveRequest request, QCAnalysisMasterModel parent) {
		List<QCAnalysisItemModel> parentItems = itemRepository.findByAnalysisMasterIdOrderByRowNoAsc(parent.getId());
		Map<String, QCAnalysisItemModel> parentMapByBarrel = parentItems.stream()
				.filter(p -> p.getFromBarrelNo() != null)
				.collect(Collectors.toMap(p -> p.getFromBarrelNo() + "-" + p.getToBarrelNo(), p -> p, (a, b) -> a));

		for (QCAnalysisItemDto item : request.getItems()) {
			String key = item.getFromBarrelNo() + "-" + item.getToBarrelNo();
			QCAnalysisItemModel p = parentMapByBarrel.get(key);
			if (p == null && item.getRowNo() != null && item.getRowNo() <= parentItems.size()) {
				p = parentItems.get(item.getRowNo() - 1);
			}

			if (p != null) {
				item.setAvgMoisturePct(avg(p.getMoisturePct(), item.getMoisturePct()));
				item.setAvgAgPct(avg(p.getAgPct(), item.getAgPct()));
				item.setAvgAgKg(avg(p.getAgKg(), item.getAgKg()));
				item.setAvgAgPpm(avg(p.getAgPpm(), item.getAgPpm()));
				item.setAvgAuPct(avg(p.getAuPct(), item.getAuPct()));
				item.setAvgAuKg(avg(p.getAuKg(), item.getAuKg()));
				item.setAvgAuPpm(avg(p.getAuPpm(), item.getAuPpm()));
				item.setAvgSePct(avg(p.getSePct(), item.getSePct()));
				item.setAvgSeKg(avg(p.getSeKg(), item.getSeKg()));
				item.setAvgCuPct(avg(p.getCuPct(), item.getCuPct()));
				item.setAvgCuKg(avg(p.getCuKg(), item.getCuKg()));
				item.setAvgPtPpm(avg(p.getPtPpm(), item.getPtPpm()));
				item.setAvgPdPpm(avg(p.getPdPpm(), item.getPdPpm()));
				item.setAvgMoPct(avg(p.getMoPct(), item.getMoPct()));
				item.setAvgFePct(avg(p.getFePct(), item.getFePct()));
				item.setAvgSPct(avg(p.getSPct(), item.getSPct()));
				item.setAvgPbPct(avg(p.getPbPct(), item.getPbPct()));
				item.setAvgOilPct(avg(p.getOilPct(), item.getOilPct()));
				item.setAvgCuCalcPct(avg(p.getCuCalcPct(), item.getCuCalcPct()));
				item.setAvgAuCalcPpm(avg(p.getAuCalcPpm(), item.getAuCalcPpm()));
				item.setAvgAgCalcPpm(avg(p.getAgCalcPpm(), item.getAgCalcPpm()));
			}
		}
	}

	private BigDecimal avg(BigDecimal a, BigDecimal b) {
		if (a == null && b == null) return null;
		if (a == null) return b;
		if (b == null) return a;
		return a.add(b).divide(TWO, 4, RoundingMode.HALF_UP);
	}
}
