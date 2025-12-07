package com.ak.rfp.controller;

import com.ak.rfp.dto.RfpCreateRequest;
import com.ak.rfp.dto.RfpFromTextRequest;
import com.ak.rfp.dto.RfpResponse;
import com.ak.rfp.dto.SendRfpRequest;
import com.ak.rfp.service.AiService;
import com.ak.rfp.service.RfpService;
import jakarta.validation.Valid;
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

    public RfpController(RfpService rfpService) {
        this.rfpService = rfpService;
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
    public ResponseEntity<Void> sendRfp(@PathVariable Long id, @RequestBody SendRfpRequest request){
        System.out.println("Received vendorIds: " + request.getVendorIds());
        System.out.println("Is null? " + (request.getVendorIds() == null));
        rfpService.sendRfpToVendors(id, request.getVendorIds());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
