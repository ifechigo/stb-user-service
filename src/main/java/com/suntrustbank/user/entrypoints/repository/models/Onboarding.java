package com.suntrustbank.user.entrypoints.repository.models;


import com.suntrustbank.user.entrypoints.repository.enums.OnboardingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "onboardings")
public class Onboarding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String reference;

    @Column(nullable = false, updatable = false)
    private String countryCode;

    @Column(nullable = false, updatable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private OnboardingStatus status;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
