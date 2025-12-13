package com.ak.rfp.controller;

import com.ak.rfp.dto.IngestEmailRequest;
import com.ak.rfp.dto.ProposalResponse;
import com.ak.rfp.serviceImplementation.ProposalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@RestController
@RequestMapping("/api/proposals")
public class ProposalController {
    private static final Logger logger = LoggerFactory.getLogger(ProposalController.class);
    private final ProposalService proposalService;

    public ProposalController(ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    @PostMapping("/ingest-email")
    public ResponseEntity<ProposalResponse> ingestVendorEmail(
            @RequestBody IngestEmailRequest request) {

        logger.info("📥 POST /api/proposals/ingest-email - RFP: {}, Vendor: {}",
                request.getRfpId(), request.getVendorId());

        try {
            ProposalResponse proposal = proposalService.createFromEmail(request);
            logger.info("✅ Proposal created successfully: {}", proposal.getId());
            return ResponseEntity.ok(proposal);
        } catch (Exception e) {
            logger.error("❌ Error ingesting email", e);
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/rfp/{rfpId}")
    public ResponseEntity<List<ProposalResponse>> getProposalsForRfp(@PathVariable Long rfpId) {
        logger.info("📋 GET /api/proposals/rfp/{} - List proposals", rfpId);
        return ResponseEntity.ok(proposalService.getProposalsForRfp(rfpId));
    }
}
