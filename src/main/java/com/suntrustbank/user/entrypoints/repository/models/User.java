package com.suntrustbank.user.entrypoints.repository.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.suntrustbank.user.entrypoints.repository.enums.Role;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity(name = "users")
public class User {
    @Id
    private String id;

    @Column(nullable = false)
    private String phoneNumber;

    private String fullName;

    private String email;

    private String address;

    private String state;

    private String lga;

    private String altPhoneNumber;

    private String dob;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String photo;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
