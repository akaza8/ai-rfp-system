package com.ak.rfp.service;

public interface AiService {

    /**
     * Generate RFP from natural language description
     *
     * @param description Natural language description (e.g., "I need 20 laptops with 16GB RAM...")
     * @return Structured RFP as JSON string
     */
    String generateRfpFromText(String description);

    /**
     * Parse vendor email response into structured proposal
     *
     * @param rfpRequirements The original RFP requirements
     * @param emailBody The vendor's email response
     * @return Structured proposal as JSON string
     */
    String parseVendorEmail(String rfpRequirements, String emailBody);

    /**
     * Score and compare proposals with AI
     *
     * @param rfpDetails The RFP details
     * @param proposals List of proposals to score
     * @return Scoring results as JSON string
     */
    String scoreProposals(String rfpDetails, String proposals);

    /**
     * Check if API is configured
     */
    boolean isConfigured();

    /**
     * Get current model
     */
    String getModel();
}