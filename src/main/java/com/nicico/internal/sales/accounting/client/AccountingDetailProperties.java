package com.nicico.internal.sales.accounting.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "accounting.detail")
public class AccountingDetailProperties {

    private PrefixConfig person = new PrefixConfig("01", 3);
    private PrefixConfig company = new PrefixConfig("01", 3);
    private FinancialInstrumentConfig financialInstrument = new FinancialInstrumentConfig();

    public String getFinancialInstrumentMainCode(com.nicico.internal.sales.accounting.dto.FinancialInstrumentType type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case LETTER_OF_CREDIT -> financialInstrument.getLetterOfCreditMainCode();
            case GAM -> financialInstrument.getGamMainCode();
            case ELECTRONIC_PROMISSORY_NOTE -> financialInstrument.getElectronicPromissoryNoteMainCode();
        };
    }

    public String getFinancialInstrumentParentCode(com.nicico.internal.sales.accounting.dto.FinancialInstrumentType type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case LETTER_OF_CREDIT -> financialInstrument.getLetterOfCreditParentCode();
            case GAM -> financialInstrument.getGamParentCode();
            case ELECTRONIC_PROMISSORY_NOTE -> financialInstrument.getElectronicPromissoryNoteParentCode();
        };
    }

    @Getter
    @Setter
    public static class FinancialInstrumentConfig {
        private String letterOfCreditMainCode = "107/18";
        private String letterOfCreditParentCode = "107";
        private String gamMainCode = "108/18";
        private String gamParentCode = "108";
        private String electronicPromissoryNoteMainCode = "109/18";
        private String electronicPromissoryNoteParentCode = "109";
    }

    @Getter
    @Setter
    public static class PrefixConfig {
        /**
         * Prefix code (e.g. "01" or "02").
         */
        private String prefix = "01";

        /**
         * Number of digits after slash that form the parent code (e.g. 3 for "01/427").
         */
        private Integer parentDigits = 3;

        public PrefixConfig() {
        }

        public PrefixConfig(String prefix, Integer parentDigits) {
            this.prefix = prefix;
            this.parentDigits = parentDigits;
        }

        /**
         * Returns prefix formatted with trailing slash if not empty, e.g. "01/".
         */
        public String getFormattedPrefix() {
            if (prefix == null || prefix.trim().isEmpty()) {
                return "";
            }
            String clean = prefix.trim();
            return clean.endsWith("/") ? clean : clean + "/";
        }
    }
}
