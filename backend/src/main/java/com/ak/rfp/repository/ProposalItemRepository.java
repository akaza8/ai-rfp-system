package com.ak.rfp.repository;

import com.ak.rfp.entity.ProposalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalItemRepository extends JpaRepository<ProposalItem, Long> {

    List<ProposalItem> findByProposalId(Long id);
}
