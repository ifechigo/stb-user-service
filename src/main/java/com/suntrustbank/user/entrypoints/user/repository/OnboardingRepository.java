package com.suntrustbank.user.entrypoints.user.repository;

import com.suntrustbank.user.entrypoints.user.repository.models.Onboarding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnboardingRepository extends JpaRepository<Onboarding, Long> {
    Optional<Onboarding> findByPhoneNumber(String phoneNumber);
    Optional<Onboarding> findByReference(String reference);
}
