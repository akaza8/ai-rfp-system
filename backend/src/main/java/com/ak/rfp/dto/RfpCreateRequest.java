package com.ak.rfp.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public class RfpCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, message = "Title must be at least 5 characters")
    private String title;
    @NotNull(message = "Budget is required")
    @PositiveOrZero(message = "Budget must be positive or zero")
    private Integer budget;
    @NotNull(message = "Delivery timeline days is required")
    @PositiveOrZero(message = "Delivery timeline days must be positive or zero")
    private Integer deliveryTimelineDays;
    private String paymentTerms;
    private String warrantyTerms;
    private List<RfpItemRequest> items;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public Integer getDeliveryTimelineDays() {
        return deliveryTimelineDays;
    }

    public void setDeliveryTimelineDays(Integer deliveryTimelineDays) {
        this.deliveryTimelineDays = deliveryTimelineDays;
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

    public List<RfpItemRequest> getItems() {
        return items;
    }

    public void setItems(List<RfpItemRequest> items) {
        this.items = items;
    }

    public static class RfpItemRequest {
        @NotNull(message = "Item type is required")
        private String itemType;
        @NotNull(message = "Quantity is required")
        private Integer quantity;
        @NotNull(message = "Required specs is required")
        private String requiredSpecs;

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
    }
}
