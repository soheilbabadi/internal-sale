package com.nicico.internal.sales.lc.service;

import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class NosaCodeServiceImpl implements NosaCodeService {
    private static final String LC_PREFIX_BASE = "107/18";
    private static final String EXTRA_BILL_PREFIX_BASE = "109/18";
    private static final String MSG_ISSUING_BANK_NOT_FOUND = "بانک گشایش کننده وجود ندارد";

    private final LcRepository lcRepository;
    private final ExtraBillRepository extraBillRepository;
    private final IssuingBankRepository issuingBankRepository;

    /**
     * Generates LC NosaCode with format:
     * '107/18' + base bank code + jalaYear two right digit + 3 digit sequence
     */
    public String generateLcNosaCode(Long issuerBankId) {
        var bank = issuingBankRepository.findById(issuerBankId)
                .orElseThrow(() -> new InternalSaleCustomException.ValidationException(
                        MSG_ISSUING_BANK_NOT_FOUND));
        String yearLast2 = String.format("%02d", DateUtility.getJalaliYear(new Date()) % 100);
        String bankCode = bank.getBankCode();
        String prefix = String.format("%s%s%s", LC_PREFIX_BASE, bankCode, yearLast2);
        String lastNosaCode = lcRepository.findLastNosaCodeByBankIdAndPrefix(issuerBankId, prefix);
        if (lastNosaCode == null) {
            return prefix + "001";
        }
        int lastNumber = Integer.parseInt(lastNosaCode.substring(prefix.length()));
        int newNumber = lastNumber + 1;
        return String.format("%s%03d", prefix, newNumber);
    }

    /**
     * Generates ExtraBill NosaCode with format:
     * '109/18' + base bank code + jalaYear two right digit + 3 digit sequence
     */
    public String generateExtraBillNosaCode(Long issuerBankId) {
        var bank = issuingBankRepository.findById(issuerBankId)
                .orElseThrow(() -> new InternalSaleCustomException.ValidationException(
                        MSG_ISSUING_BANK_NOT_FOUND));
        String yearLast2 = String.format("%02d", DateUtility.getJalaliYear(new Date()) % 100);
        String bankCode = bank.getBankCode();
        String prefix = String.format("%s%s%s", EXTRA_BILL_PREFIX_BASE, bankCode, yearLast2);
        String lastNosaCode = extraBillRepository.findLastNosaCodeByBankIdAndPrefix(issuerBankId, prefix);
        if (lastNosaCode == null) {
            return prefix + "001";
        }
        int lastNumber = Integer.parseInt(lastNosaCode.substring(prefix.length()));
        int newNumber = lastNumber + 1;
        return String.format("%s%03d", prefix, newNumber);
    }
}
