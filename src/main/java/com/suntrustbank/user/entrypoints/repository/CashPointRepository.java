package com.suntrustbank.user.entrypoints.repository;

import com.suntrustbank.user.entrypoints.repository.models.Business;
import com.suntrustbank.user.entrypoints.repository.models.CashPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CashPointRepository extends JpaRepository<CashPoint, String> {
}
