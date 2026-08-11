package com.nicico.internal.sales.pms;
import com.nicico.internal.sales.BaseIntegrationTest;
import com.nicico.internal.sales.lc.model.LcModel;
import com.nicico.internal.sales.lc.repository.LcRepository;
import com.nicico.internal.sales.pms.dto.PMSLcDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
@Disabled("این تست فقط برای اجرای دستی است - در build خودکار اجرا نمی شود")
//@ActiveProfiles("local")
class PMSLCIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private LcRepository lcRepository;

    @Test
    void createPreFactorFromProformaGoodItemTest() {
        long proformaMasterId = 1243;
        ResponseEntity<PMSLcDTO.PMSResponse> response = restTemplate.getForEntity("/api/v1/pms/lc/create-from-proforma-master-id/" + proformaMasterId, PMSLcDTO.PMSResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void updatePmsLcTest() {
        Specification<LcModel> specification = (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get("pmsLcId"));
        LcModel lcModel = lcRepository.findAll(specification, PageRequest.of(0, 1)).getContent().get(0);

        ResponseEntity<HttpStatus> response = restTemplate.getForEntity(
                "/api/v1/pms/lc/update/" + lcModel.getPmsLcId(),
                HttpStatus.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
