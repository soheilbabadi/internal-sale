//package com.nicico.internal.sales.pms;
//import com.nicico.internal.sales.BaseIntegrationTest;
//import com.nicico.internal.sales.pms.dto.PMSPreFactorDto;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import static org.assertj.core.api.Assertions.assertThat;
//@Disabled("این تست فقط برای اجرای دستی است - در build خودکار اجرا نمی شود")
//class PMSPreFactorIntegrationTest extends BaseIntegrationTest {
//    @Test
//    void createPreFactorFromPerformaGoodItemTest() {
//        String username = "minaramezani";
//        long proformaMasterId = 834L;
//        ResponseEntity<PMSPreFactorDto.ResponseForProformaMasterIdDto> response = restTemplate.getForEntity("/api/v1/pms/pre-factor/create-from-proforma-master-id/" + proformaMasterId, PMSPreFactorDto.ResponseForProformaMasterIdDto.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getInfo()).isNotEmpty();
//        response = restTemplate.getForEntity("/api/v1/pms/pre-factor/create-from-proforma-master-id/" + proformaMasterId + "?username=" + username, PMSPreFactorDto.ResponseForProformaMasterIdDto.class);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getInfo()).isNotEmpty();
//    }
//}
