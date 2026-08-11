package com.nicico.internal.sales.ins.customer.repository;

import com.nicico.internal.sales.ins.customer.model.CustomerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerModel, Long>, JpaSpecificationExecutor<CustomerModel> {

	Optional<CustomerModel> findByNationalCode(String nationalCode);


	@Query(value = """
			SELECT
			    c.*
			FROM
			    T_INS_CUSTOMER c
			LEFT JOIN MVW_DBLINK_CUSTOMER1TBL p ON
			    c.C_ECONOMIC_CODE = p.CUST_EGHTESADNUMBER or    p.CUST_SABTNUMBER like '%'||c.C_NATIONAL_CODE||'%' or c.C_NATIONAL_CODE=p.CUST_POSTCODE
			WHERE
			    p.CUST_ID IS  NULL
			""",
			countQuery = """
					    SELECT COUNT(*)
					    FROM
					    T_INS_CUSTOMER c
					LEFT JOIN MVW_DBLINK_CUSTOMER1TBL p ON
					    c.C_ECONOMIC_CODE = p.CUST_EGHTESADNUMBER or p.CUST_SABTNUMBER like '%'||c.C_NATIONAL_CODE||'%' or c.C_NATIONAL_CODE=p.CUST_POSTCODE
					WHERE
					    p.CUST_ID IS  NULL
					""", nativeQuery = true)
	Page<CustomerModel> findAllCustomersNotExistsInPms(Pageable pageable);


	boolean existsByNationalCode(String nationalCode);
}
