package com.nicico.internal.sales.notification.service;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.export.service.ExportDocService;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.lc.dto.request.LcBrokerEmailRequest;
import com.nicico.internal.sales.notification.dto.EmailRequest;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

	// ==================== CONSTANTS ====================
	private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";
	private static final String DOC_EXTENSION = ".doc";
	private static final String PDF_EXTENSION = ".pdf";
	private static final String FILE_NAME_PREFIX_PROFORMA = "proforma_";
	private static final String FILE_NAME_PREFIX_REMITTANCE = "remittance_";
	private static final String MERGE_PARAM = "merge";
	private static final String FILES_PARAM = "files";
	private static final String TRUE = "true";

	// ==================== MESSAGE TEMPLATES ====================
	private static final String MSG_PROFORMA_NOT_FOUND = "پیش فاکتور وجود ندارد";
	private static final String MSG_CUSTOMER_NOT_FOUND = "اطلاعات مشتری پیدا نشد";
	private static final String MSG_REMITTANCE_NOT_FOUND = "حواله وجود ندارد";
	private static final String MSG_FILE_WRITE_ERROR = "خطایی در هنگام نوشتن فایل اتفاق افتاد";
	private static final String MSG_FILE_EMPTY_LIST = "لیست فایلها خالی است و امکان ایجاد فایل پی دی اف وجود ندارد";
	private static final String MSG_PDF_EMPTY_RESPONSE = "فایل خالی از طرف سرویس تبدیل PDF دریافت شد";
	private static final String MSG_EMAIL_ERROR = "خطا در ارسال ایمیل {0}";

	// ==================== EMAIL TEMPLATES ====================
	private static final String EMAIL_SUBJECT_PROFORMA = "پیش فاکتور قرارداد شماره : {0}";
	private static final String EMAIL_SUBJECT_REMITTANCE = "حواله قرارداد شماره : {0}";
	private static final String EMAIL_CONTENT_PROFORMA =
			"با سلام پیش فاکتور شرکت {0} شماره قرارداد {1} مورخ {2} به پیوست ارسال میگردد.با تشکر";
	private static final String EMAIL_CONTENT_REMITTANCE =
			"با سلام حواله شرکت {0} شماره قرارداد {1} مورخ {2} به پیوست ارسال میگردد.با تشکر";
	private static final String EMAIL_SUBJECT_LC_BROKER = "تایید تسویه";

	// ==================== LOG MESSAGES ====================
	private static final String LOG_EMAIL_SUCCESS = "ایمیل {0} با موفقیت برای شناسه {1} ارسال شد. وضعیت: {2}";
	private static final String LOG_ERROR_UNEXPECTED = "خطای غیرمنتظره در ارسال ایمیل برای شناسه {0}: {1}";
	private static final String LOG_LOADING_DOC_ERROR = "خطا در بارگذاری فایل {0} با شناسه {1}: {2}";
	private static final String LOG_PDF_CONVERT_ERROR = "خطا در فراخوانی سرویس تبدیل PDF: {0}";

	// ==================== DEPENDENCIES ====================
	private final ProformaMasterRepository proformaMasterRepository;
	private final ExportDocService exportDocService;
	private final MailService mailService;
	private final CustomerRepository customerRepository;
	private final RemittanceMasterRepository remittanceMasterRepository;
	private final SmsNotificationService smsNotificationService;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;
	private final RestTemplate restTemplate;

	@Value("${nicico.pdf-api}")
	private String pdfConvertorUrl;

	@Value("${nicico.bcc-address}")
	private String bccAddress;

	@Value("${nicico.lc-bcc-address}")
	private String lcBccAddress;

	// ==================== PUBLIC API ====================

	@Override
	@Transactional
	public void sendEmailWithProformaAttachment(Long proformaMasterId) {
		executeEmailSend(
				proformaMasterId,
				EntityTypeEnum.PROFORMA,
				this::getProformaMasterModel,
				this::getActiveProformaDetailIds,
				FILE_NAME_PREFIX_PROFORMA,
				this::prepareProformaEmailRequest,
				"پیش فاکتور"
		);
	}

	@Override
	@Transactional
	public void retrySendEmailWithProformaAttachment(Long proformaMasterId) {
		sendEmailWithProformaAttachment(proformaMasterId);
		try {
			smsNotificationService.preFactorEmailedSMSNotification(proformaMasterId);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@Transactional
	public void sendEmailWithEditedRemittanceAttachment(Long remittanceId) {
		executeEmailSend(
				remittanceId,
				EntityTypeEnum.SALES_SLIP,
				this::getRemittanceMasterModel,
				this::getRemittanceDetailIds,
				FILE_NAME_PREFIX_REMITTANCE,
				this::prepareRemittanceEmailRequest,
				"حواله ویرایش شده"
		);
	}

	@Override
	public void sendEmailForLcBroker(LcBrokerEmailRequest dto, String emailContent) {
		if (!isEmailSendingEnabled(EntityTypeEnum.LETTER_OF_CREDIT)) {
			return;
		}

		try {
			EmailRequest emailRequest = EmailRequest.builder()
					.subject(EMAIL_SUBJECT_LC_BROKER)
					.bccRecipients(lcBccAddress)
					.toRecipients(dto.getBrokerEmail())
					.content(emailContent)
					.build();

			mailService.sendMail(emailRequest);
			log.info("ایمیل برای کارگزار {} با موفقیت ارسال شد", dto.getBrokerName());
		} catch (Exception ex) {
			log.error("خطا در ارسال ایمیل برای کارگزار با نام {}: {}", dto.getBrokerName(), ex.getMessage(), ex);
		}
	}

	// ==================== CORE EXECUTION ====================

	private <T> void executeEmailSend(
			Long id,
			EntityTypeEnum entityType,
			Function<Long, T> modelFetcher,
			Function<T, List<Long>> idExtractor,
			String fileNamePrefix,
			Function<T, EmailRequest> emailBuilder,
			String entityName) {

		if (!isEmailSendingEnabled(entityType)) {
			log.info("isEmailSendingEnabled(entityType) is false for {} {} {}", entityName, entityType, id);
			return;
		}

		try {
			log.info("getting model  for {} {} {}", entityName, entityType, id);
			T model = modelFetcher.apply(id);
			log.info("validating model  for {} {} {}", entityName, entityType, id);
			validateModel(model);
			log.info("getting details model  for {} {} {}", entityName, entityType, id);
			List<Long> detailIds = idExtractor.apply(model);
			log.info("starting pdfContent  model  for {} {} {}", entityName, entityType, id);
			byte[] pdfContent = convertDocumentsToPdf(detailIds, entityType);
			Path filePath = createTempFile(fileNamePrefix, getContractNo(model), pdfContent);

			EmailRequest emailRequest = emailBuilder.apply(model);
			HttpResponse<String> response = mailService.sendMail(emailRequest, filePath.toString());

			log.info(formatMessage(LOG_EMAIL_SUCCESS, entityName, id, response.body()));

			cleanupTempFile(filePath);

		} catch (InternalSaleCustomException ex) {
			log.error(formatMessage(LOG_ERROR_UNEXPECTED, id, ex.getMessage()), ex);
			throw ex;
		} catch (Exception ex) {
			log.error(formatMessage(LOG_ERROR_UNEXPECTED, id, ex.getMessage()), ex);
			throw new InternalSaleCustomException.FileContentException(
					formatMessage(MSG_EMAIL_ERROR, entityName)
			);
		}
	}

	// ==================== VALIDATION ====================

	private boolean isEmailSendingEnabled(EntityTypeEnum entityType) {
		return exportNotificationConfigRepository.findByEntityType(entityType)
				.map(config -> {
					log.debug("Email config for {}: {}", entityType, config);
					return config.getSendEmail() != null && config.getSendEmail();
				})
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
	}

	private <T> void validateModel(T model) {
		if (model == null) {
			throw new InternalSaleCustomException.ResourceNotFoundException("مدل مورد نظر یافت نشد");
		}
	}

	// ==================== MODEL FETCHERS ====================

	private ProformaMasterModel getProformaMasterModel(Long id) {
		return proformaMasterRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_PROFORMA_NOT_FOUND));
	}

	private RemittanceMasterModel getRemittanceMasterModel(Long id) {
		return remittanceMasterRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_REMITTANCE_NOT_FOUND));
	}

	private CustomerModel getCustomer(Long customerId) {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_CUSTOMER_NOT_FOUND));
	}

	// ==================== ID EXTRACTORS ====================

	private List<Long> getActiveProformaDetailIds(ProformaMasterModel masterModel) {
		return masterModel.getProformaDetailModelLists().stream()
				.filter(detail -> detail.getProformaReversalStatus() != ProformaReversalStatus.CANCELED)
				.map(ProformaDetailModel::getId)
				.toList();
	}

	private List<Long> getRemittanceDetailIds(RemittanceMasterModel model) {
		return List.of(model.getId());
	}

	// ==================== EMAIL BUILDERS ====================

	private EmailRequest prepareProformaEmailRequest(ProformaMasterModel master) {
		String customerEmail = getCustomer(master.getCustomerId()).getEmail();

		return EmailRequest.builder()
				.subject(formatMessage(EMAIL_SUBJECT_PROFORMA, master.getContractNo()))
				.bccRecipients(bccAddress)
				.toRecipients(customerEmail)
				.content(formatMessage(EMAIL_CONTENT_PROFORMA,
						master.getCustomerName(),
						master.getContractNo(),
						master.getContractDate()))
				.build();
	}

	private EmailRequest prepareRemittanceEmailRequest(RemittanceMasterModel master) {
		String customerEmail = getCustomer(master.getCustomerId()).getEmail();

		return EmailRequest.builder()
				.subject(formatMessage(EMAIL_SUBJECT_REMITTANCE, master.getContractNo()))
				.bccRecipients(bccAddress)
				.toRecipients(customerEmail)
				.content(formatMessage(EMAIL_CONTENT_REMITTANCE,
						master.getCustomerName(),
						master.getContractNo(),
						master.getContractDate()))
				.build();
	}

	// ==================== HELPER METHODS ====================

	private <T> String getContractNo(T model) {
		if (model instanceof ProformaMasterModel) {
			return String.valueOf(((ProformaMasterModel) model).getContractNo());
		} else if (model instanceof RemittanceMasterModel) {
			return String.valueOf(((RemittanceMasterModel) model).getContractNo());
		}
		throw new IllegalArgumentException("نوع مدل پشتیبانی نمی‌شود");
	}

	private String formatMessage(String template, Object... args) {
		return MessageFormat.format(template, args);
	}

	// ==================== FILE OPERATIONS ====================

	private Path createTempFile(String prefix, String contractNo, byte[] content) throws IOException {
		String fileName = prefix + contractNo + PDF_EXTENSION;
		Path filePath = Paths.get(fileName);
		Files.write(filePath, content);
		return filePath;
	}

	private void cleanupTempFile(Path filePath) {
		try {
			Files.deleteIfExists(filePath);
			log.debug("فایل موقت {} حذف شد", filePath);
		} catch (IOException ex) {
			log.warn("خطا در حذف فایل موقت {}: {}", filePath, ex.getMessage());
		}
	}

	// ==================== PDF CONVERSION ====================

	private byte[] convertDocumentsToPdf(List<Long> ids, EntityTypeEnum entityType) {
		log.info("convertDocumentsToPdf start List<XWPFDocument> documents");
		List<XWPFDocument> documents = loadDocuments(ids, entityType);

		if (documents.isEmpty()) {
			log.error("convertDocumentsToPdf  documents list is empty");
			throw new InternalSaleCustomException.FileContentException(MSG_FILE_EMPTY_LIST);
		}

		return convertToPdf(documents);
	}

	// ==================== DOCUMENT LOADING ====================

	private List<XWPFDocument> loadDocuments(List<Long> ids, EntityTypeEnum entityType) {
		return ids.stream()
				.map(id -> loadDocument(id, entityType))
				.filter(Objects::nonNull)
				.toList();
	}

	private XWPFDocument loadDocument(Long id, EntityTypeEnum entityType) {
		String documentType = entityType == EntityTypeEnum.PROFORMA ? "پیش فاکتور" : "حواله";

		try {
			byte[] docBytes = entityType == EntityTypeEnum.PROFORMA
					? exportDocService.exportProformaDoc(id)
					: exportDocService.exportRemittanceDoc(id);

			if (docBytes == null || docBytes.length == 0) {
				log.warn("فایل خالی یا نامعتبر برای {} با شناسه: {}", documentType, id);
				return null;
			}
			return new XWPFDocument(new ByteArrayInputStream(docBytes));
		} catch (IOException ex) {
			log.error(formatMessage(LOG_LOADING_DOC_ERROR, documentType, id, ex.getMessage()), ex);
			return null;
		}
	}

	// ==================== PDF CONVERTER ====================

	/**
	 * Serializes each document fully into memory (byte[]) before building the
	 * multipart request, rather than streaming via PipedInputStream/PipedOutputStream.
	 * <p>
	 * Why: a PipedOutputStream -> PipedInputStream pair has a tiny internal buffer
	 * (default 1024 bytes). doc.write(out) runs on this same thread and blocks the
	 * moment that buffer fills, because nothing drains the read side until
	 * restTemplate sends the multipart body — which can't happen until this loop
	 * finishes building bodyMap. So any document bigger than the buffer deadlocks:
	 * the writer waits for a reader that itself is waiting for the writer to finish.
	 * Reading fully into memory up front avoids the cycle.
	 */
	private byte[] convertToPdf(List<XWPFDocument> documents) {
		log.info("convertToPdf start, {} document(s)", documents.size());

		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		log.info("convertToPdf start, {} document(s)", documents.size());
		int fileCounter = 0;
		for (XWPFDocument doc : documents) {
			fileCounter++;
			String fileName = fileCounter + DOC_EXTENSION;
			byte[] docBytes = toByteArray(doc);
			bodyMap.add(FILES_PARAM, new ByteArrayResource(docBytes) {
				@Override
				public String getFilename() {
					return fileName;
				}
			});
		}

		log.info("convertToPdf all documents written to memory, sending to pdf convertor");
		bodyMap.add(MERGE_PARAM, TRUE);

		RequestEntity<MultiValueMap<String, Object>> request = RequestEntity
				.post(URI.create(pdfConvertorUrl))
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(bodyMap);

		try {
			log.info("convertToPdf calling pdf convertor at {}", pdfConvertorUrl);
			byte[] response = restTemplate.exchange(request, byte[].class).getBody();
			log.info("convertToPdf received response, {} bytes", response == null ? 0 : response.length);
			if (response == null || response.length == 0) {
				throw new InternalSaleCustomException.FileContentException(MSG_PDF_EMPTY_RESPONSE);
			}
			return response;
		} catch (Exception ex) {
			log.error(formatMessage(LOG_PDF_CONVERT_ERROR, ex.getMessage()), ex);
			throw new InternalSaleCustomException.FileContentException(MSG_FILE_WRITE_ERROR);
		}
	}

	private byte[] toByteArray(XWPFDocument doc) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			doc.write(out);
			return out.toByteArray();
		} catch (IOException ex) {
			log.error("خطا در سریال کردن سند به بایت: {}", ex.getMessage(), ex);
			throw new InternalSaleCustomException.FileContentException(MSG_FILE_WRITE_ERROR);
		}
	}
}