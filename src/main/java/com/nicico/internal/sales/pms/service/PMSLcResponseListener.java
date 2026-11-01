package com.nicico.internal.sales.pms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.pms.dto.PMSLcDTO;
import com.nicico.internal.sales.pms.enums.PMSLcMarkEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PMSLcResponseListener {
	private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";

	private final ObjectMapper objectMapper;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;
	private final PMSLcService pmsLcService;
	private final PMSGaamService pmsGaamService;
	//TODO after uncommit pmExtraBillService, uncommit this
//	private final PMExtraBillService pmExtraBillService;

	@RabbitListener(queues = "${rabbitmq.config.pms.queues.lc.response-queue}")
	public void handlePmsLcResponse(String message) throws JsonProcessingException {
		dispatchResponse(message, "Saving PMS ID");
	}

	@RabbitListener(queues = "${rabbitmq.config.pms.queues.lc.update-response-queue}")
	public void handlePmsLcUpdateResponse(String message) throws JsonProcessingException {
		dispatchResponse(message, "Updating PMS ID from update-response-queue");
	}

	private void dispatchResponse(String message, String actionName) throws JsonProcessingException {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.LETTER_OF_CREDIT)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;

		log.info("{}: {}", actionName, message);
		PMSLcDTO.RabbitListenerResponseDTO response = objectMapper.readValue(message,
				PMSLcDTO.RabbitListenerResponseDTO.class);
		if (response.getRequest() == null || response.getRequest().getId() == null) {
			log.warn("Ignoring malformed PMS response without request id: {}", message);
			return;
		}

		Long entityId = response.getRequest().getId();
		String pmsId = response.getResponse() != null ? response.getResponse().getId() : null;
		if (pmsId == null) {
			log.warn("PMS response had empty PMS id for entity id {}", entityId);
			return;
		}

		PMSLcMarkEnum mark = response.getRequest().getRequest() != null ? response.getRequest().getRequest().getMark() : null;

		if (mark == PMSLcMarkEnum.oragh || mark == PMSLcMarkEnum.ORAGH) {
			pmsGaamService.savePmsId(entityId, pmsId);
		} else if (mark == PMSLcMarkEnum.brat || mark == PMSLcMarkEnum.BRAT) {
			//TODO after uncommit pmExtraBillService, uncommit this
//			pmExtraBillService.savePmsId(entityId, pmsId);
		} else {
			pmsLcService.savePmsId(entityId, pmsId);
		}
	}
}
