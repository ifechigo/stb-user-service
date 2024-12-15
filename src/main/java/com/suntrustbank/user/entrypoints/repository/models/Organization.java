package com.suntrustbank.user.entrypoints.repository.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.suntrustbank.user.core.enums.ErrorCode;
import com.suntrustbank.user.core.errorhandling.exceptions.GenericErrorCodeException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity(name = "organizations")
public class Organization {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Business> businesses;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    public void addBusiness(Business business) {
        if (this.businesses.size() >= 2) {
            throw new GenericErrorCodeException("A user can only have a maximum of 2 businesses.", ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        if (this.getBusinesses().size() == 1 && business.getBusinessType().equals(this.getBusinesses().getFirst().getBusinessType())) {
            throw new GenericErrorCodeException(String.format("You have %s business attached to this account",
                    business.getBusinessType()), ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }

        this.businesses.add(business);
    }
}
