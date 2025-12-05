package com.ak.rfp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "rfp_item")
public class RfpItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="item_type")
    private String itemType;

    @Column(name="quantity")
    private Integer quantity;

    @Column(name="required_specs")
    private String requiredSpecs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfp_id")
    @JsonBackReference
    private Rfp rfp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getRequiredSpecs() {
        return requiredSpecs;
    }

    public void setRequiredSpecs(String requiredSpecs) {
        this.requiredSpecs = requiredSpecs;
    }

    public Rfp getRfp() {
        return rfp;
    }

    public void setRfp(Rfp rfp) {
        this.rfp = rfp;
    }
}
