package com.ak.rfp.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProposalResponse {
    private Long id;
    private Long rfpId;
    private Long vendorId;
    private String vendorName;
    private BigDecimal totalPrice;
    private String currency;
    private Integer deliveryDays;
    private String paymentTerms;
    private String warrantyTerms;
    private List<ProposalItemDto> items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRfpId() {
        return rfpId;
    }

    public void setRfpId(Long rfpId) {
        this.rfpId = rfpId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getDeliveryDays() {
        return deliveryDays;
    }

    public void setDeliveryDays(Integer deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getWarrantyTerms() {
        return warrantyTerms;
    }

    public void setWarrantyTerms(String warrantyTerms) {
        this.warrantyTerms = warrantyTerms;
    }

    public List<ProposalItemDto> getItems() {
        return items;
    }

    public void setItems(List<ProposalItemDto> items) {
        this.items = items;
    }
}
