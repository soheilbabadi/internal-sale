//package com.nicico.internal.sales.lc.service;
//
//import com.nicico.copper.common.dto.search.SearchDTO;
//import com.nicico.internal.sales.exception.InternalSaleCustomException;
//import com.nicico.internal.sales.lc.dto.*;
//import com.nicico.internal.sales.lc.model.LcModel;
//import com.nicico.internal.sales.lc.repository.LcRepository;
//import com.nicico.internal.sales.performa.bank.model.IssuingBankModel;
//import com.nicico.internal.sales.performa.bank.model.TradingBankModel;
//import com.nicico.internal.sales.performa.bank.repository.IssuingBankRepository;
//import com.nicico.internal.sales.performa.bank.repository.TradingBankRepository;
//import com.nicico.internal.sales.performa.model.PerformaDetailModel;
//import com.nicico.internal.sales.performa.model.PerformaMasterModel;
//import com.nicico.internal.sales.performa.repository.ProformaDetailRepository;
//import com.nicico.internal.sales.performa.salecondition.dto.SaleConditionDto;
//import com.nicico.internal.sales.performa.salecondition.service.SaleConditionService;
//import com.nicico.internal.sales.wf.service.StatusMarshalService;
//import org.hibernate.envers.AuditReader;
//import org.hibernate.envers.AuditReaderFactory;
//import org.hibernate.envers.DefaultRevisionEntity;
//import org.hibernate.envers.RevisionType;
//import org.hibernate.envers.query.AuditQuery;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.MessageSource;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//
//import javax.persistence.EntityManager;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class LcServiceImplTest {
//
//    @Mock
//    private EntityManager entityManager;
//
//    @Mock
//    private ProformaDetailRepository proformaDetailRepository;
//
//    @Mock
//    private IssuingBankRepository issuingBankRepository;
//
//    @Mock
//    private TradingBankRepository tradingBankRepository;
//
//    @Mock
//    private LcRepository lcRepository;
//
//    @Mock
//    private LcMapper lcMapper;
//
//    @Mock
//    private StatusMarshalService statusMarshalService;
//
//    @Mock
//    private SaleConditionService saleConditionService;
//
//    @Mock
//    private MessageSource messageSource;
//
//    @InjectMocks
//    private LcServiceImpl lcService;
//
//    private UpdateLcRequest updateLcRequest;
//    private LcModel lcModel;
//    private PerformaDetailModel performaDetail;
//    private PerformaMasterModel performaMaster;
//    private TradingBankModel tradingBank;
//    private IssuingBankModel issuingBank;
//    private LcDto.Info lcInfo;
//    private Date testDate;
//
//    @BeforeEach
//    void setUp() {
//        testDate = new Date();
//
//
//        updateLcRequest = new UpdateLcRequest();
//        updateLcRequest.setProformaId(1L);
//        updateLcRequest.setLcNo("12212121121");
//        updateLcRequest.setLcDate(testDate);
//        updateLcRequest.setTradingBankId(1L);
//        updateLcRequest.setIssuerBankId(1L);
//        updateLcRequest.setLcAttachmentId("456778777");
//        updateLcRequest.setRequireDispatchFile(false);
//
//
//        lcModel = new LcModel();
//        lcModel.setId(1L);
//        lcModel.setTradingBankId(1L);
//        lcModel.setIssuerBankId(1L);
//        lcModel.setLcNo("12212121121");
//        lcModel.setProcessId("process-123");
//
//
//        performaMaster = new PerformaMasterModel();
//        performaMaster.setId(1L);
//        performaMaster.setContractNo(1404434615003L);
//        performaMaster.setGoodId(1L);
//
//
//        performaDetail = new PerformaDetailModel();
//        performaDetail.setId(1L);
//        performaDetail.setPerformaNo("140400015");
//        performaDetail.setPerformaDate(testDate);
//        performaDetail.setCreditExpirePeriod(6);
//        performaDetail.setDeadlineDays(30);
//        performaDetail.setPerformaMasterModel(performaMaster);
//
//        // Setup TradingBank
//        tradingBank = new TradingBankModel();
//        tradingBank.setId(1L);
//        tradingBank.setBankTitle("بانک تجارت");
//        tradingBank.setBankBranchTitle("امام خمینی");
//
//        // Setup IssuingBank
//        issuingBank = new IssuingBankModel();
//        issuingBank.setId(1L);
//        issuingBank.setBankName("بانک تجارت");
//        issuingBank.setBranchName("امام خمینی ");
//        issuingBank.setBranchCode("001");
//
//        // Setup LcDto.Info
//        lcInfo = new LcDto.Info(testDate, testDate, "12212121121", "بانک تجارت", "امام خمینی");
//        lcInfo.setId(1L);
//
//        // Setup MessageSource mocks
//        when(messageSource.getMessage(eq("notification.error.lc.notfound"), any(), any()))
//                .thenReturn("اعتبار اسنادی وجود ندارد");
//        when(messageSource.getMessage(eq("notification.error.proforma.notfound"), any(), any()))
//                .thenReturn("پیش فاکتور وجود ندارد");
//        when(messageSource.getMessage(eq("notification.error.trading.bank.notfound"), any(), any()))
//                .thenReturn("شعبه بانک وجود ندارد");
//        when(messageSource.getMessage(eq("notification.error.issuing.bank.notfound"), any(), any()))
//                .thenReturn("بانک گشایش کننده وجود ندارد");
//        when(messageSource.getMessage(eq("notification.error.lc.date.empty"), any(), any()))
//                .thenReturn("تاریخ گشایش اعتبار اسنادی نمی تواند خالی باشد");
//        when(messageSource.getMessage(eq("notification.error.lc.date.before.proforma"), any(), any()))
//                .thenReturn("تاریخ گشایش اعتبار اسنادی نمی تواند قبل از تاریخ پیش فاکتور باشد");
//        when(messageSource.getMessage(eq("notification.error.lc.dispatch.file.required"), any(), any()))
//                .thenReturn("برای این اعتبار اسنادی فایل ابلاغیه فروش الزامی است");
//    }
//
//    @Test
//    void updateLc_Success() {
//
//        when(lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(anyLong()))
//                .thenReturn(Optional.of(lcModel));
//        when(tradingBankRepository.findById(anyLong())).thenReturn(Optional.of(tradingBank));
//        when(issuingBankRepository.findById(anyLong())).thenReturn(Optional.of(issuingBank));
//        when(proformaDetailRepository.findById(anyLong())).thenReturn(Optional.of(performaDetail));
//        when(lcRepository.saveAndFlush(any(LcModel.class))).thenReturn(lcModel);
//        when(lcMapper.toDTO(any(LcModel.class))).thenReturn(lcInfo);
//
//        var saleCondition = new SaleConditionDto.Info();
//        saleCondition.setCreditExpirePeriod(6);
//        when(saleConditionService.getCurrentRule(anyLong())).thenReturn(saleCondition);
//
//
//        LcDto.Info result = lcService.updateLc(updateLcRequest);
//
//
//        assertNotNull(result);
//        assertEquals("12212121121", result.getLcNo());
//        verify(statusMarshalService).refreshPrformaStatus();
//        verify(statusMarshalService).startFailedProforma();
//        verify(lcRepository).saveAndFlush(any(LcModel.class));
//    }
//
//    @Test
//    void updateLc_LcNotFound_ThrowsException() {
//
//        when(lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(anyLong()))
//                .thenReturn(Optional.empty());
//
//
//        assertThrows(InternalSaleCustomException.ValidationException.class,
//                () -> lcService.updateLc(updateLcRequest));
//    }
//
//    @Test
//    void updateLc_ProformaNotFound_ThrowsException() {
//
//        when(lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(anyLong()))
//                .thenReturn(Optional.of(lcModel));
//        when(tradingBankRepository.findById(anyLong())).thenReturn(Optional.of(tradingBank));
//        when(issuingBankRepository.findById(anyLong())).thenReturn(Optional.of(issuingBank));
//        when(proformaDetailRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//
//        assertThrows(InternalSaleCustomException.ValidationException.class,
//                () -> lcService.updateLc(updateLcRequest));
//    }
//
//    @Test
//    void updateLc_LcDateNull_ThrowsException() {
//
//        updateLcRequest.setLcDate(null);
//        when(lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(anyLong()))
//                .thenReturn(Optional.of(lcModel));
//        when(tradingBankRepository.findById(anyLong())).thenReturn(Optional.of(tradingBank));
//        when(issuingBankRepository.findById(anyLong())).thenReturn(Optional.of(issuingBank));
//        when(proformaDetailRepository.findById(anyLong())).thenReturn(Optional.of(performaDetail));
//
//
//        assertThrows(InternalSaleCustomException.ValidationException.class,
//                () -> lcService.updateLc(updateLcRequest));
//    }
//
//    @Test
//    void updateLc_LcDateBeforeProformaDate_ThrowsException() {
//
//        Date pastDate = Date.from(LocalDateTime.now().minusDays(10).atZone(ZoneId.systemDefault()).toInstant());
//        updateLcRequest.setLcDate(pastDate);
//        performaDetail.setPerformaDate(testDate);
//
//        when(lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(anyLong()))
//                .thenReturn(Optional.of(lcModel));
//        when(tradingBankRepository.findById(anyLong())).thenReturn(Optional.of(tradingBank));
//        when(issuingBankRepository.findById(anyLong())).thenReturn(Optional.of(issuingBank));
//        when(proformaDetailRepository.findById(anyLong())).thenReturn(Optional.of(performaDetail));
//
//
//        assertThrows(InternalSaleCustomException.ValidationException.class,
//                () -> lcService.updateLc(updateLcRequest));
//    }
//
//    @Test
//    void updateLc_TradingBankNotFound_ThrowsException() {
//
//        when(lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(anyLong()))
//                .thenReturn(Optional.of(lcModel));
//        when(tradingBankRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//
//        assertThrows(InternalSaleCustomException.ValidationException.class,
//                () -> lcService.updateLc(updateLcRequest));
//    }
//
//    @Test
//    void updateLc_IssuingBankNotFound_ThrowsException() {
//
//        when(lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(anyLong()))
//                .thenReturn(Optional.of(lcModel));
//        when(tradingBankRepository.findById(anyLong())).thenReturn(Optional.of(tradingBank));
//        when(issuingBankRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//
//        assertThrows(InternalSaleCustomException.ValidationException.class,
//                () -> lcService.updateLc(updateLcRequest));
//    }
//
//    @Test
//    void UpdateLc_Success() {
//
//        LcUpdateDto lcUpdateDto = new LcUpdateDto();
//        lcUpdateDto.setId(1L);
//        lcUpdateDto.setLcNo("LC-002");
//        lcUpdateDto.setLcDate(testDate);
//        lcUpdateDto.setTradingBankId(1L);
//        lcUpdateDto.setIssuerBankId(1L);
//        lcUpdateDto.setLcExpiryDate(testDate);
//        lcUpdateDto.setNosaCode("NOSA-001");
//        lcUpdateDto.setPaymentDeferral(0);
//        lcUpdateDto.setDeadlineDays(30);
//
//        when(lcRepository.findById(anyLong())).thenReturn(Optional.of(lcModel));
//        when(tradingBankRepository.findById(anyLong())).thenReturn(Optional.of(tradingBank));
//        when(issuingBankRepository.findById(anyLong())).thenReturn(Optional.of(issuingBank));
//        when(lcRepository.saveAndFlush(any(LcModel.class))).thenReturn(lcModel);
//        when(lcMapper.toDTO(any(LcModel.class))).thenReturn(lcInfo);
//
//
//        LcDto.Info result = lcService.UpdateLc(lcUpdateDto);
//
//
//        assertNotNull(result);
//        verify(lcRepository).deleteAllByProcessId(anyString());
//        verify(lcRepository).saveAndFlush(any(LcModel.class));
//    }
//
//    @Test
//    void updateLcFiles_Success() {
//
//        LcFilesDto lcFilesDto = new LcFilesDto();
//        lcFilesDto.setId(1L);
//        lcFilesDto.setNotificationFileId("234567865433");
//        lcFilesDto.setDispatchFileId("234567865433");
//        lcFilesDto.setNosaCode("NOSA-001");
//
//        lcModel.setRequireDispatchFile(false);
//        when(lcRepository.findById(anyLong())).thenReturn(Optional.of(lcModel));
//        when(lcRepository.saveAndFlush(any(LcModel.class))).thenReturn(lcModel);
//        LcFilesDto result = lcService.updateLcFiles(lcFilesDto);
//        assertNotNull(result);
//        assertEquals("NOSA-001", result.getNosaCode());
//        verify(lcRepository).saveAndFlush(any(LcModel.class));
//    }
//
//    @Test
//    void updateLcFiles_DispatchFileRequired_ThrowsException() {
//
//        LcFilesDto lcFilesDto = new LcFilesDto();
//        lcFilesDto.setId(1L);
//        lcFilesDto.setDispatchFileId(null);
//
//        lcModel.setRequireDispatchFile(true);
//        when(lcRepository.findById(anyLong())).thenReturn(Optional.of(lcModel));
//
//        assertThrows(InternalSaleCustomException.ValidationException.class,
//                () -> lcService.updateLcFiles(lcFilesDto));
//    }
//
//    @Test
//    void getLcData_Success() {
//
//        when(lcRepository.findById(anyLong())).thenReturn(Optional.of(lcModel));
//        when(lcMapper.toDTO(any(LcModel.class))).thenReturn(lcInfo);
//
//
//        LcDto.Info result = lcService.getLcData(1L);
//
//
//        assertNotNull(result);
//        assertEquals("12212121121", result.getLcNo());
//    }
//
//    @Test
//    void getLcData_NotFound_ThrowsException() {
//
//        when(lcRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//
//        assertThrows(InternalSaleCustomException.ValidationException.class,
//                () -> lcService.getLcData(1L));
//    }
//
//    @Test
//    void getAllLcDataByProformaMasterId_Success() {
//
//        List<LcModel> lcModels = Arrays.asList(lcModel);
//        when(lcRepository.findAllByProformaMasterId(anyLong())).thenReturn(lcModels);
//        when(lcMapper.toDTO(any(LcModel.class))).thenReturn(lcInfo);
//
//
//        List<LcDto.Info> result = lcService.getAllLcDataByProformaMasterId(1L);
//
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void getAllLcDataByProformaMasterId_EmptyList() {
//
//        when(lcRepository.findAllByProformaMasterId(anyLong())).thenReturn(Collections.emptyList());
//
//
//        List<LcDto.Info> result = lcService.getAllLcDataByProformaMasterId(1L);
//
//
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void getByProformaDetailId_Success() {
//
//        when(lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(anyLong()))
//                .thenReturn(Optional.of(lcModel));
//        when(lcMapper.toDTO(any(LcModel.class))).thenReturn(lcInfo);
//
//
//        LcDto.Info result = lcService.getByProformaDetailId(1L);
//
//
//        assertNotNull(result);
//        assertEquals("12212121121", result.getLcNo());
//    }
//
//    @Test
//    void getByProformaDetailId_NotFound_ReturnsNull() {
//
//        when(lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(anyLong()))
//                .thenReturn(Optional.empty());
//        when(lcMapper.toDTO(null)).thenReturn(null);
//
//
//        LcDto.Info result = lcService.getByProformaDetailId(1L);
//
//
//        assertNull(result);
//    }
//
//    @Test
//    void getAllLcDataByProcessInstanceId_Success() {
//
//        List<LcModel> lcModels = Arrays.asList(lcModel);
//        when(lcRepository.findAllByProcessId(anyString())).thenReturn(lcModels);
//        when(lcMapper.toDTO(any(LcModel.class))).thenReturn(lcInfo);
//        List<LcDto.Info> result = lcService.getAllLcDataByProcessInstanceId(UUID.randomUUID().toString());
//
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void getFailedLc_Success() {
//
//        Pageable pageable = PageRequest.of(0, 10);
//        Sort sort = Sort.by("id").descending();
//        List<LcModel> lcModels = Arrays.asList(lcModel);
//        when(lcMapper.toDTO(any(LcModel.class))).thenReturn(lcInfo);
//
//
//        List<LcDto.Info> result = lcService.getFailedLc(pageable, sort);
//
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void calculateExpireDate_Success() {
//
//        var saleCondition = new SaleConditionDto.Info();
//        saleCondition.setCreditExpirePeriod(6);
//
//        when(proformaDetailRepository.findById(anyLong())).thenReturn(Optional.of(performaDetail));
//        when(saleConditionService.getCurrentRule(anyLong())).thenReturn(saleCondition);
//        Date result = lcService.calculateExpireDate(updateLcRequest);
//        assertNotNull(result);
//    }
//
//    @Test
//    void getAuditHistory_Success() {
//        Long lcId = 1L;
//        when(lcRepository.findById(lcId)).thenReturn(Optional.of(lcModel));
//        AuditReader auditReader = mock(AuditReader.class);
//        AuditQuery auditQuery = mock(AuditQuery.class);
//        try (MockedStatic<AuditReaderFactory> mockedFactory = mockStatic(AuditReaderFactory.class)) {
//            mockedFactory.when(() -> AuditReaderFactory.get(entityManager)).thenReturn(auditReader);
//
//            List<Number> revisions = Arrays.asList(1, 2);
//            when(auditReader.getRevisions(LcModel.class, lcId)).thenReturn(revisions);
//            when(auditQuery.add(any())).thenReturn(auditQuery);
//
//            DefaultRevisionEntity revisionEntity = new DefaultRevisionEntity();
//            revisionEntity.setId(1);
//            revisionEntity.setTimestamp(System.currentTimeMillis());
//
//            Object[] auditData = {lcModel, revisionEntity, RevisionType.ADD};
//            when(auditQuery.getSingleResult()).thenReturn(auditData);
//
//
//            List<LcAuditDto> result = lcService.getAuditHistory(lcId);
//
//
//            assertNotNull(result);
//            assertEquals(2, result.size());
//        }
//    }
//
//    @Test
//    void getAuditHistory_LcNotFound_ThrowsException() {
//        when(lcRepository.findById(anyLong())).thenReturn(Optional.empty());
//        assertThrows(InternalSaleCustomException.ValidationException.class,
//                () -> lcService.getAuditHistory(1L));
//    }
//
//    @Test
//    void search_Success() {
//        SearchDTO.SearchRq searchRq = new SearchDTO.SearchRq();
//        SearchDTO.SearchRs<LcDto.Info> searchRs = new SearchDTO.SearchRs<>();
//
//        assertDoesNotThrow(() -> lcService.search(searchRq));
//    }
//}
