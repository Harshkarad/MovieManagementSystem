package com.example.Repository;

import com.example.Entity.Booking;
import com.example.Entity.Screening;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface Booking_Repo extends JpaRepository<Booking, Long> {
        @EntityGraph(attributePaths = { "screening", "screening.movies", "screening.theater", "payment"
        })
        List<Booking> findByUserId(Long userId);

        Optional<Booking> findByBookingReference(String bookingReference);

        // Or for a specific theater:
        @Query("SELECT SUM(b.totalAmount) " +
                        "FROM Booking b " +
                        "JOIN b.screening s " +
                        "WHERE s.theater.id = :theaterId")
        Double findTotalAmountByTheaterId(@Param("theaterId") Long theaterId);

        @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b " +
                        "JOIN b.screening s " +
                        "WHERE s.theater.id = :theaterId " +
                        "AND CAST(s.time AS date) = :date")
        Double findDailyRevenueByTheater(
                        @Param("theaterId") Long theaterId,
                        @Param("date") LocalDate date);

        @Query("SELECT COALESCE(SUM(b.numberOfTickets), 0) FROM Booking b " +
                        "WHERE b.screening.theater.id = :theaterId " +
                        "AND DATE(b.bookingTime) = CURRENT_DATE")
        Integer findTotalTicketsTodayByTheaterId(@Param("theaterId") Long theaterId);

        // Find all bookings for screenings in theaters owned by a specific admin
        // @Query("SELECT b FROM Booking b " +
        // "JOIN b.screening s " +
        // "JOIN s.theater t " +
        // "WHERE t.id = :adminId")
        // List<Booking> findByAdminId(@Param("adminId") Long adminId);

        @Query("SELECT b FROM Booking b " +
                        "JOIN b.screening s " +
                        "JOIN s.theater t " +
                        "WHERE t.id = :adminId " +
                        "ORDER BY b.id DESC")
        List<Booking> findByAdminId(@Param("adminId") Long adminId);

        @Query("SELECT b.seats FROM Booking b WHERE b.screening.id = :screeningId")
        List<List<String>> findBookedSeatsByScreeningId(@Param("screeningId") Long screeningId);

        @Query("SELECT s FROM Screening s WHERE s.movies.id = :movieId")
        List<Screening> findScreeningsByMovieId(@Param("movieId") Long movieId);

        @Query("SELECT b.screening.movies as movie, COUNT(b) as bookingCount, SUM(b.totalAmount) as revenue " +
                        "FROM Booking b " +
                        "WHERE b.screening.theater.id = :adminId " +
                        "GROUP BY b.screening.movies " +
                        "ORDER BY COUNT(b) DESC, SUM(b.totalAmount) DESC " +
                        "LIMIT 5")
        List<Map<String, Object>> findTop5MoviesByAdminId(@Param("adminId") Long adminId);

        // new
        @Query(value = """
                        SELECT
                            CONCAT('Week ', FLOOR((DAYOFMONTH(b.booking_time) - 1) / 7) + 1) AS week,
                            MIN(DATE(b.booking_time)) AS start_date,
                            MAX(DATE(b.booking_time)) AS end_date,
                            COUNT(b.id) AS bookings,
                            SUM(b.total_amount) AS revenue
                        FROM bookings b
                        JOIN screenings s ON b.screening_id = s.id
                        JOIN admin_entity a ON s.theater_id = a.id
                        WHERE a.id = :adminId
                          AND MONTH(b.booking_time) = MONTH(CURRENT_DATE())
                          AND YEAR(b.booking_time) = YEAR(CURRENT_DATE())
                        GROUP BY FLOOR((DAYOFMONTH(b.booking_time) - 1) / 7), week
                        ORDER BY start_date
                        """, nativeQuery = true)
        List<Map<String, Object>> findWeeklyBookingsByAdmin(@Param("adminId") Long adminId);

        // new
        @Query("SELECT COUNT(b) FROM Booking b JOIN b.screening s JOIN s.theater t " +
                        "WHERE t.id = :adminId AND b.bookingTime BETWEEN :start AND :end")
        long countByAdminAndBookingTimeBetween(@Param("adminId") long adminId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        @Query("SELECT SUM(b.numberOfTickets) FROM Booking b JOIN b.screening s JOIN s.theater t " +
                        "WHERE t.id = :adminId AND b.bookingTime BETWEEN :start AND :end")
        Long sumNumberOfTicketsByAdminAndBookingTimeBetween(@Param("adminId") long adminId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        @Query("SELECT SUM(b.totalAmount) FROM Booking b JOIN b.screening s JOIN s.theater t " +
                        "WHERE t.id = :adminId AND b.bookingTime BETWEEN :start AND :end")
        Double sumTotalAmountByAdminAndBookingTimeBetween(@Param("adminId") long adminId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b " +
                        "JOIN b.screening s " +
                        "JOIN s.theater t " +
                        "WHERE t.id = :adminId " +
                        "AND b.bookingTime BETWEEN :startDate AND :endDate")
        Double findMonthlyRevenueByAdminIdAndDateRange(
                        @Param("adminId") Long adminId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT COALESCE(AVG(b.totalAmount / b.numberOfTickets), 0) FROM Booking b " +
                        "JOIN b.screening s " +
                        "JOIN s.theater t " +
                        "WHERE t.id = :adminId " +
                        "AND b.bookingTime BETWEEN :startDate AND :endDate")
        Double findAverageTicketPriceByAdminIdAndDateRange(
                        @Param("adminId") Long adminId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Find bookings for a specific theater (admin) between dates
        @Query("SELECT b FROM Booking b JOIN b.screening s JOIN s.theater t " +
                        "WHERE t.id = :adminId AND b.bookingTime BETWEEN :start AND :end")
        List<Booking> findByTheaterIdAndBookingTimeBetween(
                        @Param("adminId") Long adminId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        // Find top movies by bookings and revenue for a specific theater (admin)
        @Query("SELECT new map(m as movie, COUNT(b) as bookingCount, SUM(b.totalAmount) as revenue) " +
                        "FROM Booking b JOIN b.screening s JOIN s.movies m JOIN s.theater t " +
                        "WHERE t.id = :adminId AND b.bookingTime BETWEEN :start AND :end " +
                        "GROUP BY m " +
                        "ORDER BY COUNT(b) DESC")
        List<Map<String, Object>> findTopMoviesByTheaterIdAndDateRange(
                        @Param("adminId") Long adminId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        @Query("SELECT b FROM Booking b WHERE b.screening.movies.admin_Entity.id = :adminId")
        Page<Booking> findByAdminId(@Param("adminId") long adminId, Pageable pageable);

        // new 
        
        // find by screenings
       List<Booking> findByScreening(Screening screening);
}