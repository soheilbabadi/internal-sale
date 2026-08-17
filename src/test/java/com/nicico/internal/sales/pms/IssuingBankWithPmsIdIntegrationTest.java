//package com.nicico.internal.sales.pms;
//import com.nicico.copper.common.dto.search.SearchDTO;
//import com.nicico.internal.sales.BaseIntegrationTest;
//import com.nicico.internal.sales.bank.dto.IssuingBankWithPmsIdDto;
//import com.nicico.internal.sales.bank.model.IssuingBankWithPmsIdView;
//import com.nicico.internal.sales.bank.repository.IssuingBankWithPmsIdRepository;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.http.*;
//import static org.assertj.core.api.Assertions.assertThat;
//@Disabled("این تست فقط برای اجرای دستی است - در build خودکار اجرا نمی شود")
//class IssuingBankWithPmsIdIntegrationTest extends BaseIntegrationTest {
//    @Autowired
//    private IssuingBankWithPmsIdRepository repository;
//    @Test
//    void testIssueBankWithPmsIdQuery() {
//
//        // اولین رکوردی که pmsLcBankId نال نیست
//        Specification<IssuingBankWithPmsIdView> spec = (root, query, cb) ->
//                cb.isNotNull(root.get("pmsLcBankId"));
//
//        Page<IssuingBankWithPmsIdView> result = repository.findAll(spec, PageRequest.of(0, 1));
//
//        // بررسی نتیجه
//        assertThat(result).isNotEmpty();
//        assertThat(result.getContent().get(0).getPmsLcBankId()).isNotNull();
//
//        // چاپ نتیجه
//        IssuingBankWithPmsIdView bank = result.getContent().get(0);
//        System.out.println("Bank Name: " + bank.getBankName());
//        System.out.println("Branch Code: " + bank.getBranchCode());
//        System.out.println("PMS LC ID: " + bank.getPmsLcBankId());
//    }
//    @Test
//    void testSearchBanksByBranchCode_WithRawJson() {
//        String jsonBody = """
//        {
//            "count": 100,
//            "sortBy": [],
//            "criteria": {
//                "operator": "and",
//                "criteria": [
//                    {
//                     "fieldName": "pmsLcBankId",
//                        "operator": "isNull",
//                        "value": false
//                    },
//                    {
//                        "fieldName": "branchCode",
//                        "operator": "startsWith",
//                        "value": "50"
//                    }
//                ]
//            },
//            "startIndex": 0
//        }
//        """;
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
//
//        ResponseEntity<SearchDTO.SearchRs<IssuingBankWithPmsIdDto.Info>> response = restTemplate.exchange(
//                "/api/v1/ins/performa/bank-issuer-with-pms-id/search",
//                HttpMethod.POST,
//                entity,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getTotalCount()).isNotNull();
//        assertThat(response.getBody().getTotalCount()).isGreaterThan(0);
//        System.out.println("Response: " + response.getBody());
//    }
//}
