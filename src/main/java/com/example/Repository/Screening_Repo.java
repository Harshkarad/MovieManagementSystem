package com.example.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Entity.Admin_Entity;
import com.example.Entity.Movies;
import com.example.Entity.Screening;

import jakarta.transaction.Transactional;

@Repository
public interface Screening_Repo extends JpaRepository<Screening, Long> {
        List<Screening> findByMoviesId(Long movieId);

        // Method to find screening by movie, theater, time and screen
        @Query("SELECT s FROM Screening s " +
                        "WHERE s.movies.id = :movieId " +
                        "AND s.theater.id = :theaterId " +
                        "AND s.time = :time " +
                        "AND s.screen = :screen")
        Optional<Screening> findByMovieAndTheaterAndTimeAndScreen(
                        @Param("movieId") Long movieId,
                        @Param("theaterId") Long theaterId,
                        @Param("time") String time,
                        @Param("screen") String screen);

        // Alternative method using entity relationships
        Optional<Screening> findByMoviesAndTheaterAndTimeAndScreen(
                        Movies movie,
                        Admin_Entity theater,
                        LocalDateTime time,
                        String screen);

        // Method to find all active movies by admin ID
        @Query("SELECT m FROM Movies m WHERE m.admin_Entity.id = :adminId AND m.status = 'active'")
        List<Movies> findActiveMoviesByAdminId(@Param("adminId") Long adminId);

        // Custom query method to find screenings by admin (theater) ID
        // @Query("SELECT s FROM Screening s WHERE s.theater.id = :adminId")
        // List<Screening> findByAdminId(@Param("adminId") Long adminId);

        // @Query("SELECT s FROM Screening s WHERE s.theater.id = :adminId ORDER BY s.id
        // DESC")
        // List<Screening> findByAdminId(@Param("adminId") Long adminId);

        @Query("SELECT s FROM Screening s WHERE s.theater.id = :adminId ORDER BY s.time ASC")
        List<Screening> findByAdminIdOrderByTimeDesc(@Param("adminId") Long adminId);

        @Query("SELECT s FROM Screening s WHERE " +
                        "s.theater.id = :adminId " +
                        "AND (:movieId IS NULL OR s.movies.id = :movieId) " +
                        "AND (:startDate IS NULL OR s.time >= :startDate) " +
                        "AND (:endDate IS NULL OR s.time < :endDate) " +
                        "AND (:status IS NULL OR s.status = :status)")
        List<Screening> findByFilters(
                        @Param("adminId") long adminId,
                        @Param("movieId") Long movieId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("status") String status);

        @Query("SELECT AVG(s.price) FROM Screening s WHERE s.theater.id = :adminId")
        Double findAveragePriceByAdmin(@Param("adminId") long adminId);

        // new

        @Query("SELECT s FROM Screening s " +
                        "WHERE s.theater.id = :adminId " +
                        "AND s.time >= :currentTime " +
                        "AND s.status = 'UPCOMING' " +
                        "ORDER BY s.time ASC " +
                        "LIMIT 7")
        List<Screening> findNext7ScreeningsByAdmin(
                        @Param("adminId") long adminId,
                        @Param("currentTime") LocalDateTime currentTime);

        @Modifying
        @Transactional
        @Query("UPDATE Screening s SET s.status = :status WHERE s.id = :id")
        void updateStatusById(@Param("id") Long id, @Param("status") String status);
}
