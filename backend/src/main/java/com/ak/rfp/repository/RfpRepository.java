package com.ak.rfp.repository;

import com.ak.rfp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RfpRepository extends JpaRepository<Rfp, Long> {}

