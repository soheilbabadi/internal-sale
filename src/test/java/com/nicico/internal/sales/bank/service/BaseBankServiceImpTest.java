package com.nicico.internal.sales.bank.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.BaseBankDto;
import com.nicico.internal.sales.bank.dto.BaseBankMapper;
import com.nicico.internal.sales.bank.model.BaseBankModel;
import com.nicico.internal.sales.bank.repository.BaseBankRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseBankServiceImpTest {

    @Mock
    private BaseBankMapper mapper;

    @Mock
    private BaseBankRepository repository;

    @InjectMocks
    private BaseBankServiceImp baseBankService;

    private BaseBankModel baseBankModel;
    private BaseBankDto.Create createDto;
    private BaseBankDto.Info infoDto;

    @BeforeEach
    void setUp() {
        baseBankModel = new BaseBankModel();
        baseBankModel.setId(1L);
        baseBankModel.setBankCode("001");
        baseBankModel.setBankTitle("بانک تجارت");
        baseBankModel.setBaseNosaCode("107/1824");
        baseBankModel.setNationalCode("1234567890");

        createDto = new BaseBankDto.Create();
        createDto.setId(1L);
        createDto.setBankCode("001");
        createDto.setBankTitle("بانک تجارت");
        createDto.setBaseNosaCode("107/1824");
        createDto.setNationalCode("1234567890");

        infoDto = new BaseBankDto.Info();
        infoDto.setId(1L);
        infoDto.setBankCode("001");
        infoDto.setBankTitle("بانک تجارت");
        infoDto.setBaseNosaCode("107/1824");
        infoDto.setNationalCode("1234567890");
    }

    @Test
    void save_Success() {
        try (MockedStatic<com.nicico.internal.sales.util.date.DateUtility> mockedDateUtility = mockStatic(com.nicico.internal.sales.util.date.DateUtility.class)) {
            mockedDateUtility.when(() -> com.nicico.internal.sales.util.date.DateUtility.getJalaliYear(any(Date.class))).thenReturn(1403);
            when(repository.updateBaseNosaCodeWithYearSuffix("03")).thenReturn(1);
            when(mapper.fromDTO(createDto)).thenReturn(baseBankModel);
            when(repository.save(any(BaseBankModel.class))).thenReturn(baseBankModel);
            when(mapper.toDTO(baseBankModel)).thenReturn(infoDto);

            BaseBankDto.Info result = baseBankService.save(createDto);

            assertNotNull(result);
            assertEquals("بانک تجارت", result.getBankTitle());
            verify(repository).updateBaseNosaCodeWithYearSuffix("03");
            verify(repository).save(baseBankModel);
        }
    }

    @Test
    void search_Success() {
        SearchDTO.SearchRq searchRq = new SearchDTO.SearchRq();
        SearchDTO.SearchRs<BaseBankDto.Info> searchRs = new SearchDTO.SearchRs<>();
        searchRs.setData(java.util.List.of(infoDto));

        when(repository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(baseBankModel)));
        when(mapper.toDTO(baseBankModel)).thenReturn(infoDto);

        SearchDTO.SearchRs<BaseBankDto.Info> result = baseBankService.search(searchRq);

        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void getById_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(baseBankModel));
        when(mapper.toDTO(baseBankModel)).thenReturn(infoDto);

        BaseBankDto.Info result = baseBankService.getById(1L);

        assertNotNull(result);
        assertEquals("بانک تجارت", result.getBankTitle());
        assertEquals(1L, result.getId());
    }

    @Test
    void getById_NotFound_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InternalSaleCustomException.ValidationException.class, () -> baseBankService.getById(1L));
    }

    @Test
    void updateAllBaseNosaCodes_Success() {
        try (MockedStatic<com.nicico.internal.sales.util.date.DateUtility> mockedDateUtility = mockStatic(com.nicico.internal.sales.util.date.DateUtility.class)) {
            mockedDateUtility.when(() -> com.nicico.internal.sales.util.date.DateUtility.getJalaliYear(any(Date.class))).thenReturn(1403);
            when(repository.updateBaseNosaCodeWithYearSuffix("03")).thenReturn(5);

            int result = baseBankService.updateAllBaseNosaCodes();

            assertEquals(5, result);
            verify(repository).updateBaseNosaCodeWithYearSuffix("03");
        }
    }

    @Test
    void updateAllBaseNosaCodes_VerifiesYearSuffixFormat() {
        try (MockedStatic<com.nicico.internal.sales.util.date.DateUtility> mockedDateUtility = mockStatic(com.nicico.internal.sales.util.date.DateUtility.class)) {
            mockedDateUtility.when(() -> com.nicico.internal.sales.util.date.DateUtility.getJalaliYear(any(Date.class))).thenReturn(1425);
            when(repository.updateBaseNosaCodeWithYearSuffix("25")).thenReturn(3);

            baseBankService.updateAllBaseNosaCodes();

            verify(repository).updateBaseNosaCodeWithYearSuffix("25");
        }
    }
}
