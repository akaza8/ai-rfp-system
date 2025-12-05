package com.ak.rfp.repository;

import com.ak.rfp.entity.RfpVendorInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RfpVendorInvitationRepository extends JpaRepository<RfpVendorInvitation, Long> {
    List<RfpVendorInvitation> findByRfpId(Long rfpId);
}
