package com.suntrustbank.user.entrypoints.user.repository;

import com.suntrustbank.user.entrypoints.user.repository.models.CashPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashPointRepository extends JpaRepository<CashPoint, Long> {
}
