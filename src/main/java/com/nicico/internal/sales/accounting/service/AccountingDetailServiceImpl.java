package com.nicico.internal.sales.accounting.service;

import com.nicico.internal.sales.accounting.client.AccountingDetailFeignClient;
import com.nicico.internal.sales.accounting.client.AccountingDetailProperties;
import com.nicico.internal.sales.accounting.dto.*;
import com.nicico.internal.sales.accounting.util.NationalCodeValidator;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountingDetailServiceImpl implements AccountingDetailService {

	private static final String MSG_NATIONAL_CODE_OR_ID_REQUIRED = "شناسه یا کد ملی نمی تواند خالی باشد";
	private static final String MSG_PERSON_NATIONAL_CODE_REQUIRED = "کد ملی شخص نمی تواند خالی باشد";
	private static final String MSG_COMPANY_NATIONAL_ID_REQUIRED = "شناسه ملی شرکت نمی تواند خالی باشد";
	private static final String MSG_PARENT_ID_REQUIRED_FOR_CHILDREN = "شناسه والد برای دریافت زیرمجموعه‌ها الزامی است";
	private static final String MSG_PARENT_CODE_REQUIRED_FOR_SEQUENCE = "کد والد برای تولید دنباله نمی‌تواند خالی باشد";
	private static final String MSG_PERSON_INFO_REQUIRED = "کد ملی و نام و نام خانوادگی برای ایجاد تفصیلی شخص الزامی است";
	private static final String MSG_PERSON_NATIONAL_CODE_INVALID = "کد ملی شخص نامعتبر است";
	private static final String MSG_PERSON_NATIONAL_CODE_LENGTH_INVALID = "طول کد ملی شخص نامعتبر است";
	private static final String MSG_PERSON_NATIONAL_CODE_NUMERIC = "کد ملی شخص باید عددی باشد";
	private static final String MSG_COMPANY_INFO_INVALID = "اطلاعات یا شناسه ملی شرکت برای ایجاد تفصیلی نامعتبر است";
	private static final String MSG_COMPANY_NATIONAL_ID_INVALID = "شناسه ملی شرکت نامعتبر است";
	private static final String MSG_COMPANY_NATIONAL_ID_LENGTH_INVALID = "طول شناسه ملی شرکت نامعتبر است";
	private static final String MSG_COMPANY_NATIONAL_ID_NUMERIC = "شناسه ملی شرکت باید عددی باشد";
	private static final String MSG_FINANCIAL_INSTRUMENT_INFO_INVALID = "اطلاعات ابزار مالی برای ایجاد تفصیلی نامعتبر است";
	private static final String MSG_FINANCIAL_INSTRUMENT_TYPE_REQUIRED = "نوع ابزار مالی نمی‌تواند خالی باشد";
	private static final String MSG_FINANCIAL_INSTRUMENT_TWO_DIGIT_CODE_INVALID = "کد دو رقمی ابزار مالی باید دقیقاً دو رقم باشد";
	private static final String MSG_FINANCIAL_INSTRUMENT_DETAIL_NAME_REQUIRED = "نام تفصیلی ابزار مالی نمی‌تواند خالی باشد";

	private final AccountingDetailFeignClient detailClient;
	private final AccountingDetailProperties properties;

	@Override
	public Optional<DetailDto> findDetailOfNationalCode(String nationalCodeOrId) {
		String normalized = NationalCodeValidator.normalize(nationalCodeOrId);
		if (StringUtils.isBlank(normalized)) {
			throw new InternalSaleCustomException.ValidationException(MSG_NATIONAL_CODE_OR_ID_REQUIRED);
		}

		log.info("Finding accounting detail for national identifier: {}", normalized);

		if (NationalCodeValidator.isCompany(normalized)) {
			return findDetailForCompany(normalized);
		} else if (normalized.length() == 10) {
			return findDetailForPerson(normalized);
		} else {
			// Attempt person first, then company if not matched by strict length
			Optional<DetailDto> personResult = findDetailForPerson(normalized);
			if (personResult.isPresent()) {
				return personResult;
			}
			return findDetailForCompany(normalized);
		}
	}

	@Override
	public Optional<DetailDto> findDetailForPerson(String nationalCode) {
		String normalized = NationalCodeValidator.normalize(nationalCode);
		if (StringUtils.isBlank(normalized)) {
			throw new InternalSaleCustomException.ValidationException(MSG_PERSON_NATIONAL_CODE_REQUIRED);
		}

		String prefix = properties.getPerson().getFormattedPrefix();
		String searchCode = prefix + normalized;
		log.info("Searching person accounting detail with code: {}", searchCode);
		List<DetailDto> results = searchDetailsByCode(searchCode, EOperator.equals);

		return results.stream()
				.filter(d -> searchCode.equalsIgnoreCase(d.getCode()))
				.findFirst()
				.or(() -> results.stream().findFirst());
	}

	@Override
	public Optional<DetailDto> findDetailForCompany(String nationalId) {
		String normalized = NationalCodeValidator.normalize(nationalId);
		if (StringUtils.isBlank(normalized)) {
			throw new InternalSaleCustomException.ValidationException(MSG_COMPANY_NATIONAL_ID_REQUIRED);
		}

		String prefix = properties.getCompany().getFormattedPrefix();
		String searchCode = prefix + normalized;
		log.info("Searching company accounting detail with code: {}", searchCode);
		List<DetailDto> results = searchDetailsByCode(searchCode, EOperator.equals);

		return results.stream()
				.filter(d -> searchCode.equalsIgnoreCase(d.getCode()))
				.findFirst()
				.or(() -> results.stream().findFirst());
	}

	public List<DetailDto> searchDetailsByCode(String detailCode) {
		return searchDetailsByCode(detailCode, EOperator.replace);
	}

	@Override
	public List<DetailDto> searchDetailsByCode(String detailCode, EOperator operator) {
		if (StringUtils.isBlank(detailCode)) {
			return Collections.emptyList();
		}

		log.info("Executing searchInDetails for code: {}", detailCode);
		Criteria cr = new Criteria("code", operator, detailCode);
		CriteriaParamDTO request = new CriteriaParamDTO();
		request.setSortBy("detailNumber")
				.setStartRow(0)
				.setEndRow(1)
				.setComponentId("isc_ListGrid_5")
				.setDataSource("isc_MyRestDataSource_66")
				.setCriteria(List.of(cr));

		TotalResponse<DetailDto> response = detailClient.getDetailGridList(request.toQueryMap());

		if (response != null && response.getResponse() != null && response.getResponse().getData() != null) {
			log.info("searchInDetails found {} records for code '{}'", response.getResponse().getData().size(), detailCode);
			return response.getResponse().getData();
		}

		log.info("searchInDetails returned no data for code '{}'", detailCode);
		return Collections.emptyList();
	}

	@Override
	public Optional<DetailDto> findParentByCode(String parentCode) {
		if (StringUtils.isBlank(parentCode)) {
			return Optional.empty();
		}
		String cleanParentCode = parentCode.trim();
		log.info("Finding parent Detail for parentCode: '{}'", cleanParentCode);

		List<DetailDto> matches = searchDetailsByCode(cleanParentCode, EOperator.equals);
		Optional<DetailDto> exactParent = matches.stream()
				.filter(d -> cleanParentCode.equalsIgnoreCase(d.getCode()))
				.findFirst();
		if (exactParent.isPresent()) {
			log.info("Found exact parent Detail by code '{}' (ID: {})", cleanParentCode, exactParent.get().getId());
			return exactParent;
		}

		if (!matches.isEmpty()) {
			log.info("Found parent Detail candidate by code '{}' (ID: {})", cleanParentCode, matches.get(0).getId());
			return Optional.of(matches.get(0));
		}

		// Fallback search with default search
		List<DetailDto> fallbackMatches = searchDetailsByCode(cleanParentCode);
		Optional<DetailDto> fallbackParent = fallbackMatches.stream()
				.filter(d -> cleanParentCode.equalsIgnoreCase(d.getCode()))
				.findFirst()
				.or(() -> fallbackMatches.stream().findFirst());

		if (fallbackParent.isPresent()) {
			log.info("Found fallback parent Detail for parentCode '{}' (ID: {})", cleanParentCode, fallbackParent.get().getId());
		} else {
			log.warn("Could not find parent Detail for parentCode: '{}'", cleanParentCode);
		}

		return fallbackParent;
	}

	@Override
	public Optional<DetailDto> resolveParentDetail(String targetCode) {
		if (StringUtils.isBlank(targetCode)) {
			return Optional.empty();
		}

		String cleanTarget = targetCode.trim();
		log.info("Resolving parent Detail for targetCode: '{}'", targetCode);

		// 1. Check if cleanTarget matches a configured financial instrument parent or main code
		for (FinancialInstrumentType fiType : FinancialInstrumentType.values()) {
			String fiParentCode = properties.getFinancialInstrumentParentCode(fiType);
			String fiMainCode = properties.getFinancialInstrumentMainCode(fiType);
			String effectiveParentCode = StringUtils.isNotBlank(fiParentCode) ? fiParentCode.trim() : (StringUtils.isNotBlank(fiMainCode) ? fiMainCode.trim() : fiType.getMainCode());
			if (cleanTarget.startsWith(effectiveParentCode) && !cleanTarget.equalsIgnoreCase(effectiveParentCode)) {
				Optional<DetailDto> exactParent = findParentByCode(effectiveParentCode);
				if (exactParent.isPresent()) {
					log.info("Resolved financial instrument parent '{}' (ID: {}) for target '{}'",
							exactParent.get().getCode(), exactParent.get().getId(), cleanTarget);
					return exactParent;
				}
			}
		}

		// 2. Calculate parent candidate code based on configured prefix and parent-digits
		// For example: targetCode = "01/4270205776", prefix="01/", parentDigits=3 -> candidate = "01/427"
		String configuredParentCandidateCode = extractConfiguredParentCode(cleanTarget);
		if (StringUtils.isNotBlank(configuredParentCandidateCode)) {
			log.info("Calculated configured parent candidate code '{}' for targetCode '{}'", configuredParentCandidateCode, cleanTarget);
			List<DetailDto> parentMatches = searchDetailsByCode(configuredParentCandidateCode, EOperator.equals);
			Optional<DetailDto> exactParent = parentMatches.stream()
					.filter(d -> configuredParentCandidateCode.equalsIgnoreCase(d.getCode()))
					.findFirst();
			if (exactParent.isPresent()) {
				log.info("Resolved exact configured parent '{}' (ID: {}) for target '{}'",
						exactParent.get().getCode(), exactParent.get().getId(), cleanTarget);
				return exactParent;
			}
		}

		// 2. Fallback prefix search: Find candidate where target starts with candidate.code and has longest matching length
		String initialPrefix = cleanTarget.contains("/") ? cleanTarget.substring(0, cleanTarget.indexOf("/") + 1) : cleanTarget.substring(0, Math.min(2, cleanTarget.length()));
		List<DetailDto> candidates = searchDetailsByCode(initialPrefix, EOperator.equals);

		String normalizedTarget = cleanTarget.replace("/", "");
		Optional<DetailDto> resolvedParent = candidates.stream()
				.filter(d -> StringUtils.isNotBlank(d.getCode()))
				.filter(d -> {
					String candidateCode = d.getCode().trim();
					if (candidateCode.equalsIgnoreCase(cleanTarget)) {
						return false; // cannot be parent of itself
					}
					if (cleanTarget.startsWith(candidateCode)) {
						return true;
					}
					String normCand = candidateCode.replace("/", "");
					return normalizedTarget.startsWith(normCand);
				})
				.sorted((d1, d2) -> {
					boolean d1CanHaveChildren = d1.getChildrenDigitCount() != null && d1.getChildrenDigitCount() > 0;
					boolean d2CanHaveChildren = d2.getChildrenDigitCount() != null && d2.getChildrenDigitCount() > 0;
					if (d1CanHaveChildren != d2CanHaveChildren) {
						return d1CanHaveChildren ? -1 : 1;
					}
					return Integer.compare(d2.getCode().length(), d1.getCode().length());
				})
				.findFirst();

		if (resolvedParent.isPresent()) {
			log.info("Resolved parent for target '{}' is '{}' (ID: {})",
					targetCode, resolvedParent.get().getCode(), resolvedParent.get().getId());
		} else {
			log.warn("No parent Detail found for targetCode: '{}'", targetCode);
		}

		return resolvedParent;
	}

	@Override
	public List<DetailDto> getChildrenByParentDetailId(Long parentDetailId, Integer startRow, Integer endRow, String sortBy) {
		if (parentDetailId == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_PARENT_ID_REQUIRED_FOR_CHILDREN);
		}

		int sRow = (startRow != null && startRow >= 0) ? startRow : 0;
		int eRow = (endRow != null && endRow > sRow) ? endRow : 500;
		String sort = StringUtils.isNotBlank(sortBy) ? sortBy.trim() : "-detailName";

		log.info("Fetching children for parentDetail.id={}, startRow={}, endRow={}, sortBy={}",
				parentDetailId, sRow, eRow, sort);

		CriteriaParamDTO criteriaParam = new CriteriaParamDTO()
				.setConstructor("AdvancedCriteria")
				.setOperator("and")
				.setStartRow(sRow)
				.setEndRow(eRow)
				.setSortBy(sort)
				.setTextMatchStyle("exact")
				.setComponentId("isc_ListGrid_5")
				.setDataSource("isc_MyRestDataSource_72")
				.setIscMetaDataPrefix("_")
				.setIscDataFormat("json")
				.setOperationType("fetch");

		criteriaParam.addCriteria(new Criteria("parentDetail.id", "equals", parentDetailId));

		TotalResponse<DetailDto> response = detailClient.getDetailGridList(criteriaParam.toQueryMap());

		if (response != null && response.getResponse() != null && response.getResponse().getData() != null) {
			log.info("getDetailGridList found {} children for parentDetail.id={}",
					response.getResponse().getData().size(), parentDetailId);
			return response.getResponse().getData();
		}

		return Collections.emptyList();
	}

	private String extractConfiguredParentCode(String targetCode) {
		if (targetCode == null) return null;
		String personPrefix = properties.getPerson().getFormattedPrefix();
		String companyPrefix = properties.getCompany().getFormattedPrefix();

		if (StringUtils.isNotBlank(personPrefix) && targetCode.startsWith(personPrefix)) {
			int digits = properties.getPerson().getParentDigits() != null ? properties.getPerson().getParentDigits() : 1;
			int prefixLen = personPrefix.length();
			if (targetCode.length() >= prefixLen + digits) {
				return targetCode.substring(0, prefixLen + digits);
			}
		} else if (StringUtils.isNotBlank(companyPrefix) && targetCode.startsWith(companyPrefix)) {
			int digits = properties.getCompany().getParentDigits() != null ? properties.getCompany().getParentDigits() : 1;
			int prefixLen = companyPrefix.length();
			if (targetCode.length() >= prefixLen + digits) {
				return targetCode.substring(0, prefixLen + digits);
			}
		}
		return null;
	}

	@Override
	public String generateNextSequenceCode(String parentCode) {
		if (StringUtils.isBlank(parentCode)) {
			throw new InternalSaleCustomException.ValidationException(MSG_PARENT_CODE_REQUIRED_FOR_SEQUENCE);
		}

		String cleanParent = parentCode.trim();
		log.info("Generating next sequence code for parent code: '{}'", cleanParent);

		// Fetch top children sorted descending by code
		List<DetailDto> latestChildren = findLatestChildrenByParentCode(cleanParent);

		// Filter direct children starting with cleanParent and not equal to cleanParent
		Optional<DetailDto> latestChild = latestChildren.stream()
				.filter(d -> d.getCode() != null)
				.filter(d -> d.getCode().startsWith(cleanParent) && !d.getCode().equalsIgnoreCase(cleanParent))
				.findFirst();

		long maxChildNumber = 0;
		int maxDigits = 4; // default sequence digit padding (e.g., 0001 - 9999)
		Pattern numberPattern = Pattern.compile("(\\d+)$");

		if (latestChild.isPresent()) {
			String childCode = latestChild.get().getCode();
			String suffix = childCode.substring(cleanParent.length()).replace("/", "");
			Matcher matcher = numberPattern.matcher(suffix);
			if (matcher.find()) {
				try {
					String numStr = matcher.group(1);
					maxChildNumber = Long.parseLong(numStr);
					maxDigits = Math.max(maxDigits, numStr.length());
				} catch (NumberFormatException ignored) {
				}
			}
		}

		long nextSequence = maxChildNumber + 1;
		String formattedNext = String.format("%0" + maxDigits + "d", nextSequence);
		String nextCode = cleanParent + formattedNext;

		log.info("Generated next sequence code for parent '{}': latest child sequence was {}, next code is '{}'",
				cleanParent, maxChildNumber, nextCode);

		return nextCode;
	}

	private List<DetailDto> findLatestChildrenByParentCode(String parentCode) {
		log.info("Executing findLatestChildrenByParentCode for parent: {}", parentCode);

		SearchInDetailsRequest request = SearchInDetailsRequest.of(
				Criteria.startsWith("code", parentCode).setConstructor("AdvancedCriteria")
		);
		request.setSortBy("-code")
				.setStartRow(0)
				.setEndRow(1)
				.setComponentId("isc_ListGrid_5")
				.setDataSource("isc_MyRestDataSource_66");

		TotalResponse<DetailDto> response = detailClient.searchInDetails(request.toFieldMap());

		if (response != null && response.getResponse() != null && response.getResponse().getData() != null) {
			log.info("findLatestChildrenByParentCode found {} records for parent '{}'",
					response.getResponse().getData().size(), parentCode);
			return response.getResponse().getData();
		}

		return Collections.emptyList();
	}

	@Override
	public DetailDto createDetailForPerson(CreatePersonDetailDto request) {
		if (request == null || StringUtils.isBlank(request.getNationalCode()) || StringUtils.isBlank(request.getFullName())) {
			throw new InternalSaleCustomException.ValidationException(MSG_PERSON_INFO_REQUIRED);
		}

		String normalizedCode = NationalCodeValidator.normalize(request.getNationalCode());
		if (StringUtils.isBlank(normalizedCode)) {
			throw new InternalSaleCustomException.ValidationException(MSG_PERSON_NATIONAL_CODE_INVALID);
		}

		int parentDigits = properties.getPerson().getParentDigits() != null ? properties.getPerson().getParentDigits() : 1;
		if (normalizedCode.length() <= parentDigits) {
			throw new InternalSaleCustomException.ValidationException(MSG_PERSON_NATIONAL_CODE_LENGTH_INVALID);
		}

		String removedNumber = normalizedCode.substring(0, parentDigits);
		String remainingCode = normalizedCode.substring(parentDigits);

		Long detailNumber;
		try {
			detailNumber = Long.parseLong(remainingCode);
		} catch (NumberFormatException e) {
			throw new InternalSaleCustomException.ValidationException(MSG_PERSON_NATIONAL_CODE_NUMERIC);
		}

		String personPrefix = properties.getPerson().getFormattedPrefix();
		String parentCode = personPrefix + removedNumber;
		log.info("Resolving parent Detail for person with parentCode: '{}'", parentCode);

		DetailDto parent = findParentByCode(parentCode)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("کد تفصیلی والد با کد " + parentCode + " یافت نشد"));

		String fullName = request.getFullName().trim();
		String note = "sales " + fullName;

		log.info("Creating person accounting detail. Name: {}, DetailNumber: {}, ParentId: {}", fullName, detailNumber, parent.getId());

		DetailDto.DetailInfoDto detailInfo = new DetailDto.DetailInfoDto();
		// NOTE: Never set code when creating details
		detailInfo.setDetailName(fullName);
		detailInfo.setDetailNumber(detailNumber);
		detailInfo.setNote(note);
		detailInfo.setDashChildrenCode(true);
		detailInfo.setBranchFree(true);
		detailInfo.setChildrenDigitCount(0L);
		detailInfo.setDetailTypes(new HashSet<>());
		detailInfo.setParentDetailId(parent.getId());

		DetailDto parentRef = new DetailDto();
		parentRef.setId(parent.getId());
		detailInfo.setParentDetail(parentRef);

		detailClient.saveDetail(null, detailInfo);
		log.info("Successfully requested saveDetail for person: {}", fullName);

		return findDetailForPerson(normalizedCode).orElse(detailInfo);
	}

	@Override
	public DetailDto createDetailForCompany(CreateCompanyDetailDto request) {
		if (request == null || StringUtils.isBlank(request.getNationalId()) || StringUtils.isBlank(request.getCompanyName())) {
			throw new InternalSaleCustomException.ValidationException(MSG_COMPANY_INFO_INVALID);
		}

		String normalizedId = NationalCodeValidator.normalize(request.getNationalId());
		if (StringUtils.isBlank(normalizedId)) {
			throw new InternalSaleCustomException.ValidationException(MSG_COMPANY_NATIONAL_ID_INVALID);
		}

		int parentDigits = properties.getCompany().getParentDigits() != null ? properties.getCompany().getParentDigits() : 1;
		if (normalizedId.length() <= parentDigits) {
			throw new InternalSaleCustomException.ValidationException(MSG_COMPANY_NATIONAL_ID_LENGTH_INVALID);
		}

		String removedNumber = normalizedId.substring(0, parentDigits);
		String remainingId = normalizedId.substring(parentDigits);

		Long detailNumber;
		try {
			detailNumber = Long.parseLong(remainingId);
		} catch (NumberFormatException e) {
			throw new InternalSaleCustomException.ValidationException(MSG_COMPANY_NATIONAL_ID_NUMERIC);
		}

		String companyPrefix = properties.getCompany().getFormattedPrefix();
		String parentCode = companyPrefix + removedNumber;
		log.info("Resolving parent Detail for company with parentCode: '{}'", parentCode);

		DetailDto parent = findParentByCode(parentCode)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("کد تفصیلی والد با کد " + parentCode + " یافت نشد"));

		log.info("Creating company accounting detail. Name: {}, DetailNumber: {}, ParentId: {}",
				request.getCompanyName(), detailNumber, parent.getId());

		DetailDto.DetailInfoDto detailInfo = new DetailDto.DetailInfoDto();
		// NOTE: Never set code when creating details
		detailInfo.setChildrenDigitCount(0L);
		detailInfo.setDetailNumber(detailNumber);
		detailInfo.setBranchFree(false);
		detailInfo.setDetailTypes(new HashSet<>());
		detailInfo.setDetailName(request.getCompanyName().trim());
		detailInfo.setDetailNameLatin(request.getDetailNameLatin());
		detailInfo.setNote(request.getNote());
		detailInfo.setParentDetailId(parent.getId());

		DetailDto parentRef = new DetailDto();
		parentRef.setId(parent.getId());
		detailInfo.setParentDetail(parentRef);

		detailClient.saveDetail(null, detailInfo);
		log.info("Successfully requested saveDetail for company: {}", request.getCompanyName());

		return findDetailForCompany(normalizedId).orElse(detailInfo);
	}

	@Override
	public Long generateNextFinancialInstrumentSequenceCode(FinancialInstrumentType instrumentType,
	                                                        String twoDigitCode,
	                                                        String yearSuffix) {
		if (instrumentType == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_FINANCIAL_INSTRUMENT_TYPE_REQUIRED);
		}
		if (StringUtils.isBlank(twoDigitCode) || !twoDigitCode.trim().matches("\\d{2}")) {
			throw new InternalSaleCustomException.ValidationException(MSG_FINANCIAL_INSTRUMENT_TWO_DIGIT_CODE_INVALID);
		}

		String basePrefix = buildFinancialInstrumentBasePrefix(instrumentType, twoDigitCode, yearSuffix);
		log.info("Generating next sequence code for financial instrument '{}' with base prefix '{}'",
				instrumentType.getTitle(), basePrefix);

		Long nextSequence = calculateNextFinancialInstrumentSequenceNumber(basePrefix);

		log.info("Generated next financial instrument sequence code for basePrefix '{}': {}", basePrefix, nextSequence);
		return nextSequence;
	}

	private Long calculateNextFinancialInstrumentSequenceNumber(String basePrefix) {
		List<DetailDto> latestChildren = findLatestFinancialInstrumentChild(basePrefix);

		Optional<DetailDto> latestChild = latestChildren.stream()
				.filter(d -> d.getCode() != null)
				.filter(d -> d.getCode().startsWith(basePrefix) && !d.getCode().equalsIgnoreCase(basePrefix))
				.findFirst();

		if (latestChild.isPresent()) {
			DetailDto child = latestChild.get();
			String childCode = child.getCode();
			if (childCode != null && childCode.length() > basePrefix.length()) {
				String suffix = childCode.substring(basePrefix.length()).replace("/", "");
				Pattern numberPattern = Pattern.compile("(\\d+)$");
				Matcher matcher = numberPattern.matcher(suffix);
				if (matcher.find()) {
					try {
						return Long.parseLong(matcher.group(1)) + 1;
					} catch (NumberFormatException ignored) {
					}
				}
			}

			if (child.getDetailNumber() != null) {
				return child.getDetailNumber() + 1;
			}
		}

		return 1L;
	}

	private List<DetailDto> findLatestFinancialInstrumentChild(String basePrefix) {
		log.info("Executing findLatestFinancialInstrumentChild for basePrefix: {}", basePrefix);

		CriteriaParamDTO request = new CriteriaParamDTO();
		Criteria criteria = Criteria.startsWith("code", basePrefix).setConstructor("AdvancedCriteria");
		request.setSortBy("-detailNumber")
				.setStartRow(0)
				.setEndRow(1)
				.setCriteria(List.of(criteria));

		TotalResponse<DetailDto> response = detailClient.getDetailGridList(request.toQueryMap());

		if (response != null && response.getResponse() != null && response.getResponse().getData() != null) {
			log.info("findLatestFinancialInstrumentChild found {} records for basePrefix '{}'",
					response.getResponse().getData().size(), basePrefix);
			return response.getResponse().getData();
		}

		return Collections.emptyList();
	}

	private String extractChildPrefix(String basePrefix, DetailDto parent) {
		if (StringUtils.isBlank(basePrefix)) {
			return "";
		}
		String childPrefix = basePrefix.trim();
		if (parent != null && StringUtils.isNotBlank(parent.getCode())) {
			String parentCode = parent.getCode().trim();
			if (childPrefix.startsWith(parentCode)) {
				childPrefix = childPrefix.substring(parentCode.length());
			} else {
				String cleanParent = parentCode.replace("/", "");
				String cleanBase = childPrefix.replace("/", "");
				if (cleanBase.startsWith(cleanParent)) {
					childPrefix = cleanBase.substring(cleanParent.length());
				}
			}
		}
		return childPrefix.replace("/", "").trim();
	}

	private String buildChildDetailNumberString(String childPrefix, Long sequenceNumber, Long childrenDigitCount) {
		long targetDigitCount = (childrenDigitCount != null && childrenDigitCount > 0)
				? childrenDigitCount
				: Math.max(8L, (long) childPrefix.length() + 4);

		int remainingDigits = (int) (targetDigitCount - childPrefix.length());
		String sequenceStr;
		if (remainingDigits > 0) {
			sequenceStr = String.format("%0" + remainingDigits + "d", sequenceNumber);
		} else {
			sequenceStr = String.valueOf(sequenceNumber);
		}
		return childPrefix + sequenceStr;
	}

	private String buildExpectedChildCode(DetailDto parent, String basePrefix, String fullChildNumberStr) {
		if (parent != null && StringUtils.isNotBlank(parent.getCode())) {
			String parentCode = parent.getCode().trim();
			return parentCode.endsWith("/") ? parentCode + fullChildNumberStr : parentCode + "/" + fullChildNumberStr;
		}
		return basePrefix + fullChildNumberStr;
	}

	private FinancialInstrumentCreationContext prepareAndSaveFinancialInstrumentDetail(CreateFinancialInstrumentDetailDto request) {
		if (request == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_FINANCIAL_INSTRUMENT_INFO_INVALID);
		}
		if (request.getInstrumentType() == null) {
			throw new InternalSaleCustomException.ValidationException(MSG_FINANCIAL_INSTRUMENT_TYPE_REQUIRED);
		}
		if (StringUtils.isBlank(request.getTwoDigitCode()) || !request.getTwoDigitCode().trim().matches("\\d{2}")) {
			throw new InternalSaleCustomException.ValidationException(MSG_FINANCIAL_INSTRUMENT_TWO_DIGIT_CODE_INVALID);
		}
		if (StringUtils.isBlank(request.getDetailName())) {
			throw new InternalSaleCustomException.ValidationException(MSG_FINANCIAL_INSTRUMENT_DETAIL_NAME_REQUIRED);
		}

		String parentCode = properties.getFinancialInstrumentParentCode(request.getInstrumentType());
		DetailDto parent = findParentByCode(parentCode)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException("کد تفصیلی والد با کد " + parentCode + " یافت نشد"));

		String basePrefix = buildFinancialInstrumentBasePrefix(request.getInstrumentType(), request.getTwoDigitCode(), request.getYearSuffix());
		Long sequenceNumber = calculateNextFinancialInstrumentSequenceNumber(basePrefix);

		String childPrefix = extractChildPrefix(basePrefix, parent);
		String fullChildNumberStr = buildChildDetailNumberString(childPrefix, sequenceNumber, parent.getChildrenDigitCount());
		Long detailNumber = Long.parseLong(fullChildNumberStr);
		String expectedCode = buildExpectedChildCode(parent, basePrefix, fullChildNumberStr);

		String detailName = request.getDetailName().trim();
		String note = StringUtils.isNotBlank(request.getNote())
				? request.getNote().trim()
				: request.getInstrumentType().getTitle() + " " + request.getTwoDigitCode().trim();

		log.info("Creating financial instrument accounting detail. Sequence: {}, DetailNumber: {}, ExpectedCode: {}, Name: {}, Instrument: {}",
				sequenceNumber, detailNumber, expectedCode, detailName, request.getInstrumentType());

		DetailDto.DetailInfoDto detailInfo = new DetailDto.DetailInfoDto();
		detailInfo.setDetailNumber(detailNumber);
		detailInfo.setDetailName(detailName);
		detailInfo.setNote(note);
		detailInfo.setDashChildrenCode(true);
		detailInfo.setBranchFree(true);
		detailInfo.setChildrenDigitCount(0L);
		detailInfo.setDetailTypes(new HashSet<>());
		detailInfo.setParentDetailId(parent.getId());

		DetailDto parentRef = new DetailDto();
		parentRef.setId(parent.getId());
		detailInfo.setParentDetail(parentRef);

		executeSaveDetailWithRetry(request.getSubmissionId(), detailInfo, "FinancialInstrument: ");
		log.info("Successfully requested saveDetail for financial instrument with expectedCode: {}", expectedCode);

		return new FinancialInstrumentCreationContext(sequenceNumber, detailNumber, expectedCode, parent, detailInfo);
	}

	@Override
	public void createDetailForFinancialInstrument(CreateFinancialInstrumentDetailDto request) {
		prepareAndSaveFinancialInstrumentDetail(request);
	}

	@Override
	public FinancialInstrumentCreateResponseDto generateAndCreateFinancialInstrumentDetail(
			FinancialInstrumentType instrumentType,
			String twoDigitCode,
			String yearSuffix,
			String detailName,
			String submissionId) {
		String resolvedDetailName = StringUtils.isNotBlank(detailName)
				? detailName.trim()
				: (instrumentType != null ? instrumentType.getTitle() + " " + (twoDigitCode != null ? twoDigitCode.trim() : "") : "");

		CreateFinancialInstrumentDetailDto request = CreateFinancialInstrumentDetailDto.builder()
				.instrumentType(instrumentType)
				.twoDigitCode(twoDigitCode)
				.yearSuffix(yearSuffix)
				.detailName(resolvedDetailName)
				.submissionId(submissionId)
				.build();

		FinancialInstrumentCreationContext context = prepareAndSaveFinancialInstrumentDetail(request);

		DetailDto createdDetail = searchDetailsByCode(context.expectedCode(), EOperator.equals).stream()
				.filter(d -> context.expectedCode().equalsIgnoreCase(d.getCode()))
				.findFirst()
				.orElse(null);

		return FinancialInstrumentCreateResponseDto.builder()
				.success(true)
				.code(context.expectedCode())
				.detailNumber(context.detailNumber())
				.submissionId(submissionId)
				.detail(createdDetail)
				.build();
	}

	private void executeSaveDetailWithRetry(String submissionId, DetailDto.DetailInfoDto detailInfo, String logContext) {
		int maxAttempts = Math.max(1, properties.getFinancialInstrument().getMaxAttempts());
		long backoffDelayMs = Math.max(0, properties.getFinancialInstrument().getBackoffDelayMs());

		int attempt = 0;

		while (attempt < maxAttempts) {
			attempt++;
			try {
				detailClient.saveDetail(submissionId, detailInfo);
				log.info("saveDetail ({}): successfully executed on attempt {}/{}", logContext, attempt, maxAttempts);
				return;
			} catch (Exception e) {
				log.warn("saveDetail failed for {} on attempt {}/{}: {}", logContext, attempt, maxAttempts, e.getMessage());
				if (attempt >= maxAttempts) {
					log.error("All {} attempts to saveDetail failed for {}.", maxAttempts, logContext);
					throw e;
				}
				if (backoffDelayMs > 0) {
					try {
						Thread.sleep(backoffDelayMs);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						throw new InternalSaleCustomException.ApplicationServerException("Retry interrupted: " + ie.getMessage());
					}
				}
			}
		}
	}

	private String buildFinancialInstrumentBasePrefix(FinancialInstrumentType instrumentType,
	                                                  String twoDigitCode,
	                                                  String yearSuffix) {
		String cleanTwoDigit = twoDigitCode.trim();
		String resolvedYear = resolveTwoDigitShamsiYear(yearSuffix);
		String mainCode = properties.getFinancialInstrumentMainCode(instrumentType);
		if (StringUtils.isBlank(mainCode)) {
			mainCode = instrumentType.getMainCode();
		}
		return mainCode + resolvedYear + cleanTwoDigit;
	}

	private String resolveTwoDigitShamsiYear(String yearSuffix) {
		if (StringUtils.isNotBlank(yearSuffix)) {
			String trimmed = yearSuffix.trim();
			if (trimmed.matches("\\d{2}")) {
				return trimmed;
			} else if (trimmed.matches("\\d{4}")) {
				return trimmed.substring(2);
			}
		}
		int currentJalaliYear = DateUtility.getCurrentJalaliYear();
		return String.format("%02d", currentJalaliYear % 100);
	}
}
