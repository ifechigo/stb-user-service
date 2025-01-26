package com.suntrustbank.user.entrypoints.adminuser.repository.models;

import com.suntrustbank.user.entrypoints.adminuser.repository.enums.AdminRole;
import com.suntrustbank.user.entrypoints.adminuser.repository.enums.AdminRoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminRole name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminRoleType roleType;

    private boolean isTeamLead;

    private String description;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @PrePersist
    public void prePersist() {
        this.reference = UUID.randomUUID().toString().replaceAll("-", "");
    }
}
