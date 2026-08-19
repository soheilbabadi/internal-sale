package com.nicico.internal.sales.wf.service;

import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.wf.model.ProcessUserAccessModel;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProcessUserAccessResolver {
	private ProcessUserAccessResolver() {
	}

	public static Map<String, String> resolveUserAccess(List<ProcessUserAccessModel> accessList, List<String> requiredVariables) {
		return requiredVariables.stream()
				.collect(Collectors.toMap(variable -> variable, variable -> {
					List<ProcessUserAccessModel> matches = accessList.stream()
							.filter(a -> a.getProcessVariable().equals(variable))
							.toList();

					String title = matches.stream()
							.findFirst()
							.map(ProcessUserAccessModel::getProcessVariableTitle)
							.orElse(variable);

					return matches.stream()
							.min(Comparator.comparing(a -> !a.getUsername().equals(SecurityUtil.getUsername())))
							.orElseThrow(() -> new InternalSaleCustomException.ValidationException(
									"متغیر " + title + " به هیچ کاربری تخصیص نیافته است"))
							.getUserId().toString();
				}));
	}

}