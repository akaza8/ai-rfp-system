package com.ak.rfp.controller;

import com.ak.rfp.dto.*;
import com.ak.rfp.service.AiService;
import com.ak.rfp.service.RfpService;
import com.ak.rfp.serviceImplementation.ProposalService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/rfps")
public class RfpController {
    private final RfpService rfpService;
    private static final Logger logger = LoggerFactory.getLogger(AiService.class);
    private final ProposalService proposalService;
    public RfpController(RfpService rfpService, ProposalService proposalService) {
        this.rfpService = rfpService;
        this.proposalService = proposalService;
    }

    @PostMapping
    public ResponseEntity<RfpResponse> create(@Valid @RequestBody RfpCreateRequest request) {
        RfpResponse created = rfpService.createRfp(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RfpResponse>> getAllRfps() {
        List<RfpResponse> rfpResponses = rfpService.getAllRfps();
        return ResponseEntity.ok(rfpResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RfpResponse> getRfpById(@PathVariable Long id) {
        RfpResponse rfpResponse = rfpService.getRfpById(id);
        return ResponseEntity.ok(rfpResponse);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rfpService.deleteRfp(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/from-text")
    public ResponseEntity<?> generateRfpFromText(@Valid @RequestBody RfpFromTextRequest request) {
        try {
            RfpResponse created = rfpService.generateRfpFromText(request);
            if (created.getTitle() == null || created.getTitle().isEmpty()) {
                return ResponseEntity.badRequest().body("Empty RFP—provide more details in description.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            logger.error("Generation failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<List<String>> sendRfp(@PathVariable Long id, @RequestBody SendRfpRequest request){
        System.out.println("Received vendorIds: " + request.getVendorIds());
        System.out.println("Is null? " + (request.getVendorIds() == null));
        List<String> f = rfpService.sendRfpToVendors(id, request.getVendorIds());
        return new ResponseEntity<>(f,HttpStatus.OK);
    }

    @GetMapping("/{id}/invitations")
    public ResponseEntity<List<VendorInvitationResponse>> getVendorInvitations(@PathVariable Long id){
        List<VendorInvitationResponse> vendorInvitations = rfpService.getVendorInvitations(id);
        return new ResponseEntity<>(vendorInvitations,HttpStatus.OK);
    }

    @GetMapping("/{id}/score-proposals")
    public ResponseEntity<List<ProposalScoreDto>> scoreProposals(@PathVariable Long id) {
        logger.info("🎯 POST /api/rfps/{}/score-proposals - AI scoring", id);

        try {
            List<ProposalScoreDto> scores = proposalService.scoreProposalsForRfp(id);
            logger.info("✅ Proposals scored successfully: {} scores", scores.size());
            return ResponseEntity.ok(scores);
        } catch (Exception e) {
            logger.error("❌ Error scoring proposals", e);
            return ResponseEntity.badRequest().build();
        }
    }


}
