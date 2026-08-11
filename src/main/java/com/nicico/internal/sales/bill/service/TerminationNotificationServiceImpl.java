//package com.nicico.internal.sales.bill.service;
//
//import com.nicico.internal.sales.notification.dto.EmailRequest;
//import com.nicico.internal.sales.notification.service.MailService;
//import com.nicico.internal.sales.remittance.model.RemittanceMasterModel;
//import com.nicico.internal.sales.remittance.repository.RemittanceMasterRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.net.http.HttpResponse;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class TerminationNotificationServiceImpl implements TerminationNotificationService {
//    private final RemittanceMasterRepository remittanceMasterRepository;
//    private final MailService mailService;
//    @Value("${nicico.terminate-remittance}")
//    private String recipientEmail;
//
//    public List<RemittanceMasterModel> extractRemittanceMasterModels(List<String> remittanceNoList) {
//        if (remittanceNoList == null || remittanceNoList.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        List<String> normalizedRemittanceNumbers = remittanceNoList.stream()
//                .filter(Objects::nonNull)
//                .map(String::trim)
//                .filter(remittanceNo -> !remittanceNo.isEmpty())
//                .distinct()
//                .toList();
//
//        if (normalizedRemittanceNumbers.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        return remittanceMasterRepository.findAllByRemittanceNumberIn(normalizedRemittanceNumbers);
//    }
//
//
//    @Override
//    public boolean sendTerminationNotice(List<String> remittanceNoList) {
//        var remittanceMasterModels = extractRemittanceMasterModels(remittanceNoList);
//        if (remittanceMasterModels == null || remittanceMasterModels.isEmpty()) {
//            log.warn("No remittance master models provided for termination notification");
//            return false;
//        }
//
//
//        List<RemittanceMasterModel> validRemittances = remittanceMasterModels.stream()
//                .filter(remittance -> remittance.getPmsId() != null && !remittance.getPmsId().trim().isEmpty())
//                .toList();
//
//        if (validRemittances.isEmpty()) {
//            log.warn("No valid remittances with PMS ID found for termination notification");
//            return false;
//        }
//
//        // Build email content
//        String subject = buildEmailSubject(validRemittances);
//        String content = buildEmailContent(validRemittances);
//
//        EmailRequest emailRequest = new EmailRequest();
//        emailRequest.setToRecipients(recipientEmail);
//        emailRequest.setSubject(subject);
//        emailRequest.setContent(content);
//        emailRequest.setBccRecipients(null);
//
//        HttpResponse<String> response = mailService.sendMail(emailRequest);
//
//        if (response != null && response.statusCode() == 200) {
//            log.info("Termination notification email sent successfully for {} remittances",
//                    validRemittances.size());
//            return true;
//        } else {
//            log.error("Failed to send termination notification email. Response: {}", response);
//            return false;
//        }
//
//    }
//
//    private String buildEmailSubject(List<RemittanceMasterModel> remittances) {
//        int count = remittances.size();
//        if (count == 1) {
//            return "اطلاعیه خاتمه حواله - " + remittances.get(0).getRemittanceNumber();
//        }
//        return String.format("اطلاعیه خاتمه %d حواله - نیازمند بررسی", count);
//    }
//
//    private String buildEmailContent(List<RemittanceMasterModel> remittances) {
//        StringBuilder content = new StringBuilder();
//
//        content.append("با سلام<br/><br/>");
//        content.append("احتراماً لطفاً شماره حواله های زیر خاتمه یابد:<br/><br/>");
//        content.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>");
//        content.append("<tr style='background-color: #f2f2f2;'>");
//        content.append("<th>ردیف</th>");
//        content.append("<th>C_PMS_ID</th>");
//        content.append("<th>شماره حواله</th>");
//        content.append("<th>نام مشتری</th>");
//        content.append("</tr>");
//
//        int rowNumber = 1;
//        for (RemittanceMasterModel remittance : remittances) {
//            content.append("<tr>");
//            content.append("<td>").append(rowNumber++).append("</td>");
//            content.append("<td>").append(escapeHtml(remittance.getPmsId())).append("</td>");
//            content.append("<td>").append(escapeHtml(remittance.getRemittanceNumber())).append("</td>");
//            content.append("<td>").append(escapeHtml(remittance.getCustomerName())).append("</td>");
//            content.append("</tr>");
//        }
//        content.append("</table><br/>");
//
//        // Add footer
//        content.append("با تشکر<br/>");
//        content.append("فروش داخلی");
//
//        return content.toString();
//    }
//
//
//    private String buildPlainTextEmailContent(List<RemittanceMasterModel> remittances) {
//        StringBuilder content = new StringBuilder();
//
//        content.append("با سلام\n\n");
//        content.append("احتراماً لطفاً شماره حواله های زیر خاتمه یابد:\n\n");
//
//        content.append("ردیف | C_PMS_ID | شماره حواله | نام مشتری\n");
//        content.append("-----|----------|------------|------------\n");
//
//        int rowNumber = 1;
//        for (RemittanceMasterModel remittance : remittances) {
//            content.append(String.format("%d | %s | %s | %s\n",
//                    rowNumber++,
//                    remittance.getPmsId(),
//                    remittance.getRemittanceNumber(),
//                    remittance.getCustomerName()));
//        }
//
//        content.append("\nبا تشکر\n");
//        content.append("فروش داخلی");
//
//        return content.toString();
//    }
//
//
//    private String escapeHtml(String text) {
//        if (text == null) {
//            return "";
//        }
//        return text.replace("&", "&amp;")
//                .replace("<", "&lt;")
//                .replace(">", "&gt;")
//                .replace("\"", "&quot;")
//                .replace("'", "&#39;");
//    }
//}