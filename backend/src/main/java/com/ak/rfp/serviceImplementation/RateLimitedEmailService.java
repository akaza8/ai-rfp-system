package com.ak.rfp.serviceImplementation;

import com.ak.rfp.entity.InvitationStatus;
import com.ak.rfp.entity.Rfp;
import com.ak.rfp.entity.RfpVendorInvitation;
import com.ak.rfp.entity.Vendor;
import com.ak.rfp.repository.RfpVendorInvitationRepository;
import com.ak.rfp.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

@Service
public class RateLimitedEmailService {
    private static final Logger log = LoggerFactory.getLogger(RateLimitedEmailService.class);
    private final EmailService emailService;
    private final RfpVendorInvitationRepository rfpVendorInvitationRepository;
//    private final Semaphore rateLimiter = new Semaphore(1);

    @Autowired
    public RateLimitedEmailService(EmailService emailService, RfpVendorInvitationRepository rfpVendorInvitationRepository) {
        this.emailService = emailService;
        this.rfpVendorInvitationRepository = rfpVendorInvitationRepository;
    }

    public void sendEmailWithRateLimiter(Rfp rfp, Vendor vendor) throws InterruptedException {
        try{
            emailService.sendRfpToVendor(rfp, vendor);
            RfpVendorInvitation invitation = new RfpVendorInvitation();
            invitation.setVendor(vendor);
            invitation.setRfp(rfp);
            invitation.setStatus(InvitationStatus.SENT);
            invitation.setSentAt(LocalDateTime.now());
            rfpVendorInvitationRepository.save(invitation);
            log.info("Emails sent successfully ... ");
            Thread.sleep(5000);
        }
        finally {

        }
    }
}
