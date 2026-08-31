//package com.nicico.internal.sales;
//import com.nicico.internal.sales.config.CurlLoggingInterceptor;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.*;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import java.util.Map;
//import static org.assertj.core.api.Assertions.assertThat;
/// **
// * کلاس پایه برای تست های یکپارچه سازی که نیاز به احراز هویت دارند
// * تمام تست هایی که نیاز به توکن دارند می توانند از این کلاس ارث بری کنند
// */
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//public abstract class BaseIntegrationTest {
//    @Autowired
//    protected TestRestTemplate restTemplate;
//    @Value("${spring.security.oauth2.client.provider.oserver.token-uri}")
//    private String tokenUrl;
//    @Value("${ins.username}")
//    private String username;
//    @Value("${ins.password}")
//    private String password;
//    @Value("${spring.application.name}")
//    private String applicationName;
//    @Value("${spring.security.oauth2.client.registration.oserver.client-secret}")
//    private String applicationSecret;
//    @BeforeAll
//    void setToken() {
//        RestTemplate client = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.setBasicAuth(applicationName, applicationSecret);
//
//        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//        formData.add("username", username);
//        formData.add("password", password);
//        formData.add("grant_type", "password");
//
//        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
//        ResponseEntity<Map<String, String>> exchange = client.exchange(tokenUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
//        });
//        assertThat(exchange.getBody()).isNotNull();
//        assertThat(exchange.getBody().get("access_token")).isNotNull();
//
//        // اضافه کردن Interceptor برای لاگ کردن cURL
//        restTemplate.getRestTemplate().getInterceptors().add(new CurlLoggingInterceptor());
//
//        // اضافه کردن Interceptor برای Authorization header
//        restTemplate.getRestTemplate().getInterceptors().add((request, body, execution) -> {
//            request.getHeaders().add("Authorization", "Bearer " + exchange.getBody().get("access_token"));
//            return execution.execute(request, body);
//        });
//    }
//}