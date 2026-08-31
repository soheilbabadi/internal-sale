package com.nicico.internal.sales.pms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicico.internal.sales.common.properties.PMSProperties;
import com.nicico.internal.sales.common.properties.RabbitConfigPMSProperties;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.goods.service.GoodsService;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.pms.dto.PMSPreFactorDto;
import com.nicico.internal.sales.pms.model.PMSCustomerModel;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PMSProformaServiceImpl implements PMSProformaService {

	private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";
	private static final String PROFORMA_NOT_FOUND_MESSAGE = "پیش فاکتور وجود ندارد";
	private final ProformaDetailRepository proformaDetailRepository;
	private final GoodsService goodsService;
	private final PMSProperties pmsProperties;
	private final PMSCustomerService pmsCustomerService;
	private final PMSPreFactorCacheService cacheService;
	private final RabbitConfigPMSProperties rabbitConfigPMSProperties;
	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;


	@Override
	public void validateForCreatePreFactorFromProformaMasterId(Long proformaMasterId) {

		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.PROFORMA)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;


		log.info("Validating pre-factor creation for ProformaMasterId: {}", proformaMasterId);
		// حالا از Service جداگانه استفاده می کنیم - Cache کار می کنه!
		List<ProformaGoodItemModel> goodItems = cacheService.getPerformaGoodItems(proformaMasterId);
		if (goodItems.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(
					"no proforma good items found for master id " + proformaMasterId);
		}
		// Validation برای هر آیتم
		for (ProformaGoodItemModel goodItem : goodItems) {
//            var performaDetailModel = proformaDetailRepository.findById(goodItem.getProformaDetailId()).get();
			var masterModel = proformaMasterRepository.findById(proformaMasterId)
					.orElseThrow(() -> new InternalSaleCustomException.ValidationException(PROFORMA_NOT_FOUND_MESSAGE));
			// بررسی وجود PMS Goods ID
			Long pmsGoodsId = goodsService.findPmsIdByGoodName(goodItem.getGoodName());
			if (pmsGoodsId == null) {
				throw new InternalSaleCustomException.ValidationException(
						"کالا در PMS یافت نشد: " + goodItem.getGoodName());
			}
			String nationalCode = masterModel.getNationalCode();
			CustomerModel customer = cacheService.getCustomerByNationalCode(nationalCode);
			cacheService.getPmsCustomerByEconomicCode(customer.getEconomicCode());
			// بررسی مقادیر
			if (getMeghdar(goodItem).compareTo(BigDecimal.ZERO) <= 0) {
				throw new InternalSaleCustomException.ValidationException(
						"مقدار کالا باید بزرگتر از صفر باشد");
			}
			if (getUnitPrice(goodItem).compareTo(BigDecimal.ZERO) <= 0) {
				throw new InternalSaleCustomException.ValidationException(
						"قیمت واحد باید بزرگتر از صفر باشد");
			}
		}
		log.info("Validation successful for ProformaMasterId: {}", proformaMasterId);
	}

	@Override
	@Transactional
	@CacheEvict(value = {"performaGoodItems", "customers", "pmsCustomers"}, allEntries = true)
	public void createPreFactorFromProformaMasterId(Long proformaMasterId, String userName, boolean resend) {


		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.PROFORMA)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;


		log.info("Creating pre-factor for ProformaGoodItem ID: {}", proformaMasterId);
		List<ProformaGoodItemModel> goodItems = cacheService.getPerformaGoodItems(proformaMasterId);
		if (goodItems.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(
					"no proforma good items found for master id " + proformaMasterId);
		}
		goodItems.forEach(goodItem -> {

			var performaDetailModel = proformaDetailRepository.findById(goodItem.getProformaDetailId())
					.orElseThrow(() -> new InternalSaleCustomException.ValidationException(PROFORMA_NOT_FOUND_MESSAGE));

			if (!resend && Objects.nonNull(performaDetailModel.getPmsId())) {
				log.info("Proforma detail id {} already sent to PMS with pms id {}, skipping resend",
						performaDetailModel.getId(), performaDetailModel.getPmsId());
				return;
			}

			Long pmsGoodsId = goodsService.findPmsIdByGoodName(goodItem.getGoodName());
			PMSPreFactorDto.Create createDto = PMSPreFactorDto.Create.lBuilder()
					.goodsId(pmsGoodsId)
					.customerId(extractCustomerId(goodItem))
					.meghdar(getMeghdar(goodItem))
					.priceUnit(getUnitPrice(goodItem).toString())
					.sodorDate(DateUtility.getJalaliDate(performaDetailModel.getPerformaDate()))
					.username(extractPmsUserName(userName))
					.letterNumber(performaDetailModel.getPerformaNo())
					.user(pmsProperties.getPreFactor().getUser())
					.pass(pmsProperties.getPreFactor().getPass())
					.lBuild();
			log.info("Calling PMS PreFactor API with data: {}", createDto);
			rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
					rabbitConfigPMSProperties.getQueues().getPreFactor().getRoutingKey(),
					new PMSPreFactorDto.RabbitListenerRequestDTO(pmsProperties.getPreFactor().getUrl(),
							goodItem.getProformaDetailId()
							, createDto,
							rabbitConfigPMSProperties.getQueues().getPreFactor().getResponseRoutingKey()));
		});
	}

	@RabbitListener(queues = "${rabbitmq.config.pms.queues.pre-factor.response-queue}")
	void savePmsPrefactorId(String message) throws JsonProcessingException {


		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.PROFORMA)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;


		log.info("Saving PMS Prefactor ID: {}", message);
		PMSPreFactorDto.RabbitListenerResponseDTO response = objectMapper.readValue(message,
				PMSPreFactorDto.RabbitListenerResponseDTO.class);
		Optional<ProformaDetailModel> detail = proformaDetailRepository.findById(response.getRequest().getId());
		if (detail.isPresent()) {
			ProformaDetailModel proformaDetailModel = detail.get();
			proformaDetailModel.setPmsId(response.getResponse().getId());
			proformaDetailRepository.save(proformaDetailModel);
		} else {
			log.error("Could not find detail for  PMS Prefactor ID: {}", message);
		}
	}

	private String extractCustomerId(ProformaGoodItemModel goodItem) {
		var performaDetailModel = proformaDetailRepository.findById(goodItem.getProformaDetailId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(PROFORMA_NOT_FOUND_MESSAGE));
		var masterModel = proformaMasterRepository.findById(performaDetailModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(PROFORMA_NOT_FOUND_MESSAGE));
		String nationalCode = masterModel.getNationalCode();
		CustomerModel customer = cacheService.getCustomerByNationalCode(nationalCode);
		PMSCustomerModel pmsCustomer = pmsCustomerService.findByEconomicCodeOrRegisterNumber(customer.getEconomicCode(),
				customer.getRegisterNumber() != null && !customer.getRegisterNumber().isEmpty() ? customer.getRegisterNumber() : customer.getNationalCode());
		return pmsCustomer.getId();


	}


	private String extractPmsUserName(String userName) {
		return Objects.nonNull(userName) ? userName :
				pmsProperties.getPreFactor().getDefaultPmsUser();
	}

	private BigDecimal getMeghdar(ProformaGoodItemModel goodItem) {
		if (Objects.nonNull(goodItem.getNetQuantity()) && goodItem.getNetQuantity().longValue() > 0) {
			return goodItem.getNetQuantity();
		}
		if (Objects.nonNull(goodItem.getCreditQuantity()) && goodItem.getCreditQuantity().longValue() > 0) {
			return goodItem.getCreditQuantity();
		}
		return goodItem.getQuantity();
	}

	private BigDecimal getUnitPrice(ProformaGoodItemModel goodItem) {
		var detailModel = proformaDetailRepository.findById(goodItem.getProformaDetailId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(PROFORMA_NOT_FOUND_MESSAGE));
		if (detailModel.getProformaIssueType().equals(ProformaIssueType.FROM_CREDIT_FACILITIES) &&
				Objects.nonNull(goodItem.getUnitPriceCash()) &&
				goodItem.getUnitPriceCash().longValue() > 0) {
			return goodItem.getUnitPriceCash();
		}
		return goodItem.getUnitPriceCredit();
	}

}