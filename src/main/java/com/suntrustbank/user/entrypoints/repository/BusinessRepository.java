package com.suntrustbank.user.entrypoints.repository;

import com.suntrustbank.user.entrypoints.repository.models.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findByReference(String reference);

    @Query("""
        SELECT b FROM businesses b
        JOIN b.organization o
        JOIN o.creator u
        WHERE u.id = :userId AND b.id = :businessId
    """)
    Optional<Business> findByUserIdAndBusinessId(@Param("userId") String userId, @Param("businessId") String businessId);
}
