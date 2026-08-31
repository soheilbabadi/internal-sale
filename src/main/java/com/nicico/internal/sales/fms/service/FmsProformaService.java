package com.nicico.internal.sales.fms.service;

import com.fgostar.fms.sdk.FmsFileService;
import com.fgostar.fms.sdk.auth.FmsCredentials;
import com.fgostar.fms.sdk.dto.FileDto;
import com.fgostar.fms.sdk.model.FilePage;
import com.fgostar.fms.sdk.model.FmsFile;
import com.fgostar.fms.sdk.model.UploadRequest;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.service.ExportDocService;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FmsProformaService {

	private static final String FILE_NAME_PREFIX = "proforma_";
	private static final String PDF_CONTENT_TYPE = "application/pdf";

	private static final String TAG_TYPE = "entityType";
	private static final String TAG_ID = "id";
	private static final String TAG_ID_CLASS = "idClass";
	private static final String TAG_TYPE_VALUE = "proforma";
	private static final String TAG_ID_CLASS_VALUE = "com.nicico.internal.sales.proforma.model.ProformaMasterModel";

	private final FmsFileService fmsFileService;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ExportDocService exportDocService;

	@Value("${nicico.fms.group-id}")
	private String fmsGroupId;

	/**
	 * Generates the signed proforma PDF and uploads it to FMS with tags:
	 * entityType=proforma, id={masterId}, idClass=ProformaMasterModel
	 * <p>
	 * Uses the current user's OAuth token for FMS authentication.
	 */
	public void uploadProformaPdfToFms(Long masterId) {
		try {
			ProformaMasterModel masterModel = findMaster(masterId);
			List<Long> activeDetailIds = getActiveDetailIds(masterModel);

			if (activeDetailIds.isEmpty()) {
				log.warn("هیچ جزئیات فعالی برای پیش فاکتور {} یافت نشد، آپلود FMS انجام نشد", masterId);
				return;
			}

			byte[] pdfContent = buildSignedProformaPdf(activeDetailIds);
			String fileName = FILE_NAME_PREFIX + masterModel.getContractNo() + ".pdf";
			uploadToFms(masterId, fileName, pdfContent);

		} catch (Exception ex) {
			log.error("خطا در آپلود فایل پیش فاکتور {} در FMS: {}", masterId, ex.getMessage(), ex);
			// Don't throw — FMS upload failure should not block the workflow
		}
	}

	/**
	 * Returns the proforma PDF from FMS if it already exists (matched by tags),
	 * otherwise generates it, uploads to FMS, and returns the result.
	 *
	 * @return FmsFile containing the PDF bytes, or null if the proforma has no active details
	 */
	public FmsFile getOrCreateProformaPdf(Long masterId) {
		FmsCredentials credentials = FmsCredentials.oauth(SecurityUtil.getCurrentUserToken());

		// 1. search by tags
		Map<String, Object> searchTags = Map.of(
				TAG_TYPE, TAG_TYPE_VALUE,
				TAG_ID, masterId,
				TAG_ID_CLASS, TAG_ID_CLASS_VALUE);

		FilePage page = fmsFileService.searchInGroup(fmsGroupId, searchTags, true, 0, 1, credentials);

		if (!page.isEmpty()) {
			FileDto existing = page.getFiles().get(0);
			log.info("فایل پیش فاکتور {} در FMS یافت شد. uuid={}", masterId, existing.getUuid());
			return fmsFileService.download(fmsGroupId, existing.getUuid(), credentials);
		}

		// 2. not found — generate, upload, return
		log.info("فایل پیش فاکتور {} در FMS یافت نشد، در حال ساخت...", masterId);
		ProformaMasterModel masterModel = findMaster(masterId);
		List<Long> activeDetailIds = getActiveDetailIds(masterModel);

		if (activeDetailIds.isEmpty()) {
			log.warn("هیچ جزئیات فعالی برای پیش فاکتور {} یافت نشد", masterId);
			return null;
		}

		byte[] pdfContent = buildSignedProformaPdf(activeDetailIds);
		String fileName = FILE_NAME_PREFIX + masterModel.getContractNo() + ".pdf";
		String uuid = uploadToFms(masterId, fileName, pdfContent, credentials);

		return new FmsFile(uuid, fileName, PDF_CONTENT_TYPE, pdfContent);
	}

	private ProformaMasterModel findMaster(Long masterId) {
		return proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("پیش فاکتور وجود ندارد"));
	}

	private List<Long> getActiveDetailIds(ProformaMasterModel masterModel) {
		return masterModel.getProformaDetailModelLists().stream()
				.filter(detail -> detail.getProformaReversalStatus() != ProformaReversalStatus.CANCELED)
				.map(ProformaDetailModel::getId)
				.toList();
	}

	private void uploadToFms(Long masterId, String fileName, byte[] pdfContent) {
		uploadToFms(masterId, fileName, pdfContent, FmsCredentials.oauth(SecurityUtil.getCurrentUserToken()));
	}

	private String uploadToFms(Long masterId, String fileName, byte[] pdfContent, FmsCredentials credentials) {
		UploadRequest request = UploadRequest.of(fmsGroupId, fileName, pdfContent)
				.contentType(PDF_CONTENT_TYPE)
				.tag(TAG_TYPE, TAG_TYPE_VALUE)
				.tag(TAG_ID, masterId)
				.tag(TAG_ID_CLASS, TAG_ID_CLASS_VALUE);

		String uuid = fmsFileService.upload(request, credentials);
		log.info("فایل پیش فاکتور {} با موفقیت در FMS آپلود شد. uuid={}", masterId, uuid);
		return uuid;
	}

	private byte[] buildSignedProformaPdf(List<Long> detailIds) {
		List<XWPFDocument> documents = detailIds.stream()
				.map(exportDocService::exportProformaDocOnlySigned)
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
}
