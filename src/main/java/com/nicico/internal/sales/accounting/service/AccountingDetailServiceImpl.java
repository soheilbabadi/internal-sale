package com.nicico.internal.sales.accounting.service;

import com.fgostar.accounting.sdk.client.DetailClient;
import com.fgostar.accounting.sdk.dto.*;
import com.nicico.internal.sales.accounting.client.AccountingDetailProperties;
import com.nicico.internal.sales.accounting.dto.CreateCompanyDetailDto;
import com.nicico.internal.sales.accounting.dto.CreateFinancialInstrumentDetailDto;
import com.nicico.internal.sales.accounting.dto.CreatePersonDetailDto;
import com.nicico.internal.sales.accounting.dto.FinancialInstrumentType;
import com.nicico.internal.sales.accounting.util.NationalCodeValidator;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.util.date.DateUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountingDetailServiceImpl implements AccountingDetailService {

    private final DetailClient detailClient;
    private final AccountingDetailProperties properties;

    @Override
    public Optional<DetailDto> findDetailOfNationalCode(String nationalCodeOrId) {
        String normalized = NationalCodeValidator.normalize(nationalCodeOrId);
        if (StringUtils.isBlank(normalized)) {
            throw new InternalSaleCustomException.ValidationException("شناسه یا کد ملی نمی تواند خالی باشد");
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
            throw new InternalSaleCustomException.ValidationException("کد ملی شخص نمی تواند خالی باشد");
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
            throw new InternalSaleCustomException.ValidationException("شناسه ملی شرکت نمی تواند خالی باشد");
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
        return searchDetailsByCode(detailCode,EOperator.replace);
    }
    @Override
    public List<DetailDto> searchDetailsByCode(String detailCode, EOperator operator) {
        if (StringUtils.isBlank(detailCode)) {
            return Collections.emptyList();
        }

        log.info("Executing searchInDetails for code: {}", detailCode);
        Criteria cr = new Criteria("code",operator,detailCode);
        CriteriaParamDTO request = new CriteriaParamDTO();
        request.setSortBy("detailNumber")
                .setStartRow(0)
                .setEndRow(1)
                .setComponentId("isc_ListGrid_5")
                .setDataSource("isc_MyRestDataSource_66")
                .setCriteria(List.of(cr));

        Call<TotalResponse<DetailDto>> call = detailClient.getDetailGridList(request);
        TotalResponse<DetailDto> response = executeCall(call, "searchInDetails (" + detailCode + ")");

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
            List<DetailDto> parentMatches = searchDetailsByCode(configuredParentCandidateCode,EOperator.equals);
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
            throw new InternalSaleCustomException.ValidationException("شناسه والد برای دریافت زیرمجموعه‌ها الزامی است");
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

        Call<TotalResponse<DetailDto>> call = detailClient.getDetailGridList(criteriaParam);
        TotalResponse<DetailDto> response = executeCall(call, "getDetailGridList (parentDetail.id=" + parentDetailId + ")");

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
            int digits = properties.getPerson().getParentDigits() != null ? properties.getPerson().getParentDigits() : 3;
            int prefixLen = personPrefix.length();
            if (targetCode.length() >= prefixLen + digits) {
                return targetCode.substring(0, prefixLen + digits);
            }
        } else if (StringUtils.isNotBlank(companyPrefix) && targetCode.startsWith(companyPrefix)) {
            int digits = properties.getCompany().getParentDigits() != null ? properties.getCompany().getParentDigits() : 3;
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
            throw new InternalSaleCustomException.ValidationException("کد والد برای تولید دنباله نمی‌تواند خالی باشد");
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

        Call<TotalResponse<DetailDto>> call = detailClient.searchInDetailsForm(request);
        TotalResponse<DetailDto> response = executeCall(call, "findLatestChildrenByParentCode (" + parentCode + ")");

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
            throw new InternalSaleCustomException.ValidationException("کد ملی و نام و نام خانوادگی برای ایجاد تفصیلی شخص الزامی است");
        }

        String normalizedCode = NationalCodeValidator.normalize(request.getNationalCode());
        if (StringUtils.isBlank(normalizedCode)) {
            throw new InternalSaleCustomException.ValidationException("کد ملی شخص نامعتبر است");
        }

        String personPrefix = properties.getPerson().getFormattedPrefix();
        String code = personPrefix + normalizedCode;
        String fullName = request.getFullName().trim();
        String note = "sales " + fullName;

        Long detailNumber;
        try {
            detailNumber = Long.parseLong(normalizedCode);
        } catch (NumberFormatException e) {
            throw new InternalSaleCustomException.ValidationException("کد ملی شخص باید عددی باشد");
        }

        log.info("Creating person accounting detail. Code: {}, Name: {}, DetailNumber: {}", code, fullName, detailNumber);

        DetailDto.DetailInfoDto detailInfo = new DetailDto.DetailInfoDto();
        detailInfo.setCode(code);
        detailInfo.setDetailName(fullName);
        detailInfo.setDetailNumber(detailNumber);
        detailInfo.setNote(note);
        detailInfo.setDashChildrenCode(true);
        detailInfo.setBranchFree(true);
        detailInfo.setChildrenDigitCount(0L);
        detailInfo.setDetailTypes(new HashSet<>());

        // Resolve parent detail
        resolveParentDetail(code).ifPresent(p -> {
            detailInfo.setParentDetailId(p.getId());
            detailInfo.setParentDetail(p);
        });

        Call<ResponseBody> call = detailClient.saveDetail(detailInfo);
        executeCall(call, "saveDetail (Person: " + code + ")");
        log.info("Successfully requested saveDetail for person code: {}", code);

        List<DetailDto> searchResults = searchDetailsByCode(code, EOperator.equals);
        return searchResults.stream()
                .filter(d -> code.equalsIgnoreCase(d.getCode()))
                .findFirst()
                .orElse(detailInfo);
    }

    @Override
    public DetailDto createDetailForCompany(CreateCompanyDetailDto request) {
        if (request == null || StringUtils.isBlank(request.getNationalId()) || StringUtils.isBlank(request.getCompanyName())) {
            throw new InternalSaleCustomException.ValidationException("اطلاعات یا شناسه ملی شرکت برای ایجاد تفصیلی نامعتبر است");
        }

        String normalizedId = NationalCodeValidator.normalize(request.getNationalId());
        String companyPrefix = properties.getCompany().getFormattedPrefix();
        String code = companyPrefix + normalizedId;

        log.info("Creating company accounting detail. Code: {}, Name: {}", code, request.getCompanyName());

        DetailDto.DetailInfoDto detailInfo = new DetailDto.DetailInfoDto();
        detailInfo.setChildrenDigitCount(0L);
        detailInfo.setDetailNumber(Long.valueOf(normalizedId));
        detailInfo.setBranchFree(false);
        detailInfo.setDetailTypes(new HashSet<>());
        detailInfo.setDetailName(request.getCompanyName());
        detailInfo.setDetailNameLatin(request.getDetailNameLatin());
        detailInfo.setNote(request.getNote());

        resolveParentDetail(code).ifPresent(p -> {
            detailInfo.setParentDetailId(p.getId());
            detailInfo.setParentDetail(p);
        });


        Call<ResponseBody> call = detailClient.saveDetail(detailInfo);
        executeCall(call, "saveDetail (Company: " + code + ")");
        log.info("Successfully requested saveDetail for company code: {}", code);

        return findDetailForCompany(normalizedId).orElse(detailInfo);
    }

    @Override
    public Long generateNextFinancialInstrumentSequenceCode(FinancialInstrumentType instrumentType,
                                                              String twoDigitCode,
                                                              String yearSuffix) {
        if (instrumentType == null) {
            throw new InternalSaleCustomException.ValidationException("نوع ابزار مالی نمی‌تواند خالی باشد");
        }
        if (StringUtils.isBlank(twoDigitCode) || !twoDigitCode.trim().matches("\\d{2}")) {
            throw new InternalSaleCustomException.ValidationException("کد دو رقمی ابزار مالی باید دقیقاً دو رقم باشد");
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
            if (child.getDetailNumber() != null) {
                return child.getDetailNumber() + 1;
            }

            // Fallback to parsing numeric suffix from code if detailNumber is null
            String childCode = child.getCode();
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

        return 1L;
    }

    private List<DetailDto> findLatestFinancialInstrumentChild(String basePrefix) {
        log.info("Executing findLatestFinancialInstrumentChild for basePrefix: {}", basePrefix);

        CriteriaParamDTO request =new CriteriaParamDTO();
        Criteria criteria = Criteria.startsWith("code", basePrefix).setConstructor("AdvancedCriteria");
        request.setSortBy("-detailNumber")
                .setStartRow(0)
                .setEndRow(1)
                .setCriteria(List.of(criteria));

        Call<TotalResponse<DetailDto>> call = detailClient.getDetailGridList(request);
        TotalResponse<DetailDto> response = executeCall(call, "findLatestFinancialInstrumentChild (" + basePrefix + ")");

        if (response != null && response.getResponse() != null && response.getResponse().getData() != null) {
            log.info("findLatestFinancialInstrumentChild found {} records for basePrefix '{}'",
                    response.getResponse().getData().size(), basePrefix);
            return response.getResponse().getData();
        }

        return Collections.emptyList();
    }

    @Override
    public void createDetailForFinancialInstrument(CreateFinancialInstrumentDetailDto request) {
        if (request == null) {
            throw new InternalSaleCustomException.ValidationException("اطلاعات ابزار مالی برای ایجاد تفصیلی نامعتبر است");
        }
        if (request.getInstrumentType() == null) {
            throw new InternalSaleCustomException.ValidationException("نوع ابزار مالی نمی‌تواند خالی باشد");
        }
        if (StringUtils.isBlank(request.getTwoDigitCode()) || !request.getTwoDigitCode().trim().matches("\\d{2}")) {
            throw new InternalSaleCustomException.ValidationException("کد دو رقمی ابزار مالی باید دقیقاً دو رقم باشد");
        }
        if (StringUtils.isBlank(request.getDetailName())) {
            throw new InternalSaleCustomException.ValidationException("نام تفصیلی ابزار مالی نمی‌تواند خالی باشد");
        }

        String basePrefix = buildFinancialInstrumentBasePrefix(request.getInstrumentType(), request.getTwoDigitCode(), request.getYearSuffix());

        Long sequenceNumber = calculateNextFinancialInstrumentSequenceNumber(basePrefix);
        String formattedNext = String.format("%04d", sequenceNumber);
        String code = basePrefix + formattedNext;

        String detailName = request.getDetailName().trim();
        String note = StringUtils.isNotBlank(request.getNote())
                ? request.getNote().trim()
                : request.getInstrumentType().getTitle() + " " + request.getTwoDigitCode().trim();

        log.info("Creating financial instrument accounting detail. Code: {}, DetailNumber: {}, Name: {}, Instrument: {}",
                code, sequenceNumber, detailName, request.getInstrumentType());

        DetailDto.DetailInfoDto detailInfo = new DetailDto.DetailInfoDto();
        detailInfo.setCode(code);
        detailInfo.setDetailNumber(sequenceNumber);
        detailInfo.setDetailName(detailName);
        detailInfo.setNote(note);
        detailInfo.setDashChildrenCode(true);
        detailInfo.setBranchFree(true);
        detailInfo.setChildrenDigitCount(0L);
        detailInfo.setDetailTypes(new HashSet<>());

        // Resolve parent detail using configured parent code
        String parentCode = properties.getFinancialInstrumentParentCode(request.getInstrumentType());


        findParentByCode(parentCode).ifPresent(p -> {
            log.info("Setting parentDetail (id={}, code={}) for financial instrument code: {}", p.getId(), p.getCode(), code);
            detailInfo.setParentDetailId(p.getId());
            detailInfo.setParentDetail(p);
        });

        Call<ResponseBody> call = detailClient.saveDetail(detailInfo);
        executeCall(call, "saveDetail (FinancialInstrument: " + code + ")");
        log.info("Successfully requested saveDetail for financial instrument code: {}", code);

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


    /**
     * Executes a Retrofit Call synchronously with comprehensive logging, timing, and exception handling.
     */
    private <T> T executeCall(Call<T> call, String operationName) {
        long startTime = System.currentTimeMillis();
        log.info(">>> Invoking Accounting SDK method: {}", operationName);

        try {
            Response<T> response = call.execute();
            long duration = System.currentTimeMillis() - startTime;

            if (!response.isSuccessful()) {
                String errorBody = "";
                if (response.errorBody() != null) {
                    try {
                        errorBody = response.errorBody().string();
                    } catch (Exception ex) {
                        errorBody = "[Failed to read errorBody: " + ex.getMessage() + "]";
                    }
                }

                log.error("<<< Accounting SDK method '{}' failed in {} ms with HTTP status {}. Error: {}",
                        operationName, duration, response.code(), errorBody);

                if (response.code() == 400 || response.code() == 422) {
                    throw new InternalSaleCustomException.ValidationException(
                            "Accounting service validation failed: " + errorBody);
                } else if (response.code() == 404) {
                    throw new InternalSaleCustomException.ResourceNotFoundException(
                            "Accounting resource not found: " + errorBody);
                } else {
                    throw new InternalSaleCustomException.ApplicationServerException(
                            "Accounting API error [" + response.code() + "]: " + errorBody);
                }
            }

            log.info("<<< Accounting SDK method '{}' completed successfully in {} ms (HTTP {})",
                    operationName, duration, response.code());
            return response.body();

        } catch (IOException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("<<< Accounting SDK network/IO error during '{}' after {} ms: {}",
                    operationName, duration, e.getMessage(), e);
            throw new InternalSaleCustomException.ApplicationServerException(
                    "Error communicating with Accounting service: " + e.getMessage());
        } catch (InternalSaleCustomException e) {
            throw e;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("<<< Unexpected error during Accounting SDK call '{}' after {} ms: {}",
                    operationName, duration, e.getMessage(), e);
            throw new InternalSaleCustomException.ApplicationServerException(
                    "Unexpected accounting service error: " + e.getMessage());
        }
    }
}
