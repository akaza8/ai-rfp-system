package com.ak.rfp.serviceImplementation;

import com.ak.rfp.dto.IngestEmailRequest;
import com.ak.rfp.dto.ProposalItemDto;
import com.ak.rfp.dto.ProposalResponse;
import com.ak.rfp.dto.ProposalScoreDto;
import com.ak.rfp.entity.*;
import com.ak.rfp.repository.*;
import com.ak.rfp.service.AiService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProposalService {
    private static final Logger logger = LoggerFactory.getLogger(ProposalService.class);
    private final ProposalRepository proposalRepository;
    private final ProposalItemRepository proposalItemRepository;
    private final ProposalScoreRepository proposalScoreRepository;
    private final RfpRepository rfpRepository;
    private final VendorRepository vendorRepository;
    private final AiService aiService;
    private final ObjectMapper objectMapper;

    public ProposalService(ProposalRepository proposalRepository, ProposalItemRepository proposalItemRepository, ProposalScoreRepository proposalScoreRepository, RfpRepository rfpRepository, VendorRepository vendorRepository, AiService aiService, ObjectMapper objectMapper) {
        this.proposalRepository = proposalRepository;
        this.proposalItemRepository = proposalItemRepository;
        this.proposalScoreRepository = proposalScoreRepository;
        this.rfpRepository = rfpRepository;
        this.vendorRepository = vendorRepository;
        this.aiService = aiService;
        this.objectMapper = objectMapper;
    }

    public ProposalResponse createFromEmail(IngestEmailRequest request) {
        logger.info("📥 Creating proposal from email - RFP: {}, Vendor: {}",
                request.getRfpId(), request.getVendorId());

        // Get RFP details for context
        Rfp rfp = rfpRepository.findById(request.getRfpId())
                .orElseThrow(() -> new RuntimeException("RFP not found: " + request.getRfpId()));

        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found: " + request.getVendorId()));

        try {
            // Call AI to parse email
            String rfpJson = objectMapper.writeValueAsString(rfp);
            String aiResponse = aiService.parseVendorEmail(rfpJson, request.getEmailBody());

            // Parse AI response
            JsonNode aiJson = objectMapper.readTree(aiResponse);

            // Create proposal
            Proposal proposal = new Proposal();
            proposal.setRfp(rfp);
            proposal.setVendor(vendor);
            proposal.setTotalPrice(new BigDecimal(aiJson.path("totalPrice").asDouble()));
            proposal.setCurrency(aiJson.path("currency").asText("USD"));
            proposal.setDeliveryDays(aiJson.path("deliveryDays").asInt());
            proposal.setPaymentTerms(aiJson.path("paymentTerms").asText());
            proposal.setWarrantyTerms(aiJson.path("warrantyTerms").asText());

            Proposal savedProposal = proposalRepository.save(proposal);

            // Create proposal items
            List<ProposalItem> items = new ArrayList<>();
            aiJson.path("items").forEach(itemNode -> {
                ProposalItem item = new ProposalItem();
                item.setProposal(savedProposal);
                item.setForRfpItemId(itemNode.path("forRfpItemId").asLong());
                item.setQuantity(itemNode.path("quantity").asInt());
                item.setUnitPrice(new BigDecimal(itemNode.path("unitPrice").asDouble()));
                item.setTotalPrice(new BigDecimal(itemNode.path("totalPrice").asDouble()));
                items.add(proposalItemRepository.save(item));
            });

            logger.info("✅ Proposal created: ID={}, Items={}", savedProposal.getId(), items.size());

            // Convert to DTO
            ProposalResponse dto = new ProposalResponse();
            dto.setId(savedProposal.getId());
            dto.setRfpId(savedProposal.getRfp().getId());
            dto.setVendorId(savedProposal.getVendor().getId());
            dto.setVendorName(savedProposal.getVendor().getName());
            dto.setTotalPrice(savedProposal.getTotalPrice());
            dto.setCurrency(savedProposal.getCurrency());
            dto.setDeliveryDays(savedProposal.getDeliveryDays());
            dto.setPaymentTerms(savedProposal.getPaymentTerms());
            dto.setWarrantyTerms(savedProposal.getWarrantyTerms());

            List<ProposalItemDto> itemDtos = new ArrayList<>();
            for (ProposalItem item : items) {
                ProposalItemDto itemDto = new ProposalItemDto();
                itemDto.setId(item.getId());
                itemDto.setForRfpItemId(item.getForRfpItemId());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUnitPrice(item.getUnitPrice());
                itemDto.setTotalPrice(item.getTotalPrice());
                itemDtos.add(itemDto);
            }
            dto.setItems(itemDtos);

            return dto;

        } catch (Exception e) {
            logger.error("❌ Error creating proposal from email", e);
            throw new RuntimeException("Failed to parse vendor email: " + e.getMessage());
        }
    }

    /**
     * Score all proposals for an RFP and store results
     */
    @Transactional
    public List<ProposalScoreDto> scoreProposalsForRfp(Long rfpId) {
        logger.info("🎯 Scoring proposals for RFP: {}", rfpId);

        // Get RFP and all proposals
        Rfp rfp = rfpRepository.findById(rfpId)
                .orElseThrow(() -> new RuntimeException("RFP not found"));

        List<Proposal> proposals = proposalRepository.findByRfpId(rfpId);
        if (proposals.isEmpty()) {
            throw new RuntimeException("No proposals found for RFP: " + rfpId);
        }

        try {
            // Prepare data for AI
            String rfpJson = objectMapper.writeValueAsString(rfp);
            String proposalsJson = objectMapper.writeValueAsString(proposals);

            // Call AI to score
            String aiResponse = aiService.scoreProposals(rfpJson, proposalsJson);
            JsonNode scoresJson = objectMapper.readTree(aiResponse);

            // Clear old scores
            proposalScoreRepository.deleteByProposalRfpId(rfpId);

            // Store new scores
            List<ProposalScoreDto> scoreDtos = new ArrayList<>();
            for (JsonNode scoreNode : scoresJson) {
                Long proposalId = scoreNode.path("proposalId").asLong();
                Proposal proposal = proposals.stream()
                        .filter(p -> p.getId().equals(proposalId))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Proposal not found: " + proposalId));

                ProposalScore score = new ProposalScore();
                score.setProposal(proposal);
                score.setOverallScore(scoreNode.path("overallScore").asDouble());
                score.setPriceScore(scoreNode.path("priceScore").asDouble());
                score.setTimelineScore(scoreNode.path("timelineScore").asDouble());
                score.setQualityScore(scoreNode.path("qualityScore").asDouble());
                score.setExplanation(scoreNode.path("explanation").asText());

                ProposalScore savedScore = proposalScoreRepository.save(score);

                // Create DTO
                ProposalScoreDto dto = new ProposalScoreDto();
                dto.setProposalId(savedScore.getProposal().getId());
                dto.setOverallScore(savedScore.getOverallScore());
                dto.setPriceScore(savedScore.getPriceScore());
                dto.setTimelineScore(savedScore.getTimelineScore());
                dto.setQualityScore(savedScore.getQualityScore());
                dto.setExplanation(savedScore.getExplanation());
                scoreDtos.add(dto);
            }

            logger.info("✅ Proposals scored: {} scores stored", scoreDtos.size());
            return scoreDtos;

        } catch (Exception e) {
            logger.error("❌ Error scoring proposals", e);
            throw new RuntimeException("Failed to score proposals: " + e.getMessage());
        }
    }

    /**
     * Get proposals for RFP (with scores if available)
     */
    public List<ProposalResponse> getProposalsForRfp(Long rfpId) {
        List<Proposal> proposals = proposalRepository.findByRfpId(rfpId);
        List<ProposalResponse> dtos = new ArrayList<>();

        for (Proposal proposal : proposals) {
            ProposalResponse dto = new ProposalResponse();
            dto.setId(proposal.getId());
            dto.setRfpId(proposal.getRfp().getId());
            dto.setVendorId(proposal.getVendor().getId());
            dto.setVendorName(proposal.getVendor().getName());
            dto.setTotalPrice(proposal.getTotalPrice());
            dto.setCurrency(proposal.getCurrency());
            dto.setDeliveryDays(proposal.getDeliveryDays());
            dto.setPaymentTerms(proposal.getPaymentTerms());
            dto.setWarrantyTerms(proposal.getWarrantyTerms());

            List<ProposalItem> items = proposalItemRepository.findByProposalId(proposal.getId());
            List<ProposalItemDto> itemDtos = new ArrayList<>();
            for (ProposalItem item : items) {
                ProposalItemDto itemDto = new ProposalItemDto();
                itemDto.setId(item.getId());
                itemDto.setForRfpItemId(item.getForRfpItemId());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setUnitPrice(item.getUnitPrice());
                itemDto.setTotalPrice(item.getTotalPrice());
                itemDtos.add(itemDto);
            }
            dto.setItems(itemDtos);

            dtos.add(dto);
        }
        return dtos;
    }
}
