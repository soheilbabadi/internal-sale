package com.nicico.internal.sales.pms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.common.properties.PMSProperties;
import com.nicico.internal.sales.common.properties.RabbitConfigPMSProperties;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.export.service.ExportDocService;
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
import com.nicico.internal.sales.notification.dto.MultipartInputStreamFileResource;
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
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
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
	private final ExportDocService exportDocService;
	private final RestTemplate restTemplate;
	private final MailService mailService;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;
	private final PmsRemittanceRepository pmsRemittanceRepository;
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
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;


		log.info("User {} - {} - {} asked for send remittance to pms for remittance id : {}",
				SecurityUtil.getUsername(),
				SecurityUtil.getNationalCode(),
				SecurityUtil.getFullName(),
				remittanceId
		);

		if (Objects.isNull(username)) {
			username = pmsProperties.getPreFactor().getDefaultPmsUser();
			log.debug("Username was null, setting default username: {}", username);
		}
		log.debug("Fetching remittance from database for remittanceId: {}", remittanceId);
		RemittanceMasterModel remittance = remittanceMasterRepository.findById(remittanceId).orElseThrow(() -> new InternalSaleCustomException.ValidationException(String.format("No Remittance found for remittanceId: %s", remittanceId)));

		if (!resend && Objects.nonNull(remittance.getPmsId())) {
			log.info("Remittance id {} already sent to PMS with pms id {}, skipping resend",
					remittanceId, remittance.getPmsId());
			return;
		}

		log.info("Remittance fetched successfully - ContractNo: {}, PaymentCode: {}, GoodName: {}", remittance.getContractNo(), remittance.getPaymentCode(), remittance.getGoodName());
		log.debug("Fetching loading place for loadingPortId: {}", remittance.getLoadingPortId());
		LoadingPlaceModel loadingPlaceModel = loadingPlaceRepository.findById(remittance.getLoadingPortId()).orElseThrow(() -> new InternalSaleCustomException.ValidationException("PMS loading place not found for loading port id " + remittance.getLoadingPortId()));
		log.debug("Loading place fetched - PmsLoadingId: {}", loadingPlaceModel.getPmsLoadingId());
		InsPmsImeMappingModel pmsCustomerBroker = null;
		log.debug("Searching for IME trade - ContractNo: {}, PaymentCode: {}", remittance.getContractNo().substring(0, 10), remittance.getPaymentCode());
		IMETradeModel imeTradeModel = imeTradeRepository.findFirstByContractNoAndPaymentCodeOrderByIdDesc(Integer.parseInt(remittance.getContractNo().substring(0, 10)), remittance.getPaymentCode()).orElse(null);
		if (Objects.nonNull(imeTradeModel)) {
			log.info("IME trade found - TradeId: {}, BuyerBrokerCode: {}", imeTradeModel.getId(), imeTradeModel.getBuyerBrokerCode());
			log.debug("Fetching broker information for brokerId: {}", imeTradeModel.getBuyerBrokerCode());
			IMEBrokerModel imeBrokerModel = imeBrokerRepository.findFirstByBrokerId(imeTradeModel.getBuyerBrokerCode()).orElse(null);
			if (Objects.nonNull(imeBrokerModel) && Objects.nonNull(imeBrokerModel.getNationalId())) {
				log.debug("Broker found - NationalId: {}, searching for PMS mapping", imeBrokerModel.getNationalId());
				pmsCustomerBroker = insPmsImeMappingRepository.findFirstByImeIdAndMappingFor(Long.valueOf(imeTradeModel.getBuyerBrokerCode()), InsPmsImeMappingTypeEnum.BROKER).orElseThrow(() -> new InternalSaleCustomException.ValidationException("No broker found in InsPmsImeMappingModel table for broker national code " + imeBrokerModel.getNationalId()));
				log.info("PMS customer broker mapping found - PmsCode: {}", pmsCustomerBroker.getPmsCode());
			} else {
				log.warn("Broker not found or has no national ID for brokerId: {}", imeTradeModel.getBuyerBrokerCode());
			}
		} else {
			log.warn("IME trade not found for ContractNo: {}, PaymentCode: {}", remittance.getContractNo().substring(0, 10), remittance.getPaymentCode());
		}
		CustomerModel customerModel = customerRepository.findByNationalCode(remittance.getNationalCode()).orElseThrow(() -> new InternalSaleCustomException.ValidationException(MessageFormat.format("customer not found for national code {0}", remittance.getNationalCode())));
		log.debug("Finding PMS goods ID for goodName: {}", remittance.getGoodName());
		Long pmsGoodsId = goodsService.findPmsIdByGoodName(remittance.getGoodName());
		log.debug("PMS goods ID found: {}", pmsGoodsId);
		PmsMappingModel pmsGoodUnit = pmsMappingRepository.findById(pmsGoodsId).orElse(null);
		if (Objects.nonNull(pmsGoodUnit)) {
			log.debug("PMS good unit mapping found - PackingCode: {}", pmsGoodUnit.getPmsPackingCode());
		} else {
			log.warn("PMS good unit mapping not found for pmsGoodsId: {}", pmsGoodsId);
		}
		String nationalCode = customerModel.getNationalCode();
		log.debug("Searching for PMS customer with nationalCode: {}", nationalCode);
		PMSCustomerModel pmsCustomerModel = pmsCustomerRepository.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(customerModel.getEconomicCode(), nationalCode).orElse(null);
		if (Objects.nonNull(pmsCustomerModel)) {
			log.info("PMS customer found - CustomerId: {}, RegisterNumber: {}", pmsCustomerModel.getId(), nationalCode);
		} else {
			log.warn("PMS customer not found for nationalCode: {}", nationalCode);
		}
		log.debug("Building PMS remittance request DTO");
		PMSRemittanceDTO request = PMSRemittanceDTO.builderr().name(extractRemittanceName(remittance, imeTradeModel)).issueDate(DateUtility.getJalaliDate(remittance.getRemittanceDate())).pmsBuyerId(Objects.nonNull(pmsCustomerModel) ? pmsCustomerModel.getId() : null).pmsGoodId(pmsGoodsId).pmsGoodUnit(Objects.nonNull(pmsGoodUnit) ? pmsGoodUnit.getPmsPackingCode() : null).amount(remittance.getRemittanceQuantity()).buildd();
		log.debug("Request DTO built - Name: {}, Amount: {}", request.getName(), request.getAmount());
		PMSRemittanceDTO.Nullables pmsRemittanceDTO = pmsRemittanceMapper.toNullablesDTO(request);
		PMSRemittanceDTO.Create create = pmsRemittanceMapper.toCreateDTO(pmsRemittanceDTO, pmsProperties.getPreFactor().getUser(), pmsProperties.getPreFactor().getPass(), username);
		create.setFinalDate(DateUtility.getJalaliDate(remittance.getValidityDate()));
		log.debug("Contract valid date set: {}", create.getContractValidDate());
		if (Objects.nonNull(remittance.getValidityDate()))
			create.setValidUntil(DateUtility.getJalaliDate(remittance.getValidityDate()));
		if (Objects.nonNull(remittance.getLotNumber()) && !remittance.getLotNumber().isEmpty()) {
			create.setLotNumber(remittance.getLotNumber());
			log.debug("Lot number set: {}", remittance.getLotNumber());
		}
		if (Objects.nonNull(remittance.getCashPercentage()) && remittance.getCashPercentage().compareTo(BigDecimal.ZERO) > 0) {
			create.setCashPercent(remittance.getCashPercentage().intValue());
			log.debug("Cash percentage set: {}", remittance.getCashPercentage());
		}
		if (Objects.nonNull(remittance.getCreditPercentage()) && remittance.getCreditPercentage().compareTo(BigDecimal.ZERO) > 0) {
			create.setCreditPercent(remittance.getCreditPercentage().intValue());
			log.debug("Credit percentage set: {}", remittance.getCreditPercentage());
		}
		if (Objects.nonNull(remittance.getRemittanceUnitPriceCash()) && remittance.getRemittanceUnitPriceCash().compareTo(BigDecimal.ZERO) > 0) {
			create.setCashUnitPrice(remittance.getRemittanceUnitPriceCash());
			log.debug("Cash unit price set: {}", remittance.getRemittanceUnitPriceCash());
		}
		if (Objects.nonNull(remittance.getRemittanceUnitPriceCredit()) &&
				(Objects.nonNull(remittance.getCreditPercentage()) &&
						remittance.getCreditPercentage().compareTo(BigDecimal.ZERO) > 0)
				&& remittance.getRemittanceUnitPriceCredit().compareTo(BigDecimal.ZERO) > 0) {
			create.setCreditUnitPrice(remittance.getRemittanceUnitPriceCredit());
			log.debug("Credit unit price set: {}", remittance.getRemittanceUnitPriceCredit());
		}
		if (Objects.nonNull(remittance.getProformaNo()) && !remittance.getProformaNo().isEmpty()) {
			create.setPrefactorId(remittance.getProformaNo());
			log.debug("Proforma number set: {}", remittance.getProformaNo());
		}
		if (Objects.nonNull(pmsCustomerBroker)) {
			create.setBrokerId(pmsCustomerBroker.getPmsCode());
			log.debug("Broker ID set: {}", pmsCustomerBroker.getPmsCode());
		}
		if (Objects.nonNull(remittance.getLcId()) && remittance.getId() > 0) {
			log.debug("Processing LC information for lcId: {}", remittance.getLcId());
			LcModel lcModel = lcRepository.findById(remittance.getLcId()).orElse(null);
			if (Objects.nonNull(lcModel) && Objects.nonNull(lcModel.getPmsLcId())) {
				log.debug("LC model found - PmsLcId: {}", lcModel.getPmsLcId());
				PmsLcModel pmsLcModel = pmsLcRepository.findById(lcModel.getPmsLcId()).orElse(null);
				if (Objects.nonNull(pmsLcModel) && Objects.nonNull(pmsLcModel.getLcId())) {
					create.setLcId(pmsLcModel.getLcId());
					log.info("PMS LC ID set: {}", pmsLcModel.getLcId());
				} else {
					log.warn("PMS LC model not found or has no LC ID for pmsLcId: {}", lcModel.getPmsLcId());
				}
			} else {
				log.warn("LC model not found or has no PMS LC ID for lcId: {}", remittance.getLcId());
			}
		}

		create.setContractDate(DateUtility.getJalaliDate(remittance.getContractDate()));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(remittance.getContractDate());
		calendar.add(Calendar.MONTH, 3);
		create.setContractValidDate(DateUtility.getJalaliDate(calendar.getTime()));
		log.debug("Contract date set: {}", create.getContractDate());
		if (Objects.nonNull(loadingPlaceModel.getPmsLoadingId())) {
			create.setLoadId(loadingPlaceModel.getPmsLoadingId());
			log.debug("Loading ID set: {}", loadingPlaceModel.getPmsLoadingId());
		}
		create.setContractCode(remittance.getContractNo());
		if (Objects.nonNull(remittance.getContractDate()))
			create.setBuyDate(DateUtility.getJalaliDate(remittance.getContractDate()));
		log.debug("Contract code set: {}", remittance.getContractNo());
		log.info("Sending PMS remittance request to PMS system - URL: {}", pmsProperties.getRemittance().getUrl());
		if (Objects.nonNull(remittance.getDescription()) && !remittance.getDescription().isBlank())
			create.setDescription(remittance.getDescription());
		if (Objects.nonNull(remittance.getProformaIssueType()))
			create.setStcProcess(mapProformaIssueTypeToPmsRemittanceGroupCode(remittance.getProformaIssueType()));
		rabbitTemplate.convertAndSend(rabbitConfigPMSProperties.getExchange(),
				rabbitConfigPMSProperties.getQueues().getHavaleh().getRoutingKey(),
				new PMSRemittanceDTO.RabbitListenerRequestDTO(
						pmsProperties.getRemittance().getUrl(),
						remittanceId, create, rabbitConfigPMSProperties.getQueues().getHavaleh().getResponseRoutingKey()


				));


	}

	@Override
	public void update(Long remittanceId, String username) throws IOException {

		log.info("Starting PMS remittance update - RemittanceId: {}, username: {}", remittanceId, username);
		if (Objects.isNull(username)) {
			username = pmsProperties.getPreFactor().getDefaultPmsUser();
			log.debug("Username was null, setting default username: {}", username);
		}
		log.debug("Fetching remittance from database for remittanceId: {}", remittanceId);
		RemittanceMasterModel remittance = remittanceMasterRepository.findById(remittanceId).orElseThrow(() -> new InternalSaleCustomException.ValidationException(String.format("No Remittance found for remittanceId: %s", remittanceId)));
		PmsRemmitanceModel pmsRemittance = pmsRemittanceRepository.findById(remittance.getPmsId()).orElse(null);
		log.info("let update remittance in pms it is like this for now: {}", pmsRemittance);
		log.info("Remittance fetched successfully - ContractNo: {}, PaymentCode: {}, GoodName: {}",
				remittance.getContractNo(), remittance.getPaymentCode(), remittance.getGoodName());
		log.debug("Fetching loading place for loadingPortId: {}", remittance.getLoadingPortId());
		LoadingPlaceModel loadingPlaceModel = loadingPlaceRepository.findById(remittance.getLoadingPortId()).orElseThrow(
				() -> new InternalSaleCustomException.ValidationException(
						"PMS loading place not found for loading port id " + remittance.getLoadingPortId()));
		log.debug("Loading place fetched - PmsLoadingId: {}", loadingPlaceModel.getPmsLoadingId());
		InsPmsImeMappingModel pmsCustomerBroker = null;
		log.debug("Searching for IME trade - ContractNo: {}, PaymentCode: {}", remittance.getContractNo().substring(0, 10), remittance.getPaymentCode());
		IMETradeModel imeTradeModel = imeTradeRepository.findFirstByContractNoAndPaymentCodeOrderByIdDesc(Integer.parseInt(remittance.getContractNo().substring(0, 10)), remittance.getPaymentCode()).orElse(null);
		if (Objects.nonNull(imeTradeModel)) {
			log.info("IME trade found - TradeId: {}, BuyerBrokerCode: {}", imeTradeModel.getId(), imeTradeModel.getBuyerBrokerCode());
			log.debug("Fetching broker information for brokerId: {}", imeTradeModel.getBuyerBrokerCode());
			IMEBrokerModel imeBrokerModel = imeBrokerRepository.findFirstByBrokerId(imeTradeModel.getBuyerBrokerCode()).orElse(null);
			if (Objects.nonNull(imeBrokerModel) && Objects.nonNull(imeBrokerModel.getNationalId())) {
				log.debug("Broker found - NationalId: {}, searching for PMS mapping", imeBrokerModel.getNationalId());
				pmsCustomerBroker = insPmsImeMappingRepository.findFirstByImeIdAndMappingFor(Long.valueOf(imeTradeModel.getBuyerBrokerCode()), InsPmsImeMappingTypeEnum.BROKER).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("No broker found in InsPmsImeMappingModel table for broker national code " + imeBrokerModel.getNationalId()));
				log.info("PMS customer broker mapping found - PmsCode: {}", pmsCustomerBroker.getPmsCode());
			} else {
				log.warn("Broker not found or has no national ID for brokerId: {}", imeTradeModel.getBuyerBrokerCode());
			}
		} else {
			log.warn("IME trade not found for ContractNo: {}, PaymentCode: {}", remittance.getContractNo().substring(0, 10), remittance.getPaymentCode());
		}
		CustomerModel customerModel = customerRepository.findByNationalCode(remittance.getNationalCode()).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MessageFormat.format("customer not found for national code {0}", remittance.getNationalCode())));
		log.debug("Finding PMS goods ID for goodName: {}", remittance.getGoodName());
		Long pmsGoodsId = goodsService.findPmsIdByGoodName(remittance.getGoodName());
		log.debug("PMS goods ID found: {}", pmsGoodsId);
		PmsMappingModel pmsGoodUnit = pmsMappingRepository.findById(pmsGoodsId).orElse(null);
		if (Objects.nonNull(pmsGoodUnit)) {
			log.debug("PMS good unit mapping found - PackingCode: {}", pmsGoodUnit.getPmsPackingCode());
		} else {
			log.warn("PMS good unit mapping not found for pmsGoodsId: {}", pmsGoodsId);
		}
		String nationalCode = customerModel.getNationalCode();
		log.debug("Searching for PMS customer with nationalCode: {}", nationalCode);
		PMSCustomerModel pmsCustomerModel = pmsCustomerRepository.findFirstByEconomicCodeContainingOrRegisterNumberContainingOrderByIdDesc(customerModel.getEconomicCode(), nationalCode).orElse(null);
		if (Objects.nonNull(pmsCustomerModel)) {
			log.info("PMS customer found - CustomerId: {}, RegisterNumber: {}", pmsCustomerModel.getId(), nationalCode);
		} else {
			log.warn("PMS customer not found for nationalCode: {}", nationalCode);
		}
		log.debug("Building PMS remittance request DTO");
		PMSRemittanceDTO request = PMSRemittanceDTO.builderr().name(extractRemittanceName(remittance, imeTradeModel)).issueDate(DateUtility.getJalaliDate(remittance.getRemittanceDate())).pmsBuyerId(Objects.nonNull(pmsCustomerModel) ? pmsCustomerModel.getId() : null).pmsGoodId(pmsGoodsId).pmsGoodUnit(Objects.nonNull(pmsGoodUnit) ? pmsGoodUnit.getPmsPackingCode() : null).amount(remittance.getRemittanceQuantity()).buildd();
		log.debug("Request DTO built - Name: {}, Amount: {}", request.getName(), request.getAmount());
		PMSRemittanceDTO.Nullables pmsRemittanceDTO = pmsRemittanceMapper.toNullablesDTO(request);
		PMSRemittanceDTO.Update create = pmsRemittanceMapper.toUpdateDTO(pmsRemittanceDTO,
				pmsProperties.getPreFactor().getUser(), pmsProperties.getPreFactor().getPass(), username);
		create.setId(remittance.getPmsId());
		create.setFinalDate(DateUtility.getJalaliDate(remittance.getValidityDate()));
		log.debug("Contract valid date set: {}", create.getContractValidDate());
		if (Objects.nonNull(remittance.getValidityDate()))
			create.setValidUntil(DateUtility.getJalaliDate(remittance.getValidityDate()));
		if (Objects.nonNull(remittance.getLotNumber()) && !remittance.getLotNumber().isEmpty()) {
			create.setLotNumber(remittance.getLotNumber());
			log.debug("Lot number set: {}", remittance.getLotNumber());
		}
		if (Objects.nonNull(remittance.getCashPercentage()) && remittance.getCashPercentage().compareTo(BigDecimal.ZERO) > 0) {
			create.setCashPercent(remittance.getCashPercentage().intValue());
			log.debug("Cash percentage set: {}", remittance.getCashPercentage());
		}
		if (Objects.nonNull(remittance.getCreditPercentage()) && remittance.getCreditPercentage().compareTo(BigDecimal.ZERO) > 0) {
			create.setCreditPercent(remittance.getCreditPercentage().intValue());
			log.debug("Credit percentage set: {}", remittance.getCreditPercentage());
		}
		if (Objects.nonNull(remittance.getRemittanceUnitPriceCash()) && remittance.getRemittanceUnitPriceCash().compareTo(BigDecimal.ZERO) > 0) {
			create.setCashUnitPrice(remittance.getRemittanceUnitPriceCash());
			log.debug("Cash unit price set: {}", remittance.getRemittanceUnitPriceCash());
		}
		if (Objects.nonNull(remittance.getRemittanceUnitPriceCredit()) &&
				(Objects.nonNull(remittance.getCreditPercentage()) &&
						remittance.getCreditPercentage().compareTo(BigDecimal.ZERO) > 0)
				&& remittance.getRemittanceUnitPriceCredit().compareTo(BigDecimal.ZERO) > 0) {
			create.setCreditUnitPrice(remittance.getRemittanceUnitPriceCredit());
			log.debug("Credit unit price set: {}", remittance.getRemittanceUnitPriceCredit());
		}
		if (Objects.nonNull(remittance.getProformaNo()) && !remittance.getProformaNo().isEmpty()) {
			create.setPrefactorId(remittance.getProformaNo());
			log.debug("Proforma number set: {}", remittance.getProformaNo());
		}
		if (Objects.nonNull(pmsCustomerBroker)) {
			create.setBrokerId(pmsCustomerBroker.getPmsCode());
			log.debug("Broker ID set: {}", pmsCustomerBroker.getPmsCode());
		}
		if (Objects.nonNull(remittance.getLcId()) && remittance.getId() > 0) {
			log.debug("Processing LC information for lcId: {}", remittance.getLcId());
			LcModel lcModel = lcRepository.findById(remittance.getLcId()).orElse(null);
			if (Objects.nonNull(lcModel) && Objects.nonNull(lcModel.getPmsLcId())) {
				log.debug("LC model found - PmsLcId: {}", lcModel.getPmsLcId());
				PmsLcModel pmsLcModel = pmsLcRepository.findById(lcModel.getPmsLcId()).orElse(null);
				if (Objects.nonNull(pmsLcModel) && Objects.nonNull(pmsLcModel.getLcId())) {
					create.setLcId(pmsLcModel.getLcId());
					log.info("PMS LC ID set: {}", pmsLcModel.getLcId());
				} else {
					log.warn("PMS LC model not found or has no LC ID for pmsLcId: {}", lcModel.getPmsLcId());
				}
			} else {
				log.warn("LC model not found or has no PMS LC ID for lcId: {}", remittance.getLcId());
			}
		}

		create.setContractDate(DateUtility.getJalaliDate(remittance.getContractDate()));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(remittance.getContractDate());
		calendar.add(Calendar.MONTH, 3);
		create.setContractValidDate(DateUtility.getJalaliDate(calendar.getTime()));
		log.debug("Contract date set: {}", create.getContractDate());
		if (Objects.nonNull(loadingPlaceModel.getPmsLoadingId())) {
			create.setLoadId(loadingPlaceModel.getPmsLoadingId());
			log.debug("Loading ID set: {}", loadingPlaceModel.getPmsLoadingId());
		}
		create.setContractCode(remittance.getContractNo());
		if (Objects.nonNull(remittance.getContractDate()))
			create.setBuyDate(DateUtility.getJalaliDate(remittance.getContractDate()));
		log.debug("Contract code set: {}", remittance.getContractNo());
		log.info("Sending PMS remittance request to PMS system - URL: {}", pmsProperties.getRemittance().getUrl());
		if (Objects.nonNull(remittance.getDescription()) && !remittance.getDescription().isBlank())
			create.setDescription(remittance.getDescription());
		if (Objects.nonNull(remittance.getProformaIssueType()))
			create.setStcProcess(mapProformaIssueTypeToPmsRemittanceGroupCode(remittance.getProformaIssueType()));
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
		log.debug("Extracting remittance name for remittanceId: {}", remittanceMasterModel.getId());
		ProformaMasterModel masterModel = proformaMasterRepository.findById(remittanceMasterModel.getProformaMasterId()).orElse(null);

		String name = pmsProperties.getRemittance().getTitleBase();
		String weightInTon = "0";
		if (Objects.isNull(masterModel)) {
			if (Objects.nonNull(tradeModel)) {
				weightInTon = String.valueOf(tradeModel.getUnitCount());
				log.debug("Performa not found, using trade unit count: {}", weightInTon);
			} else {
				log.warn("Both performa and trade model are null for remittanceId: {}", remittanceMasterModel.getId());
			}
		} else if (masterModel.getProformaIssueType().equals(ProformaIssueType.FROM_CREDIT_FACILITIES)) {
			weightInTon = String.valueOf(masterModel.getTotalQuantity().multiply(new BigDecimal("1000")).setScale(4, RoundingMode.DOWN));
			log.debug("Performa type: FROM_CREDIT_FACILITIES, calculated weight: {}", weightInTon);
		} else if (masterModel.getProformaIssueType().equals(ProformaIssueType.LETTER_OF_CREDIT_OPENING)) {
			weightInTon = String.valueOf(remittanceMasterModel.getRemittanceQuantity().multiply(new BigDecimal("1000")).setScale(4, RoundingMode.DOWN));
			log.debug("Performa type: LETTER_OF_CREDIT_OPENING, calculated weight: {}", weightInTon);
		}
		String finalName = name + weightInTon + " " + tradeModel.getCommodityPersianName();
		log.info("Remittance name extracted: {}", finalName);
		return finalName;
	}

	@RabbitListener(queues = "${rabbitmq.config.pms.queues.havaleh.response-queue}")
	void savePmsRemittanceRabbitListener(String message) throws IOException {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.SALES_SLIP)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());

		if (exportConfig.getSendPms() == false) return;

		log.info("Saving PMS remittance ID: {}", message);
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

			CustomerModel customer = customerRepository.findById(remittanceMasterModel.getCustomerId())
					.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
							MSG_CUSTOMER_NOT_FOUND));
			byte[] pdfContent = convertDocumentsToPdf(List.of(remittanceMasterModel.getId()));
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
		log.info(exportConfig.toString());
		if (exportConfig.getSendPms() == false) return;

		log.info("Updating PMS remittance from response queue: {}", message);
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


	private byte[] convertDocumentsToPdf(List<Long> ids) {
		try {
			List<XWPFDocument> documents = loadRemittanceDocuments(ids);

			if (documents.isEmpty()) {
				throw new InternalSaleCustomException.FileContentException(
						MSG_FILE_EMPTY_LIST);
			}
			return convertToPdf(documents);
		} catch (IOException ex) {
			log.error("خطا در تبدیل فایلها به PDF: {}", ex.getMessage(), ex);
			throw new InternalSaleCustomException.FileContentException(
					MSG_FILE_WRITE_ERROR);
		}
	}


	private List<XWPFDocument> loadRemittanceDocuments(List<Long> remittanceIds) {
		return remittanceIds.stream()
				.map(this::loadRemittanceDocument)
				.filter(Objects::nonNull)
				.toList();
	}


	private XWPFDocument loadRemittanceDocument(Long remittanceId) {
		try {
			byte[] docBytes = exportDocService.exportRemittanceDoc(remittanceId);
			if (docBytes == null || docBytes.length == 0) {
				log.warn("فایل خالی یا نامعتبر برای حواله با شناسه: {}", remittanceId);
				return null;
			}
			return new XWPFDocument(new ByteArrayInputStream(docBytes));
		} catch (IOException ex) {
			log.error("خطا در بارگذاری فایل حواله با شناسه {}: {}", remittanceId, ex.getMessage(), ex);
			return null;
		}
	}


	private byte[] convertToPdf(List<XWPFDocument> documents) throws IOException {
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		int fileCounter = 0;
		for (XWPFDocument doc : documents) {
			fileCounter++;
			PipedInputStream in = new PipedInputStream();

			final int counter = fileCounter;
			new Thread(() -> {
				try (PipedOutputStream out = new PipedOutputStream(in)) {
					doc.write(out);
				} catch (IOException ex) {
					log.error("خطا در نوشتن فایل به جریان داده: {}", ex.getMessage(), ex);
					throw new InternalSaleCustomException.FileContentException(
							MSG_FILE_WRITE_ERROR);
				}
			}).start();

			bodyMap.add(FILES_PARAM, new MultipartInputStreamFileResource(in, counter + DOC_EXTENSION));
		}

		bodyMap.add(MERGE_PARAM, TRUE);

		RequestEntity<MultiValueMap<String, Object>> request = RequestEntity
				.post(URI.create(pdfConvertorUrl))
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(bodyMap);

		try {
			byte[] response = restTemplate.exchange(request, byte[].class).getBody();
			if (response == null) {
				throw new InternalSaleCustomException.FileContentException(
						MSG_PDF_EMPTY_RESPONSE);
			}
			return response;
		} catch (Exception ex) {
			log.error("خطا در فراخوانی سرویس تبدیل PDF: {}", ex.getMessage(), ex);
			throw new InternalSaleCustomException.FileContentException(
					MSG_FILE_WRITE_ERROR);
		}
	}

}
