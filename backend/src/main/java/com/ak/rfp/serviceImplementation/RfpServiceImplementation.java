package com.ak.rfp.serviceImplementation;
import com.ak.rfp.dto.RfpCreateRequest;
import com.ak.rfp.dto.RfpFromTextRequest;
import com.ak.rfp.dto.RfpResponse;
import com.ak.rfp.entity.*;
import com.ak.rfp.mapper.RfpMapper;
import com.ak.rfp.repository.RfpRepository;
import com.ak.rfp.repository.RfpVendorInvitationRepository;
import com.ak.rfp.repository.VendorRepository;
import com.ak.rfp.service.AiService;
import com.ak.rfp.service.EmailService;
import com.ak.rfp.service.RfpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RfpServiceImplementation implements RfpService {
    private static final Logger logger = LoggerFactory.getLogger(AiService.class);
    private final RfpRepository rfpRepository;
    private final RfpMapper rfpMapper;
    private final ObjectMapper objectMapper;
    private final AiService aiService;
    private final EmailService emailService;
    private final VendorRepository vendorRepository;
    private final RfpVendorInvitationRepository invitationRepository;

    public RfpServiceImplementation(RfpRepository rfpRepository, VendorRepository vendorRepository, RfpVendorInvitationRepository invitationRepository, RfpMapper rfpMapper, ObjectMapper objectMapper, AiService aiService, EmailService emailService) {
        this.rfpRepository = rfpRepository;
        this.vendorRepository = vendorRepository;
        this.invitationRepository = invitationRepository;
        this.rfpMapper = rfpMapper;
        this.objectMapper = objectMapper;
        this.aiService = aiService;
        this.emailService = emailService;
    }
    @Transactional
    @Override
    public RfpResponse createRfp(RfpCreateRequest request) {
        Rfp rfp = new Rfp();
        rfp.setTitle(request.getTitle());
        rfp.setBudget(request.getBudget());
        rfp.setDeliveryTimelineDays(request.getDeliveryTimelineDays());
        rfp.setPaymentTerms(request.getPaymentTerms());
        rfp.setWarrantyTerms(request.getWarrantyTerms());
        List<RfpItem> item = new ArrayList<>();
        for (RfpCreateRequest.RfpItemRequest rfpItemRequest : request.getItems()) {
            RfpItem rfpItem = new RfpItem();
            rfpItem.setItemType(rfpItemRequest.getItemType());
            rfpItem.setQuantity(rfpItemRequest.getQuantity());
            rfpItem.setRequiredSpecs(rfpItemRequest.getRequiredSpecs());
            rfpItem.setRfp(rfp);
            item.add(rfpItem);
        }
        rfp.setItems(item);
        Rfp saved = rfpRepository.save(rfp);
        return rfpMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RfpResponse> getAllRfps() {
        List<Rfp> rfps = rfpRepository.findAll();
        return rfps.stream().map(rfpMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void deleteRfp(Long id) {
        if (!rfpRepository.existsById(id)) {
            throw new RuntimeException("RFP not found");
        }
        rfpRepository.deleteById(id);
    }

    @Transactional
    @Override
    public RfpResponse generateRfpFromText(RfpFromTextRequest request) {
        try {
            // Check if AI is configured
            if (!aiService.isConfigured()) {
               throw new RuntimeException("AI service not configured. Set PERPLEXITY_API_KEY environment variable.");
            }

            // Call AI to generate structured RFP
            String aiResponse = aiService.generateRfpFromText(request.getDescription());

            // Parse JSON response
            Map<String, Object> rfpData = objectMapper.readValue(aiResponse, Map.class);
            logger.debug("Parsed RFP data: {}", rfpData);
            String title = (String) rfpData.getOrDefault("title", "");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) rfpData.getOrDefault("items", List.of());

            if (title.isEmpty() && items.isEmpty()) {
                throw new IllegalArgumentException("AI returned empty RFP. Description too vague—add details like quantity, specs.");
            }

            if (items.isEmpty()) {
                logger.warn("No items generated—adding default");
                items = List.of(Map.of("itemType", "Generic Item", "quantity", 1, "requiredSpecs", "Basic"));
            }

            RfpResponse rfp = createRfpAi(rfpData);
            return rfp;

        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error generating RFP: " + e.getMessage());
            throw new RuntimeException("Error generating RFP: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void sendRfpToVendors(Long rfpId, List<Long> vendorIds) {
        Rfp rfp = rfpRepository.findById(rfpId)
                .orElseThrow(() -> new IllegalArgumentException("RFP not found: " + rfpId));

        if(vendorIds == null || vendorIds.isEmpty()) {
            throw new IllegalArgumentException("No vendors provided");
        }

        List<Vendor> vendors = vendorRepository.findAllById(vendorIds);
        if(vendors.size() != vendorIds.size()) {
            throw new IllegalArgumentException("Some vendors not found");
        }

        LocalDateTime sentTime = LocalDateTime.now();
        List<String> failedVendors = new ArrayList<>();

        for(int i = 0; i < vendors.size(); i++) {
            Vendor vendor = vendors.get(i);
            try {
                // Send email
                emailService.sendRfpToVendor(rfp, vendor);

                // Save invitation record
                RfpVendorInvitation invitation = new RfpVendorInvitation();
                invitation.setVendor(vendor);
                invitation.setRfp(rfp);
                invitation.setStatus(InvitationStatus.SENT);
                invitation.setSentAt(sentTime);
                invitationRepository.save(invitation);

                // Wait between emails (except for last one)
                if(i < vendors.size() - 1) {
                    Thread.sleep(5000); // Increased to 3 seconds for Mailtrap
                }

            } catch (Exception e) {
                // Log the error but continue with other vendors
                failedVendors.add(vendor.getEmail() + ": " + e.getMessage());

                // Still save the invitation with FAILED status
                RfpVendorInvitation invitation = new RfpVendorInvitation();
                invitation.setVendor(vendor);
                invitation.setRfp(rfp);
                invitation.setStatus(InvitationStatus.FAILED); // Add this status to your enum
                invitation.setSentAt(sentTime);
                invitationRepository.save(invitation);
            }
        }

        if(!failedVendors.isEmpty()) {
            throw new RuntimeException("Failed to send emails to: " + String.join(", ", failedVendors));
        }
    }

    @Transactional
    private RfpResponse createRfpAi(Map<String, Object> rfpData) {
        Rfp rfp = new Rfp();
        rfp.setTitle((String) rfpData.getOrDefault("title", ""));  // Default empty if missing
        rfp.setBudget((Integer) rfpData.getOrDefault("budget", 0));  // Safe default
        rfp.setDeliveryTimelineDays((Integer) rfpData.getOrDefault("deliveryTimelineDays", 0));
        rfp.setPaymentTerms((String) rfpData.getOrDefault("paymentTerms", ""));
        rfp.setWarrantyTerms((String) rfpData.getOrDefault("warrantyTerms", ""));

        // Fix: Safe handling for items
        List<RfpItem> items = new ArrayList<>();
        Object itemsObj = rfpData.get("items");
        if (itemsObj instanceof List) {
            List<Map<String, Object>> itemsList = (List<Map<String, Object>>) itemsObj;
            for (Map<String, Object> itemData : itemsList) {
                RfpItem item = new RfpItem();
                item.setItemType((String) itemData.getOrDefault("itemType", ""));
                item.setQuantity((Integer) itemData.getOrDefault("quantity", 0));
                item.setRequiredSpecs((String) itemData.getOrDefault("requiredSpecs", ""));
                item.setRfp(rfp);
                items.add(item);
            }
        } else if (itemsObj != null) {
            logger.warn("Items field is not a List: {}", itemsObj.getClass());  // Log type mismatch
        } else {
            logger.info("No 'items' in AI response—using empty list");
        }
        rfp.setItems(items);

        // Save and map
        Rfp savedRfp = rfpRepository.save(rfp);
        return rfpMapper.toResponse(savedRfp);
    }

}
