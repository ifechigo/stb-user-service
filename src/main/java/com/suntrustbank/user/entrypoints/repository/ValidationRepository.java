package com.suntrustbank.user.entrypoints.repository;

import com.suntrustbank.user.entrypoints.repository.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface ValidationRepository {

    @Query("""
       SELECT EXISTS (SELECT 1 FROM users u WHERE u.email = :email)
           OR EXISTS (SELECT 1 FROM businesses b WHERE b.email = :email)
       """)
    boolean isEmailTaken(@Param("email") String email);

    @Query("""
       SELECT EXISTS (SELECT 1 FROM users u WHERE u.phoneNumber = :phoneNumber OR u.altPhoneNumber = :phoneNumber)
           OR EXISTS (SELECT 1 FROM businesses b WHERE b.phoneNumber = :phoneNumber)
       """)
    boolean isPhoneNumberTaken(@Param("phoneNumber") String phoneNumber);


//    @Query("""
//       SELECT CASE
//           WHEN EXISTS (SELECT 1 FROM users u WHERE u.email = :email) THEN true
//           WHEN EXISTS (SELECT 1 FROM businesses b WHERE b.email = :email) THEN true
//           ELSE false
//       END
//       """)
//    boolean isEmailTaken(@Param("email") String email);
//
//    @Query("""
//       SELECT CASE
//           WHEN EXISTS (SELECT 1 FROM users u WHERE u.phone_number = :phoneNumber OR u.alt_phone_number = :phoneNumber) THEN true
//           WHEN EXISTS (SELECT 1 FROM businesses b WHERE b.phone_number = :phoneNumber) THEN true
//           ELSE false
//       END
//       """)
//    boolean isPhoneNumberTaken(@Param("phoneNumber") String phoneNumber);
}
