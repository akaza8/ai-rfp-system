package com.ak.rfp.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "proposal_score")
public class ProposalScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Column(name="overall_score")
    private Double overallScore;

    @Column(name="price_score")
    private Double priceScore;

    @Column(name="timeline_score")
    private Double timelineScore;

    @Column(name="quality_score")
    private Double qualityScore;

    private String explanation;

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
