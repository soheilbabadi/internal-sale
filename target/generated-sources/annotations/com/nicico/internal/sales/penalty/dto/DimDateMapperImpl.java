package com.nicico.internal.sales.penalty.dto;

import com.nicico.internal.sales.penalty.dto.DimDateDto.Create;
import com.nicico.internal.sales.penalty.dto.DimDateDto.Info;
import com.nicico.internal.sales.penalty.model.DimDateModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-31T09:45:37+0330",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 17.0.20.1 (Ubuntu)"
)
@Component
public class DimDateMapperImpl implements DimDateMapper {

    @Override
    public Info toDTO(DimDateModel request) {
        if ( request == null ) {
            return null;
        }

        Info info = new Info();

        info.setId( request.getId() );
        info.setShortDate( request.getShortDate() );
        info.setLongDate( request.getLongDate() );
        info.setDayOfWeek( request.getDayOfWeek() );
        info.setMonthName( request.getMonthName() );
        info.setDayInYear( request.getDayInYear() );
        info.setWeekInYear( request.getWeekInYear() );
        info.setGregorianMonth( request.getGregorianMonth() );
        info.setGregorianDaysInMonth( request.getGregorianDaysInMonth() );
        info.setGregorianDay( request.getGregorianDay() );
        info.setQuarter( request.getQuarter() );
        info.setGregorianYear( request.getGregorianYear() );
        info.setPersianShortDate( request.getPersianShortDate() );
        info.setPersianLongDate( request.getPersianLongDate() );
        info.setPersianDay( request.getPersianDay() );
        info.setPersianMonth( request.getPersianMonth() );
        info.setPersianYear( request.getPersianYear() );
        info.setPersianWeekdayName( request.getPersianWeekdayName() );
        info.setPersianMonthName( request.getPersianMonthName() );
        info.setIsLeapYear( request.getIsLeapYear() );
        info.setHijriShortDate( request.getHijriShortDate() );
        info.setHijriLongDate( request.getHijriLongDate() );
        info.setHijriDay( request.getHijriDay() );
        info.setHijriMonth( request.getHijriMonth() );
        info.setHijriYear( request.getHijriYear() );
        info.setHijriMonthName( request.getHijriMonthName() );
        info.setIsWeekend( request.getIsWeekend() );
        info.setIsHoliday( request.getIsHoliday() );

        return info;
    }

    @Override
    public DimDateModel fromDTO(Create request) {
        if ( request == null ) {
            return null;
        }

        DimDateModel dimDateModel = new DimDateModel();

        dimDateModel.setId( request.getId() );
        dimDateModel.setShortDate( request.getShortDate() );
        dimDateModel.setLongDate( request.getLongDate() );
        dimDateModel.setDayOfWeek( request.getDayOfWeek() );
        dimDateModel.setMonthName( request.getMonthName() );
        dimDateModel.setDayInYear( request.getDayInYear() );
        dimDateModel.setWeekInYear( request.getWeekInYear() );
        dimDateModel.setGregorianMonth( request.getGregorianMonth() );
        dimDateModel.setGregorianDaysInMonth( request.getGregorianDaysInMonth() );
        dimDateModel.setGregorianDay( request.getGregorianDay() );
        dimDateModel.setQuarter( request.getQuarter() );
        dimDateModel.setGregorianYear( request.getGregorianYear() );
        dimDateModel.setPersianShortDate( request.getPersianShortDate() );
        dimDateModel.setPersianLongDate( request.getPersianLongDate() );
        dimDateModel.setPersianDay( request.getPersianDay() );
        dimDateModel.setPersianMonth( request.getPersianMonth() );
        dimDateModel.setPersianYear( request.getPersianYear() );
        dimDateModel.setPersianWeekdayName( request.getPersianWeekdayName() );
        dimDateModel.setPersianMonthName( request.getPersianMonthName() );
        dimDateModel.setIsLeapYear( request.getIsLeapYear() );
        dimDateModel.setHijriShortDate( request.getHijriShortDate() );
        dimDateModel.setHijriLongDate( request.getHijriLongDate() );
        dimDateModel.setHijriDay( request.getHijriDay() );
        dimDateModel.setHijriMonth( request.getHijriMonth() );
        dimDateModel.setHijriYear( request.getHijriYear() );
        dimDateModel.setHijriMonthName( request.getHijriMonthName() );
        dimDateModel.setIsWeekend( request.getIsWeekend() );
        dimDateModel.setIsHoliday( request.getIsHoliday() );

        return dimDateModel;
    }
}
