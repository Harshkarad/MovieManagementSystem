package com.example.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.example.Entity.SettingsActivity;

public interface SettingsActivity_Repo extends JpaRepository<SettingsActivity, Long> {
    // Optimized native query to get recent 4 activities by admin ID
    @Query(value = """
            SELECT * FROM settings_activities
            WHERE admin_id = :adminId
            ORDER BY timestamp DESC
            LIMIT 4
            """, nativeQuery = true)
    List<SettingsActivity> findRecentActivitiesByAdmin(@Param("adminId") Long adminId);
}
