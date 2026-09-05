package com.nicico.internal.sales.export.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.dto.ExportConfigMapper;
import com.nicico.internal.sales.export.dto.ExportNotificationConfigDto;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.model.ExportNotificationConfigModel;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExportNotificationConfigServiceImp implements ExportNotificationConfigService {

	private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";
	private final ExportConfigMapper mapper;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;

	@Override
	public SearchDTO.SearchRs<ExportNotificationConfigDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(exportNotificationConfigRepository, request, mapper::toDTO);
	}

	@Override
	public ExportNotificationConfigDto.Info getById(long id) {
		var exportConfig = exportNotificationConfigRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
		return mapper.toDTO(exportConfig);
	}

	@Override
	public ExportNotificationConfigDto.Info getByEntityType(EntityTypeEnum entityTypeEnum) {
		initializeDefaultConfigurations();
		var exportConfig = exportNotificationConfigRepository.findByEntityType(entityTypeEnum)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		return mapper.toDTO(exportConfig);
	}

	@Override
	public ExportNotificationConfigDto.Info save(ExportNotificationConfigDto.Create request) {

		request.setId((long) request.getEntityType().getPersianName().hashCode());

		exportNotificationConfigRepository.findById(request.getId())
				.ifPresent(exportNotificationConfig -> {
					exportNotificationConfigRepository.deleteById(request.getId());
					exportNotificationConfigRepository.flush();
				});

		var exportConfig = mapper.fromDTO(request);
		exportNotificationConfigRepository.save(exportConfig);
		return mapper.toDTO(exportConfig);
	}

	@Override
	public void initializeDefaultConfigurations() {
		try {
			EntityTypeEnum[] allEntityTypes = EntityTypeEnum.values();
			for (EntityTypeEnum entityType : allEntityTypes) {
				Optional<ExportNotificationConfigModel> existingConfig = exportNotificationConfigRepository.findByEntityType(entityType);

				if (existingConfig.isEmpty()) {
					ExportNotificationConfigModel newConfig = new ExportNotificationConfigModel();
					newConfig.setId((long) entityType.getPersianName().hashCode());
					newConfig.setEntityType(entityType);
					newConfig.setSendEmail(true);
					newConfig.setSendSms(true);
					newConfig.setSendPms(true);
					exportNotificationConfigRepository.saveAndFlush(newConfig);
				}
			}
		} catch (Exception exception) {
			log.error(exception.getMessage());
		}
	}


}
