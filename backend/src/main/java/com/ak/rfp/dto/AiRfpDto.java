package com.ak.rfp.dto;
import java.util.List;

public class AiRfpDto {

    private String title;
    private Integer budget;
    private Integer deliveryTimelineDays;
    private String paymentTerms;
    private String warrantyTerms;
    private List<AiRfpItemDto> items;

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

    public List<AiRfpItemDto> getItems() {
        return items;
    }

    public void setItems(List<AiRfpItemDto> items) {
        this.items = items;
    }

    public static class AiRfpItemDto {
        private String itemType;
        private Integer quantity;
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

