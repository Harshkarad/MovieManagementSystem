package com.example.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Entity.User;

public interface User_Repo extends JpaRepository<User, Long> {
        // check if mobile number already registered
        boolean existsByMobile(String mobile);

        // check if email number already registered
        boolean existsByEmail(String email);

        // verifying logging credentials
        Optional<User> findByMobileAndPassword(String mobile, String password);

        // Find user by mobile number
        Optional<User> findByMobile(String mobile);

        @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :start AND :end")
        List<User> findUsersCreatedBetweenDates(
                        @Param("start") LocalDateTime startOfMonth,
                        @Param("end") LocalDateTime endOfMonth);

        // Search method using custom query
        @Query("SELECT u FROM User u WHERE " +
                        "(:includeInactive = true OR u.status = 'Active') AND " +
                        "(LOWER(u.firstname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(u.lastname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(u.mobile) LIKE LOWER(CONCAT('%', :query, '%')))")
        List<User> searchCustomers(
                        @Param("query") String query,
                        @Param("includeInactive") boolean includeInactive);

        // Find user by email
        Optional<User> findByEmail(String email);

        
}
