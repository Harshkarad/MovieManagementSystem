package com.example.Controller;

import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Entity.Booking;
import com.example.Entity.User;
import com.example.Service.BookingService;
import com.example.Service.User_Service;
import jakarta.servlet.http.HttpSession;

@Controller
public class Booking_Controller {
    @Autowired
    private User_Service user_Service;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/booking-history")
    public String bookingHistory(HttpSession session, Model model) {
        String mobile = (String) session.getAttribute("mobile");
        System.out.println("Mobile is :" + mobile);

        Optional<User> userOptional = user_Service.findByMobile(mobile);
        Long userId = userOptional.get().getId();

        List<com.example.Entity.Booking> booking = bookingService.getBookingsByUserId(userId);
        System.out.println(booking);

        // Debug output
        booking.forEach(bookings -> {
            System.out.println("Booking ID: " + bookings.getBookingReference());
            System.out.println("Movie: " + bookings.getScreening().getMovies().getTitle());
            System.out.println("Theater: " + bookings.getScreening().getTheater().getTheatreName());
            System.out.println("Time: " + bookings.getScreening().getTime());
            System.out.println("-------------------");
        });
        model.addAttribute("bookings", booking);
        model.addAttribute("userId", userId);
        return "booking-history";
    }

    @GetMapping("/print-ticket")
    public String printTicket(@RequestParam String bookingReference, Model model) {
        try {
            Booking booking = bookingService.findByBookingReference(bookingReference);

            // Format the LocalDateTime directly (no parsing needed)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");
            String formattedTime = booking.getScreening().getTime().format(formatter);
            System.out.println("time is:"+formattedTime);

            String movie_name = booking.getScreening().getMovies().getPoster();
            System.out.println(movie_name);
            System.out.println(booking.getScreening().getTime());
            // Add necessary attributes to the model
            model.addAttribute("booking", booking);
            model.addAttribute("movie", booking.getScreening().getMovies());
            model.addAttribute("theater", booking.getScreening().getTheater());
            model.addAttribute("formattedTime", formattedTime);

            return "download-ticket"; // Name of your Thymeleaf template
        } catch (RuntimeException e) {
            // Handle booking not found
            model.addAttribute("error", e.getMessage());
            return "error-page"; // Create an error page template
        }
    }
}
