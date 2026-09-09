package com.nicico.internal.sales.fms.service;

import com.fgostar.fms.sdk.FmsFileService;
import com.fgostar.fms.sdk.auth.FmsCredentials;
import com.fgostar.fms.sdk.dto.FileDto;
import com.fgostar.fms.sdk.model.FilePage;
import com.fgostar.fms.sdk.model.FmsFile;
import com.fgostar.fms.sdk.model.UploadRequest;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.service.ExportDocService;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FmsDocumentServiceImpl implements FmsDocumentService {

	private static final String PROFORMA_FILE_NAME_PREFIX = "proforma_";

	private static final String PROFORMA_TAG_TYPE = "entityType";
	private static final String PROFORMA_TAG_ID = "id";
	private static final String PROFORMA_TAG_ID_CLASS = "idClass";

	private static final String PROFORMA_TAG_TYPE_VALUE = "proforma";
	private static final String PROFORMA_TAG_ID_CLASS_VALUE = "com.nicico.internal.sales.proforma.model.ProformaMasterModel";


	private static final String PDF_CONTENT_TYPE = "application/pdf";

	private static final String REMITTANCE_TAG_TYPE = "entityType";
	private static final String REMITTANCE_TAG_ID = "id";
	private static final String REMITTANCE_TAG_ID_CLASS = "idClass";

	private static final String REMITTANCE_TAG_TYPE_VALUE = "remittance";
	private static final String REMITTANCE_TAG_ID_CLASS_VALUE = "com.nicico.internal.sales.remittance.model.RemittanceMasterModel";

	private static final String ERR_PROFORMA_NOT_FOUND = "پیش فاکتور پیدا نشد";
	private static final String ERR_AUTH_TOKEN_NOT_FOUND = "توکن احراز هویت یافت نشد";
	private static final String ERR_INVALID_AUTH_HEADER = "فرمت توکن احراز هویت نامعتبر است";

	private final FmsFileService fmsFileService;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ExportDocService exportDocService;
	private final RemittanceMasterRepository remittanceMasterRepository;
	private final HttpServletRequest request;

	@Value("${nicico.fms.group-id}")
	private String fmsGroupId;


	public FmsFile getOrCreateProformaPdf(Long detailId) {

		ProformaDetailModel detailModel = proformaDetailRepository.findById(detailId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(ERR_PROFORMA_NOT_FOUND));

		ProformaMasterModel masterModel=proformaMasterRepository.findById(detailModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(ERR_PROFORMA_NOT_FOUND));

		if (masterModel.getWorkflowApproveStatus() ==WorkflowApproveStatus.IN_PROGRESS)
		{
			return new FmsFile(UUID.randomUUID().toString(),detailId + ".pdf",PDF_CONTENT_TYPE,exportDocService.exportProformaPdf(detailId));

		}

		if (masterModel.getWorkflowApproveStatus() ==WorkflowApproveStatus.CANCELED || detailModel.getProformaReversalStatus()==ProformaReversalStatus.CANCELED)
		{
			throw new InternalSaleCustomException.ValidationException("پیش فاکتور با شناسه " + detailModel.getPerformaNo() + " ابطال شده است و نمی‌توان آن را صادر کرد.");

		}


		FmsCredentials credentials = FmsCredentials.oauth(getCurrentUserToken());
		Map<String, Object> searchTags = Map.of(
				PROFORMA_TAG_TYPE,
				PROFORMA_TAG_TYPE_VALUE,
				PROFORMA_TAG_ID,
				detailId,
				PROFORMA_TAG_ID_CLASS,
				PROFORMA_TAG_ID_CLASS_VALUE
		);

		FilePage page = fmsFileService.searchInGroup(fmsGroupId, searchTags, true, 0, 1, credentials);
		if (!page.isEmpty()) {
			FileDto existing = page.getFiles().get(0);
			log.info("منبع فایل پیش فاکتور {}: FMS (از قبل موجود بود). uuid={}", detailId, existing.getUuid());
			return fmsFileService.download(fmsGroupId, existing.getUuid(), credentials);
		}

		log.info("فایل پیش فاکتور {} در FMS یافت نشد، در حال ساخت...", detailId);





		byte[] pdfContent = buildSignedProformaPdf(List.of(detailModel.getId()));
		String fileName = PROFORMA_FILE_NAME_PREFIX + detailModel.getPerformaNo() + ".pdf";

		if (detailModel.getProformaMasterModel().getWorkflowApproveStatus() == WorkflowApproveStatus.ACCEPTED) {
			String uuid = uploadProformaToFms(detailId, fileName, pdfContent, credentials);
			saveProformaFileIdToDetails(List.of(detailId), uuid);
			log.info("منبع فایل پیش فاکتور {}: تازه ساخته و در FMS آپلود شد. uuid={}", detailId, uuid);
			return new FmsFile(uuid, fileName, PDF_CONTENT_TYPE, pdfContent);
		}

		log.info("فایل پیش فاکتور {} ساخته شد اما به دلیل وضعیت غیر ACCEPTED ذخیره نشد.", detailId);
		return new FmsFile(UUID.randomUUID().toString(), fileName, PDF_CONTENT_TYPE, pdfContent);
	}

	public byte[] getProformaPdfBytes(Long detailId) {
		return exportDocService.exportProformaPdf(detailId);
//		ProformaDetailModel detail = proformaDetailRepository.findById(detailId)
//				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("جزئیات پیش فاکتور وجود ندارد"));
//		if (detail.getProformaFileId() != null && !detail.getProformaFileId().isEmpty()) {
//			log.info("منبع بایت‌های پیش فاکتور {}: دانلود از FMS (فایل از قبل ثبت شده بود). fileId={}", detailId, detail.getProformaFileId());
//			FmsCredentials credentials = FmsCredentials.oauth(getCurrentUserToken());
//			FmsFile fmsFile = fmsFileService.download(fmsGroupId, detail.getProformaFileId(), credentials);
//			return fmsFile.getContent();
//		}
//		if (detail.getProformaReversalStatus() == ProformaReversalStatus.CANCELED) {
//			throw new InternalSaleCustomException.ValidationException("پیش فاکتور با شناسه " + detail.getPerformaNo() + " ابطال شده است و نمی‌توان آن را صادر کرد.");
//		}
//
//		log.info("منبع بایت‌های پیش فاکتور {}: fileId ثبت نشده، در حال ساخت PDF جدید...", detailId);
//		byte[] pdfContent = buildSignedProformaPdf(List.of(detail.getId()));
//		String fileName = PROFORMA_FILE_NAME_PREFIX + detail.getPerformaNo() + ".pdf";
//		FmsFile fmsFile = uploadProformaToFmsAndGetFile(detailId, fileName, pdfContent);
//		saveProformaFileIdToDetails(List.of(detail.getId()), fmsFile.getUuid());
//		log.info("منبع بایت‌های پیش فاکتور {}: تازه ساخته و در FMS آپلود شد. uuid={}", detailId, fmsFile.getUuid());
//		return pdfContent;
	}

//	@Override
//	public FmsFile downloadProformaPdfFromFms(Long detailId) {
//		ProformaDetailModel detail = proformaDetailRepository.findById(detailId)
//				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("جزئیات پیش فاکتور وجود ندارد"));
//		if (detail.getProformaFileId() == null || detail.getProformaFileId().isEmpty()) return null;
//		FmsCredentials credentials = FmsCredentials.oauth(getCurrentUserToken());
//		return fmsFileService.download(fmsGroupId, detail.getProformaFileId(), credentials);
//	}

//	@Override
//	public FmsFile uploadRemittancePdfToFms(Long masterId) {
//
//		try {
//			byte[] pdfContent = exportDocService.exportRemittancePdf(masterId);
//			if (pdfContent == null || pdfContent.length == 0) {
//				throw new IllegalStateException("Failed to generate Remittance PDF for ID: " + masterId);
//			}
//			String fileName = "remittance_" + masterId + ".pdf";
//			FmsFile fmsFile = uploadRemittanceToFmsAndGetFile(masterId, fileName, pdfContent);
//			saveRemittanceFileIdToMaster(masterId, fmsFile.getUuid());
//			return fmsFile;
//
//		} catch (Exception ex) {
//			log.error("خطا در آپلود فایل حواله {} در FMS: {}", masterId, ex.getMessage(), ex);
//			return null;
//		}
//	}


	public FmsFile getOrCreateRemittancePdf(Long masterId) {
		FmsCredentials credentials = FmsCredentials.oauth(getCurrentUserToken());
		Map<String, Object> searchTags = Map.of(
				REMITTANCE_TAG_TYPE,
				REMITTANCE_TAG_TYPE_VALUE,
				REMITTANCE_TAG_ID,
				masterId,
				REMITTANCE_TAG_ID_CLASS,
				REMITTANCE_TAG_ID_CLASS_VALUE
		);

		FilePage page = fmsFileService.searchInGroup(fmsGroupId, searchTags, true, 0, 1, credentials);

		if (!page.isEmpty()) {
			FileDto existing = page.getFiles().get(0);
			log.info("منبع فایل حواله {}: FMS (از قبل موجود بود). uuid={}", masterId, existing.getUuid());
			return fmsFileService.download(fmsGroupId, existing.getUuid(), credentials);
		}

		log.info("فایل حواله {} در FMS یافت نشد، در حال ساخت...", masterId);

		byte[] pdfContent = exportDocService.exportRemittancePdf(masterId);
		if (pdfContent == null || pdfContent.length == 0) {
			throw new IllegalStateException("Failed to generate Remittance PDF for ID: " + masterId);
		}
		String fileName = "remittance_" + masterId + ".pdf";
		String uuid = uploadRemittanceToFms(masterId, fileName, pdfContent, credentials);
		log.info("منبع فایل حواله {}: تازه ساخته و در FMS آپلود شد. uuid={}", masterId, uuid);
		return new FmsFile(uuid, fileName, PDF_CONTENT_TYPE, pdfContent);
	}


	@Override
	public byte[] getRemittancePdfBytes(Long masterId) {

		return exportDocService.exportRemittancePdf(masterId);
//		RemittanceMasterModel master = findRemittanceMaster(masterId);
//		if (master.getRemittanceFileId() != null && !master.getRemittanceFileId().isEmpty()) {
//			log.info("منبع بایت‌های حواله {}: دانلود از FMS (فایل از قبل ثبت شده بود). fileId={}", masterId, master.getRemittanceFileId());
//			FmsCredentials credentials = FmsCredentials.oauth(getCurrentUserToken());
//			FmsFile fmsFile = fmsFileService.download(fmsGroupId, master.getRemittanceFileId(), credentials);
//			return fmsFile.getContent();
//		}
//		log.info("منبع بایت‌های حواله {}: fileId ثبت نشده، در حال ساخت PDF جدید...", masterId);
//		byte[] pdfContent = exportDocService.exportRemittancePdf(masterId);
//		if (pdfContent == null || pdfContent.length == 0) {
//			throw new IllegalStateException("Failed to generate Remittance PDF for ID: " + masterId);
//		}
//		String fileName = "remittance_" + masterId + ".pdf";
//		FmsFile fmsFile = uploadRemittanceToFmsAndGetFile(masterId, fileName, pdfContent);
//		saveRemittanceFileIdToMaster(masterId, fmsFile.getUuid());
//		log.info("منبع بایت‌های حواله {}: تازه ساخته و در FMS آپلود شد. uuid={}", masterId, fmsFile.getUuid());
//		return pdfContent;
	}


	/**
	 * دریافت Bearer Token از درخواست جاری.
	 */
	private String getCurrentUserToken() {
		String authorization = request.getHeader("Authorization");

		if (authorization == null || authorization.isBlank()) {
			throw new InternalSaleCustomException.ValidationException(ERR_AUTH_TOKEN_NOT_FOUND);
		}

		if (!authorization.startsWith("Bearer ")) {
			throw new InternalSaleCustomException.ValidationException(ERR_INVALID_AUTH_HEADER);
		}

		String token = authorization.substring("Bearer ".length()).trim();

		if (token.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(ERR_AUTH_TOKEN_NOT_FOUND);
		}

		return token;
	}


	private String uploadProformaToFms(Long detailId, String fileName, byte[] pdfContent, FmsCredentials credentials) {

				UploadRequest uploadRequest = UploadRequest.of(fmsGroupId, fileName, pdfContent)
				.contentType(PDF_CONTENT_TYPE)
				.tag(PROFORMA_TAG_TYPE, PROFORMA_TAG_TYPE_VALUE)
				.tag(PROFORMA_TAG_ID, detailId)
				.tag(PROFORMA_TAG_ID_CLASS, PROFORMA_TAG_ID_CLASS_VALUE);

		String uuid = fmsFileService.upload(uploadRequest, credentials);

		log.info("فایل پیش فاکتور {} با موفقیت در FMS آپلود شد. uuid={}", detailId, uuid);

		return uuid;
	}

	private FmsFile uploadProformaToFmsAndGetFile(Long detailId, String fileName, byte[] pdfContent) {
		FmsCredentials credentials = FmsCredentials.oauth(getCurrentUserToken());
		UploadRequest uploadRequest = UploadRequest.of(fmsGroupId, fileName, pdfContent)
				.contentType(PDF_CONTENT_TYPE)
				.tag(PROFORMA_TAG_TYPE, PROFORMA_TAG_TYPE_VALUE)
				.tag(PROFORMA_TAG_ID, detailId)
				.tag(PROFORMA_TAG_ID_CLASS, PROFORMA_TAG_ID_CLASS_VALUE);
		String uuid = fmsFileService.upload(uploadRequest, credentials);
		return new FmsFile(uuid, fileName, PDF_CONTENT_TYPE, pdfContent);
	}

	private void saveProformaFileIdToDetails(List<Long> detailIds, String fileId) {

		if (fileId == null || detailIds == null || detailIds.isEmpty()) {
			return;
		}
		List<ProformaDetailModel> details = proformaDetailRepository.findAllById(detailIds);
		for (ProformaDetailModel detail : details) {
			detail.setProformaFileId(fileId);
		}
		proformaDetailRepository.saveAll(details);
		log.info("شناسه فایل FMS برای {} جزئیات پیش فاکتور ذخیره شد: {}", details.size(), fileId);
	}

	private byte[] buildSignedProformaPdf(List<Long> detailIds) {

		List<XWPFDocument> documents = detailIds.stream()
				.map(exportDocService::exportProformaDoc)
				.filter(bytes -> bytes != null && bytes.length > 0)
				.map(this::toXwpfDocument)
				.toList();

		if (documents.isEmpty()) {
			throw new InternalSaleCustomException.FileContentException("لیست فایلها خالی است");
		}

		return exportDocService.convertDocListToPdf(documents);
	}

	private XWPFDocument toXwpfDocument(byte[] docBytes) {
		try {
			return new XWPFDocument(new ByteArrayInputStream(docBytes));
		} catch (IOException ex) {
			throw new InternalSaleCustomException.FileContentException("خطا در بارگذاری فایل پیش فاکتور");
		}
	}

	private RemittanceMasterModel findRemittanceMaster(Long masterId) {
		return remittanceMasterRepository.findById(masterId)
				.orElseThrow(() -> new IllegalArgumentException("Remittance Master not found with ID: " + masterId));
	}

	private String uploadRemittanceToFms(Long masterId, String fileName, byte[] pdfContent, FmsCredentials credentials) {

		UploadRequest uploadRequest = UploadRequest.of(fmsGroupId, fileName, pdfContent)
				.contentType(PDF_CONTENT_TYPE)
				.tag(REMITTANCE_TAG_TYPE, REMITTANCE_TAG_TYPE_VALUE)
				.tag(REMITTANCE_TAG_ID, masterId)
				.tag(REMITTANCE_TAG_ID_CLASS, REMITTANCE_TAG_ID_CLASS_VALUE);

		String uuid = fmsFileService.upload(uploadRequest, credentials);

		log.info("فایل حواله {} با موفقیت در FMS آپلود شد. uuid={}", masterId, uuid);

		return uuid;
	}

	private FmsFile uploadRemittanceToFmsAndGetFile(Long masterId, String fileName, byte[] pdfContent) {

		FmsCredentials credentials = FmsCredentials.oauth(getCurrentUserToken());

		UploadRequest uploadRequest = UploadRequest.of(fmsGroupId, fileName, pdfContent)
				.contentType(PDF_CONTENT_TYPE)
				.tag(REMITTANCE_TAG_TYPE, REMITTANCE_TAG_TYPE_VALUE)
				.tag(REMITTANCE_TAG_ID, masterId)
				.tag(REMITTANCE_TAG_ID_CLASS, REMITTANCE_TAG_ID_CLASS_VALUE);

		String uuid = fmsFileService.upload(uploadRequest, credentials);

		return new FmsFile(uuid, fileName, PDF_CONTENT_TYPE, pdfContent);
	}

	private void saveRemittanceFileIdToMaster(Long masterId, String fileId) {
		RemittanceMasterModel master = findRemittanceMaster(masterId);
		if (!Objects.equals(master.getRemittanceFileId(), fileId)) {
			master.setRemittanceFileId(fileId);
			remittanceMasterRepository.save(master);
			log.info("Saved Remittance File ID {} to Master {}", fileId, masterId);
		}
	}
}