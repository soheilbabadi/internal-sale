package com.nicico.internal.sales.wf.service;

import com.nicico.copper.common.domain.criteria.SearchUtil;
import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.copper.oauth.common.enumeration.EOAUserStatus;
import com.nicico.copper.oauth.common.repository.OAUserDAO;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.wf.dto.ProcessUserAccessDto;
import com.nicico.internal.sales.wf.dto.ProcessUserAccessRequest;
import com.nicico.internal.sales.wf.dto.mapper.ProcessUserAccessMapper;
import com.nicico.internal.sales.wf.dto.request.UserDataDto;
import com.nicico.internal.sales.wf.enums.LcProcessVariable;
import com.nicico.internal.sales.wf.enums.ProformaProcessVariable;
import com.nicico.internal.sales.wf.enums.RemittanceProcessVariable;
import com.nicico.internal.sales.wf.enums.ReversalProcessVariable;
import com.nicico.internal.sales.wf.model.ProcessUserAccessModel;
import com.nicico.internal.sales.wf.repository.ProcessUserAccessRepository;
import com.nicico.internal.sales.wf.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ProcessUserAccessServiceImpl implements ProcessUserAccessService {
	private final ProcessUserAccessRepository processUserAccessRepository;
	private final ProcessUserAccessMapper processUserAccessMapper;
	private final OAUserDAO oaUserDAO;
	private final WorkflowRepository workflowRepository;

	@Override
	public List<ProcessUserAccessDto.Info> getMyAccess() {
		String username = SecurityUtil.getUsername();
		return processUserAccessRepository.findAllByUsername(username).stream().map(processUserAccessMapper::toDTO).toList();
	}

	@Override
	public void delete(String processTitle) {
		processUserAccessRepository.deleteAllByProcessTitle(processTitle);
	}

	@Override
	public ProcessUserAccessDto.Info save(ProcessUserAccessRequest request) {
		var workflow = workflowRepository.findByProcessTitleIgnoreCase(request.getProcessTitle()).orElseThrow();
		var userInfo = oaUserDAO.findById(request.getUserId()).orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("کاربر در سیستم ثب نشده است"));

		Set<String> authorities = SecurityUtil.getAuthorities();
		boolean hasProforma = authorities.contains("C_INS_PROFORMA");
		boolean haslc = authorities.contains("C_INS_LC");

		var model = new ProcessUserAccessModel();
		model.setProcessId(workflow.getId());
		model.setProcessTitle(workflow.getProcessTitle());
		model.setProcessLocalTitle(workflow.getProcessLocalTitle());
		model.setUserId(request.getUserId());
		model.setUsername(userInfo.getUsername());
		model.setFullName(userInfo.getFullName());
		model.setNationalCode(StringUtils.left(userInfo.getNationalCode(), 10));
		String title = request.getProcessTitle();
		String variable = request.getProcessVariable();

		switch (title) {
			case "LC" -> {
				var processVar = LcProcessVariable.fromString(variable);
				assert processVar != null;
				model.setProcessVariable(processVar.name());
				model.setProcessVariableTitle(processVar.getValue());
			}
			case "REMITTANCE" -> {
				var processVar = RemittanceProcessVariable.fromString(variable);
				model.setProcessVariable(processVar.name());
				model.setProcessVariableTitle(processVar.getValue());
			}
			case "PREINVOICE" -> {
				var processVar = ProformaProcessVariable.fromString(variable);
				model.setProcessVariable(processVar.name());
				model.setProcessVariableTitle(processVar.getValue());
			}
			case "REVERSAL" -> {
				var processVar = ReversalProcessVariable.fromString(variable);
				model.setProcessVariable(processVar.name());
				model.setProcessVariableTitle(processVar.getValue());
			}

			default -> throw new InternalSaleCustomException.ValidationException("این فرایند تعریف نشده است: " + title);
		}

		var existingAccess = processUserAccessRepository.findByProcessIdAndUserIdAndProcessVariable(
				model.getProcessId(),
				model.getUserId(),
				model.getProcessVariable()
		);
		if (existingAccess.isPresent()) {
			return processUserAccessMapper.toDTO(existingAccess.get());
		}
		processUserAccessRepository.save(model);
		return processUserAccessMapper.toDTO(model);
	}

	@Override
	public SearchDTO.SearchRs<ProcessUserAccessDto.Info> search(SearchDTO.SearchRq request) {
		return SearchUtil.search(processUserAccessRepository, request, processUserAccessMapper::toDTO);
	}

	@Override
	public void deleteById(Long id) {
		processUserAccessRepository.deleteById(id);
	}

	@Override
	public List<UserDataDto> getUserList(String fullName) {
		if (fullName == null) {
			return oaUserDAO.findAll().stream()
					.filter(x -> x.getStatus() == EOAUserStatus.Enabled)
					.limit(20).map(user -> new UserDataDto(user.getId(), user.getUsername(), user.getFullName(), user.getNationalCode(), user.getStatus().name())).toList();
		}
		return oaUserDAO.findByFullNameContains(fullName).stream()
				.filter(x -> x.getStatus() == EOAUserStatus.Enabled)
				.limit(20).map(user -> new UserDataDto(user.getId(), user.getUsername(), user.getFullName(), user.getNationalCode(), user.getStatus().name())).toList();
	}
}
