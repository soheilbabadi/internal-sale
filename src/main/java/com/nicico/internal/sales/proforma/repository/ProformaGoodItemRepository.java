package com.nicico.internal.sales.proforma.repository;

import com.nicico.internal.sales.proforma.model.ProformaDetailModel;
import com.nicico.internal.sales.proforma.model.ProformaGoodItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProformaGoodItemRepository extends JpaRepository<ProformaGoodItemModel, Long>, JpaSpecificationExecutor<ProformaGoodItemModel> {
	@Query(value = "SELECT g.* FROM T_INS_PERFORMA_GOOD_ITEM g INNER JOIN T_INS_PERFORMA_DETAIL pd ON g.F_PERFORMA_DETAIL_ID = pd.ID WHERE pd.F_PERFORMA_MASTER_ID = :proformaMasterId  AND pd.C_PROFORMA_REVERSAL_STATUS <> 'CANCELED'", nativeQuery = true)
	List<ProformaGoodItemModel> findActiveItemsWithProformaMasterId(Long proformaMasterId);

	List<ProformaGoodItemModel> findAllByProformaDetailModel(ProformaDetailModel proformaDetailModel);


	@Query(value = """
			SELECT g.*
			FROM T_INS_PERFORMA_GOOD_ITEM g
			INNER JOIN T_INS_PERFORMA_DETAIL pd
			    ON g.F_PERFORMA_DETAIL_ID = pd.ID
			WHERE pd.F_PERFORMA_MASTER_ID = :proformaMasterId
			  AND pd.C_PROFORMA_REVERSAL_STATUS <> 'CANCELED'
			ORDER BY g.ID DESC
			FETCH FIRST 1 ROW ONLY
			""", nativeQuery = true)
	ProformaGoodItemModel findLatestActiveItemWithProformaMasterId(Long proformaMasterId);
}
