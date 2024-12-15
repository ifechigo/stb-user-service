package com.suntrustbank.user.entrypoints.repository;

import com.suntrustbank.user.entrypoints.repository.enums.OnboardingStatus;
import com.suntrustbank.user.entrypoints.repository.models.Business;
import com.suntrustbank.user.entrypoints.repository.models.Onboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OnboardingRepository extends JpaRepository<Onboarding, String> {
    Optional<Onboarding> findByPhoneNumber(String phoneNumber);

    @Transactional
    @Modifying
    @Query("UPDATE onboardings o SET o.status = :status, o.updatedAt = CURRENT_TIMESTAMP WHERE o.id = :id")
    int updateStatusById(String id, OnboardingStatus status);
}
