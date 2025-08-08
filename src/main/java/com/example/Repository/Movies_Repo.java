package com.example.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Entity.Movies;

import jakarta.transaction.Transactional;

@Repository
public interface Movies_Repo extends JpaRepository<Movies, Long> {
        Movies findByTitle(String title);

        // Method to find all movies by admin ID (custom query)
        @Query("SELECT m FROM Movies m WHERE m.admin_Entity.id = :adminId")
        List<Movies> findAllMoviesByAdminId(@Param("adminId") Long adminId);

        // OR use @Query for explicit control
        @Query("SELECT COUNT(m) FROM Movies m WHERE m.admin_Entity.id = :adminId")
        long countMoviesByAdminId(@Param("adminId") Long adminId);

        // new
        @Query("SELECT m FROM Movies m WHERE LOWER(m.status) = LOWER('active') AND m.admin_Entity.id = :adminId")
        List<Movies> findActiveMoviesByAdminId(@Param("adminId") Long adminId);

        @Query("SELECT m FROM Movies m WHERE LOWER(m.status) = LOWER('inactive') AND m.admin_Entity.id = :adminId")
        List<Movies> findInActiveMoviesByAdminId(@Param("adminId") Long adminId);

        // List<Movies> findByAdmin_Entity_IdAndTitleContainingIgnoreCase(long adminId,
        // String title);

        // You can add more search methods as needed
        // Find by title containing for a specific admin (case-insensitive)
        @Query("SELECT m FROM Movies m WHERE m.admin_Entity.id = :adminId AND LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))")
        List<Movies> findByAdminIdAndTitleContainingIgnoreCase(@Param("adminId") Long adminId,
                        @Param("title") String title);

        @Modifying
        @Transactional
        @Query("UPDATE Movies m SET m.status = :newStatus WHERE m.id = :id")
        void updateStatusById(@Param("id") long id, @Param("newStatus") String newStatus);

        @Query("SELECT m FROM Movies m JOIN m.admin_Entity a " +
                        "JOIN a.settings_Prefrences s " +
                        "WHERE s.maintenanceMode = false")
        List<Movies> findAllMoviesFromActiveAdmins();

        
}
