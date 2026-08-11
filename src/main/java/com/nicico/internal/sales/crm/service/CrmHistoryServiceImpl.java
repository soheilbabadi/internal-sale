package com.nicico.internal.sales.crm.service;

import com.nicico.copper.common.dto.search.SearchDTO;
import com.nicico.copper.core.SecurityUtil;
import com.nicico.internal.sales.common.properties.OAuthProperties;
import com.nicico.internal.sales.crm.dto.CrmApprovedCompanyDto;
import com.nicico.internal.sales.crm.dto.LcViewMapper;
import com.nicico.internal.sales.crm.dto.LcWithProformaDto;
import com.nicico.internal.sales.crm.repository.LcViewRepository;
import com.nicico.internal.sales.exception.InternalSaleCustomException;
import com.nicico.internal.sales.export.service.ExportDocService;
import com.nicico.internal.sales.history.dto.HistoryExtractMapper;
import com.nicico.internal.sales.history.dto.HistoryExtractMasterDto;
import com.nicico.internal.sales.history.repository.HistoryRepository;
import com.nicico.internal.sales.lc.dto.LcDto;
import com.nicico.internal.sales.lc.dto.LcMapper;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.proforma.dto.*;
import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaMasterModel;
import com.nicico.internal.sales.proforma.repository.ProformaDetailRepository;
import com.nicico.internal.sales.proforma.repository.ProformaMasterRepository;
import com.nicico.internal.sales.remittance.dto.RemittanceGoodItemDto;
import com.nicico.internal.sales.remittance.dto.RemittanceMasterDto;
import com.nicico.internal.sales.remittance.dto.RemittanceMasterMapper;
import com.nicico.internal.sales.remittance.mapper.RemittanceGoodItemMapper;
import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
import com.nicico.internal.sales.wf.service.ProformaProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrmHistoryServiceImpl implements CrmHistoryService {


	//	private static final String APPROVED_COMPANY_REQUESTS_PATH = "http://prod_crm-gateway/sso/api/user-request-application/my-approved-company-requests";
	private static final String ERR_PROFORMA_NOT_FOUND = "پیش فاکتور پیدا نشد";
	private static final String ERR_PROFORMA_ACCESS_DENIED = "شما دسترسی به این پیش فاکتور ندارید";
	private static final String ERR_REMITTANCE_NOT_FOUND = "حواله پیدا نشد";
	private static final String ERR_REMITTANCE_ACCESS_DENIED = "شما دسترسی به این حواله ندارید";
	private static final String ERR_SSO_FETCH_FAILED = "خطا در دریافت اطلاعات شرکت های تایید شده از SSO";

	private final RestTemplate restTemplate;
	private final HttpServletRequest httpServletRequest;
	private final OAuthProperties oAuthProperties;
	private final HistoryRepository historyRepository;
	private final HistoryExtractMapper historyExtractMapper;
	private final ProformaMasterRepository proformaMasterRepository;
	private final ProformaMasterMapper proformaMasterMapper;
	private final ProformaProcessService proformaProcessService;
	private final LcViewRepository lcViewRepository;
	private final LcViewMapper lcViewMapper;
	private final RemittanceMasterMapper remittanceMasterMapper;
	private final RemittanceMasterRepository remittanceMasterRepository;
	private final ExportDocService exportDocService;
	private final ProformaDetailRepository proformaDetailRepository;
	private final LcRepository lcRepository;
	private final LcMapper lcMapper;
	private final RemittanceGoodItemMapper remittanceGoodItemMapper;

	@Value("${nicico.crm-link}")
	private String approvedCompanyRequestsPath;


	@Override
	public List<CrmApprovedCompanyDto> getApprovedCompanyRequests() {
		return fetchApprovedCompanies();
	}

	@Override
	public SearchDTO.SearchRs<HistoryExtractMasterDto.Info> getFilteredHistory() {
		List<String> nationalCodes = fetchNationalCodes();
		if (nationalCodes.isEmpty()) return emptyResponse();

		var dtoList = historyRepository.findAllByBuyerNationalCodeIn(nationalCodes)
				.stream().map(historyExtractMapper::toDTO).toList();

		SearchDTO.SearchRs<HistoryExtractMasterDto.Info> response = new SearchDTO.SearchRs<>();
		response.setList(dtoList);
		response.setTotalCount((long) dtoList.size());
		return response;
	}

	@Override
	public SearchDTO.SearchRs<ProformaMasterDTO.Info> getProformaList() {

		log.info("User name from CRM 8==>" + SecurityUtil.getUsername());
		List<String> nationalCodes = fetchNationalCodes();
		if (nationalCodes.isEmpty()) return emptyResponse();

		var dtoList = proformaMasterRepository.findAllByNationalCodeInOrderByIdDesc(nationalCodes)
				.stream().map(proformaMasterMapper::toDTO).toList();

		SearchDTO.SearchRs<ProformaMasterDTO.Info> response = new SearchDTO.SearchRs<>();
		response.setList(dtoList);
		response.setTotalCount((long) dtoList.size());
		return response;
	}

	@Override
	public List<LcDto.Info> getAllLcDataByProformaMasterId(Long proformaMasterId) {
		var lcModels = lcRepository.findAllByProformaMasterId(proformaMasterId);
		if (lcModels.isEmpty()) {
			return List.of();
		}

		return lcModels.stream().map(lcMapper::toDTO).toList();
	}

	@Override
	public ProformaResponseDto getProformaDetailById(long id) {
		List<String> nationalCodes = fetchNationalCodes();
		proformaProcessService.refreshProformaStatus();

		ProformaMasterModel master = proformaMasterRepository.findById(id)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERR_PROFORMA_NOT_FOUND));

		if (nationalCodes.stream().noneMatch(code -> code.equalsIgnoreCase(master.getNationalCode()))) {
			throw new InternalSaleCustomException.AccessDeniedException(ERR_PROFORMA_ACCESS_DENIED);
		}

		List<ProformaDetailDto> detailDtoList = master.getProformaDetailModelLists().stream().map(model -> {
			var dto = new ProformaDetailDto();
			BeanUtils.copyProperties(model, dto);
			dto.setProformaGoodItemDtos(model.getProformaGoodItemModels().stream().map(item -> {
				var goodItemDto = new ProformaGoodItemDto();
				BeanUtils.copyProperties(item, goodItemDto);
				goodItemDto.setGoodsName(item.getGoodName());
				goodItemDto.setGoodsId(item.getGoodId());
				goodItemDto.setUnitName("کیلوگرم");
				return goodItemDto;
			}).toList());
			return dto;
		}).toList();

		var masterDto = proformaMasterMapper.toDTO(master);
		if (!detailDtoList.isEmpty() && !detailDtoList.get(0).getProformaGoodItemDtos().isEmpty()) {
			masterDto.setGoodId(detailDtoList.get(0).getProformaGoodItemDtos().get(0).getGoodsId());
			masterDto.setGoodName(detailDtoList.get(0).getProformaGoodItemDtos().get(0).getGoodsName());
		}

		var responseDto = new ProformaResponseDto();
		responseDto.setMasterDTO(masterDto);
		responseDto.setDetailDtoList(detailDtoList);
		return responseDto;
	}

	@Override
	public SearchDTO.SearchRs<LcWithProformaDto.Info> searchLc(SearchDTO.SearchRq request) {
		List<String> nationalCodes = fetchNationalCodes();
		if (nationalCodes.isEmpty()) return emptyResponse();

		var dtoList = lcViewRepository.findAllByNationalCodeInOrderByContractDateDesc(nationalCodes)
				.stream().map(lcViewMapper::toDTO).toList();

		SearchDTO.SearchRs<LcWithProformaDto.Info> response = new SearchDTO.SearchRs<>();
		response.setList(dtoList);
		response.setTotalCount((long) dtoList.size());
		return response;
	}

	@Override
	public SearchDTO.SearchRs<ProformaMasterDTO.Info> searchProforma(SearchDTO.SearchRq request) {
		List<String> nationalCodes = fetchNationalCodes();
		if (nationalCodes.isEmpty()) return emptyResponse();

		var dtoList = proformaMasterRepository.findAllByNationalCodeInOrderByIdDesc(nationalCodes)
				.stream().map(proformaMasterMapper::toDTO).toList();

		SearchDTO.SearchRs<ProformaMasterDTO.Info> response = new SearchDTO.SearchRs<>();
		response.setList(dtoList);
		response.setTotalCount((long) dtoList.size());
		return response;
	}

	@Override
	public SearchDTO.SearchRs<RemittanceMasterDto.Info> searchRemittance(SearchDTO.SearchRq request) {
		List<String> nationalCodes = fetchNationalCodes();
		if (nationalCodes.isEmpty()) return emptyResponse();

		var dtoList = remittanceMasterRepository.findAllByNationalCodeIn(nationalCodes)
				.stream().map(remittanceMasterMapper::toDTO).toList();

		SearchDTO.SearchRs<RemittanceMasterDto.Info> response = new SearchDTO.SearchRs<>();
		response.setList(dtoList);
		response.setTotalCount((long) dtoList.size());
		return response;
	}

	@Override
	public RemittanceMasterDto.Info getRemittanceDetailById(Long remittanceId) {
		RemittanceMasterModel remittance = remittanceMasterRepository.findById(remittanceId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERR_REMITTANCE_NOT_FOUND));
		List<RemittanceGoodItemDto.Info> remittanceDetails = remittance.getRemittanceGoodItemModels()
				.stream()
				.map(remittanceGoodItemMapper::toDTO)
				.toList();
		RemittanceMasterDto.Info remittanceDto = remittanceMasterMapper.toDTO(remittance);
		remittanceDto.setRemittanceGoodItemDtos(remittanceDetails);
		return remittanceDto;
	}

	@Override
	public List<RemittanceMasterDto.Info> getAllByProformaMasterId(Long proformaMasterId) {
		ProformaMasterModel proformaMaster = proformaMasterRepository.findById(proformaMasterId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERR_PROFORMA_NOT_FOUND));

		List<String> nationalCodes = fetchNationalCodes();
		if (nationalCodes.stream().noneMatch(code -> code.equalsIgnoreCase(proformaMaster.getNationalCode()))) {
			throw new InternalSaleCustomException.AccessDeniedException(ERR_PROFORMA_ACCESS_DENIED);
		}

		List<RemittanceMasterModel> remittances = remittanceMasterRepository
				.findAllByProformaMasterId(proformaMaster.getId());
		if (remittances == null || remittances.isEmpty()) {
			return Collections.emptyList();
		}
		return remittances.stream()
				.map(remittanceMasterMapper::toDTO)
				.toList();
	}

	@Override
	public byte[] exportRemittanceDoc(long remittanceId) {
		RemittanceMasterModel remittance = remittanceMasterRepository.findById(remittanceId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERR_REMITTANCE_NOT_FOUND));
		checkRemittanceAccess(remittance, fetchNationalCodes());
		return exportDocService.exportRemittanceDoc(remittanceId);
	}

	@Override
	public byte[] exportRemittancePdf(long remittanceId) {
		RemittanceMasterModel remittance = remittanceMasterRepository.findById(remittanceId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERR_REMITTANCE_NOT_FOUND));
		checkRemittanceAccess(remittance, fetchNationalCodes());
		return exportDocService.exportRemittancePdf(remittanceId);
	}

	@Override
	public byte[] exportProformaDoc(long proformaDetailId) {
		checkProformaAccess(proformaDetailId, fetchNationalCodes());
		return exportDocService.exportProformaDoc(proformaDetailId);
	}

	@Override
	public byte[] exportProformaPdf(long proformaDetailId) {
		checkProformaAccess(proformaDetailId, fetchNationalCodes());
		return exportDocService.exportProformaPdf(proformaDetailId);
	}

	private HttpHeaders buildSsoHeaders() {
		HttpHeaders headers = new HttpHeaders();

		String authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorization != null) {
			headers.set(HttpHeaders.AUTHORIZATION, authorization);
		}

		Cookie[] cookies = httpServletRequest.getCookies();
		if (cookies != null) {
			Arrays.stream(cookies)
					.filter(c -> "SESSION".equals(c.getName()))
					.map(c -> "SESSION=" + c.getValue())
					.findFirst()
					.ifPresent(cookie -> headers.set(HttpHeaders.COOKIE, cookie));
		}

		return headers;
	}

	private String resolveSsoBaseUrl() {
		String landingAddress = oAuthProperties.getLandingAddress();
		if (landingAddress == null || landingAddress.isBlank()) {
			throw new InternalSaleCustomException.ApplicationServerException("ui.landing-address is not configured");
		}

		URI uri = URI.create(landingAddress);
		String scheme = uri.getScheme();
		String host = uri.getHost();
		int port = uri.getPort();

		if (scheme == null || host == null) {
			throw new InternalSaleCustomException.ApplicationServerException("ui.landing-address is invalid");
		}

		return port > 0 ? scheme + "://" + host + ":" + port : scheme + "://" + host;
	}

	private String resolveRequestType() {
		String landingAddress = oAuthProperties.getLandingAddress();
		if (landingAddress == null || landingAddress.isBlank()) {
			throw new InternalSaleCustomException.ApplicationServerException("ui.landing-address is not configured");
		}

		String path = URI.create(landingAddress).getPath();
		if (path == null || path.isBlank() || "/".equals(path)) {
			throw new InternalSaleCustomException.ApplicationServerException("ui.landing-address path is invalid");
		}

		String normalized = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
		int lastSlash = normalized.lastIndexOf('/');
		return lastSlash >= 0 ? normalized.substring(lastSlash + 1) : normalized;
	}

	private List<CrmApprovedCompanyDto> fetchApprovedCompanies() {
		String ssoBaseUrl = resolveSsoBaseUrl();
		String requestType = resolveRequestType();
		String url = String.format("%s%s/%s", "", approvedCompanyRequestsPath, requestType);

		try {
			ResponseEntity<CrmApprovedCompanyDto[]> response = restTemplate.exchange(
					url, HttpMethod.GET, new HttpEntity<>(buildSsoHeaders()),
					CrmApprovedCompanyDto[].class
			);
			CrmApprovedCompanyDto[] body = response.getBody();
			return body == null ? List.of() : Arrays.asList(body);

		} catch (HttpClientErrorException.NotFound ex) {
			log.warn("SSO endpoint returned 404 for requestType='{}' — returning empty list", requestType);
			return List.of();
		} catch (Exception ex) {
			log.error("SSO call failed for url='{}': {}", url, ex.getMessage());
			throw new InternalSaleCustomException.ApplicationServerException(ERR_SSO_FETCH_FAILED);
		}
	}

	private List<String> fetchNationalCodes() {
		return fetchApprovedCompanies().stream()
				.map(CrmApprovedCompanyDto::getNationalCode)
				.filter(code -> code != null && !code.isBlank())
				.distinct()
				.toList();
	}

	private void checkRemittanceAccess(RemittanceMasterModel remittance, List<String> nationalCodes) {
		if (nationalCodes.stream().noneMatch(code -> code.equalsIgnoreCase(remittance.getNationalCode()))) {
			throw new InternalSaleCustomException.AccessDeniedException(ERR_REMITTANCE_ACCESS_DENIED);
		}
	}

	private void checkProformaAccess(long proformaDetailId, List<String> nationalCodes) {
		ProformaDetailModel detail = proformaDetailRepository.findById(proformaDetailId)
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERR_PROFORMA_NOT_FOUND));
		ProformaMasterModel master = proformaMasterRepository.findById(detail.getProformaMasterId())
				.orElseThrow(() -> new InternalSaleCustomException.ResourceNotFoundException(ERR_PROFORMA_NOT_FOUND));

		if (nationalCodes.stream().noneMatch(code -> code.equalsIgnoreCase(master.getNationalCode()))) {
			throw new InternalSaleCustomException.AccessDeniedException(ERR_PROFORMA_ACCESS_DENIED);
		}
	}

	private <T> SearchDTO.SearchRs<T> emptyResponse() {
		SearchDTO.SearchRs<T> response = new SearchDTO.SearchRs<>();
		response.setList(List.of());
		response.setTotalCount(0L);
		return response;
	}
}