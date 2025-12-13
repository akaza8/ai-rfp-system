package com.ak.rfp.service;

import com.ak.rfp.dto.RfpCreateRequest;
import com.ak.rfp.dto.RfpFromTextRequest;
import com.ak.rfp.dto.RfpResponse;
import com.ak.rfp.dto.VendorInvitationResponse;
import com.ak.rfp.entity.Rfp;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RfpService {

    RfpResponse createRfp(RfpCreateRequest request);

    List<RfpResponse> getAllRfps();

    void deleteRfp(Long id);

    RfpResponse generateRfpFromText(RfpFromTextRequest request);

    List<String> sendRfpToVendors(Long rfpId, List<Long> vendorIds);

    RfpResponse getRfpById(Long id);

    List<VendorInvitationResponse> getVendorInvitations(Long id);
}
