package com.nicico.internal.sales.lc.service;

public interface NosaCodeService {

    /**
     * Generates LC NosaCode with format:
     * '107/18' + base bank code + jalaYear two right digit + 3 digit sequence
     *
     * @param issuerBankId the ID of the issuing bank
     * @return generated LC NosaCode
     */
    String generateLcNosaCode(Long issuerBankId);

    /**
     * Generates ExtraBill NosaCode with format:
     * '109/18' + base bank code + jalaYear two right digit + 3 digit sequence
     *
     * @param issuerBankId the ID of the issuing bank
     * @return generated ExtraBill NosaCode
     */
    String generateExtraBillNosaCode(Long issuerBankId);
}
