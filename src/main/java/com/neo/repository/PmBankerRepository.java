package com.neo.repository;

import com.neo.modal.Banker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PmBankerRepository extends JpaRepository<Banker, Long> {
}
