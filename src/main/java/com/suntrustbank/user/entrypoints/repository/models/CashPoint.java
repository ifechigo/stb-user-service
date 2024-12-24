package com.suntrustbank.user.entrypoints.repository.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.suntrustbank.user.entrypoints.repository.enums.Status;
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
@Entity(name = "cash_points")
public class CashPoint {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    private String virtualAccountNo;

    private String walletId;

    @Enumerated(EnumType.STRING)
    private Status status;

    private boolean isMain;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
