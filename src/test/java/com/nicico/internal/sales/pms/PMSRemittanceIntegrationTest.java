//package com.nicico.internal.sales.pms;
//import com.nicico.internal.sales.BaseIntegrationTest;
//import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
//import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ActiveProfiles;
//import static org.assertj.core.api.Assertions.assertThat;
//@ActiveProfiles("local")
//@Disabled("این تست فقط برای اجرای دستی است - در build خودکار اجرا نمی شود")
//class PMSRemittanceIntegrationTest extends BaseIntegrationTest {
//    @Autowired
//    private RemittanceMasterRepository remittanceMasterRepository;
//    @Test
//    void createPMSRemittanceIntegrationTest() {
//
//        RemittanceMasterModel remittanceMasterModel = remittanceMasterRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
//        ResponseEntity<Void> forEntity = restTemplate.getForEntity(String.format("/api/v1/pms/remittance/create-from-remittance-master-id/%s",
//                remittanceMasterModel.getId()), Void.class);
//        assertThat(forEntity.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.CREATED);
//    }
//
//    @Test
//    void updatePMSRemittanceIntegrationTest() {
//        Specification<RemittanceMasterModel> specification = (root, query, criteriaBuilder) ->
//                criteriaBuilder.isNotNull(root.get("pmsId"));
//        RemittanceMasterModel remittanceMasterModel = remittanceMasterRepository
//                .findAll(specification, PageRequest.of(0, 1))
//                .getContent()
//                .get(0);
//
//        ResponseEntity<Void> forEntity = restTemplate.getForEntity(String.format("/api/v1/pms/remittance/update/%s",
//                remittanceMasterModel.getId()), Void.class);
//        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
//}
