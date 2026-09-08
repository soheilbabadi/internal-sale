package com.nicico.internal.sales.pms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicico.internal.sales.common.properties.PMSProperties;
import com.nicico.internal.sales.common.properties.RabbitConfigPMSProperties;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.fms.service.FmsDocumentService;
import com.nicico.internal.sales.goods.model.PmsMappingModel;
import com.nicico.internal.sales.goods.repository.PmsMappingRepository;
import com.nicico.internal.sales.goods.service.GoodsService;
import com.nicico.internal.sales.ime.broker.model.IMEBrokerModel;
import com.nicico.internal.sales.ime.broker.repository.IMEBrokerRepository;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import com.nicico.internal.sales.ins.InsPmsImeMapping.InsPmsImeMappingModel;
import com.nicico.internal.sales.ins.InsPmsImeMapping.InsPmsImeMappingRepository;
import com.nicico.internal.sales.ins.InsPmsImeMapping.InsPmsImeMappingTypeEnum;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.loading.model.LoadingPlaceModel;
import com.nicico.internal.sales.loading.repository.LoadingPlaceRepository;
import com.nicico.internal.sales.notification.dto.EmailRequest;
import com.nicico.internal.sales.notification.service.MailService;
import com.nicico.internal.sales.pms.dto.PMSRemittanceDTO;
import com.nicico.internal.sales.pms.dto.PMSRemittanceMapper;
import com.nicico.internal.sales.pms.model.PMSCustomerModel;
import com.nicico.internal.sales.pms.model.PmsLcModel;
import com.nicico.internal.sales.pms.model.PmsRemmitanceModel;
import com.nicico.internal.sales.pms.repository.PMSCustomerRepository;
import com.nicico.internal.sales.pms.repository.PmsLcRepository;
import com.nicico.internal.sales.pms.repository.PmsRemittanceRepository;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PMSRemittanceServiceImpl implements PMSRemittanceService {
	private static final String MSG_CUSTOMER_NOT_FOUND = "اطلاعات مشتری پیدا نشد";
	private static final String MSG_FILE_WRITE_ERROR = "خطایی در هنگام نوشتن فایل اتفاق افتاد";
	private static final String MSG_FILE_EMPTY_LIST = "لیست فایلها خالی است و امکان ایجاد فایل پی دی اف وجود ندارد";
	private static final String MSG_PDF_EMPTY_RESPONSE = "فایل خالی از طرف سرویس تبدیل PDF دریافت شد";
	private static final String MSG_EMAIL_REMITTANCE_SUBJECT = "حواله قرارداد شماره ";
	private static final String MSG_EMAIL_REMITTANCE_CONTENT = "با سلام حواله شرکت {0} شماره قرارداد {1} مورخ {2} به پیوست ارسال میگردد.با تشکر";
	private static final String DOC_EXTENSION = ".doc";
	private static final String PDF_EXTENSION = ".pdf";
	private static final String FILE_NAME_PREFIX_REMITTANCE = "remittance_";
	private static final String MERGE_PARAM = "merge";
	private static final String FILES_PARAM = "files";
	private static final String TRUE = "true";
	private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";
	private final RemittanceMasterRepository remittanceMasterRepository;
	private final PMSCustomerRepository pmsCustomerRepository;
	private final GoodsService goodsService;
	private final PMSProperties pmsProperties;
	private final PMSRemittanceMapper pmsRemittanceMapper;
	private final PmsMappingRepository pmsMappingRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final IMETradeRepository imeTradeRepository;
	private final IMEBrokerRepository imeBrokerRepository;
	private final LcRepository lcRepository;
	private final PmsLcRepository pmsLcRepository;
	private final LoadingPlaceRepository loadingPlaceRepository;
	private final InsPmsImeMappingRepository insPmsImeMappingRepository;
	private final CustomerRepository customerRepository;
	private final RabbitTemplate rabbitTemplate;
	private final RabbitConfigPMSProperties rabbitConfigPMSProperties;
	private final ObjectMapper objectMapper;
	//	private final ExportDocService exportDocService;
	private final RestTemplate restTemplate;
	private final MailService mailService;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;
	private final PmsRemittanceRepository pmsRemittanceRepository;
	private final FmsDocumentService fmsDocumentService;
	@Value("${nicico.pdf-api}")
	private String pdfConvertorUrl;


	@Value("${nicico.bcc-address}")
	private String bccAddress;
	@Value("${nicico.lc-bcc-address}")
	private String lcBccAddress;


	@Override
	@Transactional(readOnly = true)
	public void create(Long remittanceId, String username, boolean resend) throws IOException {

		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.SALES_SLIP)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
		if (exportConfig.getSendPms() == false) return;

		if (Objects.isNull(username)) {
			username = pmsProperties.getPreFactor().getDefaultPmsUser();
		}
		RemittanceMasterModel remittance = remittanceMasterRepository.findById(remittanceId).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(String.format("No Remittance found for remittanceId: %s", remittanceId)));

		if (!resend && Objects.nonNull(remittance.getPmsId())) {
			log.info("Remittance id {} already sent to PMS with pms id {}, skipping resend",
					remittanceId, remittance.getPmsId());
			return;
		}

		LoadingPlaceModel loadingPlaceModel = loadingPlaceRepository.findById(remittance.getLoadingPortId()).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("PMS loading place not found for loading port id " + remittance.getLoadingPortId()));
		InsPmsImeMappingModel pmsCustomerBroker = null;
		IMETradeModel imeTradeModel = imeTradeRepository.findFirstByContractNoAndPaymentCodeOrderByIdDesc(Integer.parseInt(remittance.getContractNo().substring(0, 10)), remittance.getPaymentCode()).orElse(null);
		if (Objects.nonNull(imeTradeModel)) {
			IMEBrokerModel imeBrokerModel = imeBrokerRepository.findFirstByBrokerId(imeTradeModel.getBuyerBrokerCode()).orElse(null);
			if (Objects.nonNull(imeBrokerModel) && Objects.nonNull(imeBrokerModel.getNationalId())) {
				pmsCustomerBroker = insPmsImeMappingRepository.findFirstByImeIdAndMappingFor(Long.valueOf(imeTradeModel.getBuyerBrokerCode()), InsPmsImeMappingTypeEnum.BROKER).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("No broker found in InsPmsImeMappingModel table for broker national code " + imeBrokerModel.getNationalId()));
			}
		}
		CustomerModel customerModel = customerRepository.findByNationalCode(remittance.getNationalCode()).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MessageFormat.format("customer not found for national code {0}", remittance.getNationalCode())));
		Long pmsGoodsId = goodsService.findPmsIdByGoodName(remittance.getGoodName());
		PmsMappingModel pmsGoodUnit = pmsMappingRepository.findById(pmsGoodsId).orElse(null);
		String nationalCode = customerModel.getNationalCode();
		PMSCustomerModel pmsCustomerModel = pmsCustomerRepository.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(customerModel.getEconomicCode(), nationalCode).orElse(null);

		PMSRemittanceDTO request = PMSRemittanceDTO.builderr().name(extractRemittanceName(remittance, imeTradeModel)).issueDate(DateUtility.getJalaliDate(remittance.getRemittanceDate())).pmsBuyerId(Objects.nonNull(pmsCustomerModel) ? pmsCustomerModel.getId() : null).pmsGoodId(pmsGoodsId).pmsGoodUnit(Objects.nonNull(pmsGoodUnit) ? pmsGoodUnit.getPmsPackingCode() : null).amount(remittance.getRemittanceQuantity()).buildd();
		PMSRemittanceDTO.Nullables pmsRemittanceDTO = pmsRemittanceMapper.toNullablesDTO(request);
		PMSRemittanceDTO.Create create = pmsRemittanceMapper.toCreateDTO(pmsRemittanceDTO, pmsProperties.getPreFactor().getUser(), pmsProperties.getPreFactor().getPass(), username);
		create.setFinalDate(DateUtility.getJalaliDate(remittance.getValidityDate()));
		if (Objects.nonNull(remittance.getValidityDate()))
			create.setValidUntil(DateUtility.getJalaliDate(remittance.getValidityDate()));
		if (Objects.nonNull(remittance.getLotNumber()) && !remittance.getLotNumber().isEmpty()) {
			create.setLotNumber(remittance.getLotNumber());
		}
		if (Objects.nonNull(remittance.getCashPercentage()) && remittance.getCashPercentage().compareTo(BigDecimal.ZERO) > 0) {
			create.setCashPercent(remittance.getCashPercentage().intValue());
		}
		if (Objects.nonNull(remittance.getCreditPercentage()) && remittance.getCreditPercentage().compareTo(BigDecimal.ZERO) > 0) {
			create.setCreditPercent(remittance.getCreditPercentage().intValue());
		}
		if (Objects.nonNull(remittance.getRemittanceUnitPriceCash()) && remittance.getRemittanceUnitPriceCash().compareTo(BigDecimal.ZERO) > 0) {
			create.setCashUnitPrice(remittance.getRemittanceUnitPriceCash());
		}
		if (Objects.nonNull(remittance.getRemittanceUnitPriceCredit()) &&
				(Objects.nonNull(remittance.getCreditPercentage()) &&
						remittance.getCreditPercentage().compareTo(BigDecimal.ZERO) > 0)
				&& remittance.getRemittanceUnitPriceCredit().compareTo(BigDecimal.ZERO) > 0) {
			create.setCreditUnitPrice(remittance.getRemittanceUnitPriceCredit());
		}
		if (Objects.nonNull(remittance.getProformaNo()) && !remittance.getProformaNo().isEmpty()) {
			create.setPrefactorId(remittance.getProformaNo());
		}
		if (Objects.nonNull(pmsCustomerBroker)) {
			create.setBrokerId(pmsCustomerBroker.getPmsCode());
		}
		if (Objects.nonNull(remittance.getLcId()) && remittance.getId() > 0) {
			LcModel lcModel = lcRepository.findById(remittance.getLcId()).orElse(null);
			if (Objects.nonNull(lcModel) && Objects.nonNull(lcModel.getPmsLcId())) {
				PmsLcModel pmsLcModel = pmsLcRepository.findById(lcModel.getPmsLcId()).orElse(null);
				if (Objects.nonNull(pmsLcModel) && Objects.nonNull(pmsLcModel.getLcId())) {
					create.setLcId(pmsLcModel.getLcId());
				}
			}
		}

		create.setContractDate(DateUtility.getJalaliDate(remittance.getContractDate()));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(remittance.getContractDate());
		calendar.add(Calendar.MONTH, 3);
		create.setContractValidDate(DateUtility.getJalaliDate(calendar.getTime()));
		if (Objects.nonNull(loadingPlaceModel.getPmsLoadingId())) {
			create.setLoadId(loadingPlaceModel.getPmsLoadingId());
		}
		create.setContractCode(remittance.getContractNo());
		if (Objects.nonNull(remittance.getContractDate()))
			create.setBuyDate(DateUtility.getJalaliDate(remittance.getContractDate()));
		if (Objects.nonNull(remittance.getDescription()) && !remittance.getDescription().isBlank())
			create.setDescription(remittance.getDescription());
		if (Objects.nonNull(remittance.getProformaIssueType()))
			create.setStcProcess(mapProformaIssueTypeToPmsRemittanceGroupCode(remittance.getProformaIssueType()));

		log.info("Sending PMS remittance create request to PMS system - RemittanceId: {}, ContractNo: {}, URL: {}",
				remittanceId, remittance.getContractNo(), pmsProperties.getRemittance().getUrl());
		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
				rabbitConfigPMSProperties.getQueues().getHavaleh().getRoutingKey(),
				new PMSRemittanceDTO.RabbitListenerRequestDTO(
						pmsProperties.getRemittance().getUrl(),
						remittanceId, create, rabbitConfigPMSProperties.getQueues().getHavaleh().getResponseRoutingKey()
				));
	}

	@Override
	public void update(Long remittanceId, String username) throws IOException {

		if (Objects.isNull(username)) {
			username = pmsProperties.getPreFactor().getDefaultPmsUser();
		}
		RemittanceMasterModel remittance = remittanceMasterRepository.findById(remittanceId).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(String.format("No Remittance found for remittanceId: %s", remittanceId)));
		PmsRemmitanceModel pmsRemittance = pmsRemittanceRepository.findById(remittance.getPmsId()).orElse(null);
		LoadingPlaceModel loadingPlaceModel = loadingPlaceRepository.findById(remittance.getLoadingPortId()).orElseThrow(
				() -> new InternalSaleCustomException.ResourceNotFoundException(
						"PMS loading place not found for loading port id " + remittance.getLoadingPortId()));
		InsPmsImeMappingModel pmsCustomerBroker = null;
		IMETradeModel imeTradeModel = imeTradeRepository.findFirstByContractNoAndPaymentCodeOrderByIdDesc(Integer.parseInt(remittance.getContractNo().substring(0, 10)), remittance.getPaymentCode()).orElse(null);
		if (Objects.nonNull(imeTradeModel)) {
			IMEBrokerModel imeBrokerModel = imeBrokerRepository.findFirstByBrokerId(imeTradeModel.getBuyerBrokerCode()).orElse(null);
			if (Objects.nonNull(imeBrokerModel) && Objects.nonNull(imeBrokerModel.getNationalId())) {
				pmsCustomerBroker = insPmsImeMappingRepository.findFirstByImeIdAndMappingFor(Long.valueOf(imeTradeModel.getBuyerBrokerCode()), InsPmsImeMappingTypeEnum.BROKER).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("No broker found in InsPmsImeMappingModel table for broker national code " + imeBrokerModel.getNationalId()));
			}
		}
		CustomerModel customerModel = customerRepository.findByNationalCode(remittance.getNationalCode()).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MessageFormat.format("customer not found for national code {0}", remittance.getNationalCode())));
		Long pmsGoodsId = goodsService.findPmsIdByGoodName(remittance.getGoodName());
		PmsMappingModel pmsGoodUnit = pmsMappingRepository.findById(pmsGoodsId).orElse(null);
		String nationalCode = customerModel.getNationalCode();
		PMSCustomerModel pmsCustomerModel = pmsCustomerRepository.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(customerModel.getEconomicCode(), nationalCode).orElse(null);

		PMSRemittanceDTO request = PMSRemittanceDTO.builderr().name(extractRemittanceName(remittance, imeTradeModel)).issueDate(DateUtility.getJalaliDate(remittance.getRemittanceDate())).pmsBuyerId(Objects.nonNull(pmsCustomerModel) ? pmsCustomerModel.getId() : null).pmsGoodId(pmsGoodsId).pmsGoodUnit(Objects.nonNull(pmsGoodUnit) ? pmsGoodUnit.getPmsPackingCode() : null).amount(remittance.getRemittanceQuantity()).buildd();
		PMSRemittanceDTO.Nullables pmsRemittanceDTO = pmsRemittanceMapper.toNullablesDTO(request);
		PMSRemittanceDTO.Update create = pmsRemittanceMapper.toUpdateDTO(pmsRemittanceDTO,
				pmsProperties.getPreFactor().getUser(), pmsProperties.getPreFactor().getPass(), username);
		create.setId(remittance.getPmsId());
		create.setFinalDate(DateUtility.getJalaliDate(remittance.getValidityDate()));
		if (Objects.nonNull(remittance.getValidityDate()))
			create.setValidUntil(DateUtility.getJalaliDate(remittance.getValidityDate()));
		if (Objects.nonNull(remittance.getLotNumber()) && !remittance.getLotNumber().isEmpty()) {
			create.setLotNumber(remittance.getLotNumber());
		}
		if (Objects.nonNull(remittance.getCashPercentage()) && remittance.getCashPercentage().compareTo(BigDecimal.ZERO) > 0) {
			create.setCashPercent(remittance.getCashPercentage().intValue());
		}
		if (Objects.nonNull(remittance.getCreditPercentage()) && remittance.getCreditPercentage().compareTo(BigDecimal.ZERO) > 0) {
			create.setCreditPercent(remittance.getCreditPercentage().intValue());
		}
		if (Objects.nonNull(remittance.getRemittanceUnitPriceCash()) && remittance.getRemittanceUnitPriceCash().compareTo(BigDecimal.ZERO) > 0) {
			create.setCashUnitPrice(remittance.getRemittanceUnitPriceCash());
		}
		if (Objects.nonNull(remittance.getRemittanceUnitPriceCredit()) &&
				(Objects.nonNull(remittance.getCreditPercentage()) &&
						remittance.getCreditPercentage().compareTo(BigDecimal.ZERO) > 0)
				&& remittance.getRemittanceUnitPriceCredit().compareTo(BigDecimal.ZERO) > 0) {
			create.setCreditUnitPrice(remittance.getRemittanceUnitPriceCredit());
		}
		if (Objects.nonNull(remittance.getProformaNo()) && !remittance.getProformaNo().isEmpty()) {
			create.setPrefactorId(remittance.getProformaNo());
		}
		if (Objects.nonNull(pmsCustomerBroker)) {
			create.setBrokerId(pmsCustomerBroker.getPmsCode());
		}
		if (Objects.nonNull(remittance.getLcId()) && remittance.getId() > 0) {
			LcModel lcModel = lcRepository.findById(remittance.getLcId()).orElse(null);
			if (Objects.nonNull(lcModel) && Objects.nonNull(lcModel.getPmsLcId())) {
				PmsLcModel pmsLcModel = pmsLcRepository.findById(lcModel.getPmsLcId()).orElse(null);
				if (Objects.nonNull(pmsLcModel) && Objects.nonNull(pmsLcModel.getLcId())) {
					create.setLcId(pmsLcModel.getLcId());
				}
			}
		}

		create.setContractDate(DateUtility.getJalaliDate(remittance.getContractDate()));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(remittance.getContractDate());
		calendar.add(Calendar.MONTH, 3);
		create.setContractValidDate(DateUtility.getJalaliDate(calendar.getTime()));
		if (Objects.nonNull(loadingPlaceModel.getPmsLoadingId())) {
			create.setLoadId(loadingPlaceModel.getPmsLoadingId());
		}
		create.setContractCode(remittance.getContractNo());
		if (Objects.nonNull(remittance.getContractDate()))
			create.setBuyDate(DateUtility.getJalaliDate(remittance.getContractDate()));
		if (Objects.nonNull(remittance.getDescription()) && !remittance.getDescription().isBlank())
			create.setDescription(remittance.getDescription());
		if (Objects.nonNull(remittance.getProformaIssueType()))
			create.setStcProcess(mapProformaIssueTypeToPmsRemittanceGroupCode(remittance.getProformaIssueType()));

		log.info("Sending PMS remittance update request to PMS system - RemittanceId: {}, PmsId: {}, URL: {}",
				remittanceId, remittance.getPmsId(), pmsProperties.getRemittance().getUrl());
		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
				rabbitConfigPMSProperties.getQueues().getHavaleh().getUpdateRoutingKey(),
				new PMSRemittanceDTO.RabbitListenerRequestDTO(
						pmsProperties.getRemittance().getUrl(),
						remittanceId, create,
						rabbitConfigPMSProperties.getQueues().getHavaleh().getUpdateResponseRoutingKey()
				));
	}

	@Override
	public void pmsRemittanceValidationErrorList(List<String> errors, String customerEconomicCode, String customerNationalCode, String goodsName, String issueDate, BigDecimal amount) {
		try {
			Optional<PMSCustomerModel> pmsCustomerModel = pmsCustomerRepository.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(customerEconomicCode, customerNationalCode);
			if (pmsCustomerModel.isEmpty()) {
				errors.add(MessageFormat.format("no customer found in pms for customerEconomicCode {0}  customerNationalCode {1}", customerEconomicCode, customerNationalCode));
			}
		} catch (Exception exception) {
			throw new InternalSaleCustomException.ValidationException("امکان ثبت در سیستم لجستیک وجود ندارد", List.of(exception.getMessage()));
		}


		if (goodsName == null) {
			errors.add("goodsName can not be null");
		}
		Long pmsGoodsId = goodsService.findPmsIdByGoodName(goodsName);
		if (pmsMappingRepository.findById(pmsGoodsId).isEmpty()) {
			errors.add(MessageFormat.format("please add pms goods unit id in T_INS_PMS_IME_MAPPING for goods {0} with goods id {1}", goodsName, pmsGoodsId));
		}
		if (issueDate == null) {
			errors.add("pms issueDate can not be null");
		}
		if (amount == null) {
			errors.add("pms amount can not be null");
		}
		if (!errors.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException("امکان ثبت در سیستم لجستیک وجود ندارد", errors);
		}

	}

	private Integer mapProformaIssueTypeToPmsRemittanceGroupCode(ProformaIssueType proformaIssueType) {
		switch (proformaIssueType) {
			case CASH -> {
				return 1;
			}
			case FROM_CREDIT_FACILITIES -> {
				return 529;
			}
			case LETTER_OF_CREDIT_OPENING -> {
				return 481;
			}
			case GUARANTEE_CHECK -> {
				return 533;
			}
			case GAM_BONDS -> {
				return 542;
			}
			default ->
					throw new InternalSaleCustomException.ResourceNotFoundException("no pms remittance group code found for  " + proformaIssueType.getValue());

		}
	}

	private String extractRemittanceName(RemittanceMasterModel remittanceMasterModel, IMETradeModel tradeModel) {
		ProformaMasterModel proformaMasterModel = proformaMasterRepository.findById(remittanceMasterModel.getProformaMasterId()).orElse(null);

		String name = pmsProperties.getRemittance().getTitleBase();
		String weightInTon = "0";
		if (Objects.isNull(proformaMasterModel)) {
			if (Objects.nonNull(tradeModel)) {
				weightInTon = String.valueOf(tradeModel.getUnitCount());
			}
		} else if (proformaMasterModel.getProformaIssueType().equals(ProformaIssueType.FROM_CREDIT_FACILITIES)) {
			weightInTon = String.valueOf(proformaMasterModel.getTotalQuantity().multiply(new BigDecimal("1000")).setScale(4, RoundingMode.DOWN));
		} else if (proformaMasterModel.getProformaIssueType().equals(ProformaIssueType.LETTER_OF_CREDIT_OPENING)) {
			weightInTon = String.valueOf(remittanceMasterModel.getRemittanceQuantity().multiply(new BigDecimal("1000")).setScale(4, RoundingMode.DOWN));
		}
		return name + weightInTon + " " + tradeModel.getCommodityPersianName();
	}

	@RabbitListener(queues = "${rabbitmq.config.pms.queues.havaleh.response-queue}")
	void savePmsRemittanceRabbitListener(String message) throws IOException {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.SALES_SLIP)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
		if (exportConfig.getSendPms() == false) return;

		log.info("Received PMS remittance create response from queue: {}", message);
		PMSRemittanceDTO.RabbitListenerResponseDTO req = objectMapper.readValue(message,
				PMSRemittanceDTO.RabbitListenerResponseDTO.class);
		Optional<RemittanceMasterModel> remittance = remittanceMasterRepository.findById(req.getRequest().getId());
		if (remittance.isPresent()) {
			RemittanceMasterModel remittanceMasterModel = remittance.get();
			remittanceMasterModel.setPmsId(req.getResponse().getCode());
			remittanceMasterModel.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
			remittanceMasterModel.setProcessFinal(true);
			remittanceMasterModel.setRemittanceDate(new Date());
			remittanceMasterRepository.save(remittanceMasterModel);
			log.info("Remittance registered in PMS - RemittanceId: {}, PmsId: {}", remittanceMasterModel.getId(), remittanceMasterModel.getPmsId());

			CustomerModel customer = customerRepository.findById(remittanceMasterModel.getCustomerId())
					.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
							MSG_CUSTOMER_NOT_FOUND));
			byte[] pdfContent = fmsDocumentService.getRemittancePdfBytes (remittanceMasterModel.getId());
			Path filePath = createTempFile(String.valueOf(remittanceMasterModel.getContractNo()), pdfContent);
			EmailRequest emailRequest = prepareRemittanceEmailRequest(remittanceMasterModel, customer);
			mailService.sendMail(emailRequest, filePath.toString());
		} else {
			log.error("Could not find remittance for PMS remittance ID: {}", message);
		}
	}

	@RabbitListener(queues = "${rabbitmq.config.pms.queues.havaleh.update-response-queue}")
	void updatePmsRemittanceRabbitListener(String message) throws IOException {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.SALES_SLIP)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
		if (exportConfig.getSendPms() == false) return;

		log.info("Received PMS remittance update response from queue: {}", message);
		PMSRemittanceDTO.RabbitListenerResponseDTO req = objectMapper.readValue(message,
				PMSRemittanceDTO.RabbitListenerResponseDTO.class);
		if (req.getRequest() == null || req.getRequest().getId() == null) {
			log.warn("Ignoring malformed PMS remittance update response without request id: {}", message);
			return;
		}
		Optional<RemittanceMasterModel> remittance = remittanceMasterRepository.findById(req.getRequest().getId());
		if (remittance.isPresent()) {
			RemittanceMasterModel remittanceMasterModel = remittance.get();
			if (req.getResponse() == null || req.getResponse().getCode() == null) {
				log.warn("PMS remittance update response had empty code for remittance id {}", req.getRequest().getId());
				return;
			}
			remittanceMasterModel.setPmsId(req.getResponse().getCode());
			remittanceMasterRepository.save(remittanceMasterModel);
			log.info("PMS remittance id updated from response for remittance id {}", remittanceMasterModel.getId());
		} else {
			log.error("Could not find remittance for update response PMS remittance id: {}", message);
		}
	}

	private Path createTempFile(String contractNo, byte[] content) throws IOException {
		String fileName = FILE_NAME_PREFIX_REMITTANCE + contractNo + PDF_EXTENSION;
		Path filePath = Paths.get(fileName);
		try (OutputStream outputStream = Files.newOutputStream(filePath)) {
			outputStream.write(content);
		}
		return filePath;
	}


	private EmailRequest prepareRemittanceEmailRequest(RemittanceMasterModel masterModel, CustomerModel customer) {
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setSubject(MSG_EMAIL_REMITTANCE_SUBJECT + " : " + masterModel.getContractNo());
		emailRequest.setBccRecipients(bccAddress);
		emailRequest.setToRecipients(customer.getEmail());
		emailRequest.setContent(generateRemittanceEmailContent(masterModel));
		return emailRequest;
	}

	private String generateRemittanceEmailContent(RemittanceMasterModel masterModel) {
		return MessageFormat.format(MSG_EMAIL_REMITTANCE_CONTENT,
				masterModel.getCustomerName(),
				masterModel.getContractNo(),
				masterModel.getContractDate());
	}

}