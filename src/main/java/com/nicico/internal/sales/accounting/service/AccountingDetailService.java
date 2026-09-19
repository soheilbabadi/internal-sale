package com.nicico.internal.sales.accounting.service;

import com.fgostar.accounting.sdk.dto.DetailDto;
import com.fgostar.accounting.sdk.dto.EOperator;
import com.nicico.internal.sales.accounting.dto.CreateCompanyDetailDto;
import com.nicico.internal.sales.accounting.dto.CreatePersonDetailDto;

import java.util.List;
import java.util.Optional;

/**
 * Service for querying and creating accounting detail accounts (تفصیلی) via Accounting SDK.
 */
public interface AccountingDetailService {

	/**
	 * Finds detail account by national identifier.
	 * Automatically applies prefix "01/" for 10-digit person national codes,
	 * and prefix "02/" for 11-digit company national IDs.
	 *
	 * @param nationalCodeOrId Valid national code (person) or national ID (company)
	 * @return Optional containing the matching DetailDto, or empty if not found
	 */
	Optional<DetailDto> findDetailOfNationalCode(String nationalCodeOrId);

	/**
	 * Finds detail account for a natural person by national code.
	 *
	 * @param nationalCode 10-digit Iranian national code
	 * @return Optional containing the matching DetailDto, or empty if not found
	 */
	Optional<DetailDto> findDetailForPerson(String nationalCode);

	/**
	 * Finds detail account for a legal entity / company by national ID.
	 *
	 * @param nationalId 11-digit Iranian company national ID
	 * @return Optional containing the matching DetailDto, or empty if not found
	 */
	Optional<DetailDto> findDetailForCompany(String nationalId);

	/**
	 * Searches detail accounts matching a raw detail code or code prefix.
	 *
	 * @param detailCode Detail code string
	 * @param equals
	 * @return List of matching DetailDto
	 */
	List<DetailDto> searchDetailsByCode(String detailCode, EOperator equals);

	/**
	 * Resolves the parent Detail by finding the candidate with the longest matching prefix
	 * that has childrenDigitCount > 0.
	 * For example, for code '01/4270205776' with candidates ['0', '01', '01/4', '01/427015'],
	 * it resolves '01/4'.
	 *
	 * @param targetCode Target detail code (e.g. 01/4270205776)
	 * @return Optional containing resolved parent DetailDto
	 */
	Optional<DetailDto> resolveParentDetail(String targetCode);

	/**
	 * Finds parent detail by its code.
	 *
	 * @param parentCode Parent detail code (e.g. "107/18")
	 * @return Optional containing matching parent DetailDto
	 */
	Optional<DetailDto> findParentByCode(String parentCode);

	/**
	 * Fetches child details belonging to a parent detail ID using /rest/detail/detailGridFetch.
	 *
	 * @param parentDetailId Parent detail ID (e.g. 73005)
	 * @param startRow       Optional start row index (defaults to 0)
	 * @param endRow         Optional end row index (defaults to 500)
	 * @param sortBy         Optional sort field (e.g. "-detailName")
	 * @return List of matching child DetailDto
	 */
	List<DetailDto> getChildrenByParentDetailId(Long parentDetailId, Integer startRow, Integer endRow, String sortBy);

	/**
	 * Generates the next sequence-based detail code under the specified parent
	 * (or sequence prefix + year suffix) by calculating max(existingChildNumber) + 1.
	 *
	 * @param parentCode Parent detail code (e.g. "04/03" or "0403")
	 * @return Next generated detail code string
	 */
	String generateNextSequenceCode(String parentCode);

	/**
	 * Creates an accounting detail account for a natural person.
	 *
	 * @param request Person detail data
	 * @return Created DetailDto
	 */
	DetailDto createDetailForPerson(CreatePersonDetailDto request);

	/**
	 * Creates an accounting detail account for a company / legal entity.
	 *
	 * @param request Company detail data
	 * @return Created DetailDto
	 */
	DetailDto createDetailForCompany(CreateCompanyDetailDto request);

	/**
	 * Generates next sequence-based detail code for a financial instrument
	 * (Letter of Credit, GAM, Electronic Promissory Note) using pattern:
	 * mainCode + twoDigit + YY + sequence.
	 *
	 * @param instrumentType Instrument type (Letter of Credit 107/18, GAM 108/18, Electronic Promissory Note 109/18)
	 * @param twoDigitCode   2-digit user/branch sub-code (e.g. 01)
	 * @param yearSuffix     Optional 2-digit Shamsi year (e.g. 03). Defaults to current year if null/empty.
	 * @return Next generated code string
	 */
	Long generateNextFinancialInstrumentSequenceCode(
			com.nicico.internal.sales.accounting.dto.FinancialInstrumentType instrumentType,
			String twoDigitCode,
			String yearSuffix);

	/**
	 * Creates an accounting detail account for a financial payment instrument.
	 *
	 * @param request Financial instrument detail data
	 * @return Created DetailDto
	 */
	void createDetailForFinancialInstrument(
			com.nicico.internal.sales.accounting.dto.CreateFinancialInstrumentDetailDto request);

}
