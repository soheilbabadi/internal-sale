//package com.nicico.internal.sales.pms;
//
//import com.nicico.internal.sales.BaseIntegrationTest;
//import com.nicico.internal.sales.bank.model.IssuingBankWithPmsIdView;
//import com.nicico.internal.sales.bank.repository.IssuingBankWithPmsIdRepository;
//import com.nicico.internal.sales.common.properties.PMSProperties;
//import com.nicico.internal.sales.ins.customer.model.CustomerModel;
//import com.nicico.internal.sales.ins.customer.repository.CustomerRepository;
//import com.nicico.internal.sales.pms.dto.PMSCreateBankDto;
//import com.nicico.internal.sales.pms.repository.PMSCustomerRepository;
//import com.nicico.internal.sales.pms.service.PmsBankCreateRabbitService;
//import com.nicico.internal.sales.pms.service.PmsCustomerCreateRabbitService;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.jpa.domain.Specification;
//
//@Disabled("این تست فقط برای اجرای دستی است - در build خودکار اجرا نمی شود")
////@ActiveProfiles("local")
//class PMSCreateBankCustomerWithRabbitTest extends BaseIntegrationTest {
//    @Autowired
//    private PmsBankCreateRabbitService pmsBankCreateRabbitService;
//    @Autowired
//    private IssuingBankWithPmsIdRepository issuingBankWithPmsIdRepository;
//     @Autowired
//    private PMSProperties pmsProperties;
//    @Autowired
//    private CustomerRepository customerRepository;
//    @Autowired
//    private  PMSCustomerRepository pmsCustomerRepository;
//    @Autowired
//    private PmsCustomerCreateRabbitService pmsCustomerCreateRabbitService;
//    @Test
//    void createCustomerInPmsWithRabbitTest(){
//        pmsCustomerRepository.updatePmsCustomerMaterializedView();
//        CustomerModel customerModel = customerRepository.findAllCustomersNotExistsInPms(PageRequest.of(0, 1)).get().toList().get(0);
//        pmsCustomerCreateRabbitService.createCustomer(customerModel);
//    }
//    @Test
//    void createPreFactorFromProformaGoodItemTest() {
//        pmsCustomerRepository.updatePmsCustomerMaterializedView();
//        Specification<IssuingBankWithPmsIdView> specification=Specification.where(  (root,
//                                                                                     query,
//                                                                                     criteriaBuilder) ->
//                criteriaBuilder.isNull(root.get("pmsLcBankId")));
//        Page<IssuingBankWithPmsIdView> all = issuingBankWithPmsIdRepository.findAll(specification, PageRequest.of(0, 1));
//        IssuingBankWithPmsIdView bank = all.get().toList().get(0);
//        bank = issuingBankWithPmsIdRepository.findById(17718L).orElse(bank);
//        PMSCreateBankDto.Create bankDto = PMSCreateBankDto.Create.builderr().baseBankId(bank.getPmsBaseBankId())
//                .user(pmsProperties.getPreFactor().getUser())
//                .pass(pmsProperties.getPreFactor().getPass())
//                .accountingId(-bank.getId())
//                .branchCode(bank.getBranchCode())
//                .branchDescription(String.format("%s %s %s", bank.getBankName(), bank.getBranchName(), bank.getCity()))
//                .buildd();
//        pmsBankCreateRabbitService.createBank(bankDto);
//    }
//}
