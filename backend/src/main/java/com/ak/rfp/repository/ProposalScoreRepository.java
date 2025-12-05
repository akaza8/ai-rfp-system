package com.ak.rfp.repository;

import com.ak.rfp.entity.ProposalScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProposalScoreRepository extends JpaRepository<ProposalScore, Long> {
    List<ProposalScore> findByProposalId(Long proposalId);
}
