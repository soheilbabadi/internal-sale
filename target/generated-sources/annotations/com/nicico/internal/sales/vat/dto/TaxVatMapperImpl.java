package com.nicico.internal.sales.vat.dto;

import com.nicico.internal.sales.vat.dto.VatDTO.Create;
import com.nicico.internal.sales.vat.dto.VatDTO.Info;
import com.nicico.internal.sales.vat.model.TaxVatModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class TaxVatMapperImpl implements TaxVatMapper {

    @Override
    public Info toDTO(TaxVatModel imeTrade) {
        if ( imeTrade == null ) {
            return null;
        }

        Info info = new Info();

        info.setJalaliYear( imeTrade.getJalaliYear() );
        info.setEmissionTax( imeTrade.getEmissionTax() );
        info.setTaxCoefficient( imeTrade.getTaxCoefficient() );
        info.setVatCoefficient( imeTrade.getVatCoefficient() );
        info.setId( imeTrade.getId() );
        info.setCreatedDate( imeTrade.getCreatedDate() );
        info.setLastModifiedDate( imeTrade.getLastModifiedDate() );
        info.setCreatedBy( imeTrade.getCreatedBy() );
        info.setLastModifiedBy( imeTrade.getLastModifiedBy() );
        info.setComment( imeTrade.getComment() );
        info.setDescription( imeTrade.getDescription() );

        return info;
    }

    @Override
    public TaxVatModel fromDTO(Create imeTrade) {
        if ( imeTrade == null ) {
            return null;
        }

        TaxVatModel taxVatModel = new TaxVatModel();

        taxVatModel.setJalaliYear( imeTrade.getJalaliYear() );
        taxVatModel.setEmissionTax( imeTrade.getEmissionTax() );
        taxVatModel.setTaxCoefficient( imeTrade.getTaxCoefficient() );
        taxVatModel.setVatCoefficient( imeTrade.getVatCoefficient() );

        return taxVatModel;
    }
}
