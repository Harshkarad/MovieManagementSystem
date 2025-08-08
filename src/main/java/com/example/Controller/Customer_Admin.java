package com.example.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.Entity.Booking;
import com.example.Entity.User;
import com.example.Repository.User_Repo;
import com.example.Service.BookingService;
import com.example.Service.User_Service;

import jakarta.servlet.http.HttpSession;

@Controller
public class Customer_Admin {
    @Autowired
    private User_Repo user_Repo;
    @Autowired
    private BookingService bookingService;

    @Autowired
    private User_Service user_Service;

    @GetMapping("/customers")
    public String CustomersList(Model model, HttpSession session) {

        // user id
        long adminId = (long) session.getAttribute("IdAdmin");

        // all users
        List<User> users = user_Service.AllUser();

        // booking by user id
        List<Booking> bookings = bookingService.findByAdmin(adminId);
        long Bookingsize = bookings.size();

        // no of user for this month
        List<User> users2 = user_Service.getUsersCreatedThisMonthAlternative();
        long userSize = users.size();

        double Averagebooking = Bookingsize / userSize;

        // Models
        model.addAttribute("users", users);
        model.addAttribute("newuser", users2.size());
        model.addAttribute("AverageBooking", Averagebooking);
        return "customers";
    }

    // In Customer_Admin.java
    @GetMapping("/api/customers/{id}")
    @ResponseBody
    public Map<String, Object> getCustomerById(@PathVariable Long id) {
        User user = user_Service.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Get booking history
        List<Map<String, Object>> bookingHistory = user.getBookings().stream()
                .map(booking -> {
                    Map<String, Object> bookingMap = new HashMap<>();
                    bookingMap.put("id", booking.getId());
                    bookingMap.put("movie", booking.getScreening().getMovies().getTitle());
                    bookingMap.put("date", booking.getScreening().getTime().toString());
                    bookingMap.put("time", booking.getTime());
                    bookingMap.put("seats", String.join(", ", booking.getSeats()));
                    bookingMap.put("amount", booking.getTotalAmount());
                    return bookingMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("customer", user);
        response.put("totalBookings", user.getTotalBookings());
        response.put("totalSpent", user.getTotalSpent());
        response.put("bookingHistory", bookingHistory);

        return response;
    }

    // Updated endpoint for updating a customer
    @PutMapping("/api/customers/{id}")
    @ResponseBody
    public ResponseEntity<User> updateCustomer(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = user_Service.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/customers/{id}/status")
    @ResponseBody
    public User updateCustomerStatus(@PathVariable Long id, @RequestBody User user) {
        return user_Service.updateUserStatus(id, user.getStatus());
    }

    @PostMapping("/api/customers")
    @ResponseBody
    public ResponseEntity<User> addCustomer(@RequestBody User user) {
        System.out.println("Received user: " + user);

        // Set default status if not provided
        if (user.getStatus() == null) {
            user.setStatus("Active");
        }

        try {
            User savedUser = user_Service.saveUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/customers/filter")
    public ResponseEntity<List<User>> filterCustomers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateRange,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String searchTerm) {

        // Start with all customers
        List<User> customers = user_Repo.findAll();

        // Apply filters
        if (searchTerm != null && !searchTerm.isEmpty()) {
            customers = customers.stream()
                    .filter(user -> user.getFirstname().toLowerCase().contains(searchTerm.toLowerCase()) ||
                            user.getLastname().toLowerCase().contains(searchTerm.toLowerCase()) ||
                            user.getEmail().toLowerCase().contains(searchTerm.toLowerCase()) ||
                            user.getMobile().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.equals("all")) {
            customers = customers.stream()
                    .filter(user -> user.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        if (dateRange != null && !dateRange.equals("all")) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = null;

            switch (dateRange) {
                case "today":
                    startDate = now.withHour(0).withMinute(0).withSecond(0);
                    break;
                case "week":
                    startDate = now.minusWeeks(1);
                    break;
                case "month":
                    startDate = now.minusMonths(1);
                    break;
                case "year":
                    startDate = now.minusYears(1);
                    break;
            }

            if (startDate != null) {
                final LocalDateTime filterStartDate = startDate;
                customers = customers.stream()
                        .filter(user -> user.getCreatedAt().isAfter(filterStartDate))
                        .collect(Collectors.toList());
            }
        }

        // Apply sorting
        if (sortBy != null) {
            switch (sortBy) {
                case "newest":
                    customers.sort((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()));
                    break;
                case "oldest":
                    customers.sort((u1, u2) -> u1.getCreatedAt().compareTo(u2.getCreatedAt()));
                    break;
                case "name_asc":
                    customers.sort((u1, u2) -> (u1.getFirstname() + u1.getLastname())
                            .compareToIgnoreCase(u2.getFirstname() + u2.getLastname()));
                    break;
                case "name_desc":
                    customers.sort((u1, u2) -> (u2.getFirstname() + u2.getLastname())
                            .compareToIgnoreCase(u1.getFirstname() + u1.getLastname()));
                    break;
                case "bookings":
                    customers.sort((u1, u2) -> Integer.compare(
                            u2.getBookings() != null ? u2.getBookings().size() : 0,
                            u1.getBookings() != null ? u1.getBookings().size() : 0));
                    break;
            }
        }

        return ResponseEntity.ok(customers);
    }

    // Search endpoint
    @GetMapping("/api/customers/search")
    public ResponseEntity<List<User>> searchCustomers(
            @RequestParam String query,
            @RequestParam(defaultValue = "false") boolean includeInactive) {

        // Search in multiple fields (firstname, lastname, email, mobile)
        List<User> customers = user_Repo.searchCustomers(
                query.toLowerCase(),
                includeInactive);

        return ResponseEntity.ok(customers);
    }

    // Combined search and filter endpoint
    @GetMapping("/api/customers/search-filter")
    public ResponseEntity<List<User>> searchAndFilterCustomers(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateRange,
            @RequestParam(required = false) String sortBy) {

        // Start with all customers or search results
        List<User> customers;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            customers = user_Repo.searchCustomers(searchTerm.toLowerCase(), true);
        } else {
            customers = user_Repo.findAll();
        }

        // Apply additional filters (same as before)
        if (status != null && !status.equals("all")) {
            customers = customers.stream()
                    .filter(user -> user.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        if (dateRange != null && !dateRange.equals("all")) {
            // Date filtering logic (same as before)
        }

        // Apply sorting (same as before)
        if (sortBy != null) {
            // Sorting logic (same as before)
        }

        return ResponseEntity.ok(customers);
    }
}
