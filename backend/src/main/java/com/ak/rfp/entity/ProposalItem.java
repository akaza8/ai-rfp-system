package com.ak.rfp.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "proposal_item")
public class ProposalItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Column(name="for_rfp_item_id")
    private Long forRfpItemId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name="unit_price")
    private BigDecimal unitPrice;

    @Column(name="total_price")
    private BigDecimal totalPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Proposal getProposal() {
        return proposal;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    public Long getForRfpItemId() {
        return forRfpItemId;
    }

    public void setForRfpItemId(Long forRfpItemId) {
        this.forRfpItemId = forRfpItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
