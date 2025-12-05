package com.ak.rfp.controller;

import com.ak.rfp.dto.RfpCreateRequest;
import com.ak.rfp.dto.RfpResponse;
import com.ak.rfp.service.RfpService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rfps")
public class RfpController {
    private final RfpService rfpService;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rfpService.deleteRfp(id);
        return ResponseEntity.ok().build();
    }

}
