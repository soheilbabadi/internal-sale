//package com.nicico.internal.sales.bank.service;
//
//import com.nicico.copper.common.dto.search.SearchDTO;
//import com.nicico.internal.sales.bank.dto.IssuingBankDto;
//import com.nicico.internal.sales.bank.dto.IssuingBankMapper;
//import com.nicico.internal.sales.bank.model.BaseBankModel;
//import com.nicico.internal.sales.bank.model.IssuingBankModel;
//import com.nicico.internal.sales.bank.repository.BaseBankRepository;
//import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
//import com.nicico.internal.sales.exception.InternalSaleCustomException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class IssuingServiceImplTest {
//
//    @Mock
//    private IssuingBankRepository repository;
//
//    @Mock
//    private IssuingBankMapper mapper;
//
//    @Mock
//    private BaseBankRepository baseBankRepository;
//
//    @InjectMocks
//    private IssuingServiceImpl issuingService;
//
//    private IssuingBankModel issuingBankModel;
//    private BaseBankModel baseBankModel;
//    private IssuingBankDto.Create createDto;
//    private IssuingBankDto.Info infoDto;
//
//    @BeforeEach
//    void setUp() {
//        baseBankModel = new BaseBankModel();
//        baseBankModel.setId(1L);
//        baseBankModel.setBankCode("001");
//        baseBankModel.setBankTitle("بانک تجارت");
//        baseBankModel.setBaseNosaCode("107/1824");
//        baseBankModel.setNationalCode("1234567890");
//
//        issuingBankModel = new IssuingBankModel();
//        issuingBankModel.setId(1L);
//        issuingBankModel.setBankName("بانک تجارت");
//        issuingBankModel.setBranchName("شعبه مرکزی");
//        issuingBankModel.setBranchCode("001");
//        issuingBankModel.setCity("تهران");
//        issuingBankModel.setBankCode("001");
//        issuingBankModel.setBaseNosaCode("107/1824");
//
//        createDto = new IssuingBankDto.Create();
//        createDto.setId(1L);
//        createDto.setBankName("بانک تجارت");
//        createDto.setBranchName("شعبه مرکزی");
//        createDto.setBranchCode("001");
//        createDto.setCity("تهران");
//        createDto.setBankCode("001");
//
//        infoDto = new IssuingBankDto.Info();
//        infoDto.setId(1L);
//        infoDto.setBankName("بانک تجارت");
//        infoDto.setBranchName("شعبه مرکزی");
//        infoDto.setBranchCode("001");
//        infoDto.setCity("تهران");
//        infoDto.setBankCode("001");
//        infoDto.setBaseNosaCode("107/1824");
//    }
//
//    @Test
//    void save_NewBank_Success() {
//        try (MockedStatic<com.nicico.internal.sales.util.date.DateUtility> mockedDateUtility = mockStatic(com.nicico.internal.sales.util.date.DateUtility.class)) {
//            mockedDateUtility.when(() -> com.nicico.internal.sales.util.date.DateUtility.getJalaliYear(any(Date.class))).thenReturn(1403);
//
//            when(repository.findById(null)).thenReturn(Optional.empty());
//            when(mapper.fromDTO(createDto)).thenReturn(issuingBankModel);
//            when(baseBankRepository.findById(Long.valueOf("001"))).thenReturn(Optional.of(baseBankModel));
//            when(repository.save(any(IssuingBankModel.class))).thenReturn(issuingBankModel);
//            when(mapper.toDTO(issuingBankModel)).thenReturn(infoDto);
//
//            IssuingBankDto.Info result = issuingService.save(createDto);
//
//            assertNotNull(result);
//            assertEquals("بانک تجارت", result.getBankName());
//            verify(repository).updateBaseNosaCodeWithYearSuffix("03");
//            verify(repository).save(issuingBankModel);
//        }
//    }
//
//    @Test
//    void save_ExistingBank_Update_Success() {
//        try (MockedStatic<com.nicico.internal.sales.util.date.DateUtility> mockedDateUtility = mockStatic(com.nicico.internal.sales.util.date.DateUtility.class)) {
//            mockedDateUtility.when(() -> com.nicico.internal.sales.util.date.DateUtility.getJalaliYear(any(Date.class))).thenReturn(1403);
//
//            createDto.setId(1L);
//            when(repository.findById(1L)).thenReturn(Optional.of(issuingBankModel));
//            when(baseBankRepository.findById(Long.valueOf("001"))).thenReturn(Optional.of(baseBankModel));
//            when(repository.save(any(IssuingBankModel.class))).thenReturn(issuingBankModel);
//            when(mapper.toDTO(issuingBankModel)).thenReturn(infoDto);
//
//            IssuingBankDto.Info result = issuingService.save(createDto);
//
//            assertNotNull(result);
//            verify(repository).findById(1L);
//            verify(repository).save(issuingBankModel);
//        }
//    }
//
//    @Test
//    void save_BaseBankNotFound_ThrowsException() {
//        try (MockedStatic<com.nicico.internal.sales.util.date.DateUtility> mockedDateUtility = mockStatic(com.nicico.internal.sales.util.date.DateUtility.class)) {
//            mockedDateUtility.when(() -> com.nicico.internal.sales.util.date.DateUtility.getJalaliYear(any(Date.class))).thenReturn(1403);
//
//            when(repository.findById(null)).thenReturn(Optional.empty());
//            when(mapper.fromDTO(createDto)).thenReturn(issuingBankModel);
//            when(baseBankRepository.findById(Long.valueOf("001"))).thenReturn(Optional.empty());
//
//            assertThrows(InternalSaleCustomException.ValidationException.class, () -> issuingService.save(createDto));
//        }
//    }
//
//    @Test
//    void search_Success() {
//        SearchDTO.SearchRq searchRq = new SearchDTO.SearchRq();
//
//        when(repository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
//                .thenReturn(new org.springframework.data.domain.PageImpl<>(Arrays.asList(issuingBankModel)));
//        when(mapper.toDTO(issuingBankModel)).thenReturn(infoDto);
//
//        SearchDTO.SearchRs<IssuingBankDto.Info> result = issuingService.search(searchRq);
//
//        assertNotNull(result);
//        assertNotNull(result.getData());
//    }
//
//    @Test
//    void getAll_Success() {
//        List<IssuingBankModel> bankList = Arrays.asList(issuingBankModel);
//        when(repository.findAll()).thenReturn(bankList);
//        when(mapper.toDTO(issuingBankModel)).thenReturn(infoDto);
//
//        List<IssuingBankDto.Info> result = issuingService.getAll();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("بانک تجارت", result.get(0).getBankName());
//    }
//
//    @Test
//    void getById_Success() {
//        when(repository.findById(1L)).thenReturn(Optional.of(issuingBankModel));
//        when(mapper.toDTO(issuingBankModel)).thenReturn(infoDto);
//
//        IssuingBankDto.Info result = issuingService.getById(1L);
//
//        assertNotNull(result);
//        assertEquals("بانک تجارت", result.getBankName());
//        assertEquals(1L, result.getId());
//    }
//
//    @Test
//    void getById_NotFound_ReturnsNull() {
//        when(repository.findById(1L)).thenReturn(Optional.empty());
//        when(mapper.toDTO(null)).thenReturn(null);
//
//        IssuingBankDto.Info result = issuingService.getById(1L);
//
//        assertNull(result);
//    }
//}
