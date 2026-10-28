package com.nicico.internal.sales.deposit.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceHistory;
import com.nicico.bpmsclient.model.flowable.task.UserTaskReportDTO;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.copper.oauth.common.repository.OAUserDAO;
import com.nicico.internal.sales.deposit.dto.CashDepositIssueHistoryDto;
import com.nicico.internal.sales.deposit.dto.CashDepositIssueMapper;
import com.nicico.internal.sales.deposit.dto.CashDepositRevokingReadyMapper;
import com.nicico.internal.sales.deposit.model.CashDepositIssueHistoryModel;
import com.nicico.internal.sales.deposit.model.CashDepositModel;
import com.nicico.internal.sales.deposit.dto.CashDepositReadyReckoningDto;
import com.nicico.internal.sales.deposit.repository.CashDepositReadyRevokingRepository;
import com.nicico.internal.sales.deposit.repository.CashDepositRepository;
import com.nicico.internal.sales.deposit.repository.CashDipositIssueHistoryRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.lc.dto.request.BrokerEmailRequest;
import com.nicico.internal.sales.lc.enums.Acknowledgment;
import com.nicico.internal.sales.lc.service.CreditHelper;
import com.nicico.internal.sales.notification.service.NotificationService;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.util.TextUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashDepositServiceImpl implements CashDepositService {

	private static final String MSG_PROFORMA_LC_ISSUE_NOT_FOUND = "رکورد پیش فاکتور جهت صدور اعتبار پیدا نشد";
	private final CashDipositIssueHistoryRepository repository;
	private final CashDepositRepository cashDepositRepository;
	private final CashDepositIssueMapper cashDepositIssueMapper;
	private final CashDepositRevokingReadyMapper cashDepositRevokingReadyMapper;
	private final CashDepositReadyRevokingRepository cashDepositReadyRevokingRepository;
	private final ProformaDetailRepository proformaDetailRepository;
	private final ProformaMasterRepository proformaMasterRepository;
	private final NotificationService notificationService;
	private final CreditHelper creditHelper;
	private final BpmsClientService bpmsClientService;
	private final OAUserDAO oaUserDAO;

	@Override
	public CashDepositIssueHistoryDto.Info getById(Long id) {
		CashDepositIssueHistoryModel entity = repository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
						MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		return cashDepositIssueMapper.toDTO(entity);
	}

	@Override
	public List<CashDepositIssueHistoryDto.Info> getByMasterId(Long masterId) {
		var entities = repository.findByMasterId(masterId);
		return entities.stream()
				.map(cashDepositIssueMapper::toDTO)
				.toList();
	}

	@Override
	public SearchDTO.SearchRs<CashDepositReadyReckoningDto.Info> findReadyReckoning(SearchDTO.SearchRq request) {
		SearchDTO.SearchRs<CashDepositReadyReckoningDto.Info> result =
				SearchUtil.search(cashDepositReadyRevokingRepository, request, cashDepositRevokingReadyMapper::toDTO);
		if (result.getList() != null) {
			result.getList().forEach(item -> {
				if (item.getReckoningSendDate() != null) {
					item.setReckoningSend(true);
				}
			});
		}
		return result;
	}

	@Override
	public SearchDTO.SearchRs<CashDepositIssueHistoryDto.Info> searchIssueHistory(SearchDTO.SearchRq request) {
		return SearchUtil.search(repository, request, cashDepositIssueMapper::toDTO);
	}

	@Override
	public String generateBrokerEmailContent(long id) {

		CashDepositModel billModel = cashDepositRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		ProformaDetailModel detail = proformaDetailRepository.findById(billModel.getProformaDetailId()).orElseThrow(
				() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		BrokerEmailRequest dto = buildGaamBrokerEmailRequest(detail);
		return "کارگزاری محترم " + dto.getBrokerName() + " : قرارداد شماره " + dto.getContractNo() +
				"  مورخ  " + dto.getContractDate() + " جهت خرید " + dto.getQuantity() +
				" کیلوگرم محصول " + dto.getGoodName() + " توسط شرکت:  " + dto.getCustomerName() +
				" جهت تسویه مورد تایید می باشد";

	}

	public void markAllAsReckoning(Long proformaMasterId) {
		List<CashDepositModel> cashDepositModels = cashDepositRepository.findAllByProformaMasterId(proformaMasterId);
		for (CashDepositModel item : cashDepositModels) {
				item.setReckoningSend(true);
				item.setReckoningSendDate(new Date());
				item.setAcknowledgment(Acknowledgment.RECKONING);
			cashDepositRepository.saveAndFlush(item);
		}
	}

	public BrokerEmailRequest buildGaamBrokerEmailRequest(ProformaDetailModel detail) {
		markAllAsReckoning(detail.getProformaMasterId());
		var masterModel = proformaMasterRepository.findById(detail.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		var broker = creditHelper.fetchBrokerForTrade(masterModel.getTradeId());

		BrokerEmailRequest request = new BrokerEmailRequest();

		request.setContractNo(masterModel.getContractNo());
		request.setContractDate(detail.getContractDate());
		request.setQuantity(masterModel.getTotalQuantity().longValue());
		request.setCustomerName(masterModel.getCustomerName());
		request.setGoodName(masterModel.getGoodName());
		request.setBrokerName(broker.getName());
		request.setBrokerEmail(broker.getEmail());

		return request;
	}

	@Transactional
	@Override
	public void sendReckoningEmail(Long id) {
		CashDepositModel cashDepositModel = cashDepositRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		markAllAsReckoning(cashDepositModel.getProformaMasterId());
		ProformaDetailModel detail = proformaDetailRepository.findById(cashDepositModel.getProformaDetailId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		BrokerEmailRequest emailRequest = buildGaamBrokerEmailRequest(detail);
		String emailContent = generateBrokerEmailContent(id);
		sendBrokerReckoningEmail(emailRequest, emailContent);
	}

	@Override
	public void sendBrokerReckoningEmail(BrokerEmailRequest emailRequest, String emailContent) {
		log.info("Generated GAAM broker reckoning email content for broker: {} - Content: {}",
				emailRequest.getBrokerName(), emailContent);
		notificationService.sendEmailForLcBroker(emailRequest, emailContent);
	}

	@Override
	public ProcessInstanceHistory getHistoryDetail(Long id) {
		CashDepositModel cashDepositModel = cashDepositRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		ProformaMasterModel masterModel = proformaMasterRepository.findById(cashDepositModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		return bpmsClientService.getProcessInstanceHistoryById(masterModel.getProcessId());

	}

	@Override
	public Map<String, List<UserTaskReportDTO>> getUserTasksReport(Long id) {
		CashDepositModel cashDepositModel = cashDepositRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		ProformaMasterModel masterModel = proformaMasterRepository.findById(cashDepositModel.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ValidationException(MSG_PROFORMA_LC_ISSUE_NOT_FOUND));
		if (!TextUtility.isValidUUID(masterModel.getProcessId())) return null;
		var report = bpmsClientService.getUserTasksReport(List.of(masterModel.getProcessId()));
		report.values().forEach(taskList -> taskList.forEach(task -> oaUserDAO.findById(Long.valueOf(task.getAssignee())).ifPresent(user -> task.setAssignee(user.getFullName()))));
		return report;

	}
}
