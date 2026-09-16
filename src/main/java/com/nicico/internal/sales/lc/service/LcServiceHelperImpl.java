package com.nicico.internal.sales.lc.service;


import com.nicico.internal.sales.bank.model.IssuingBankModel;
import com.nicico.internal.sales.bank.model.TradingBankModel;
import com.nicico.internal.sales.bank.repository.IssuingBankRepository;
import com.nicico.internal.sales.bank.repository.TradingBankRepository;
import com.nicico.internal.sales.broker.model.BrokerModel;
import com.nicico.internal.sales.broker.repository.BrokerRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.extrabill.model.ExtraBankBillModel;
import com.nicico.internal.sales.extrabill.repository.ExtraBillRepository;
import com.nicico.internal.sales.gaam.dto.GaamCancelRequest;
import com.nicico.internal.sales.gaam.repository.GaamRepository;
import com.nicico.internal.sales.ime.trade.IMETradeRepository;
import com.nicico.internal.sales.lc.dto.request.BrokerEmailRequest;
import com.nicico.internal.sales.lc.dto.request.LcCancelRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateAcceptedLcRequest;
import com.nicico.internal.sales.lc.dto.request.UpdateStartedLcRequest;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.enums.LcCancellationReason;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.nosa.LcNosaCodeService;
import com.nicico.internal.sales.notification.service.NotificationService;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class LcServiceHelperImpl   {
	private static final String MSG_PROFORMA_NOT_FOUND = "پیش فاکتور وجود ندارد";
	private static final String MSG_LC_NOT_FOUND = "اعتبار اسنادی وجود ندارد";
	private static final String MSG_TRADING_BANK_NOT_FOUND = "شعبه بانک وجود ندارد";
	private static final String ERROR_TRADE_NOT_FOUND = "کالای مورد نظر وجود ندارد";
	private static final String MSG_ISSUING_BANK_NOT_FOUND = "بانک گشایش کننده وجود ندارد";
	private static final String MSG_ISSUING_BANK_NOT_FOUND_FOR_LC = "برای این اعتبار اسنادی بانک گشایش کننده وجود ندارد";
	private static final String MSG_LC_DATE_EMPTY = "تاریخ گشایش اعتبار اسنادی نمی تواند خالی باشد";
	private static final String MSG_LC_DATE_BEFORE_PROFORMA = "تاریخ گشایش اعتبار اسنادی نمی تواند قبل از تاریخ پیش فاکتور باشد";
	private static final String MSG_LC_DISPATCH_FILE_REQUIRED = "برای این اعتبار اسنادی فایل ابلاغیه فروش الزامی است";
	private static final String MSG_BROKER_EMAIL_MISSING = "اطلاعات تماس ایمیل کارگزار  موجود نمی باشد.";
	private static final int PAYMENT_DEFERRAL_NONE = 0;

	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final IssuingBankRepository issuingBankRepository;
	private final TradingBankRepository tradingBankRepository;
	private final LcRepository lcRepository;
	private final NotificationService notificationService;
	private final BrokerRepository brokerRepository;
	private final IMETradeRepository imeTradeRepository;
	private final LcNosaCodeService lcNosaCodeService;
	private final GaamRepository gaamRepository;
	private final ExtraBillRepository extraBillRepository;

	
	public ProformaDetailModel findProformaDetail(Long proformaId) {
		return proformaDetailRepository.findById(proformaId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_PROFORMA_NOT_FOUND));
	}

	
	public LcModel findLcModel(Long proformaId) {
		return lcRepository.findFirstByProformaDetailIdOrderByCreatedDateDesc(proformaId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_LC_NOT_FOUND));
	}

	
	public TradingBankModel findBankBranch(Long requestId, Long fallbackId) {
		Long id = requestId != null ? requestId : fallbackId;
		return tradingBankRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_TRADING_BANK_NOT_FOUND));
	}

	
	public IssuingBankModel findIssuingBank(Long requestId, Long fallbackId) {
		Long id = requestId != null ? requestId : fallbackId;
		String notFoundMsg = requestId != null
				? MSG_ISSUING_BANK_NOT_FOUND
				: MSG_ISSUING_BANK_NOT_FOUND_FOR_LC;
		return issuingBankRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(notFoundMsg));
	}


	
	public BrokerModel fetchBrokerForTrade(Long tradeId) {
		var sellerBrokerCode = imeTradeRepository.findSellerBrokerCodeById(tradeId)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						ERROR_TRADE_NOT_FOUND));
		return brokerRepository.findById(sellerBrokerCode)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_BROKER_EMAIL_MISSING));
	}

	
	public void validateAndAdjustLcDate(UpdateStartedLcRequest lcRequest, ProformaDetailModel proformaDetail) {
		if (lcRequest.getLcDate() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_LC_DATE_EMPTY);
		}

		Date lcDate = lcRequest.getLcDate();
		Date performaDate = proformaDetail.getPerformaDate();
		LocalDateTime lcDateTime = lcDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime performDateTime = performaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDate lcDatePart = lcDateTime.toLocalDate();
		LocalDate performDatePart = performDateTime.toLocalDate();

		if (lcDatePart.equals(performDatePart)) {
			lcRequest.setLcDate(DateUtility.toDate(performDateTime.plusMinutes(1).toLocalDate()));
		} else if (lcDate.before(performaDate)) {
			throw new InternalSaleCustomException.ValidationException(MSG_LC_DATE_BEFORE_PROFORMA);
		}
	}


	
	public void validateAndAdjustLcDate(UpdateAcceptedLcRequest lcRequest, ProformaDetailModel proformaDetail) {
		if (lcRequest.getLcDate() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_LC_DATE_EMPTY);
		}

		Date lcDate = lcRequest.getLcDate();
		Date performaDate = proformaDetail.getPerformaDate();
		LocalDateTime lcDateTime = lcDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime performDateTime = performaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDate lcDatePart = lcDateTime.toLocalDate();
		LocalDate performDatePart = performDateTime.toLocalDate();

		if (lcDatePart.equals(performDatePart)) {
			lcRequest.setLcDate(DateUtility.toDate(performDateTime.plusMinutes(1).toLocalDate()));
		} else if (lcDate.before(performaDate)) {
			throw new InternalSaleCustomException.ValidationException(MSG_LC_DATE_BEFORE_PROFORMA);
		}
	}


	
	public void populateLcModel(LcModel lcModel, UpdateStartedLcRequest lcRequest,
	                            ProformaDetailModel proformaDetail, ProformaMasterModel proformaMaster,
	                            TradingBankModel tradingBank, IssuingBankModel issuingBank, Date expireDate) {

		lcModel.setPerformaNo(proformaDetail.getPerformaNo());
		lcModel.setPerformaDate(DateUtility.getJalaliDate(proformaDetail.getPerformaDate()));
		lcModel.setProformaMasterId(proformaMaster.getId());
		lcModel.setProformaDetailId(proformaDetail.getId());

		// LC basic information
		lcModel.setContractNo(proformaMaster.getContractNo());
		lcModel.setLcNo(lcRequest.getLcNo());
		lcModel.setLcDate(lcRequest.getLcDate());
		lcModel.setLcExpiryDate(expireDate);

		// Trading bank information
		lcModel.setTradingBankId(tradingBank.getId());
		lcModel.setTradingBankTitle(tradingBank.getBankTitle());
		lcModel.setTradingBankBranchTitle(tradingBank.getBankBranchTitle());

		// Issuing bank information
		lcModel.setIssuerBankId(issuingBank.getId());
		lcModel.setIssuerBankName(issuingBank.getBankName());
		lcModel.setIssuerBankBranchName(issuingBank.getBranchName());
		lcModel.setIssuerBankBranchCode(issuingBank.getBranchCode());
		lcModel.setNosaCode(lcNosaCodeService.getNosaCode(issuingBank.getId()));

		// Additional configuration
		lcModel.setCreditExpirePeriod(proformaDetail.getCreditExpirePeriod());
		lcModel.setDeadlineDays(proformaDetail.getDeadlineDays());
		lcModel.setPaymentDeferral(PAYMENT_DEFERRAL_NONE);
		lcModel.setRequireDispatchFile(lcRequest.getRequireDispatchFile());
		lcModel.setLcAttachmentId(lcRequest.getLcAttachmentId());
		lcModel.setAcknowledgment(Acknowledgment.RECKONING);

	}

	
	public void updateTradingBankIfPresent(LcModel lc, UpdateAcceptedLcRequest request) {
		if (request.getTradingBankId() != null) {
			TradingBankModel tradingBank = tradingBankRepository.findById(request.getTradingBankId())
					.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
							MSG_TRADING_BANK_NOT_FOUND));
			lc.setTradingBankTitle(tradingBank.getBankTitle());
			lc.setTradingBankBranchTitle(tradingBank.getBankBranchTitle());
			lc.setTradingBankId(tradingBank.getId());
		}
	}


	
	public void updateIssuingBankIfPresent(LcModel lc, UpdateAcceptedLcRequest request) {
		if (request.getIssuerBankId() != null) {
			IssuingBankModel issuingBank = issuingBankRepository.findById(request.getIssuerBankId())
					.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
							MSG_ISSUING_BANK_NOT_FOUND));
			lc.setIssuerBankName(issuingBank.getBankName());
			lc.setIssuerBankBranchName(issuingBank.getBranchName());
			lc.setIssuerBankBranchCode(issuingBank.getBranchCode());
			lc.setIssuerBankId(issuingBank.getId());
		}
	}


	
	public void updateLcDetailsIfPresent(LcModel lc, UpdateAcceptedLcRequest request) {
		if (request.getLcNo() != null) {
			lc.setLcNo(request.getLcNo());
		}
		if (request.getLcDate() != null) {
			lc.setLcDate(request.getLcDate());
		}
		if (request.getLcExpiryDate() != null) {
			lc.setLcExpiryDate(request.getLcExpiryDate());
		}
		if (request.getNosaCode() != null) {
			lc.setNosaCode(request.getNosaCode());
		}
		if (request.getSettlementDueDate() != null) {
			lc.setSettlementDueDate(request.getSettlementDueDate());
		}
		if (request.getLcAttachmentId() != null) {
			lc.setLcAttachmentId(request.getLcAttachmentId());
		}
		if (request.getDispatchAttachmentId() != null) {
			lc.setDispatchAttachmentId(request.getDispatchAttachmentId());
		}
		if (request.getNotificationDocumentId() != null) {
			lc.setNotificationDocumentId(request.getNotificationDocumentId());
		}
	}


	
	public void validateDispatchFileRequirement(LcModel lcModel, String dispatchFileId) {
		if (Boolean.TRUE.equals(lcModel.getRequireDispatchFile()) && dispatchFileId == null) {
			throw new InternalSaleCustomException.ValidationException(
					MSG_LC_DISPATCH_FILE_REQUIRED);
		}
	}


	
	public BrokerEmailRequest buildLcBrokerEmailRequest(ProformaDetailModel detail, BrokerModel broker) {

		var proformaMaster = proformaMasterRepository.findById(detail.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(
						"قرارداد فروش وجود ندارد "));

		BrokerEmailRequest request = new BrokerEmailRequest();
		request.setContractNo(proformaMaster.getContractNo());
		request.setContractDate(detail.getContractDate());
		request.setQuantity(proformaMaster.getTotalQuantity().longValue());
		request.setCustomerName(proformaMaster.getCustomerName());
		request.setGoodName(proformaMaster.getGoodName());
		request.setBrokerName(broker.getName());
		request.setBrokerEmail(broker.getEmail());
		return request;
	}


	
	public void markAllLcsAsReckoning(Long proformaMasterId) {
		List<LcModel> lcItems = lcRepository.findByMasterId(proformaMasterId);

		if (lcItems == null || lcItems.isEmpty()) {
			log.warn("No LC items found for proformaMasterId: {}", proformaMasterId);
			return;
		}


		for (LcModel lcItem : lcItems) {
			boolean oldReckoningSend = lcItem.isReckoningSend();
			if (!oldReckoningSend) {
				Date newReckoningSendDate = new Date();
				lcItem.setReckoningSend(true);
				lcItem.setReckoningSendDate(newReckoningSendDate);
				lcItem.setAcknowledgment(Acknowledgment.RECKONING);

			}
		}
		lcRepository.saveAllAndFlush(lcItems);
	}

	
	public void sendLcBrokerReckoningEmail(BrokerEmailRequest brokerEmailRequest, String emailContent) {
		log.info("Generated LC broker reckoning email content for broker: {} - Content: {}",
				brokerEmailRequest.getBrokerName(), emailContent);
		notificationService.sendEmailForLcBroker(brokerEmailRequest, emailContent);
	}


	
	public String buildCancellationRecord(LcCancelRequest request) {
		String timestamp = DateUtility.getJalaliDate(new Date());
		String userFullName = com.nicico.copper.core.SecurityUtil.getFullName();
		String notes = request.getDescription() != null ? request.getDescription() : "ندارد";

		return String.format(
				"""
						سابقه ابطال اعتبار اسنادی
						**************************
						تاریخ و زمان ابطال: %s
						نام کاربری اقدام کننده: %s
						دلیل ابطال: %s
						توضیحات تکمیلی: %s
						وضعیت: ابطال شده
						**************************""",
				timestamp, userFullName, LcCancellationReason.BUYER_WITHDRAWAL, notes
		);
	}


	
	public void appendCancellationRecord(LcModel model, String cancellationRecord) {
		String existingDesc = model.getDescription() != null ? model.getDescription() : "";
		if (!existingDesc.isEmpty()) {
			model.setDescription(existingDesc + "\n\n" + cancellationRecord);
		} else {
			model.setDescription(cancellationRecord);
		}
	}

	
	public BrokerEmailRequest buildExtraBillBrokerEmailRequest(ProformaMasterModel proformaMaster, BrokerModel broker) {
		markAllAsReckoning(proformaMaster.getId());

		BrokerEmailRequest request = new BrokerEmailRequest();
		request.setContractNo(proformaMaster.getContractNo());
		request.setContractDate(proformaMaster.getContractDate());
		request.setQuantity(proformaMaster.getTotalQuantity().longValue());
		request.setCustomerName(proformaMaster.getCustomerName());
		request.setGoodName(proformaMaster.getGoodName());
		request.setBrokerName(broker.getName());
		request.setBrokerEmail(broker.getEmail());

		return request;
	}

	
	public String generateExtraBillBrokerEmailContent(BrokerEmailRequest dto) {
		return "کارگزاری محترم " + dto.getBrokerName() + " : قرارداد شماره " + dto.getContractNo() +
				"  مورخ  " + dto.getContractDate() + " جهت خرید " + dto.getQuantity() +
				" کیلوگرم محصول " + dto.getGoodName() + " توسط شرکت:  " + dto.getCustomerName() +
				" جهت تسویه مورد تایید می باشد";
	}

	
	public String generateExtraBillBrokerEmailContent(long extraBillId) {
		throw new UnsupportedOperationException("This method requires ExtraBillRepository and should be called from ExtraBillService");
	}

	
	public void sendExtraBillBrokerReckoningEmail(BrokerEmailRequest emailRequest, String emailContent) {
		log.info("Generated Extra Bill broker reckoning email content for broker: {} - Content: {}",
				emailRequest.getBrokerName(), emailContent);
		notificationService.sendEmailForLcBroker(emailRequest, emailContent);
	}

	
	public void markAllAsReckoning(Long proformaMasterId) {
		List<com.nicico.internal.sales.extrabill.model.ExtraBankBillModel> billModels =
				extraBillRepository.findAllByProformaMasterId(proformaMasterId);

		if (billModels == null || billModels.isEmpty()) {
			log.warn("No ExtraBill items found for proformaMasterId: {}", proformaMasterId);
			return;
		}


		for (ExtraBankBillModel item : billModels) {
			boolean oldReckoningSend = item.isReckoningSend();
			if (!oldReckoningSend) {
				Date newReckoningSendDate = new Date();
				item.setReckoningSend(true);
				item.setReckoningSendDate(newReckoningSendDate);
				item.setAcknowledgment(Acknowledgment.RECKONING);

			}
		}
	}

	
	public String buildExtraBillCancellationRecord(com.nicico.internal.sales.extrabill.dto.ExtraBillCancelRequest request) {
		String timestamp = DateUtility.getJalaliDate(new Date());
		String userFullName = com.nicico.copper.core.SecurityUtil.getFullName();
		String notes = request.getDescription() != null ? request.getDescription() : "ندارد";

		return String.format(
				"""
						سابقه ابطال برات الکترونیک
						**************************
						تاریخ و زمان ابطال: %s
						نام کاربری اقدام کننده: %s
						دلیل ابطال: %s
						توضیحات تکمیلی: %s
						وضعیت: ابطال شده
						**************************""",
				timestamp, userFullName, com.nicico.internal.sales.lc.enums.LcCancellationReason.BUYER_WITHDRAWAL, notes
		);
	}

	
	public void appendExtraBillCancellationRecord(com.nicico.internal.sales.extrabill.model.ExtraBankBillModel model, String cancellationRecord) {
		String existingDesc = model.getDescription() != null ? model.getDescription() : "";
		if (!existingDesc.isEmpty()) {
			model.setDescription(existingDesc + "\n\n" + cancellationRecord);
		} else {
			model.setDescription(cancellationRecord);
		}
	}

	
	public BrokerEmailRequest buildGaamBrokerEmailRequest(
			com.nicico.internal.sales.proforma.model.ProformaDetailModel detail, BrokerModel broker) {
		markAllGaamAsReckoning(detail.getProformaMasterId());

		BrokerEmailRequest request = new BrokerEmailRequest();
		request.setContractNo(proformaMasterRepository.findById(detail.getProformaMasterId())
				.map(com.nicico.internal.sales.proforma.model.ProformaMasterModel::getContractNo).get());

		request.setContractDate(detail.getContractDate());
		request.setQuantity(
				proformaMasterRepository.findById(detail.getProformaMasterId())
						.map(m -> m.getTotalQuantity().longValue())
						.get());

		request.setCustomerName(
				proformaMasterRepository.findById(detail.getProformaMasterId())
						.map(com.nicico.internal.sales.proforma.model.ProformaMasterModel::getCustomerName)
						.get());
		request.setGoodName(
				proformaMasterRepository.findById(detail.getProformaMasterId())
						.map(com.nicico.internal.sales.proforma.model.ProformaMasterModel::getGoodName)
						.get());
		request.setBrokerName(broker.getName());
		request.setBrokerEmail(broker.getEmail());

		return request;
	}

	
	public String generateGaamBrokerEmailContent(BrokerEmailRequest dto) {
		return "کارگزاری محترم " + dto.getBrokerName() + " : قرارداد شماره " + dto.getContractNo() +
				"  مورخ  " + dto.getContractDate() + " جهت خرید " + dto.getQuantity() +
				" کیلوگرم محصول " + dto.getGoodName() + " توسط شرکت:  " + dto.getCustomerName() +
				" جهت تسویه مورد تایید می باشد";
	}

	
	public String generateGaamBrokerEmailContent(long gaamId) {
		throw new UnsupportedOperationException("This method requires GaamRepository and should be called from GaamService");
	}

	
	public void sendGaamBrokerReckoningEmail(BrokerEmailRequest emailRequest, String emailContent) {
		log.info("Generated GAAM broker reckoning email content for broker: {} - Content: {}",
				emailRequest.getBrokerName(), emailContent);
		notificationService.sendEmailForLcBroker(emailRequest, emailContent);
	}

	
	public void markAllGaamAsReckoning(Long proformaMasterId) {
		List<com.nicico.internal.sales.gaam.model.GaamModel> billModels =
				gaamRepository.findAllByProformaMasterId(proformaMasterId);

		if (billModels == null || billModels.isEmpty()) {
			log.warn("No GAAM items found for proformaMasterId: {}", proformaMasterId);
			return;
		}


		for (com.nicico.internal.sales.gaam.model.GaamModel item : billModels) {
			boolean oldReckoningSend = item.isReckoningSend();
			if (!oldReckoningSend) {
				Date newReckoningSendDate = new Date();
				item.setReckoningSend(true);
				item.setReckoningSendDate(newReckoningSendDate);
				item.setAcknowledgment(Acknowledgment.RECKONING);

			}
		}

	}

	
	public String buildGaamCancellationRecord(GaamCancelRequest request) {
		String timestamp = DateUtility.getJalaliDate(new Date());
		String userFullName = com.nicico.copper.core.SecurityUtil.getFullName();
		String notes = request.getDescription() != null ? request.getDescription() : "ندارد";

		return String.format(
				"""
						سابقه ابطال اوراق گام
						**************************
						تاریخ و زمان ابطال: %s
						نام کاربری اقدام کننده: %s
						دلیل ابطال: %s
						توضیحات تکمیلی: %s
						وضعیت: ابطال شده
						**************************""",
				timestamp, userFullName, com.nicico.internal.sales.lc.enums.LcCancellationReason.BUYER_WITHDRAWAL, notes
		);
	}

	
	public void appendGaamCancellationRecord(com.nicico.internal.sales.gaam.model.GaamModel model, String cancellationRecord) {
		String existingDesc = model.getDescription() != null ? model.getDescription() : "";
		if (!existingDesc.isEmpty()) {
			model.setDescription(existingDesc + "\n\n" + cancellationRecord);
		} else {
			model.setDescription(cancellationRecord);
		}
	}
}

