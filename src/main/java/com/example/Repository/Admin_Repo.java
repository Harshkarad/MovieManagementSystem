package com.example.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Entity.Admin_Entity;

import jakarta.transaction.Transactional;

@Repository
public interface Admin_Repo extends JpaRepository<Admin_Entity, Long> {
    // Method to find all theatres with Active status
    List<Admin_Entity> findByStatus(String status);

    // find using mobile and password
    Optional<Admin_Entity> findByMobileAndPassword(String mobile, String password);

    Admin_Entity findByMobile(String mobile);

    Admin_Entity findByTheatreName(String theatreName);

    @Transactional
    @Modifying
    @Query("UPDATE Admin_Entity a SET a.email = :email, a.password = :password WHERE a.id = :id")
    void updateAdminCredentials(@Param("id") Long id,
            @Param("email") String email,
            @Param("password") String password);

    // Optional: Find by email method if needed for validation
    Admin_Entity findByEmail(String email);
}
