package com.nicico.internal.sales.ins.customer.service;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.ins.customer.dto.CustomerContactDto;
import com.nicico.internal.sales.ins.customer.dto.CustomerDTO;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.pms.service.PMSCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerValidationServiceImpl implements CustomerValidationService {

    private static final String MSG_NAME_REQUIRED = "نام مشتری الزامی است";
    private static final String MSG_NATIONAL_CODE_REQUIRED = "شناسه ملی الزامی است";
    private static final String MSG_NATIONAL_CODE_INVALID = "شناسه ملی باید 10، 11، 12 یا 14 رقم باشد";
    private static final String MSG_CUSTOMER_EXISTS = "این مشتری قبلا ثبت شده است";
    private static final String MSG_ECONOMIC_CODE_REQUIRED = "کد اقتصادی (economicCode) مشتری الزامی است";
    private static final String MSG_ECONOMIC_CODE_INVALID = "کد اقتصادی باید 10، 11، 12 یا 14 رقم باشد";
    private static final String MSG_REGISTER_NUMBER_REQUIRED = "شماره ثبت (registerNumber) مشتری الزامی است";
    private static final String MSG_CONTACT_ADDRESS_REQUIRED = "آدرس مشتری الزامی است";
    private static final String MSG_CONTACT_MOBILE_REQUIRED = "شماره موبایل مشتری الزامی است";
    private static final String MSG_CONTACT_EMAIL_REQUIRED = "ایمیل مشتری الزامی است";
    private static final String MSG_INVALID_DATA = "اطلاعات مشتری نادرست است";
    
    // Customer Contact validation messages
    private static final String MSG_CONTACT_PHONE_REQUIRED = "شماره تماس اجباری است";
    private static final String MSG_CONTACT_POSTCODE_REQUIRED = "کد پستی اجباری است";
    private static final String MSG_CONTACT_POSTCODE_INVALID = "کد پستی باید ده رقم باشد";
    private static final String MSG_CONTACT_EMAIL_REQUIRED_MSG = "ایمیل اجباری است";
    private static final String MSG_CONTACT_EMAIL_INVALID = "ایمیل معتبر نیست";
    private static final String MSG_CONTACT_MOBILE_REQUIRED_MSG = "شماره موبایل اجباری است";
    private static final String MSG_CONTACT_MOBILE_INVALID = "شماره موبایل معتبر نیست";

    private final CustomerRepository customerRepository;
    private final PMSCustomerService pmsCustomerService;

    @Override
    public List<String> validateCreateCustomer(CustomerDTO.Create requestDto) {
        List<String> errors = new ArrayList<>();

        if (requestDto.getName() == null || requestDto.getName().isBlank()) {
            errors.add(MSG_NAME_REQUIRED);
        }

        if (requestDto.getNationalCode() == null || requestDto.getNationalCode().isBlank()) {
            errors.add(MSG_NATIONAL_CODE_REQUIRED);
        } else if (!requestDto.getNationalCode().matches("\\d{10}|\\d{11}|\\d{12}|\\d{14}")) {
            errors.add(MSG_NATIONAL_CODE_INVALID);
        }

        if (customerRepository.existsByNationalCode(requestDto.getNationalCode())) {
            errors.add(MSG_CUSTOMER_EXISTS);
        }

        errors.addAll(validateCustomerFields(requestDto));

        throwIfErrors(errors);
        return errors;
    }

    @Override
    public List<String> validateUpdateCustomer(CustomerDTO.Create requestDto) {
        List<String> errors = new ArrayList<>();

        if (requestDto.getName() == null || requestDto.getName().isBlank()) {
            errors.add(MSG_NAME_REQUIRED);
        }

        if (requestDto.getNationalCode() == null || requestDto.getNationalCode().isBlank()) {
            errors.add(MSG_NATIONAL_CODE_REQUIRED);
        } else if (!requestDto.getNationalCode().matches("\\d{10}|\\d{11}|\\d{12}|\\d{14}")) {
            errors.add(MSG_NATIONAL_CODE_INVALID);
        }

        errors.addAll(validateCustomerFields(requestDto));

        throwIfErrors(errors);
        return errors;
    }

    @Override
    public List<String> validateCreateCustomerContact(CustomerContactDto.Create requestDto) {
        List<String> errors = new ArrayList<>();

        if (requestDto.getPhone() == null || requestDto.getPhone().isBlank()) {
            errors.add(MSG_CONTACT_PHONE_REQUIRED);
        }

        if (requestDto.getPostCode() == null || requestDto.getPostCode().isBlank()) {
            errors.add(MSG_CONTACT_POSTCODE_REQUIRED);
        } else if (!requestDto.getPostCode().matches("^\\d{10}$")) {
            errors.add(MSG_CONTACT_POSTCODE_INVALID);
        }

        if (requestDto.getEmail() == null || requestDto.getEmail().isBlank()) {
            errors.add(MSG_CONTACT_EMAIL_REQUIRED_MSG);
        } else if (!isValidEmail(requestDto.getEmail())) {
            errors.add(MSG_CONTACT_EMAIL_INVALID);
        }

        if (requestDto.getMobile() == null || requestDto.getMobile().isBlank()) {
            errors.add(MSG_CONTACT_MOBILE_REQUIRED_MSG);
        } else if (!requestDto.getMobile().matches("^09\\d{9}$")) {
            errors.add(MSG_CONTACT_MOBILE_INVALID);
        }

        throwIfErrors(errors);
        return errors;
    }

    @Override
    public List<String> validateUpdateCustomerContact(CustomerContactDto.Create requestDto) {
        return validateCreateCustomerContact(requestDto);
    }

    private List<String> validateCustomerFields(CustomerDTO.Create requestDto) {
        List<String> errors = new ArrayList<>();

        if (requestDto.getEconomicCode() == null || requestDto.getEconomicCode().isBlank()) {
            errors.add(MSG_ECONOMIC_CODE_REQUIRED);
            return errors;
        } else if (!requestDto.getEconomicCode().matches("\\d{10}|\\d{11}|\\d{12}|\\d{14}")) {
            errors.add(MSG_ECONOMIC_CODE_INVALID);
        }

        if (requestDto.getRegisterNumber() == null || requestDto.getRegisterNumber().isBlank()) {
            errors.add(MSG_REGISTER_NUMBER_REQUIRED);
        }

        if (requestDto.getAddress() == null || requestDto.getAddress().isBlank()) {
            errors.add(MSG_CONTACT_ADDRESS_REQUIRED);
        }

        if (requestDto.getMobile() == null || requestDto.getMobile().isBlank()) {
            errors.add(MSG_CONTACT_MOBILE_REQUIRED);
        }

        if (requestDto.getEmail() == null || requestDto.getEmail().isBlank()) {
            errors.add(MSG_CONTACT_EMAIL_REQUIRED);
        }

        errors.addAll(validatePmsCustomer(requestDto.getEconomicCode(), requestDto.getRegisterNumber()));

        return errors;
    }

    private List<String> validatePmsCustomer(String economicCode, String registerNumber) {
        try {
            pmsCustomerService.findByEconomicCodeOrRegisterNumber(economicCode, registerNumber);
            return List.of();
        } catch (InternalSaleCustomException e) {
            return List.of(e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    private void throwIfErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new InternalSaleCustomException.ValidationException(MSG_INVALID_DATA, errors);
        }
    }
}
