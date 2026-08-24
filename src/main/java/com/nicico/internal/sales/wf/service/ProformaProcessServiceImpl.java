package com.nicico.internal.sales.wf.service;

import com.nicico.bpmsclient.model.flowable.process.ProcessInsHistoryDTO;
import com.nicico.bpmsclient.model.flowable.process.ProcessInstance;
import com.nicico.bpmsclient.model.flowable.process.ProcessInstanceStatus;
import com.nicico.bpmsclient.model.flowable.process.StartProcessWithDataDTO;
import com.nicico.bpmsclient.model.flowable.task.FlowTaskDto;
import com.nicico.bpmsclient.model.flowable.task.GridDTO;
import com.nicico.bpmsclient.model.request.ReviewTaskRequest;
import com.nicico.bpmsclient.service.BpmsClientService;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.enums.EntityTypeEnum;
import com.nicico.internal.sales.export.repository.ExportNotificationConfigRepository;
import com.nicico.internal.sales.export.service.ExportDocService;
import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.notification.dto.EmailRequest;
import com.nicico.internal.sales.notification.service.MailService;
import com.nicico.internal.sales.proforma.enums.ProformaReversalStatus;
import com.nicico.internal.sales.proforma.enums.WorkflowApproveStatus;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.wf.dto.ProformaVariablesInput;
import com.nicico.internal.sales.wf.dto.TaskActionDto;
import com.nicico.internal.sales.wf.enums.ProformaProcessVariable;
import com.nicico.internal.sales.wf.model.WorkflowModel;
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProformaProcessServiceImpl implements ProformaProcessService {

	private static final String MSG_PROFORMA_NOT_FOUND = "پیش فاکتور وجود ندارد";
	private static final String MSG_CUSTOMER_NOT_FOUND = "اطلاعات مشتری پیدا نشد";
	private static final String MSG_FILE_WRITE_ERROR = "خطایی در هنگام نوشتن فایل اتفاق افتاد";
	private static final String MSG_FILE_EMPTY_LIST = "لیست فایلها خالی است و امکان ایجاد فایل پی دی اف وجود ندارد";
	private static final String MSG_EMAIL_PROFORMA_ERROR = "خطا در ارسال ایمیل پیش فاکتور";
	private static final String MSG_EMAIL_PROFORMA_SUBJECT = "پیش فاکتور قرارداد شماره";
	private static final String MSG_EMAIL_PROFORMA_CONTENT = "با سلام پیش فاکتور شرکت {0} شماره قرارداد {1} مورخ {2} به پیوست ارسال میگردد.با تشکر";
	private static final String MSG_LOG_EMAIL_PROFORMA_SUCCESS = "ایمیل پیش فاکتور با موفقیت برای شناسه {0} ارسال شد. وضعیت: {1}";
	private static final String MSG_LOG_ERROR_UNEXPECTED = "خطای غیرمنتظره در ارسال ایمیل برای شناسه {0}: {1}";
	private static final String MSG_ACCESS_DENIED_START_PROCESS = "شما اجازه شروع فرایند پیش فاکتور را ندارید";
	private static final String MSG_BPMS_CONNECTION_ERROR = "خطا در اتصال به کارتابل";
	private static final String CONFIG_NOT_FOUND_MESSAGE = "تنظیمات پیکربندی وجود ندارد";

	// --- File/param constants ---
	private static final String PDF_EXTENSION = ".pdf";
	private static final String FILE_NAME_PREFIX_PROFORMA = "proforma_";
	private static final String PROCESS_ID_PLACEHOLDER = "-";

	private final ProformaMasterRepository proformaMasterRepository;
	private final BpmsClientService bpmsClientService;
	private final ProcessVariableProvider processVariableProvider;
	private final ProcessUserAccessRepository processUserAccessRepository;
	private final ExportNotificationConfigRepository exportNotificationConfigRepository;
	private final CustomerRepository customerRepository;
	private final MailService mailService;
	private final ExportDocService exportDocService;
	private final LcRepository lcRepository;
	private final RemittanceMasterRepository remittanceMasterRepository;

	@Value("${nicico.bcc-address}")
	private String bccAddress;

	@Override
	public ProcessInstance startProcessWithData(StartProcessWithDataDTO startProcessDto) {
		return bpmsClientService.startProcessWithData(startProcessDto);
	}

	@Override
	public ProcessInstance startProformaProcess(ProformaVariablesInput input) {
		assertCanStartProcess();
		Map<String, Object> variables = processVariableProvider.createProformaRequestVariables(input);
		StartProcessWithDataDTO dataDTO = buildStartProcessDTO(variables);
		return bpmsClientService.startProcessWithData(dataDTO);
	}

	@Override
	public ProcessInstance startProformaProcess(Long proformaMasterId) {
		assertCanStartProcess();
		try {
			ProformaMasterModel masterModel = proformaMasterRepository.findById(proformaMasterId)
					.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_PROFORMA_NOT_FOUND));

			ProformaVariablesInput input = buildProformaVariablesInput(masterModel);
			Map<String, Object> variables = processVariableProvider.createProformaRequestVariables(input);
			StartProcessWithDataDTO dataDTO = buildStartProcessDTO(variables);
			return this.startProcessWithData(dataDTO);

		} catch (Exception ex) {
			throw new InternalSaleCustomException.BpmsClientException(
					MSG_BPMS_CONNECTION_ERROR,
					new ArrayList<>(Collections.singletonList(ex.getMessage())));
		}
	}

	@Override
	public List<String> cancelDoungelingProcess(GridDTO gridDTO) {
		List<String> cancelledIds = new ArrayList<>();

		if (gridDTO == null || gridDTO.getData() == null) {
			return cancelledIds;
		}

		WorkflowModel proformaWorkflow = processVariableProvider.getProformaWorkflowByTitle();
		WorkflowModel lcWorkflow = processVariableProvider.getLcWorkflowByTitle();
		WorkflowModel reversalWorkflow = processVariableProvider.getReversalWorkflowByTitle();
		WorkflowModel remittanceWorkflow = processVariableProvider.getRemittanceWorkflowByTitle();

		for (FlowTaskDto task : gridDTO.getData()) {
			if (task == null || task.getProcessInstanceId() == null) continue;

			cancelledIds.add(task.getProcessInstanceId());
			var history = bpmsClientService.getProcessInstanceHistoryById(task.getProcessInstanceId());
			String definitionKey = history.getProcessDefinitionKey();
			String processInstanceId = task.getProcessInstanceId();

			if (definitionKey.equals(proformaWorkflow.getDefinitionKey())) {
				cancelIfOrphan(processInstanceId,
						proformaMasterRepository.findByProcessId(processInstanceId).orElse(null));

			} else if (definitionKey.equals(reversalWorkflow.getDefinitionKey())) {
				cancelIfOrphan(processInstanceId,
						proformaMasterRepository.findByReversalProcessId(processInstanceId).orElse(null));

			} else if (definitionKey.equals(lcWorkflow.getDefinitionKey())) {
				if (lcRepository.findByProcessId(processInstanceId).isEmpty()) {
					bpmsClientService.cancelProcessInstance(processInstanceId);
				}

			} else if (definitionKey.equals(remittanceWorkflow.getDefinitionKey())) {
				cancelIfOrphan(processInstanceId,
						remittanceMasterRepository.findByProcessId(processInstanceId).orElse(null));
			}
		}

		return cancelledIds;
	}

	@Override
	@Transactional
	public void reviewTask(ReviewTaskRequest reviewTaskRequest) {
		try {
			bpmsClientService.reviewTask(reviewTaskRequest);
			if (!reviewTaskRequest.getApprove()) {
				proformaMasterRepository.findByProcessId(reviewTaskRequest.getProcessInstanceId()).ifPresent(masterModel -> {
					applyStatus(masterModel, WorkflowApproveStatus.CANCELED, true);
					proformaMasterRepository.saveAndFlush(masterModel);
					bpmsClientService.cancelProcessInstance(reviewTaskRequest.getProcessInstanceId());
				});
				return;
			}

			proformaMasterRepository.findByProcessId(reviewTaskRequest.getProcessInstanceId()).ifPresent(masterModel -> {
				ProcessInsHistoryDTO status = bpmsClientService
						.getProcessInstanceHistory(reviewTaskRequest.getProcessInstanceId());
				if (status == null) return;

				switch (status.getStatus()) {
					case ACTIVE:
						applyStatus(masterModel, WorkflowApproveStatus.IN_PROGRESS, false);
						break;

					case CANCELED:
						applyStatus(masterModel, WorkflowApproveStatus.CANCELED, true);
						break;

					case FINISHED:
						boolean accepted = processVariableProvider
								.isProcessAcceptedFinally(reviewTaskRequest.getProcessInstanceId());
						applyStatus(masterModel,
								accepted ? WorkflowApproveStatus.ACCEPTED : WorkflowApproveStatus.CANCELED,
								true);

						if (accepted) {
							sendEmailWithProformaAttachment(masterModel.getId());
						}
						break;
				}
				proformaMasterRepository.saveAndFlush(masterModel);
			});
		} catch (Exception ex) {
			throw new InternalSaleCustomException.BpmsClientException(MSG_BPMS_CONNECTION_ERROR,
					new ArrayList<>(Collections.singletonList(ex.getMessage())));
		}
	}

	@Transactional
	@Override
	public void approveTask(TaskActionDto taskActionDto) {
		taskActionDto.setApprove(true);
		this.reviewTask(processVariableProvider.prepareReviewTaskRequest(taskActionDto));
	}

	@Transactional
	@Override
	public void rejectTask(TaskActionDto taskActionDto) {
		taskActionDto.setApprove(false);
		this.reviewTask(processVariableProvider.prepareReviewTaskRequest(taskActionDto));
	}

	@Override
	@Transactional
	public void refreshProformaStatus() {
		List<ProformaMasterModel> masterModelList = proformaMasterRepository
				.findAllByWorkflowApproveStatusIn(
						List.of(WorkflowApproveStatus.DRAFT, WorkflowApproveStatus.IN_PROGRESS));

		for (ProformaMasterModel masterModel : masterModelList) {
			try {
				if (masterModel.getIsProcessFinal() || PROCESS_ID_PLACEHOLDER.equals(masterModel.getProcessId())) {
					continue;
				}

				ProcessInsHistoryDTO status = bpmsClientService.getProcessInstanceHistory(masterModel.getProcessId());
				if (status == null) continue;

				switch (status.getStatus()) {
					case ACTIVE:
						applyStatus(masterModel, WorkflowApproveStatus.IN_PROGRESS, false);
						break;

					case CANCELED:
						applyStatus(masterModel, WorkflowApproveStatus.CANCELED, true);
						break;

					case FINISHED:
						boolean accepted = processVariableProvider.isProcessAcceptedFinally(masterModel.getProcessId());
						applyStatus(masterModel,
								accepted ? WorkflowApproveStatus.ACCEPTED : WorkflowApproveStatus.CANCELED,
								true);
						break;
				}

				proformaMasterRepository.saveAndFlush(masterModel);


			} catch (Exception ex) {
				log.error("خطا در به روزرسانی وضعیت پیش فاکتور {}: {}", masterModel.getId(), ex.getMessage());
			}
		}
	}

	@Override
	public void startFailedProcess() {
		List<ProformaMasterModel> masterModelList = proformaMasterRepository
				.findAllByWorkflowApproveStatusIn(
						List.of(WorkflowApproveStatus.DRAFT, WorkflowApproveStatus.IN_PROGRESS));

		for (ProformaMasterModel masterModel : masterModelList) {
			try {
				if (isProcessAlive(masterModel.getProcessId())) {
					log.info("فرایند پیش فاکتور {} هنوز فعال است، رد شد", masterModel.getId());
					continue;
				}
				startProformaProcess(masterModel.getId());
			} catch (Exception ex) {
				log.error("خطا در شروع مجدد فرایند پیش فاکتور {}: {}", masterModel.getId(), ex.getMessage());
			}
		}
	}

	@Override
	public boolean canStartProcess() {
		var workflow = processVariableProvider.getProformaWorkflowByTitle();
		return processUserAccessRepository.findAllByProcessTitle(workflow.getProcessTitle())
				.stream()
				.anyMatch(access -> Objects.equals(access.getUserId(), SecurityUtil.getUserId())
						&& ProformaProcessVariable.Proforma.name().equalsIgnoreCase(access.getProcessVariable()));
	}

	// -------------------------------------------------------------------------
	// Private helpers — process management
	// -------------------------------------------------------------------------

	private void assertCanStartProcess() {
		if (!canStartProcess()) {
			throw new InternalSaleCustomException.AccessDeniedException(MSG_ACCESS_DENIED_START_PROCESS);
		}
	}

	private boolean isProcessAlive(String processId) {
		if (processId == null || PROCESS_ID_PLACEHOLDER.equals(processId)) return false;
		try {
			ProcessInsHistoryDTO status = bpmsClientService.getProcessInstanceHistory(processId);
			return status != null && status.getStatus() == ProcessInstanceStatus.ACTIVE;
		} catch (Exception ex) {
			log.warn("خطا در بررسی وضعیت فرایند {}: {}", processId, ex.getMessage());
			return false;
		}
	}


	private void applyStatus(ProformaMasterModel model,
	                         WorkflowApproveStatus status,
	                         boolean isProcessFinal) {
		model.setWorkflowApproveStatus(status);
		model.setIsProcessFinal(isProcessFinal);
		model.setIsReversalProcessFinal(false);
	}

	private void cancelIfOrphan(String processInstanceId, Object model) {
		if (model == null) {
			bpmsClientService.cancelProcessInstance(processInstanceId);
		}
	}

	private StartProcessWithDataDTO buildStartProcessDTO(Map<String, Object> variables) {
		var workflow = processVariableProvider.getProformaWorkflowByTitle();
		StartProcessWithDataDTO dataDTO = new StartProcessWithDataDTO();
		dataDTO.setProcessDefinitionKey(workflow.getDefinitionKey());
		dataDTO.setVariables(variables);
		return dataDTO;
	}

	private ProformaVariablesInput buildProformaVariablesInput(ProformaMasterModel masterModel) {
		ProformaVariablesInput input = new ProformaVariablesInput();
		var detailList = masterModel.getProformaDetailModelLists();
		input.setProformaMasterId(masterModel.getId());
		input.setContractDate(detailList.get(0).getContractDate());
		input.setGoodId(masterModel.getGoodId());
		input.setGoodName(masterModel.getGoodName());
		input.setCustomerName(masterModel.getCustomerName());
		input.setContractNo(String.valueOf(masterModel.getContractNo()));
		input.setCommission(masterModel.getCommissionPercentage());
		return input;
	}

	private void sendEmailWithProformaAttachment(Long proformaMasterId) {
		var exportConfig = exportNotificationConfigRepository.findByEntityType(EntityTypeEnum.PROFORMA)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(CONFIG_NOT_FOUND_MESSAGE));
		log.info(exportConfig.toString());

		if (!exportConfig.getSendEmail()) return;

		try {
			ProformaMasterModel masterModel = proformaMasterRepository.findById(proformaMasterId)
					.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_PROFORMA_NOT_FOUND));
			CustomerModel customer = customerRepository.findById(masterModel.getCustomerId())
					.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(MSG_CUSTOMER_NOT_FOUND));

			List<Long> activeDetailIds = getActiveProformaDetailIds(masterModel);
			byte[] pdfContent = buildSignedProformaPdf(activeDetailIds);
			Path filePath = createTempFile(String.valueOf(masterModel.getContractNo()), pdfContent);
			EmailRequest emailRequest = prepareProformaEmailRequest(masterModel, customer);

			HttpResponse<String> emailResponse = mailService.sendMail(emailRequest, filePath.toString());
			log.info(formatMessage(MSG_LOG_EMAIL_PROFORMA_SUCCESS, proformaMasterId, emailResponse.body()));

		} catch (InternalSaleCustomException ex) {
			log.error(formatMessage(MSG_LOG_ERROR_UNEXPECTED, proformaMasterId, ex.getMessage()), ex);
			throw ex;
		} catch (Exception ex) {
			log.error(formatMessage(MSG_LOG_ERROR_UNEXPECTED, proformaMasterId, ex.getMessage()), ex);
			throw new InternalSaleCustomException.FileContentException(MSG_EMAIL_PROFORMA_ERROR);
		}
	}

	private List<Long> getActiveProformaDetailIds(ProformaMasterModel masterModel) {
		return masterModel.getProformaDetailModelLists().stream()
				.filter(detail -> detail.getProformaReversalStatus() != ProformaReversalStatus.CANCELED)
				.map(ProformaDetailModel::getId)
				.toList();
	}

	/**
	 * Exports each active proforma detail as its own signed .docx (via
	 * {@link ExportDocService#exportProformaDocOnlySigned(Long)}), then merges
	 * them all into a single PDF via {@link ExportDocService#convertDocListToPdf(List)}.
	 */
	private byte[] buildSignedProformaPdf(List<Long> detailIds) {
		List<XWPFDocument> documents = detailIds.stream()
				.map(exportDocService::exportProformaDocOnlySigned)
				.filter(bytes -> bytes != null && bytes.length > 0)
				.map(this::toXwpfDocument)
				.toList();

		if (documents.isEmpty()) {
			throw new InternalSaleCustomException.FileContentException(MSG_FILE_EMPTY_LIST);
		}

		return exportDocService.convertDocListToPdf(documents);
	}

	private XWPFDocument toXwpfDocument(byte[] docBytes) {
		try {
			return new XWPFDocument(new ByteArrayInputStream(docBytes));
		} catch (IOException ex) {
			log.error("خطا در بارگذاری فایل پیش فاکتور: {}", ex.getMessage(), ex);
			throw new InternalSaleCustomException.FileContentException(MSG_FILE_WRITE_ERROR);
		}
	}

	private Path createTempFile(String contractNo, byte[] content) throws IOException {
		Path filePath = Paths.get(ProformaProcessServiceImpl.FILE_NAME_PREFIX_PROFORMA + contractNo + PDF_EXTENSION);
		try (OutputStream outputStream = Files.newOutputStream(filePath)) {
			outputStream.write(content);
		}
		return filePath;
	}

	private EmailRequest prepareProformaEmailRequest(ProformaMasterModel masterModel, CustomerModel customer) {
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setSubject(MSG_EMAIL_PROFORMA_SUBJECT + " : " + masterModel.getContractNo());
		emailRequest.setBccRecipients(bccAddress);
		emailRequest.setToRecipients(customer.getEmail());
		emailRequest.setContent(formatMessage(MSG_EMAIL_PROFORMA_CONTENT,
				masterModel.getCustomerName(),
				masterModel.getContractNo(),
				masterModel.getContractDate()));
		return emailRequest;
	}

	private String formatMessage(String template, Object... args) {
		return MessageFormat.format(template, args);
	}
}