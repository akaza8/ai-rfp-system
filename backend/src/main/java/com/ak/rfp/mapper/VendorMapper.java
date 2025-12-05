package com.ak.rfp.mapper;

import com.ak.rfp.dto.VendorResponse;
import com.ak.rfp.entity.Vendor;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

@Component
public class VendorMapper {
    public VendorResponse toResponse(Vendor vendor) {
        VendorResponse response = new VendorResponse();
        response.setId(vendor.getId());
        response.setName(vendor.getName());
        response.setEmail(vendor.getEmail());
        return response;
    }
}
