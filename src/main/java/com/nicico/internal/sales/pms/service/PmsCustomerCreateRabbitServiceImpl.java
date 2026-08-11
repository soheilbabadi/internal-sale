package com.nicico.internal.sales.pms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicico.internal.sales.common.properties.PMSProperties;
import com.nicico.internal.sales.common.properties.RabbitConfigPMSProperties;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.pms.dto.PMSCreateCustomerDto;
import com.nicico.internal.sales.pms.model.PMSCustomerModel;
import com.nicico.internal.sales.pms.repository.PMSCustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PmsCustomerCreateRabbitServiceImpl implements PmsCustomerCreateRabbitService {
	private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";
	private final RabbitTemplate rabbitTemplate;
	private final RabbitConfigPMSProperties rabbitConfigPMSProperties;
	private final PMSProperties pmsProperties;
	private final CustomerRepository customerRepository;
	private final PMSCustomerRepository pmsCustomerRepository;
	private final ObjectMapper objectMapper;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;

	@Override
	public void createCustomer(PMSCreateCustomerDto.RabbitListenerRequestDTO customer) {

		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.CUSTOMER)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		if (exportConfig.getSendPms() == false) return;

		var customerModel = customerRepository.findByNationalCode(customer.getRequest().getNationalCode());


//
//        return pmsCustomerRepository.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(customer.getRequest().get,nationalCode)
//                .orElseThrow(() -> new InternalSaleCustomException.ValidationException("مشتری با شناسه " + goodItem.getPerformaDetailModel()
//                        .getProformaMasterModel().getNationalCode() + " وجود ندارد")).getId();


		Optional<PMSCustomerModel> exists = pmsCustomerRepository.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(
				customerModel.get().getEconomicCode(), customerModel.get().getRegisterNumber()
		);
		if (exists.isPresent()) {
			throw new InternalSaleCustomException.ValidationException("Customer already exists in pms");
		}

		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
				rabbitConfigPMSProperties.getQueues().getCustomer().getRoutingKey(),
				customer
		);
	}

	@Override
	public void createCustomer(CustomerModel customer) {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.CUSTOMER)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		if (exportConfig.getSendPms() == false) return;

		PMSCreateCustomerDto.RabbitListenerRequestDTO requestDTO = PMSCreateCustomerDto.RabbitListenerRequestDTO.builderr().id(customer.getId())
				.request(PMSCreateCustomerDto.Create.builderr()
						.name(customer.getName())
						.phone(customer.getPhone())
						.address(customer.getAddress())
						.economyNumber(customer.getEconomicCode())
						.nationalCode(customer.getNationalCode())
						.postalCode(customer.getPostCode())
						.description(String.format("EconomicCode:%s --- NationalCode:%s ---  RegisterNumber:%s ",
								customer.getEconomicCode(),
								customer.getNationalCode(),
								customer.getRegisterNumber()))
						.user(pmsProperties.getPreFactor().getUser())
						.pass(pmsProperties.getPreFactor().getPass())
						.buildd()
				)
				.responseRoutingKey(rabbitConfigPMSProperties.getQueues().getCustomer().getResponseRoutingKey())
				.url(pmsProperties.getCustomer().getUrl()).buildd();
		if (customer.getDescription() != null && !customer.getDescription().isBlank())
			requestDTO.getRequest().setDescription(String.format("%s ------ %s", requestDTO.getRequest().getDescription(),
					customer.getDescription()));
		createCustomer(requestDTO);
	}

	@Override
	public void createCustomer(Long customerId) {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.CUSTOMER)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
		if (exportConfig.getSendPms() == false) return;
		createCustomer(customerRepository.findById(customerId).orElseThrow(
				() -> new InternalSaleCustomException.ResourceNotFoundException("customer not found")));
	}

	@RabbitListener(queues = "${rabbitmq.config.pms.queues.customer.response-queue}")
	private void pmsCreated(String message) throws JsonProcessingException {
		PMSCreateCustomerDto.RabbitListenerResponseDTO responseDTO = objectMapper.readValue(message, PMSCreateCustomerDto.RabbitListenerResponseDTO.class);
		CustomerModel customerModel = customerRepository.findById(responseDTO.getRequest().getId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(""));
		customerModel.setPmsCustomerCode(responseDTO.getResponse().getId());
		customerRepository.save(customerModel);
		pmsCustomerRepository.updatePmsCustomerMaterializedView();
	}
}
