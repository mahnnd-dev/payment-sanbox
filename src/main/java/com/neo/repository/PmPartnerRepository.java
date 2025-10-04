package com.neo.repository;

import com.neo.modal.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PmPartnerRepository extends JpaRepository<Partner, Long> {
    Partner findAllByTmnCode(String tmnCode);
}
