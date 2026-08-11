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
				.collect(Collectors.toMap(variable -> variable, variable -> accessList.stream()
						.filter(a -> a.getProcessVariable().equals(variable))
						.min(Comparator.comparing(a -> !a.getUsername().equals(SecurityUtil.getUsername())))
						.orElseThrow(() -> new InternalSaleCustomException.ValidationException("متغیر " + variable + " در سیستم تعریف نشده است")).getUserId().toString()));
	}

	public static Map<String, List<String>> resolveUserAccessMultiple(List<ProcessUserAccessModel> accessList, List<String> requiredVariables) {
		return requiredVariables.stream().collect(Collectors.toMap(variable -> variable, variable -> accessList.stream().filter(a -> a.getProcessVariable().equals(variable)).map(a -> a.getUserId().toString()).toList()));
	}
}