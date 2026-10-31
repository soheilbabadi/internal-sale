package com.nicico.internal.sales.pricing.dto;

import com.nicico.internal.sales.pricing.model.QCAnalysisItemModel;
import com.nicico.internal.sales.pricing.model.QCAnalysisMasterModel;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QCAnalysisMapper {

	@Mapping(target = "items", source = "items")
	QCAnalysisMasterDto.Info toDto(QCAnalysisMasterModel model);

	QCAnalysisItemDto toItemDto(QCAnalysisItemModel model);

	List<QCAnalysisItemDto> toItemDtoList(List<QCAnalysisItemModel> models);

	@Mapping(target = "items", ignore = true)
	@Mapping(target = "childAnalyses", ignore = true)
	@Mapping(target = "parentAnalysis", ignore = true)
	QCAnalysisMasterModel toModel(QCAnalysisMasterDto.SaveRequest request);

	QCAnalysisItemModel toItemModel(QCAnalysisItemDto dto);

	List<QCAnalysisItemModel> toItemModelList(List<QCAnalysisItemDto> dtos);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "analysisMaster", ignore = true)
	@Mapping(target = "analysisMasterId", ignore = true)
	@Mapping(target = "rowNo", ignore = true)
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateItemModelFromDto(QCAnalysisItemDto dto, @MappingTarget QCAnalysisItemModel model);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "items", ignore = true)
	@Mapping(target = "childAnalyses", ignore = true)
	@Mapping(target = "parentAnalysis", ignore = true)
	void updateModelFromRequest(QCAnalysisMasterDto.SaveRequest request, @MappingTarget QCAnalysisMasterModel model);

	QCAnalysisMasterDto.ParentOption toParentOption(QCAnalysisMasterModel model);

	List<QCAnalysisMasterDto.ParentOption> toParentOptionList(List<QCAnalysisMasterModel> models);

	@AfterMapping
	default void formatBarrelRange(QCAnalysisItemModel model, @MappingTarget QCAnalysisItemDto dto) {
		if (model.getFromBarrelNo() != null && model.getToBarrelNo() != null) {
			dto.setBarrelRange(model.getFromBarrelNo() + "-" + model.getToBarrelNo());
			dto.setTotalBarrelsCount((int) (model.getToBarrelNo() - model.getFromBarrelNo() + 1));
		} else if (model.getFromBarrelNo() != null) {
			dto.setBarrelRange(String.valueOf(model.getFromBarrelNo()));
			dto.setTotalBarrelsCount(1);
		}
	}
}
