package com.nicico.internal.sales.notification;
import com.nicico.internal.sales.BaseIntegrationTest;
import com.nicico.internal.sales.notification.dto.SmsDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
@Disabled("Tests manually only")
public class SmsNotificationIntegrationTest extends BaseIntegrationTest {
    @Test
    void preFactorEmailedSMSNotificationTest() {
        long proformaMasterId = 833L;
        ResponseEntity<SmsDTO.Response> response = restTemplate.getForEntity("/api/v1/ins/loading/sms-notification/pre-factor-emailed/" + proformaMasterId,
                SmsDTO.Response.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
