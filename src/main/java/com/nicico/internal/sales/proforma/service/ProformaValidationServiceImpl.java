package com.nicico.internal.sales.proforma.service;

import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.goods.model.GoodsBucketModel;
import com.nicico.internal.sales.goods.repository.GoodsRepository;
import com.nicico.internal.sales.goods.service.GoodBucketService;
import com.nicico.internal.sales.goods.service.GoodsService;
import com.nicico.internal.sales.goods.special.repository.PreciousMetalRepository;
import com.nicico.internal.sales.ime.trade.IMETradeModel;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.pms.service.PMSCustomerService;
import com.nicico.internal.sales.proforma.dto.*;
import com.nicico.internal.sales.proforma.enums.ProformaIssueType;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.trade.model.TradeExtractStartProformaModel;
import com.nicico.internal.sales.trade.repository.TradeExtractStartProformaRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import com.nicico.internal.sales.vat.repository.VatRepository;
import com.nicico.internal.sales.wf.service.ProcessVariableProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProformaValidationServiceImpl implements ProformaValidationService {

	private static final String MSG_TRADE_NOT_FOUND = "آگهی عرضه وجود ندارد";
	private static final String MSG_PROFORMA_NOT_FOUND = "پیش فاکتور وجود ندارد";
	private static final String MSG_CUSTOMER_NOT_FOUND = "اطلاعات مشتری پیدا نشد";
	private static final String MSG_PAYMENT_CODE_NOT_FOUND = "کد پرداخت وجود ندارد";
	private static final String MSG_VAT_NOT_FOUND = "مالیات بر ارزش افزوده تعریف نشده است";
	private static final String MSG_INVALID_DATA = "اطلاعات پیش فاکتور نادرست است";
	private static final String MSG_WEIGHT_NOT_DEFINED = "وزن پیش فاکتور تعریف نشده است";
	private static final String MSG_ORDER_DATE_REQUIRED = "ورود تاریخ سفارش اجباری است";
	private static final String MSG_CONTRACT_EXISTS = "برای این شماره قرارداد قبلا پیش فاکتور صادر شده است";
	private static final String MSG_REVERSAL_IN_PROGRESS = "پیش فاکتور با شماره قرارداد {0} در فرایند ابطال است و امکان صدور پیش فاکتور جدید وجود ندارد";
	private static final String MSG_PRECIOUS_METAL_NOT_DEFINED = "{0} از نوع فلزات گرانبها تعریف نشده است";
	private static final String MSG_NET_WEIGHT_INVALID = "وزن خالص نادرست است";
	private static final String MSG_ORDER_DATE_RANGE = "تاریخ سفارش پیش فاکتور باید بعد از تاریخ قرارداد و حداکثر ۶ روز کاری پس از آن باشد";
	private static final String MSG_CONTRACT_NO_PROFORMA = "این شماره قرارداد فاقد پیش فاکتور است";
	private static final String MSG_LC_EXISTS_NO_REVERSAL = "برای این شماره قرارداد اعتبار اسنادی صادر شده و امکان ابطال آن وجود ندارد";
	private static final String MSG_REVERSAL_NOT_FINISHED = "فرایند ابطال پیش فاکتور قرارداد شماره {0} هنوز پایان نیافته است";
	private static final String MSG_CASH_SALE_WEIGHT_AND_PARTS = "در صورت تعریف وزن خشک، نمیتوان بیش از یک دیف پیش فاکتور ثبت کرد";
	private static final String MSG_CASH_SALE_ONLY_CREDIT = "فقط برای خرید نقدی از محل مطالبات امکان صدور پیش فاکتور وجود دارد";
	private static final String MSG_GOOD_BUCKET_NOT_DEFINED = "برای کالای {0} ضرایب پیش فاکتور تعریف نشده است. لطفا ضرایب پیش فاکتور را در قسمت اطلاعات پایه تکمیل کنید";
	private static final String MSG_ECONOMIC_CODE_REQUIRED = "اقتصاد کد (economicCode) مشتری الزامی است";
	private static final String MSG_CONTACT_REQUIRED = "اطلاعات تماس مشتری ناقص است";
	private static final String MSG_REGISTER_NUMBER_REQUIRED = "شماره ثبت (registerNumber) مشتری الزامی است و باید برابر با اقتصاد کد باشد";
	private static final String MSG_PMS_GOOD_NOT_FOUND = "PMS id not found for good name: {0}";
	private static final String MSG_LC_EXISTS_NO_CANCEL = "برای این پیش فاکتور اعتبار اسنادی صادر شده است";
	private static final String MSG_CONTACT_ADDRESS_REQUIRED = "آدرس مشتری الزامی است";
	private static final String MSG_CONTACT_MOBILE_REQUIRED = "شماره موبایل مشتری الزامی است";
	private static final String MSG_CONTACT_EMAIL_REQUIRED = "ایمیل مشتری الزامی است";

	private final IMETradeRepository imeTradeRepository;
	private final GoodsRepository goodsRepository;
	private final VatRepository taxVatRepository;
	private final CustomerRepository customerRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final TradeExtractStartProformaRepository tradeExtractRepository;
	private final PreciousMetalRepository preciousMetalRepository;
	private final LcRepository lcRepository;
	private final ProcessVariableProvider processVariableProvider;
	private final GoodBucketService goodBucketService;
	private final PMSCustomerService pMSCustomerService;
	private final GoodsService goodsService;


	@Override
	public List<String> validateProformaData(PerfomaCreateRequest requestDto) {
		var tradeExtract = requireTradeExtract(requestDto.getTradeId());

		List<String> errors = new ArrayList<>();
		if (requestDto.getParts() == null || requestDto.getParts().isEmpty()) {
			errors.add(MSG_WEIGHT_NOT_DEFINED);
		}
		if (requestDto.getOrderDate() == null) {
			errors.add(MSG_ORDER_DATE_REQUIRED);
		}
		errors.addAll(validateCommonProformaData(tradeExtract.getPaymentCode()));
		if (isContractExists(Long.valueOf(tradeExtract.getContractNo()))) {
			errors.add(MSG_CONTRACT_EXISTS);
		}
		throwIfErrors(errors);
		return errors;
	}

	@Override
	public List<String> validateProformaData(BaseOrderRequest requestDto) {
		List<String> errors = new ArrayList<>();
		if (requestDto.getOrderDate() == null) {
			errors.add(MSG_ORDER_DATE_REQUIRED);
		}
		var tradeExtract = requireTradeExtract(requestDto.getTradeId());
		errors.addAll(validateCommonProformaData(tradeExtract.getPaymentCode()));
		if (isContractExists(Long.valueOf(tradeExtract.getContractNo()))) {
			errors.add(MSG_CONTRACT_EXISTS);
		}
		checkReversalForStart(Long.valueOf(tradeExtract.getContractNo()));
		throwIfErrors(errors);
		return errors;
	}

	@Override
	public List<String> validateProformaData(PreciousMetalProfomaCreateRequest requestDto) {
		var tradeExtract = requireTradeExtract(requestDto.getTradeId());


		List<String> errors = new ArrayList<>();
		if (requestDto.getOrderDate() == null) {
			errors.add(MSG_ORDER_DATE_REQUIRED);
		}
		var goodModel = goodsRepository
				.findByImeCommodityId(Long.valueOf(getTradeModel(tradeExtract.getPaymentCode()).getCommodityCode()))
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND));

		if (!preciousMetalRepository.existsById(goodModel.getId())) {
			errors.add(MessageFormat.format(MSG_PRECIOUS_METAL_NOT_DEFINED, goodModel.getDescription()));
		}
		errors.addAll(validateCommonProformaData(tradeExtract.getPaymentCode()));
		if (requestDto.getNetWeight() == null) {
			errors.add(MSG_NET_WEIGHT_INVALID);
		}
		if (isContractExists(Long.valueOf(tradeExtract.getContractNo()))) {
			errors.add(MSG_CONTRACT_EXISTS);
		}
		throwIfErrors(errors);
		return errors;
	}

	@Override
	public List<String> validateCashSaleData(CashSaleCreateRequest requestDto) {
		var tradeExtract = requireTradeExtract(requestDto.getTradeId());

		List<String> errors = new ArrayList<>();
		if (requestDto.getNetWeight() != null && !requestDto.getParts().isEmpty()) {
			errors.add(MSG_CASH_SALE_WEIGHT_AND_PARTS);
		}
		if (requestDto.getProformaIssueType() != ProformaIssueType.FROM_CREDIT_FACILITIES) {
			errors.add(MSG_CASH_SALE_ONLY_CREDIT);
		}
		if (requestDto.getOrderDate() == null) {
			errors.add(MSG_ORDER_DATE_REQUIRED);
		}
		errors.addAll(validateCommonProformaData(tradeExtract.getPaymentCode()));
		if (isContractExists(Long.valueOf(tradeExtract.getContractNo()))) {
			errors.add(MSG_CONTRACT_EXISTS);
		}
		throwIfErrors(errors);
		return errors;
	}

	@Override
	public List<String> validateMixedProforma(MixedProformaRequest requestDto) {
		var tradeExtract = requireTradeExtract(requestDto.getTradeId());

		List<String> errors = new ArrayList<>();
		if (requestDto.getOrderDate() == null) {
			errors.add(MSG_ORDER_DATE_REQUIRED);
		}
		if (isContractExists(Long.valueOf(tradeExtract.getContractNo()))) {
			errors.add(MSG_CONTRACT_EXISTS);
		}
		if (requestDto.getOrderDate() != null) {
			LocalDate proformaDate = toLocalDate(requestDto.getOrderDate()).plusDays(1);
			LocalDate contractDate = toGregorianContractDate(tradeExtract.getPaymentCode());
			if (proformaDate.isBefore(contractDate) || proformaDate.isAfter(contractDate.plusDays(12))) {
				errors.add(MSG_ORDER_DATE_RANGE);
			}
		}
		throwIfErrors(errors);
		return errors;
	}

	@Override
	public List<String> validateReversal(Long masterId) {
		List<String> errors = new ArrayList<>();
		var proforma = proformaMasterRepository.findById(masterId);
		if (proforma.isEmpty()) {
			errors.add(MSG_CONTRACT_NO_PROFORMA);
			throw new InternalSaleCustomException.ValidationException(MSG_INVALID_DATA, errors);
		}
		if (!lcRepository.findByMasterId(masterId).isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(MSG_LC_EXISTS_NO_REVERSAL, errors);
		}
		checkReversalForStart(proforma.get().getContractNo());
		return errors;
	}

	@Override
	public boolean canStartReversal(Long masterId) {
		ProformaMasterModel proforma = proformaMasterRepository.findById(masterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_PROFORMA_NOT_FOUND));
		if (!processVariableProvider.isProcessFinished(proforma.getReversalProcessId())) {
			throw new InternalSaleCustomException.ValidationException(
					MessageFormat.format(MSG_REVERSAL_NOT_FINISHED, proforma.getContractNo()));
		}
		if (!lcRepository.findByMasterId(masterId).isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(MSG_LC_EXISTS_NO_CANCEL);
		}
		return true;
	}

	@Override
	public void validateDate(PerfomaCreateRequest requestDto) {
		var tradeExtract = requireTradeExtract(requestDto.getTradeId());
		LocalDate proformaDate = toLocalDate(requestDto.getOrderDate()).plusDays(1);
		LocalDate contractDate = toGregorianContractDate(tradeExtract.getPaymentCode());
		if (proformaDate.isBefore(contractDate) || proformaDate.isAfter(contractDate.plusDays(10))) {
			throw new InternalSaleCustomException.ValidationException(MSG_ORDER_DATE_RANGE);
		}
	}

	@Override
	public void validateDate(CashSaleCreateRequest requestDto) {
		var tradeExtract = requireTradeExtract(requestDto.getTradeId());
		LocalDate proformaDate = toLocalDate(requestDto.getOrderDate()).plusDays(1);
		LocalDate contractDate = toGregorianContractDate(tradeExtract.getPaymentCode());
		if (proformaDate.isBefore(contractDate) || proformaDate.isAfter(contractDate.plusDays(6))) {
			throw new InternalSaleCustomException.ValidationException(MSG_ORDER_DATE_RANGE);
		}
	}

	public void checkReversalForStart(Long contractNo) {
		proformaMasterRepository.findAllByContractNoOrderByIdDesc(contractNo).forEach(p -> {
			if (p.getWorkflowApproveStatus() == WorkflowApproveStatus.REVERSAL
					&& !processVariableProvider.isProcessAcceptedFinally(p.getReversalProcessId())) {
				throw new InternalSaleCustomException.ValidationException(
						MessageFormat.format(MSG_REVERSAL_IN_PROGRESS, contractNo));
			}
		});
	}

	public boolean isContractExists(Long contractNo) {
		List<WorkflowApproveStatus> statuses = List.of(WorkflowApproveStatus.DRAFT, WorkflowApproveStatus.IN_PROGRESS, WorkflowApproveStatus.ACCEPTED);
		return proformaMasterRepository.existsByContractNoAndWorkflowApproveStatusIn(contractNo, statuses);
	}

	private List<String> validateCommonProformaData(String paymentCode) {
		tradeExtractRepository.findFirstByPaymentCodeOrderByIdDesc(paymentCode)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PAYMENT_CODE_NOT_FOUND));

		List<String> errors = new ArrayList<>();

		errors.addAll(validateVat());
		errors.addAll(validateGoodBucket(paymentCode));
		errors.addAll(validateCustomer(paymentCode));

		return errors;
	}

	private List<String> validateVat() {
		if (taxVatRepository.findByJalaliYear(DateUtility.getCurrentJalaliYear()).isEmpty()) {
			return List.of(MSG_VAT_NOT_FOUND);
		}
		return List.of();
	}

	private List<String> validateGoodBucket(String paymentCode) {
		List<String> errors = new ArrayList<>();

		var commodityCode = tradeExtractRepository.findFirstByPaymentCodeOrderByIdDesc(paymentCode)
				.orElse(null);
		var goodOptional = goodsRepository.findByImeCommodityId(commodityCode.getCommodityCode());
		GoodsBucketModel goodBucket = goodBucketService.findByPaymentCodeModel(paymentCode);

		if ((goodBucket == null || goodBucket.getPackagingSize() == null) && goodOptional.isPresent()) {
			errors.add(MessageFormat.format(MSG_GOOD_BUCKET_NOT_DEFINED, goodOptional.get().getDescription()));
		}
		if (goodBucket != null) {
			errors.addAll(pmsGoodsValidation(goodBucket.getGoodName()));
		}

		return errors;
	}

	private List<String> validateCustomer(String paymentCode) {
		var imeTradeOpt = imeTradeRepository.findFirstByPaymentCodeOrderByIdDesc(paymentCode);
		if (imeTradeOpt.isEmpty()) {
			return List.of(MSG_TRADE_NOT_FOUND);
		}

		var customerOpt = customerRepository.findByNationalCode(imeTradeOpt.get().getBuyerNationalCode());
		if (customerOpt.isEmpty()) {
			return List.of(MSG_CUSTOMER_NOT_FOUND);
		}

		List<String> errors = new ArrayList<>();
		CustomerModel customer = customerOpt.get();

		errors.addAll(validateCustomerContact(customer));
		errors.addAll(validateCustomerFields(customer));

		return errors;
	}

	private List<String> validateCustomerContact(CustomerModel customer) {
		List<String> errors = new ArrayList<>();

		if (customer.getAddress() == null || customer.getAddress().isBlank()) {
			errors.add(MSG_CONTACT_ADDRESS_REQUIRED);
		}
		if (customer.getMobile() == null || customer.getMobile().isBlank()) {
			errors.add(MSG_CONTACT_MOBILE_REQUIRED);
		}
		if (customer.getEmail() == null || customer.getEmail().isBlank()) {
			errors.add(MSG_CONTACT_EMAIL_REQUIRED);
		}

		return errors;
	}

	private List<String> validateCustomerFields(CustomerModel customer) {
		List<String> errors = new ArrayList<>();

		if (customer.getEconomicCode() == null || customer.getEconomicCode().isBlank()) {
			errors.add(MSG_ECONOMIC_CODE_REQUIRED);
			return errors;
		}
		if (customer.getRegisterNumber() == null || customer.getRegisterNumber().isBlank()) {
			errors.add(MSG_REGISTER_NUMBER_REQUIRED);
		}
		errors.addAll(pmsCustomerValidations(customer.getEconomicCode(), customer.getRegisterNumber()));

		return errors;
	}

	private List<String> pmsCustomerValidations(String economicCode, String registerNumber) {
		try {
			pMSCustomerService.findByEconomicCodeOrRegisterNumber(economicCode, registerNumber);
			return List.of();
		} catch (InternalSaleCustomException e) {
			return List.of(e.getMessage());
		}
	}

	private List<String> pmsGoodsValidation(String goodsName) {
		Long pmsId = goodsService.findPmsIdByGoodName(goodsName);
		if (pmsId == null) {
			return List.of(MessageFormat.format(MSG_PMS_GOOD_NOT_FOUND, goodsName));
		}
		return List.of();
	}

	private TradeExtractStartProformaModel requireTradeExtract(Long tradeId) {
		return tradeExtractRepository.findById(tradeId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND));
	}

	private IMETradeModel getTradeModel(String paymentCode) {
		return imeTradeRepository.findFirstByPaymentCodeOrderByIdDesc(paymentCode)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_TRADE_NOT_FOUND));
	}

	private LocalDate toGregorianContractDate(String paymentCode) {
		return DateUtility.toGregorianDate(getTradeModel(paymentCode).getContractDate())
				.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private LocalDate toLocalDate(java.util.Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private void throwIfErrors(List<String> errors) {
		if (!errors.isEmpty()) {
			throw new InternalSaleCustomException.ValidationException(MSG_INVALID_DATA, errors);
		}
	}


}