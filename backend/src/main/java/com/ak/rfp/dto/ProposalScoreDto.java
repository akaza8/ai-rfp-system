package com.ak.rfp.dto;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"proposalId", "overallScore", "priceScore", "timelineScore", "qualityScore", "explanation"})
public class ProposalScoreDto {
    private Long proposalId;
    private Double overallScore;
    private Double priceScore;
    private Double timelineScore;
    private Double qualityScore;
    private String explanation;

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public Double getPriceScore() {
        return priceScore;
    }

    public void setPriceScore(Double priceScore) {
        this.priceScore = priceScore;
    }

    public Double getTimelineScore() {
        return timelineScore;
    }

    public void setTimelineScore(Double timelineScore) {
        this.timelineScore = timelineScore;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
