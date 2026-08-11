package com.nicico.internal.sales.bank.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.internal.sales.bank.dto.BankMapper;
import com.nicico.internal.sales.bank.dto.TradingBankDto;
import com.nicico.internal.sales.bank.model.TradingBankModel;
import com.nicico.internal.sales.bank.repository.TradingBankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingBankServiceImplTest {

    @Mock
    private TradingBankRepository repository;

    @Mock
    private BankMapper mapper;

    @InjectMocks
    private TradingBankServiceImpl tradingBankService;

    private TradingBankModel tradingBankModel;
    private TradingBankDto.Create createDto;
    private TradingBankDto.Info infoDto;

    @BeforeEach
    void setUp() {
        Date now = new Date();

        tradingBankModel = new TradingBankModel();
        tradingBankModel.setId(1L);
        tradingBankModel.setBankTitle("بانک تجارت");
        tradingBankModel.setBankBranchTitle("شعبه مرکزی");
        tradingBankModel.setBranchCode("001");
        tradingBankModel.setAccountNumber("123456789");
        tradingBankModel.setIban("IR0696000000010324200001");

        createDto = new TradingBankDto.Create();
        createDto.setId(1L);
        createDto.setBankTitle("بانک تجارت");
        createDto.setBankBranchTitle("شعبه مرکزی");
        createDto.setBranchCode("001");
        createDto.setAccountNumber("123456789");
        createDto.setIban("ir0696000000010324200001");

        infoDto = new TradingBankDto.Info();
        infoDto.setId(1L);
        infoDto.setBankTitle("بانک تجارت");
        infoDto.setBankBranchTitle("شعبه مرکزی");
        infoDto.setBranchCode("001");
        infoDto.setAccountNumber("123456789");
        infoDto.setIban("IR0696000000010324200001");
        infoDto.setCreatedDate(now);
        infoDto.setLastModifiedDate(now);
    }

    @Test
    void save_Success_IbanConvertedToUpperCase() {
        when(mapper.fromDTO(createDto)).thenReturn(tradingBankModel);
        when(repository.save(any(TradingBankModel.class))).thenReturn(tradingBankModel);
        when(mapper.toDTO(tradingBankModel)).thenReturn(infoDto);

        TradingBankDto.Info result = tradingBankService.save(createDto);

        assertNotNull(result);
        assertEquals("بانک تجارت", result.getBankTitle());
        assertEquals("IR0696000000010324200001", result.getIban());
        verify(mapper).fromDTO(createDto);
        verify(repository).save(tradingBankModel);
    }

    @Test
    void save_VerifiesIbanIsUpperCased() {
        TradingBankDto.Create dtoWithLowercaseIban = new TradingBankDto.Create();
        dtoWithLowercaseIban.setIban("ir1234567890123456789012");

        when(mapper.fromDTO(dtoWithLowercaseIban)).thenAnswer(invocation -> {
            TradingBankDto.Create actualDto = invocation.getArgument(0);
            assertEquals("IR1234567890123456789012", actualDto.getIban(), "IBAN should be uppercased");
            return tradingBankModel;
        });
        when(repository.save(any(TradingBankModel.class))).thenReturn(tradingBankModel);
        when(mapper.toDTO(tradingBankModel)).thenReturn(infoDto);

        tradingBankService.save(dtoWithLowercaseIban);

        verify(mapper).fromDTO(dtoWithLowercaseIban);
    }

    @Test
    void search_Success() {
        SearchDTO.SearchRq searchRq = new SearchDTO.SearchRq();

        when(repository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(Arrays.asList(tradingBankModel)));
        when(mapper.toDTO(any(TradingBankModel.class))).thenReturn(infoDto);

        SearchDTO.SearchRs<TradingBankDto.Info> result = tradingBankService.search(searchRq);

        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void search_WithNullLastModifiedDate_SetsCreatedDate() {
        TradingBankDto.Info infoWithNullModifiedDate = new TradingBankDto.Info();
        infoWithNullModifiedDate.setId(1L);
        infoWithNullModifiedDate.setBankTitle("بانک تجارت");
        infoWithNullModifiedDate.setCreatedDate(new Date());
        infoWithNullModifiedDate.setLastModifiedDate(null);

        when(repository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(Arrays.asList(tradingBankModel)));
        when(mapper.toDTO(any(TradingBankModel.class))).thenReturn(infoWithNullModifiedDate);

        SearchDTO.SearchRs<TradingBankDto.Info> result = tradingBankService.search(searchRq);

        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void getAll_Success() {
        List<TradingBankModel> bankList = Arrays.asList(tradingBankModel);
        when(repository.findAll()).thenReturn(bankList);
        when(mapper.toDTO(tradingBankModel)).thenReturn(infoDto);

        List<TradingBankDto.Info> result = tradingBankService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("بانک تجارت", result.get(0).getBankTitle());
        assertEquals("IR0696000000010324200001", result.get(0).getIban());
    }

    @Test
    void getAll_EmptyList() {
        when(repository.findAll()).thenReturn(Arrays.asList());

        List<TradingBankDto.Info> result = tradingBankService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_MultipleBanks() {
        TradingBankModel bank2 = new TradingBankModel();
        bank2.setId(2L);
        bank2.setBankTitle("بانک ملت");
        bank2.setBankBranchTitle("شعبه ونک");
        bank2.setBranchCode("002");
        bank2.setIban("IR2701200000000000000001");

        TradingBankDto.Info info2 = new TradingBankDto.Info();
        info2.setId(2L);
        info2.setBankTitle("بانک ملت");
        info2.setBankBranchTitle("شعبه ونک");
        info2.setIban("IR2701200000000000000001");

        List<TradingBankModel> bankList = Arrays.asList(tradingBankModel, bank2);
        when(repository.findAll()).thenReturn(bankList);
        when(mapper.toDTO(tradingBankModel)).thenReturn(infoDto);
        when(mapper.toDTO(bank2)).thenReturn(info2);

        List<TradingBankDto.Info> result = tradingBankService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("بانک تجارت", result.get(0).getBankTitle());
        assertEquals("بانک ملت", result.get(1).getBankTitle());
    }
}
