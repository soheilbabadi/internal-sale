package com.nicico.internal.sales.notification.service;

import com.nicico.internal.sales.common.properties.MessageServiceProperties;
import com.nicico.internal.sales.config.INSOkHttpClient;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.notification.dto.SmsDTO;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
class SmsNotificationServiceImpl implements SmsNotificationService {
	private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";
	private static final String PROFORMA_NOT_FOUND_MESSAGE = "پیش فاکتور با شناسه %d یافت نشد";
	private static final String CUSTOMER_NOT_FOUND_MESSAGE = "مشتری با شناسه %d یافت نشد";
	private final ProformaMasterRepository proformaMasterRepository;
	private final CustomerRepository customerRepository;
	private final INSOkHttpClient httpClient;
	private final MessageServiceProperties messageServiceProperties;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;

	@Override
	public SmsDTO.Response preFactorEmailedSMSNotification(Long proformaMasterId) throws IOException {

		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.PROFORMA)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		if (exportConfig.getSendSms() == false) return null;
		ProformaMasterModel ProformaMasterModel = proformaMasterRepository.findById(proformaMasterId).orElseThrow(() -> new InternalSaleCustomException.ValidationException(
				String.format(PROFORMA_NOT_FOUND_MESSAGE, proformaMasterId)));

		CustomerModel customerModel = customerRepository.findById(ProformaMasterModel.getCustomerId()).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
				String.format(CUSTOMER_NOT_FOUND_MESSAGE, ProformaMasterModel.getCustomerId())));

		SmsDTO.SMSServicePattern pattern = SmsDTO.SMSServicePattern
				.builderr()
				.pid(messageServiceProperties.getSms().getPattern().getPatterns().getPreFactorEmailed())
				.to(List.of(customerModel.getMobile()))
				.params(
						SmsDTO.SMSServicePattern.Params
								.builderr()
								.date_(ProformaMasterModel.getContractDate())
								.contractNumber(String.valueOf(ProformaMasterModel.getContractNo()))
								.companyName(customerModel.getName())
								.buildd())
				.buildd();
		SmsDTO.Response response = httpClient.post(messageServiceProperties.getSms().getPattern().getUrl().getNimad(),
				pattern, SmsDTO.Response.class);
		log.info("response of sending notification sms for preFactorEmailedSMSNotification response is :{}  ", response);
		return response;
	}
}
