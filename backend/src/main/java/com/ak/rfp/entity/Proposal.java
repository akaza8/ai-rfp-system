package com.ak.rfp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "proposal")
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfp_id")
    private Rfp rfp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @Column(name = "currency")
    private String currency;
    @Column(name = "delivery_days")
    private Integer deliveryDays;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "warranty_terms")
    private String warrantyTerms;

//    @Lob
//    @Column(name = "raw_email_body")
//    private String rawEmailBody;
//
//    @Lob
//    @Column(name = "ai_parsed_json")
//    private String aiParsedJson;


    @OneToMany(
            mappedBy = "proposal",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<ProposalItem> items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Rfp getRfp() {
        return rfp;
    }

    public void setRfp(Rfp rfp) {
        this.rfp = rfp;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
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

    public List<ProposalItem> getItems() {
        return items;
    }

    public void setItems(List<ProposalItem> items) {
        this.items = items;
    }

//    public String getRawEmailBody() {
//        return rawEmailBody;
//    }
//
//    public void setRawEmailBody(String rawEmailBody) {
//        this.rawEmailBody = rawEmailBody;
//    }
//
//    public String getAiParsedJson() {
//        return aiParsedJson;
//    }
//
//    public void setAiParsedJson(String aiParsedJson) {
//        this.aiParsedJson = aiParsedJson;
//    }
}
