package com.suntrustbank.user.entrypoints.repository;

import com.suntrustbank.user.entrypoints.repository.enums.OnboardingStatus;
import com.suntrustbank.user.entrypoints.repository.models.Business;
import com.suntrustbank.user.entrypoints.repository.models.Onboarding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OnboardingRepository extends JpaRepository<Onboarding, Long> {
    Optional<Onboarding> findByPhoneNumber(String phoneNumber);
    Optional<Onboarding> findByReference(String reference);
}
