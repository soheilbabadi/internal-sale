package com.nicico.internal.sales.nosa;

import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class LcNosaCodeServiceImpl implements LcNosaCodeService {
	private static final String NOSA_CODE_PATTERN = "^%s\\d{3}$";
	private static final String MSG_ISSUING_BANK_NOT_FOUND = "بانک گشایش کننده وجود ندارد";
	private final LcRepository lcRepository;
	private final IssuingBankRepository issuingBankRepository;

	@Override
	public String getNosaCode(Long issuerBankId) {
		var bank = issuingBankRepository.findById(issuerBankId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_ISSUING_BANK_NOT_FOUND));
		String yearLast2 = String.format("%02d", DateUtility.getJalaliYear(new Date()) % 100);
		String bankCode = bank.getBankCode();
		String prefix = String.format("107/18%s%s", yearLast2, bankCode);
		String lastNosaCode = lcRepository.findLastNosaCodeByBankId(bank.getId());
		String pattern = String.format(NOSA_CODE_PATTERN, prefix);
		if (lastNosaCode == null || !lastNosaCode.matches(pattern)) {
			return prefix + "001";
		}
		int lastNumber = Integer.parseInt(lastNosaCode.substring(prefix.length()));
		int newNumber = lastNumber + 1;
		return String.format("%s%03d", prefix, newNumber);
	}
}