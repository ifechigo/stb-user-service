package com.suntrustbank.user.entrypoints.organizationuser.repository.models;

import com.suntrustbank.user.entrypoints.organizationuser.repository.enums.OrganizationRole;
import com.suntrustbank.user.entrypoints.user.repository.enums.Status;
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
@Entity(name = "organization_users")
public class OrganizationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String reference;

    private String firstName;

    private String lastName;

    private String email;

    private String countryCode;

    private String phoneNumber;

    private String profilePhoto;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrganizationRole role;

    private boolean isTeamLead;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
