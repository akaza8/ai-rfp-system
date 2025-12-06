package com.ak.rfp.serviceImplementation;

import com.ak.rfp.entity.Rfp;
import com.ak.rfp.entity.RfpItem;
import com.ak.rfp.entity.Vendor;
import com.ak.rfp.service.AiService;
import com.ak.rfp.service.EmailService;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class EmailServiceImplementation implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(AiService.class);
    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailServiceImplementation(JavaMailSender mailSender, @Value("${app.mail.from:akashlnetwin@yahoo.com}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }
    @Override
    public void sendRfpToVendor(Rfp rfp, Vendor vendor) {
        log.info("Attempting to send email to vendor: {} ({})", vendor.getName(), vendor.getEmail());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(vendor.getEmail());
            message.setSubject("RFP: " + rfp.getTitle());

            StringBuilder body = new StringBuilder();
            body.append("Dear ").append(vendor.getName()).append(",\n\n")
                    .append("We are inviting you to submit a proposal for the following RFP:\n\n")
                    .append("Title: ").append(rfp.getTitle()).append("\n")
                    .append("Budget: ").append(rfp.getBudget()).append("\n")
                    .append("Delivery timeline (days): ").append(rfp.getDeliveryTimelineDays()).append("\n")
                    .append("Payment terms: ").append(nullSafe(rfp.getPaymentTerms())).append("\n")
                    .append("Warranty terms: ").append(nullSafe(rfp.getWarrantyTerms())).append("\n\n")
                    .append("Items:\n");

            List<RfpItem> items = rfp.getItems();
            if(items != null && !items.isEmpty()){
                for (RfpItem item : items) {
                    body.append(" - ")
                            .append(item.getItemType())
                            .append(" | qty: ").append(item.getQuantity())
                            .append(" | specs: ").append(nullSafe(item.getRequiredSpecs()))
                            .append("\n");
                }
            } else {
                body.append(" (No items listed)\n");
            }

            body.append("\n")
                    .append("Please reply to this email with your proposal, including:\n")
                    .append(" - Line-item pricing\n")
                    .append(" - Total price\n")
                    .append(" - Delivery timeline\n")
                    .append(" - Payment and warranty terms\n\n")
                    .append("Best regards,\n")
                    .append("Procurement Team\n");

            message.setText(body.toString());

            log.info("Sending email from: {} to: {}", fromAddress, vendor.getEmail());
            mailSender.send(message);
            log.info("Email sent successfully to: {}", vendor.getEmail());

        } catch (MailAuthenticationException e) {
            log.error("Authentication failed for vendor: {}", vendor.getEmail(), e);
            throw new RuntimeException("Email authentication failed for vendor: " + vendor.getEmail() +
                    " - Check your SMTP credentials. Error: " + e.getMessage(), e);
        } catch (MailSendException e) {
            log.error("Failed to send email to vendor: {}", vendor.getEmail(), e);
            throw new RuntimeException("Failed to send email to vendor: " + vendor.getEmail() +
                    " - SMTP error: " + e.getMessage(), e);
        } catch (MailException e) {
            log.error("Mail exception for vendor: {}", vendor.getEmail(), e);
            throw new RuntimeException("Failed to send email to vendor: " + vendor.getEmail() +
                    " - Error: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to vendor: {}", vendor.getEmail(), e);
            throw new RuntimeException("Unexpected error sending email to vendor: " + vendor.getEmail() +
                    " - Error: " + e.getMessage(), e);
        }
    }
//    public void sendRfpToVendor(Rfp rfp, Vendor vendor) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(fromAddress);
//            message.setTo(vendor.getEmail());
//            message.setSubject("RFP: " + rfp.getTitle());
//
//            StringBuilder body = new StringBuilder();
//            body.append("Dear ").append(vendor.getName()).append(",\n\n")
//                    .append("We are inviting you to submit a proposal for the following RFP:\n\n")
//                    .append("Title: ").append(rfp.getTitle()).append("\n")
//                    .append("Budget: ").append(rfp.getBudget()).append("\n")
//                    .append("Delivery timeline (days): ").append(rfp.getDeliveryTimelineDays()).append("\n")
//                    .append("Payment terms: ").append(nullSafe(rfp.getPaymentTerms())).append("\n")
//                    .append("Warranty terms: ").append(nullSafe(rfp.getWarrantyTerms())).append("\n\n")
//                    .append("Items:\n");
//
//            List<RfpItem> items = rfp.getItems();
//            if(items != null && !items.isEmpty()){
//                for (RfpItem item : items) {
//                    body.append(" - ")
//                            .append(item.getItemType())
//                            .append(" | qty: ").append(item.getQuantity())
//                            .append(" | specs: ").append(nullSafe(item.getRequiredSpecs()))
//                            .append("\n");
//                }
//            } else {
//                body.append(" (No items listed)\n");
//            }
//
//            body.append("\n")
//                    .append("Please reply to this email with your proposal, including:\n")
//                    .append(" - Line-item pricing\n")
//                    .append(" - Total price\n")
//                    .append(" - Delivery timeline\n")
//                    .append(" - Payment and warranty terms\n\n")
//                    .append("Best regards,\n")
//                    .append("Procurement Team\n");
//
//            message.setText(body.toString());
//
//            // Send with retry logic
//            mailSender.send(message);
//
//        } catch (MailAuthenticationException e) {
//            throw new RuntimeException("Email authentication failed for vendor: " + vendor.getEmail(), e);
//        } catch (MailException e) {
//            throw new RuntimeException("Failed to send email to vendor: " + vendor.getEmail(), e);
//        }
//    }

    private String nullSafe(String value){
        return value==null?"-":value;
    }
}
