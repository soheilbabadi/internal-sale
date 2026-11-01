package com.nicico.internal.sales.pms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicico.internal.sales.common.properties.PMSProperties;
import com.nicico.internal.sales.common.properties.RabbitConfigPMSProperties;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.customer.dto.CustomerDTO;
import com.nicico.internal.sales.customer.dto.CustomerMapper;
import com.nicico.internal.sales.customer.model.CustomerModel;
import com.nicico.internal.sales.customer.repository.CustomerRepository;
import com.nicico.internal.sales.pms.dto.PMSCreateCustomerDto;
import com.nicico.internal.sales.pms.model.PMSCustomerModel;
import com.nicico.internal.sales.pms.repository.PMSCustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class PmsCustomerCreateRabbitServiceImpl implements PmsCustomerCreateRabbitService {
	private final RabbitTemplate rabbitTemplate;
	private final RabbitConfigPMSProperties rabbitConfigPMSProperties;
	private final PMSProperties pmsProperties;
	private final CustomerRepository customerRepository;
	private final PMSCustomerRepository pmsCustomerRepository;
	private final ObjectMapper objectMapper;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;
	private final CustomerMapper customerMapper;

	private boolean isPmsExportEnabled() {
		return exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.CUSTOMER)
				.map(config -> Boolean.TRUE.equals(config.getSendPms()))
				.orElse(false);
	}

	/**
	 * Single entry point for provisioning a customer in PMS.
	 * <p>
	 * Loads the row by internal id, builds the Rabbit payload and publishes it. The two former
	 * overloads (one taking the Rabbit payload, one taking {@code CustomerModel}) were a
	 * pass-through and a pure builder that shared this name; they are now private methods whose
	 * names state what they actually do.
	 */
	@Override
	public void createCustomer(Long customerId) {
		if (!isPmsExportEnabled()) return;

		CustomerModel customer = customerRepository.findById(customerId).orElseThrow(
				() -> new InternalSaleCustomException.ResourceNotFoundException("Customer not found for id " + customerId));

		sendToPms(buildRequestDTO(customer));
	}

	private PMSCreateCustomerDto.RabbitListenerRequestDTO buildRequestDTO(CustomerModel customer) {
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
		return requestDTO;
	}

	private void sendToPms(PMSCreateCustomerDto.RabbitListenerRequestDTO request) {
		CustomerModel customerModel = customerRepository.findByNationalCode(request.getRequest().getNationalCode())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						"Customer not found for national code " + request.getRequest().getNationalCode()));

		// Only economicCode is used as the identity key here. registerNumber is not a reliable
		// identifier: CustomerServiceImpl fills it with RandomUtils.nextInt(...) when absent,
		// TradeExtractServiceImpl overloads it with the buyer national code, and
		// ProformaValidationServiceImpl requires it to equal economicCode. Matching on it can
		// therefore produce a false positive - and a false positive silently drops the send.
		Optional<PMSCustomerModel> exists = pmsCustomerRepository.findFirstByEconomicCodeContainingOrderByIdDesc(
				customerModel.getEconomicCode()
		);
		if (exists.isPresent()) {
			// Idempotent skip, not an error: this customer is already provisioned in PMS.
			// Throwing here would requeue the message forever and would also make the
			// scheduled re-send job fail on every run for the same row.
			log.info("Customer {} already exists in PMS (pms code {}); skipping send",
					customerModel.getId(), exists.get().getId());
			return;
		}

		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
				rabbitConfigPMSProperties.getQueues().getCustomer().getRoutingKey(),
				request
		);
	}

	/**
	 * Compensation path (mirrors PMSLcServiceImpl#findRemittanceLcWithoutPmsId).
	 * <p>
	 * The response listener swallows failures on purpose so a bad message cannot be requeued
	 * forever; that means a lost PMS response used to leave the customer without a pmsCustomerCode
	 * permanently. This job finds exactly those rows and re-publishes them.
	 * <p>
	 * Two guards keep it safe to run repeatedly:
	 *  - the query only returns rows with C_PMS_CUSTOMER_CODE IS NULL, so already-linked customers
	 *    are never touched;
	 *  - sendToPms skips rows that already exist in PMS, so no duplicate is
	 *    created on the PMS side.
	 * Each row has its own try/catch: one failing customer cannot abort the whole batch.
	 */
	@Override
	public List<CustomerDTO.Info> sendCustomersWithoutPmsCode() {
		List<CustomerModel> customers = customerRepository.findCustomersWithoutPmsCode();
		log.info("Found {} customers without PMS code for re-send", customers.size());
		for (CustomerModel customer : customers) {
			try {
				createCustomer(customer.getId());
			} catch (Exception ex) {
				log.error("خطا در ارسال مجدد مشتری {} به PMS", customer.getId(), ex);
			}
		}
		return customers.stream()
				.map(customerMapper::toDTO)
				.toList();
	}

	@RabbitListener(queues = "${rabbitmq.config.pms.queues.customer.response-queue}")
	public void handlePmsCustomerResponse(String message) {
		log.info("Saving PMS customer code: {}", message);
		try {
			PMSCreateCustomerDto.RabbitListenerResponseDTO responseDTO =
					objectMapper.readValue(message, PMSCreateCustomerDto.RabbitListenerResponseDTO.class);

			if (responseDTO.getRequest() == null || responseDTO.getRequest().getId() == null) {
				log.warn("Ignoring malformed PMS customer response without request id: {}", message);
				return;
			}

			Long customerId = responseDTO.getRequest().getId();
			String pmsCustomerCode = responseDTO.getResponse() != null ? responseDTO.getResponse().getId() : null;
			if (pmsCustomerCode == null) {
				log.warn("PMS customer response had empty PMS code for customer id {}", customerId);
				return;
			}

			Optional<CustomerModel> customer = customerRepository.findById(customerId);
			if (customer.isEmpty()) {
				log.error("Could not find customer for id {} while saving PMS customer code {}", pmsCustomerCode, customerId);
				return;
			}

			CustomerModel customerModel = customer.get();
			if (pmsCustomerCode.equals(customerModel.getPmsCustomerCode())) {
				log.info("PMS customer code {} already saved for customer id {}, skipping", pmsCustomerCode, customerId);
				return;
			}

			customerModel.setPmsCustomerCode(pmsCustomerCode);
			customerRepository.save(customerModel);
			log.info("PMS customer code {} saved for customer id {}", pmsCustomerCode, customerId);

			pmsCustomerRepository.updatePmsCustomerMaterializedView();
		} catch (Exception ex) {
			log.error("خطا در ذخیره کد PMS مشتری از پیام پاسخ", ex);
		}
	}
}
