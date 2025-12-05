package com.ak.rfp.service;

import com.ak.rfp.dto.RfpCreateRequest;
import com.ak.rfp.dto.RfpResponse;
import com.ak.rfp.entity.Rfp;

import java.util.List;

public interface RfpService {

    RfpResponse createRfp(RfpCreateRequest request);

    List<RfpResponse> getAllRfps();

    void deleteRfp(Long id);
}
