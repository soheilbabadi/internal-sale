package com.nicico.internal.sales.fms.service;

import com.fgostar.fms.sdk.FmsFileService;
import com.fgostar.fms.sdk.auth.FmsCredentials;
import com.fgostar.fms.sdk.dto.FileDto;
import com.fgostar.fms.sdk.model.FilePage;
import com.fgostar.fms.sdk.model.FmsFile;
import com.fgostar.fms.sdk.model.UploadRequest;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.insurance.domain.model.RemittanceMasterModel;
import com.nicico.insurance.domain.repository.RemittanceMasterRepository;
import com.nicico.insurance.service.ExportDocService;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private static final String REMITTANCE_GROUP_ID = "remittance-group";
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final FmsFileService fmsFileService;
    private final ProformaMasterRepository proformaMasterRepository;
    private final ProformaDetailRepository proformaDetailRepository;
    private final RemittanceMasterRepository remittanceMasterRepository;
    private final ExportDocService exportDocService;

    @Value("${nicico.fms.group-id}")
    private String fmsGroupId;

    @Override
    public FmsFile uploadProformaPdfToFms(Long masterId) {
        try {
            ProformaMasterModel masterModel = findProformaMaster(masterId);
            List<Long> activeDetailIds = getActiveProformaDetailIds(masterModel);

            if (activeDetailIds.isEmpty()) {
                log.warn("هیچ جزئیات فعالی برای پیش فاکتور {} یافت نشد، آپلود FMS انجام نشد", masterId);
                return null;
            }

            byte[] pdfContent = buildSignedProformaPdf(activeDetailIds);
            String fileName = PROFORMA_FILE_NAME_PREFIX + masterModel.getContractNo() + ".pdf";
            FmsFile fmsFile = uploadProformaToFmsAndGetFile(masterId, fileName, pdfContent);
            saveProformaFileIdToDetails(activeDetailIds, fmsFile.getUuid());
            return fmsFile;

        } catch (Exception ex) {
            log.error("خطا در آپلود فایل پیش فاکتور {} در FMS: {}", masterId, ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    public FmsFile getOrCreateProformaPdf(Long masterId) {
        FmsCredentials credentials = FmsCredentials.oauth(SecurityUtil.getCurrentUserToken());

        Map<String, Object> searchTags = Map.of(
                PROFORMA_TAG_TYPE, PROFORMA_TAG_TYPE_VALUE,
                PROFORMA_TAG_ID, masterId,
                PROFORMA_TAG_ID_CLASS, PROFORMA_TAG_ID_CLASS_VALUE);

        FilePage page = fmsFileService.searchInGroup(fmsGroupId, searchTags, true, 0, 1, credentials);

        if (!page.isEmpty()) {
            FileDto existing = page.getFiles().get(0);
            log.info("فایل پیش فاکتور {} در FMS یافت شد. uuid={}", masterId, existing.getUuid());
            return fmsFileService.download(fmsGroupId, existing.getUuid(), credentials);
        }

        log.info("فایل پیش فاکتور {} در FMS یافت نشد، در حال ساخت...", masterId);
        ProformaMasterModel masterModel = findProformaMaster(masterId);
        List<Long> activeDetailIds = getActiveProformaDetailIds(masterModel);

        if (activeDetailIds.isEmpty()) {
            log.warn("هیچ جزئیات فعالی برای پیش فاکتور {} یافت نشد", masterId);
            return null;
        }

        byte[] pdfContent = buildSignedProformaPdf(activeDetailIds);
        String fileName = PROFORMA_FILE_NAME_PREFIX + masterModel.getContractNo() + ".pdf";
        String uuid = uploadProformaToFms(masterId, fileName, pdfContent, credentials);

        return new FmsFile(uuid, fileName, PDF_CONTENT_TYPE, pdfContent);
    }

    @Override
    public byte[] getProformaPdfBytes(Long detailId) {
        ProformaDetailModel detail = proformaDetailRepository.findById(detailId)
                .orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("جزئیات پیش فاکتور وجود ندارد"));

        if (detail.getProformaFileId() != null && !detail.getProformaFileId().isEmpty()) {
            log.info("فایل پیش فاکتور {} در FMS موجود است. uuid={}", detailId, detail.getProformaFileId());
            FmsCredentials credentials = FmsCredentials.oauth(SecurityUtil.getCurrentUserToken());
            FmsFile fmsFile = fmsFileService.download(fmsGroupId, detail.getProformaFileId(), credentials);
            return fmsFile.getContent();
        }

        log.info("فایل پیش فاکتور {} در FMS موجود نیست، در حال ساخت...", detailId);
        
        if (detail.getProformaReversalStatus() == ProformaReversalStatus.CANCELED) {
            log.warn("جزئیات پیش فاکتور {} لغو شده است", detailId);
            return null;
        }

        Long masterId = detail.getProformaMasterId();
        ProformaMasterModel masterModel = findProformaMaster(masterId);
        List<Long> activeDetailIds = getActiveProformaDetailIds(masterModel);

        if (activeDetailIds.isEmpty()) {
            log.warn("هیچ جزئیات فعالی برای پیش فاکتور {} یافت نشد", masterId);
            return null;
        }

        byte[] pdfContent = buildSignedProformaPdf(activeDetailIds);
        String fileName = PROFORMA_FILE_NAME_PREFIX + masterModel.getContractNo() + ".pdf";
        FmsFile fmsFile = uploadProformaToFmsAndGetFile(masterId, fileName, pdfContent);
        saveProformaFileIdToDetails(activeDetailIds, fmsFile.getUuid());

        log.info("فایل پیش فاکتور {} با موفقیت در FMS آپلود و ذخیره شد. uuid={}", detailId, fmsFile.getUuid());
        return pdfContent;
    }

    @Override
    public FmsFile downloadProformaPdfFromFms(Long detailId) {
        ProformaDetailModel detail = proformaDetailRepository.findById(detailId)
                .orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("جزئیات پیش فاکتور وجود ندارد"));

        if (detail.getProformaFileId() == null || detail.getProformaFileId().isEmpty()) {
            log.warn("شناسه فایل FMS برای جزئیات پیش فاکتور {} یافت نشد", detailId);
            return null;
        }

        log.info("در حال دانلود فایل پیش فاکتور {} از FMS. uuid={}", detailId, detail.getProformaFileId());
        FmsCredentials credentials = FmsCredentials.oauth(SecurityUtil.getCurrentUserToken());
        FmsFile fmsFile = fmsFileService.download(fmsGroupId, detail.getProformaFileId(), credentials);
        log.info("فایل پیش فاکتور {} با موفقیت از FMS دانلود شد", detailId);
        return fmsFile;
    }

    @Override
    @Transactional
    public FmsFile uploadRemittancePdfToFms(Long masterId) {
        RemittanceMasterModel master = findRemittanceMaster(masterId);
        
        byte[] pdfContent = exportDocService.exportRemittancePdf(masterId);
        
        if (pdfContent == null || pdfContent.length == 0) {
            throw new IllegalStateException("Failed to generate Remittance PDF for ID: " + masterId);
        }

        String fileName = "remittance_" + masterId + ".pdf";
        FmsFile fmsFile = uploadRemittanceToFmsAndGetFile(masterId, fileName, pdfContent);
        saveRemittanceFileIdToMaster(masterId, fmsFile.getUuid());
        
        return fmsFile;
    }

    @Override
    @Transactional
    public FmsFile getOrCreateRemittancePdf(Long masterId) {
        RemittanceMasterModel master = findRemittanceMaster(masterId);
        String fileId = master.getRemittanceFileId();

        if (fileId != null && !fileId.isEmpty()) {
            try {
                log.info("Remittance PDF found in FMS for master {}: {}", masterId, fileId);
                return fmsFileService.download(fileId);
            } catch (Exception e) {
                log.warn("Failed to download existing Remittance PDF from FMS ({}). Regenerating...", fileId, e);
            }
        }

        log.info("Remittance PDF not found in FMS for master {}. Generating and uploading...", masterId);
        return uploadRemittancePdfToFms(masterId);
    }

    @Override
    public byte[] getRemittancePdfBytes(Long masterId) {
        FmsFile file = getOrCreateRemittancePdf(masterId);
        return file != null ? file.getContent() : null;
    }

    @Override
    public FmsFile downloadRemittancePdfFromFms(Long masterId) {
        RemittanceMasterModel master = findRemittanceMaster(masterId);
        String uuid = master.getRemittanceFileId();

        if (uuid == null || uuid.isEmpty()) {
            log.warn("No Remittance File ID found in database for master {}. Cannot download from FMS.", masterId);
            return null;
        }

        log.info("Downloading Remittance PDF from FMS for master {}: {}", masterId, uuid);
        return fmsFileService.download(uuid);
    }

    private ProformaMasterModel findProformaMaster(Long masterId) {
        return proformaMasterRepository.findById(masterId)
                .orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("پیش فاکتور وجود ندارد"));
    }

    private List<Long> getActiveProformaDetailIds(ProformaMasterModel masterModel) {
        return masterModel.getProformaDetailModelLists().stream()
                .filter(detail -> detail.getProformaReversalStatus() != ProformaReversalStatus.CANCELED)
                .map(ProformaDetailModel::getId)
                .toList();
    }

    private String uploadProformaToFms(Long masterId, String fileName, byte[] pdfContent) {
        return uploadProformaToFms(masterId, fileName, pdfContent, FmsCredentials.oauth(SecurityUtil.getCurrentUserToken()));
    }

    private String uploadProformaToFms(Long masterId, String fileName, byte[] pdfContent, FmsCredentials credentials) {
        UploadRequest request = UploadRequest.of(fmsGroupId, fileName, pdfContent)
                .contentType(PDF_CONTENT_TYPE)
                .tag(PROFORMA_TAG_TYPE, PROFORMA_TAG_TYPE_VALUE)
                .tag(PROFORMA_TAG_ID, masterId)
                .tag(PROFORMA_TAG_ID_CLASS, PROFORMA_TAG_ID_CLASS_VALUE);

        String uuid = fmsFileService.upload(request, credentials);
        log.info("فایل پیش فاکتور {} با موفقیت در FMS آپلود شد. uuid={}", masterId, uuid);
        return uuid;
    }

    private FmsFile uploadProformaToFmsAndGetFile(Long masterId, String fileName, byte[] pdfContent) {
        FmsCredentials credentials = FmsCredentials.oauth(SecurityUtil.getCurrentUserToken());
        UploadRequest request = UploadRequest.of(fmsGroupId, fileName, pdfContent)
                .contentType(PDF_CONTENT_TYPE)
                .tag(PROFORMA_TAG_TYPE, PROFORMA_TAG_TYPE_VALUE)
                .tag(PROFORMA_TAG_ID, masterId)
                .tag(PROFORMA_TAG_ID_CLASS, PROFORMA_TAG_ID_CLASS_VALUE);

        String uuid = fmsFileService.upload(request, credentials);
        log.info("فایل پیش فاکتور {} با موفقیت در FMS آپلود شد. uuid={}", masterId, uuid);
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

    private RemittanceMasterModel findRemittanceMaster(Long masterId) {
        return remittanceMasterRepository.findById(masterId)
                .orElseThrow(() -> new IllegalArgumentException("Remittance Master not found with ID: " + masterId));
    }

    private String uploadRemittanceToFms(Long masterId, String fileName, byte[] pdfContent) {
        return fmsFileService.upload(REMITTANCE_GROUP_ID, fileName, pdfContent);
    }

    private FmsFile uploadRemittanceToFmsAndGetFile(Long masterId, String fileName, byte[] pdfContent) {
        String uuid = fmsFileService.upload(REMITTANCE_GROUP_ID, fileName, pdfContent);
        log.info("Uploaded Remittance PDF to FMS. UUID: {}, Master: {}", uuid, masterId);
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
