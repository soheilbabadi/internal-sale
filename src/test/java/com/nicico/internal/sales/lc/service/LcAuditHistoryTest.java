//
//package com.nicico.internal.sales.lc.service;
//
//import com.nicico.internal.sales.lc.dto.LcAuditDto;
//import com.nicico.internal.sales.lc.model.LcModel;
//import com.nicico.internal.sales.lc.repository.LcRepository;
//import com.nicico.internal.sales.lc.service.LcService;
//import com.nicico.internal.sales.performa.enums.WorkflowApproveStatus;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@SpringBootTest
//@Transactional
//class LcAuditHistoryTest {
//
//    @Autowired
//    private LcService lcService;
//
//    @Autowired
//    private LcRepository lcRepository;
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldReturnAllRevisions() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400015");
//        lc.setPerformaDate("1403/10/05");
//        lc.setLcNo("LC-SOHEIL-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(999L);
//        lc.setPaymentCode("102578132400105");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        savedLc.setLcNo("6667788899000");
//        savedLc.setNosaCode("NOSA-TEST");
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//        assertEquals(0, auditHistory.size());
//
//
//
//        System.out.println("Audit history test passed!");
//        System.out.println("Total revisions: " + auditHistory.size());
//
//    }
//
//    @Test
//    void testGetAuditHistory_shouldShowMultipleUpdates() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400015");
//        lc.setPerformaDate("1403/10/01");
//        lc.setLcNo("6667788899000");
//        lc.setLcDate(new Date());
//        lc.setContractNo(888L);
//        lc.setPaymentCode("102578132400105");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        savedLc.setLcNo("6667788899001");
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setNosaCode("NOSA-MULTI");
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setTradingBankTitle("بانک تجارت");
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertEquals(0, auditHistory.size());
//
//        System.out.println("\n*** Audit Trail for LC ID: " + lcId + " ***");
//        for (LcAuditDto audit : auditHistory) {
//            System.out.printf(
//                    "Revision %d: [%s] LC No: %s, NOSA: %s, Bank: %s @ %s%n",
//                    audit.getRevisionNumber(),
//                    audit.getRevisionType(),
//                    audit.getLcNo(),
//                    audit.getNosaCode(),
//                    audit.getTradingBankTitle(),
//                    audit.getRevisionDate()
//            );
//        }
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldReturnEmptyForNonExistentId() {
//        Long nonExistentId = 999999999L;
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(nonExistentId);
//
//        assertNotNull(auditHistory);
//        assertTrue(auditHistory.isEmpty());
//
//        System.out.println("Empty audit history test passed for non-existent ID");
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackDateChanges() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400020");
//        lc.setPerformaDate("1403/10/15");
//        lc.setLcNo("LC-DATE-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(1111L);
//        lc.setPaymentCode("102578132400106");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        Date newLcDate = new Date(System.currentTimeMillis() + 86400000L);
//        savedLc.setLcDate(newLcDate);
//        savedLc.setLcExpiryDate(new Date(System.currentTimeMillis() + 15552000000L));
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("Date fora udit for LC ID: " + lcId + " ***");
//
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackBankInformationChanges() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400025");
//        lc.setPerformaDate("1403/10/20");
//        lc.setLcNo("LC-BANK-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(2222L);
//        lc.setPaymentCode("102578132400107");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//        lc.setTradingBankTitle("بانک ملی");
//        lc.setTradingBankBranchTitle("شعبه مرکزی");
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        savedLc.setTradingBankTitle("بانک صادرات");
//        savedLc.setTradingBankBranchTitle("شعبه ولیعصر");
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setIssuerBankName("بانک تجارت");
//        savedLc.setIssuerBankBranchName("شعبه آزادی");
//        savedLc.setIssuerBankBranchCode("1234");
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** Bank Information Changes Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions: " + auditHistory.size());
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackPaymentTermsChanges() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400030");
//        lc.setPerformaDate("1403/10/25");
//        lc.setLcNo("LC-PAYMENT-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(3333L);
//        lc.setPaymentCode("102578132400108");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        savedLc.setCreditExpirePeriod(12);
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setPaymentDeferral(60);
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setDeadlineDays(90);
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** Payment Terms Changes Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions: " + auditHistory.size());
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackFileAttachmentChanges() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400035");
//        lc.setPerformaDate("1403/10/28");
//        lc.setLcNo("LC-FILE-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(4444L);
//        lc.setPaymentCode("102578132400109");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        savedLc.setLcAttachmentId("FILE-001");
//        savedLc.setDispatchAttachmentId("DISPATCH-001");
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setProformaFileId("PROFORMA-001");
//        savedLc.setNotificationDocumentId("NOTIFICATION-001");
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** File Attachment Changes Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions: " + auditHistory.size());
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackNosaCodeAndPmsChanges() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400040");
//        lc.setPerformaDate("1403/11/01");
//        lc.setLcNo("LC-NOSA-PMS-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(5555L);
//        lc.setPaymentCode("102578132400110");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        savedLc.setNosaCode("NOSA-2024-001");
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setPmsLcId("PMS-LC-2024-001");
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setNosaCode("NOSA-2024-002");
//        savedLc.setPmsLcId("PMS-LC-2024-002");
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** NOSA and PMS Changes Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions: " + auditHistory.size());
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackCompleteLifecycle() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400045");
//        lc.setPerformaDate("1403/11/05");
//        lc.setLcNo("LC-LIFECYCLE-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(6666L);
//        lc.setPaymentCode("102578132400111");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//        lc.setTradingBankTitle("بانک ملی");
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        // Step 2: Add NOSA code
//        savedLc.setNosaCode("NOSA-LIFECYCLE-001");
//        lcRepository.saveAndFlush(savedLc);
//
//        // Step 3: Update bank information
//        savedLc.setIssuerBankName("بانک تجارت");
//        savedLc.setIssuerBankBranchName("شعبه مرکزی");
//        lcRepository.saveAndFlush(savedLc);
//
//        // Step 4: Add file attachments
//        savedLc.setLcAttachmentId("LC-FILE-001");
//        savedLc.setDispatchAttachmentId("DISPATCH-FILE-001");
//        lcRepository.saveAndFlush(savedLc);
//
//        // Step 5: Update payment terms
//        savedLc.setCreditExpirePeriod(12);
//        savedLc.setPaymentDeferral(60);
//        lcRepository.saveAndFlush(savedLc);
//
//        // Step 6: Add PMS ID
//        savedLc.setPmsLcId("PMS-LIFECYCLE-001");
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** Complete Lifecycle Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions in lifecycle: " + auditHistory.size());
//        for (int i = 0; i < auditHistory.size(); i++) {
//            LcAuditDto audit = auditHistory.get(i);
//            System.out.printf("Step %d - Revision %d: [%s] @ %s%n",
//                    i + 1,
//                    audit.getRevisionNumber(),
//                    audit.getRevisionType(),
//                    audit.getRevisionDate()
//            );
//        }
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldHandleMinimalUpdates() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400050");
//        lc.setPerformaDate("1403/11/10");
//        lc.setLcNo("LC-MINIMAL-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(7777L);
//        lc.setPaymentCode("102578132400112");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        // Only one small update
//        savedLc.setRequireDispatchFile(true);
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** Minimal Update Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions: " + auditHistory.size());
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackMultipleLcNoChanges() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400055");
//        lc.setPerformaDate("1403/11/15");
//        lc.setLcNo("LC-MULTI-NO-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(8888L);
//        lc.setPaymentCode("102578132400113");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        savedLc.setLcNo("LC-MULTI-NO-002");
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setLcNo("LC-MULTI-NO-003");
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setLcNo("LC-MULTI-NO-FINAL");
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** Multiple LC Number Changes Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions: " + auditHistory.size());
//        for (LcAuditDto audit : auditHistory) {
//            System.out.printf("Revision %d: LC No = %s%n",
//                    audit.getRevisionNumber(),
//                    audit.getLcNo()
//            );
//        }
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackWorkflowStatusChanges() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400060");
//        lc.setPerformaDate("1403/11/20");
//        lc.setLcNo("LC-WORKFLOW-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(9999L);
//        lc.setPaymentCode("102578132400114");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//        lc.setWorkflowApproveStatus(WorkflowApproveStatus.IN_PROGRESS);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        savedLc.setWorkflowApproveStatus(WorkflowApproveStatus.ACCEPTED);
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setProcessId(UUID.randomUUID().toString());
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** Workflow Status Changes Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions: " + auditHistory.size());
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackSettlementDates() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400065");
//        lc.setPerformaDate("1403/11/25");
//        lc.setLcNo("LC-SETTLEMENT-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(10001L);
//        lc.setPaymentCode("102578132400115");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        savedLc.setSettlementDueDate(new Date(System.currentTimeMillis() + 2592000000L)); // 30 days
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setSettlementDueDate(new Date(System.currentTimeMillis() + 5184000000L)); // 60 days
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** Settlement Date Changes Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions: " + auditHistory.size());
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackMultipleFieldsSimultaneously() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400070");
//        lc.setPerformaDate("1403/12/01");
//        lc.setLcNo("LC-MULTI-FIELD-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(10002L);
//        lc.setPaymentCode("102578132400116");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        // Update multiple fields at once
//        savedLc.setLcNo("LC-MULTI-FIELD-TEST-002");
//        savedLc.setNosaCode("NOSA-MULTI-001");
//        savedLc.setTradingBankTitle("بانک ملت");
//        savedLc.setCreditExpirePeriod(12);
//        savedLc.setIssuerBankName("بانک پاسارگاد");
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** Multiple Fields Simultaneous Update Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions: " + auditHistory.size());
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackProformaRelationshipChanges() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400075");
//        lc.setPerformaDate("1403/12/05");
//        lc.setLcNo("LC-PROFORMA-REL-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(10003L);
//        lc.setPaymentCode("102578132400117");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//        lc.setProformaMasterId(1L);
//        lc.setProformaDetailId(1L);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        savedLc.setProformaMasterId(2L);
//        savedLc.setProformaDetailId(2L);
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setPerformaNo("140400076");
//        savedLc.setPerformaDate("1403/12/06");
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** Proforma Relationship Changes Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions: " + auditHistory.size());
//    }
//
//    @Test
//    @Rollback
//    void testGetAuditHistory_shouldTrackInstanceIdChanges() {
//        LcModel lc = new LcModel();
//        lc.setPerformaNo("140400080");
//        lc.setPerformaDate("1403/12/10");
//        lc.setLcNo("LC-INSTANCE-TEST-001");
//        lc.setLcDate(new Date());
//        lc.setContractNo(10004L);
//        lc.setPaymentCode("102578132400118");
//        lc.setCreditExpirePeriod(6);
//        lc.setPaymentDeferral(30);
//        lc.setDeadlineDays(45);
//
//        LcModel savedLc = lcRepository.saveAndFlush(lc);
//        Long lcId = savedLc.getId();
//
//        savedLc.setLcInstanceId(UUID.randomUUID().toString());
//        lcRepository.saveAndFlush(savedLc);
//
//        savedLc.setProcessId(UUID.randomUUID().toString());
//        savedLc.setLcInstanceId(UUID.randomUUID().toString());
//        lcRepository.saveAndFlush(savedLc);
//
//        List<LcAuditDto> auditHistory = lcService.getAuditHistory(lcId);
//
//        assertNotNull(auditHistory);
//
//        System.out.println("\n*** Instance ID Changes Audit for LC ID: " + lcId + " ***");
//        System.out.println("Total revisions: " + auditHistory.size());
//    }
//}
//
