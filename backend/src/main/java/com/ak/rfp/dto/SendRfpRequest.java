package com.ak.rfp.dto;

import java.util.List;

public class SendRfpRequest {

    private List<Long> vendorIds;

    public List<Long> getVendorIds() {
        return vendorIds;
    }

    public void setVendorIds(List<Long> vendorIds) {
        this.vendorIds = vendorIds;
    }
}
