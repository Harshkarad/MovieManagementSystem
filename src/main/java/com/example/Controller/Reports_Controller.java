package com.example.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Entity.Admin_Entity;
import com.example.Entity.Booking;
import com.example.Entity.Movies;
import com.example.Service.BookingService;
import com.example.Service.Movie_Service;
import com.example.Service.Report_Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class Reports_Controller {
    @Autowired
    private Report_Service report_Service;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private Movie_Service movie_Service;

    @GetMapping("/reports")
    public String generateReport(HttpSession session, Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String mobile = (String) session.getAttribute("mobile");

        // Find admin by mobile
        Admin_Entity admin = movie_Service.findAdminByMobile(mobile);
        long adminId = admin.getId();

        Map<String, Object> statastics = report_Service.getBookingStats(adminId);
        // Extracting each value separately
        System.out.println(statastics);
        // In Reports_Controller
        long lastMonthTickets = (long) statastics.get("lastMonthTickets");
        double currentMonthRevenue = (double) statastics.get("currentMonthRevenue");
        long currentMonthTickets = (long) statastics.get("currentMonthTickets");
        long lastMonthBookings = (long) statastics.get("lastMonthBookings"); //
        long currentMonthBookings = (long) statastics.get("currentMonthBookings"); //
        double averageTicketPrice = (double) statastics.get("averageTicketPrice");

        // Calculate percentage difference for bookings
        long bookingDifference = currentMonthBookings - lastMonthBookings;
        double percentageDifferenceBookings = 0.0;
        boolean isBookingIncrease = true;

        if (lastMonthBookings != 0) {
            percentageDifferenceBookings = ((double) bookingDifference / lastMonthBookings) * 100;
            isBookingIncrease = bookingDifference >= 0;
        } else if (currentMonthBookings != 0) {
            percentageDifferenceBookings = 100.0;
            isBookingIncrease = true;
        }

        String formattedBookingPercentage = String.format("%.1f", Math.abs(percentageDifferenceBookings));
        System.out.println("Booking percentage:" + formattedBookingPercentage);
        // Add to model
        model.addAttribute("bookingPercentageDifference", formattedBookingPercentage);
        model.addAttribute("isBookingIncrease", isBookingIncrease);

        // Calculate percentage difference for tickets
        long ticketDifference = currentMonthTickets - lastMonthTickets;
        double percentageDifferenceTickets = 0.0;
        boolean isTicketIncrease = true;

        // Avoid division by zero if last month had no tickets
        if (lastMonthTickets != 0) {
            percentageDifferenceTickets = ((double) ticketDifference / lastMonthTickets) * 100;
            isTicketIncrease = ticketDifference >= 0;
        } else if (currentMonthTickets != 0) {
            // If last month was 0 and current month has tickets, it's 100% increase
            percentageDifferenceTickets = 100.0;
            isTicketIncrease = true;
        }

        String formattedPercentageDifference = String.format("%.1f", Math.abs(percentageDifferenceTickets));

        // Add to model
        model.addAttribute("ticketPercentageDifference", formattedPercentageDifference);
        model.addAttribute("isTicketIncrease", isTicketIncrease);

        System.out.println("Diffrence is:" + formattedPercentageDifference);
        String updatedcurrentMonthRevenue = String.format("%.2f", currentMonthRevenue);
        String updatedAverageTicketPrice = String.format("%.2f", averageTicketPrice);
        double lastMonthRevenue = report_Service.getPreviousMonthRevenue(adminId);

        // Calculate percentage difference for revenue
        double revenueDifference = currentMonthRevenue - lastMonthRevenue;
        double percentageDifferenceRevenue = 0.0;
        boolean isRevenueIncrease = true;

        if (lastMonthRevenue != 0) {
            percentageDifferenceRevenue = (revenueDifference / lastMonthRevenue) * 100;
            isRevenueIncrease = revenueDifference >= 0;
        } else if (currentMonthRevenue != 0) {
            percentageDifferenceRevenue = 100.0;
            isRevenueIncrease = true;
        }

        String formattedRevenuePercentage = String.format("%.1f", Math.abs(percentageDifferenceRevenue));

        // Add to model
        model.addAttribute("revenuePercentageDifference", formattedRevenuePercentage);
        model.addAttribute("isRevenueIncrease", isRevenueIncrease);

        double lastMonthAverageTicketPrice = report_Service.getAverageTicketPricePreviousMonth(adminId);
        System.out.println(lastMonthAverageTicketPrice);

        // Calculate percentage difference for average ticket price
        double averageTicketPriceDifference = averageTicketPrice - lastMonthAverageTicketPrice;
        double percentageDifferenceAvgPrice = 0.0;
        boolean isAvgPriceIncrease = true;

        if (lastMonthAverageTicketPrice != 0) {
            percentageDifferenceAvgPrice = (averageTicketPriceDifference / lastMonthAverageTicketPrice) * 100;
            isAvgPriceIncrease = averageTicketPriceDifference >= 0;
        } else if (averageTicketPrice != 0) {
            percentageDifferenceAvgPrice = 100.0;
            isAvgPriceIncrease = true;
        }

        String formattedAvgPricePercentage = String.format("%.1f", Math.abs(percentageDifferenceAvgPrice));
        System.out.println(formattedAvgPricePercentage);
        // Add to model
        model.addAttribute("avgPricePercentageDifference", formattedAvgPricePercentage);
        model.addAttribute("isAvgPriceIncrease", isAvgPriceIncrease);

        // Models
        model.addAttribute("lastMonthTickets", lastMonthTickets);
        model.addAttribute("currentMonthRevenue", updatedcurrentMonthRevenue);
        model.addAttribute("currentMonthTickets", currentMonthTickets);
        model.addAttribute("lastMonthBookings", lastMonthBookings);
        model.addAttribute("currentMonthBookings", currentMonthBookings);
        model.addAttribute("averageTicketPrice", updatedAverageTicketPrice);

        // Get top 5 movies by bookings and revenue
        List<Map<String, Object>> topMovies = report_Service.findTop5MoviesByBookingCount(adminId);

        // Extract data for the chart
        List<String> movieTitles = topMovies.stream()
                .map(movieMap -> ((Movies) movieMap.get("movie")).getTitle())
                .collect(Collectors.toList());

        List<Integer> bookingCounts = topMovies.stream()
                .map(movieMap -> ((Long) movieMap.get("bookingCount")).intValue())
                .collect(Collectors.toList());

        System.out.println("Booking Counts:" + bookingCounts);

        List<Double> revenues = topMovies.stream()
                .map(movieMap -> ((Number) movieMap.get("revenue")).doubleValue())
                .collect(Collectors.toList());

        System.out.println("Revenue is:" + revenues);

        // Prepare data for the detailed table
        List<Map<String, Object>> movieDetails = topMovies.stream()
                .map(movieMap -> {
                    Map<String, Object> details = new LinkedHashMap<>();
                    details.put("title", ((Movies) movieMap.get("movie")).getTitle());
                    details.put("bookingCount", movieMap.get("bookingCount"));
                    details.put("revenue", movieMap.get("revenue"));
                    return details;
                })
                .collect(Collectors.toList());

        // Convert to JSON for JavaScript charts
        ObjectMapper mapper = new ObjectMapper();
        try {
            // In your controller, add debug logging:
            System.out.println("Movie Titles JSON: " + mapper.writeValueAsString(movieTitles));
            System.out.println("Booking Counts JSON: " + mapper.writeValueAsString(bookingCounts));
            System.out.println("Revenues JSON: " + mapper.writeValueAsString(revenues));

            model.addAttribute("movieTitlesJson", new Gson().toJson(movieTitles));
            model.addAttribute("bookingCountsJson", new Gson().toJson(bookingCounts));
            model.addAttribute("revenuesJson", new Gson().toJson(revenues));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // new
        List<Map<String, Object>> weeklyBookings = report_Service.getWeeklyBookingReport(adminId);
        System.out.println("Weekly report is:" + weeklyBookings);
        model.addAttribute("weeklyBookings", weeklyBookings);
        model.addAttribute("weeklyBookingsJson", new Gson().toJson(weeklyBookings));

        // Get all bookings and movies for the admin
        List<Booking> bookings = bookingService.findByAdmin(adminId);
        List<Movies> movies = report_Service.findByAdmin(adminId);

        LocalDate today = LocalDate.now();
        String currentMonthName = today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String previousMonthName = today.minusMonths(1).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        model.addAttribute("bookings", bookings);

        // Add attributes to model
        model.addAttribute("currentMonthName", currentMonthName);
        model.addAttribute("previousMonthName", previousMonthName);
        model.addAttribute("topMovies", movieDetails);
        model.addAttribute("movies", movies);
        // model.addAttribute("bookings", bookings);

        return "reports";
    }

    @GetMapping("/export-report")
    public void exportReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response,
            HttpSession session) throws IOException {
        String mobile = (String) session.getAttribute("mobile");
        Admin_Entity admin = movie_Service.findAdminByMobile(mobile);
        long adminId = admin.getId();

        // Parse dates if provided
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"sales_report_" + start + "_to_" + end + ".csv\"");

        PrintWriter writer = response.getWriter();

        // Write header
        writer.println("Movie Ticket System - Sales Report");
        writer.println("Report Period:," + start + " to " + end);
        writer.println();

        // Section 1: Summary Statistics
        writer.println("Summary Statistics");
        writer.println("Metric,Current Period,Previous Period,Change (%)");

        Map<String, Object> stats = report_Service.getBookingStatsForPeriod(adminId, startDateTime, endDateTime);
        System.out.println("stat are:" + stats);
        Map<String, Object> prevStats = report_Service.getBookingStatsForPeriod(adminId,
                startDateTime.minusMonths(1), endDateTime.minusMonths(1));
        System.out.println("prev are:" + prevStats);
        writer.println(String.format("Total Revenue,%.2f,%.2f,%.1f%%",
                (Double) stats.get("totalRevenue"),
                (Double) prevStats.get("totalRevenue"),
                ((Double) stats.get("totalRevenue") - (Double) prevStats.get("totalRevenue")) /
                        (Double) prevStats.get("totalRevenue") * 100));

        writer.println(String.format("Total Bookings,%d,%d,%.1f%%",
                (Long) stats.get("totalBookings"),
                (Long) prevStats.get("totalBookings"),
                ((Long) stats.get("totalBookings") - (Long) prevStats.get("totalBookings")) * 100.0 /
                        (Long) prevStats.get("totalBookings")));

        writer.println(String.format("Total Tickets,%d,%d,%.1f%%",
                (Long) stats.get("totalTickets"),
                (Long) prevStats.get("totalTickets"),
                ((Long) stats.get("totalTickets") - (Long) prevStats.get("totalTickets")) * 100.0 /
                        (Long) prevStats.get("totalTickets")));

        writer.println(String.format("Average Ticket Price,%.2f,%.2f,%.1f%%",
                (Double) stats.get("averageTicketPrice"),
                (Double) prevStats.get("averageTicketPrice"),
                ((Double) stats.get("averageTicketPrice") - (Double) prevStats.get("averageTicketPrice")) * 100.0 /
                        (Double) prevStats.get("averageTicketPrice")));

        writer.println();

        // Section 2: Top Movies
        writer.println("Top Performing Movies");
        writer.println("Rank,Movie Title,Bookings Count,Revenue (₹),Average Ticket Price");

        List<Map<String, Object>> topMovies = report_Service.findTopMoviesByBookingCountAndDateRange(
                adminId, startDateTime, endDateTime);

        int rank = 1;
        System.out.println("Top movies are:" + topMovies);
        for (Map<String, Object> movie : topMovies) {
            Movies movieObj = (Movies) movie.get("movie");
            long bookingCount = (Long) movie.get("bookingCount");
            double revenue = ((Number) movie.get("revenue")).doubleValue();
            double avgPrice = bookingCount > 0 ? revenue / bookingCount : 0;

            writer.println(String.format("%d,\"%s\",%d,%.2f,%.2f",
                    rank++,
                    movieObj.getTitle(),
                    bookingCount,
                    revenue,
                    avgPrice));
        }

        writer.println();

        // Section 3: Detailed Bookings
        writer.println("Detailed Bookings");
        writer.println("Booking ID,Customer Name,Movie,Show Time,Seats,Amount,Booking Date");

        List<Booking> bookings = bookingService.findByAdminAndDateRange(adminId, startDateTime, endDateTime);
        System.out.println("Bookings are:" + bookings);
        for (Booking booking : bookings) {
            writer.println(String.format("%s,\"%s %s\",\"%s\",%s,\"%s\",%.2f,%s",
                    booking.getBookingReference(),
                    booking.getUser().getFirstname(),
                    booking.getUser().getLastname(),
                    booking.getScreening().getMovies().getTitle(),
                    booking.getScreening().getTime().toString(),
                    String.join(", ", booking.getSeats()),
                    booking.getTotalAmount(),
                    booking.getBookingTime().toString()));
        }

        writer.flush();
        writer.close();
    }
}
