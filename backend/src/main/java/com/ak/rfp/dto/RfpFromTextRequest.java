package com.ak.rfp.dto;

public class RfpFromTextRequest {
    /**
     * Free-form description from procurement manager.
     * Example:
     * "We need 20 laptops with 16GB RAM and 24-inch monitors, delivery in 30 days, budget 5 lakhs..."
     */
    private String description;

    public RfpFromTextRequest(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
