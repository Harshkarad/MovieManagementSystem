package com.example.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Entity.Booking;
import com.example.Entity.Movies;
import com.example.Repository.Booking_Repo;
import com.example.Repository.Movies_Repo;
import com.example.Repository.Screening_Repo;

@Service
public class Report_Service {
    @Autowired
    private Screening_Repo screeningRepository;

    @Autowired
    private Movies_Repo movies_Repo;

    @Autowired
    private Booking_Repo booking_Repo;

    // find movie by adin id
    public List<Movies> findByAdmin(long AdminId) {
        return movies_Repo.findAllMoviesByAdminId(AdminId);
    }

    public List<Map<String, Object>> findTop5MoviesByBookingCount(Long adminId) {
        return booking_Repo.findTop5MoviesByAdminId(adminId);
    }

    public List<Map<String, Object>> getWeeklyBookingReport(Long adminId) {
        return booking_Repo.findWeeklyBookingsByAdmin(adminId);
    }

    // new
    public Map<String, Object> getBookingStats(long adminId) {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfCurrentMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfLastMonth = startOfCurrentMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfCurrentMonth.minusSeconds(1);

        // Booking counts
        long currentMonthBookings = booking_Repo.countByAdminAndBookingTimeBetween(
                adminId, startOfCurrentMonth, now);
        // System.out.println("Current Month Booking:"+currentMonthBookings);

        long lastMonthBookings = booking_Repo.countByAdminAndBookingTimeBetween(
                adminId, startOfLastMonth, endOfLastMonth);
        // System.out.println("Last Month Booking:"+lastMonthBookings);

        // Ticket counts
        Long currentMonthTickets = booking_Repo.sumNumberOfTicketsByAdminAndBookingTimeBetween(
                adminId, startOfCurrentMonth, now);
        // System.out.println("Ticket Count:"+currentMonthTickets);

        Long lastMonthTickets = booking_Repo.sumNumberOfTicketsByAdminAndBookingTimeBetween(
                adminId, startOfLastMonth, endOfLastMonth);
        // System.out.println("Last month booking:"+lastMonthTickets);

        // Average ticket price
        Double averageTicketPrice = screeningRepository.findAveragePriceByAdmin(adminId);
        // System.out.println("Average ticket price:"+averageTicketPrice);

        // Current month revenue
        Double currentMonthRevenue = booking_Repo.sumTotalAmountByAdminAndBookingTimeBetween(
                adminId, startOfCurrentMonth, now);
        // System.out.println("Current month booking:"+currentMonthRevenue);

        stats.put("currentMonthBookings", currentMonthBookings);
        stats.put("lastMonthBookings", lastMonthBookings);
        stats.put("currentMonthTickets", currentMonthTickets != null ? currentMonthTickets : 0);
        stats.put("lastMonthTickets", lastMonthTickets != null ? lastMonthTickets : 0);
        stats.put("averageTicketPrice", averageTicketPrice != null ? averageTicketPrice : 0);
        stats.put("currentMonthRevenue", currentMonthRevenue != null ? currentMonthRevenue : 0);

        return stats;
    }

    public Double getPreviousMonthRevenue(Long adminId) {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfPreviousMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfPreviousMonth = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

        LocalDateTime start = firstDayOfPreviousMonth.atStartOfDay();
        LocalDateTime end = lastDayOfPreviousMonth.atTime(LocalTime.MAX);

        return booking_Repo.findMonthlyRevenueByAdminIdAndDateRange(adminId, start, end);
    }

    public Double getAverageTicketPricePreviousMonth(Long adminId) {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfPreviousMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfPreviousMonth = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

        LocalDateTime start = firstDayOfPreviousMonth.atStartOfDay();
        LocalDateTime end = lastDayOfPreviousMonth.atTime(LocalTime.MAX);

        return booking_Repo.findAverageTicketPriceByAdminIdAndDateRange(adminId, start, end);
    }

    public Map<String, Object> getBookingStatsForPeriod(Long adminId, LocalDateTime start, LocalDateTime end) {
        Map<String, Object> stats = new HashMap<>();

        // Booking counts
        long totalBookings = booking_Repo.countByAdminAndBookingTimeBetween(adminId, start, end);

        // Ticket counts
        Long totalTickets = booking_Repo.sumNumberOfTicketsByAdminAndBookingTimeBetween(adminId, start, end);

        // Revenue
        Double totalRevenue = booking_Repo.sumTotalAmountByAdminAndBookingTimeBetween(adminId, start, end);

        // Average ticket price
        Double averageTicketPrice = totalTickets != null && totalTickets > 0 ? totalRevenue / totalTickets : 0.0;

        stats.put("totalBookings", totalBookings);
        stats.put("totalTickets", totalTickets != null ? totalTickets : 0);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        stats.put("averageTicketPrice", averageTicketPrice);

        return stats;
    }

    public List<Map<String, Object>> findTopMoviesByBookingCountAndDateRange(Long adminId,
            LocalDateTime start, LocalDateTime end) {
        return booking_Repo.findTopMoviesByTheaterIdAndDateRange(adminId, start, end);
    }
}
