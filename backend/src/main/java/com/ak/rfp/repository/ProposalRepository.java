package com.ak.rfp.repository;

import com.ak.rfp.entity.Proposal;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    @Query("SELECT p FROM Proposal p WHERE p.rfp.id = :rfpId")
    List<Proposal> findByRfpId(Long rfpId);
    List<Proposal> findByRfpIdAndVendorId(Long rfpId, Long vendorId);
}
