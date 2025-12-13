package com.ak.rfp.dto;

import com.ak.rfp.entity.InvitationStatus;
import com.ak.rfp.entity.Rfp;
import com.ak.rfp.entity.Vendor;

import java.time.LocalDateTime;

public class VendorInvitationResponse {
    private Long id;
    private Long vendorId;
    private Long rfpId;
    private InvitationStatus status;
    private LocalDateTime sentAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Long getRfpId() {
        return rfpId;
    }

    public void setRfpId(Long rfpId) {
        this.rfpId = rfpId;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
