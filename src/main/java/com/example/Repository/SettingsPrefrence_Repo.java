package com.example.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Entity.Settings_Prefrences;

import jakarta.transaction.Transactional;

@Repository
public interface SettingsPrefrence_Repo extends JpaRepository<Settings_Prefrences, Long> {
    // Method to update settings by admin ID
    @Modifying
    @Transactional
    @Query("UPDATE Settings_Prefrences s SET " +
            "s.maintenanceMode = :maintenanceMode, " +
            "s.userRegistration = :userRegistration, " +
            "s.emailNotifications = :emailNotifications, " +
            "s.auditLogging = :auditLogging " +
            "WHERE s.admin_Entity.id = :adminId")
    int updateSettingsByAdminId(
            @Param("adminId") Long adminId,
            @Param("maintenanceMode") boolean maintenanceMode,
            @Param("userRegistration") boolean userRegistration,
            @Param("emailNotifications") boolean emailNotifications,
            @Param("auditLogging") boolean auditLogging);

    @Query("SELECT s FROM Settings_Prefrences s WHERE s.admin_Entity.id = :adminId")
    Settings_Prefrences findSettingsByAdminId(@Param("adminId") Long adminId);
}
