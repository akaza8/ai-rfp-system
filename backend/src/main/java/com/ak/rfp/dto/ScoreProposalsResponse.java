package com.ak.rfp.dto;

import java.util.List;

public class ScoreProposalsResponse {
    private List<ProposalScoreDto> scores;
    private Long recommendedProposalId;
    private String recommendedVendorName;
    private String explanation;

    public List<ProposalScoreDto> getScores() {
        return scores;
    }

    public void setScores(List<ProposalScoreDto> scores) {
        this.scores = scores;
    }

    public Long getRecommendedProposalId() {
        return recommendedProposalId;
    }

    public void setRecommendedProposalId(Long recommendedProposalId) {
        this.recommendedProposalId = recommendedProposalId;
    }

    public String getRecommendedVendorName() {
        return recommendedVendorName;
    }

    public void setRecommendedVendorName(String recommendedVendorName) {
        this.recommendedVendorName = recommendedVendorName;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
