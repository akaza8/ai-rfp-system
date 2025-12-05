package com.ak.rfp.service;

import com.ak.rfp.dto.VendorRequest;
import com.ak.rfp.dto.VendorResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface VendorService {
    VendorResponse createVendor(VendorRequest request);

    List<VendorResponse> getAllVendors();

    void deleteVendor(Long id);

    VendorResponse updateVendor(Long id, @Valid Map<String, Object> updates);
}
