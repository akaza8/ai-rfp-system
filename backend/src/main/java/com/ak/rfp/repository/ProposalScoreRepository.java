package com.ak.rfp.repository;

import com.ak.rfp.entity.ProposalScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProposalScoreRepository extends JpaRepository<ProposalScore, Long> {
    List<ProposalScore> findByProposalId(Long proposalId);
    @Query("SELECT ps FROM ProposalScore ps WHERE ps.proposal.rfp.id = :rfpId")
    List<ProposalScore> findByProposalRfpId(@Param("rfpId") Long rfpId);
    void deleteByProposalRfpId(Long rfpId);
}
