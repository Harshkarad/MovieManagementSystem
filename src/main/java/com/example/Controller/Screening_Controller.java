package com.example.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Entity.Admin_Entity;
import com.example.Entity.Booking;
import com.example.Entity.Movies;
import com.example.Entity.Screening;
import com.example.Repository.Booking_Repo;
import com.example.Service.BookingService;
import com.example.Service.Movie_Service;
import com.example.Service.Screening_Service;

import jakarta.servlet.http.HttpSession;

@Controller
public class Screening_Controller {
    @Autowired
    private Booking_Repo booking_Repo;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private Screening_Service screening_Service;

    @Autowired
    private Movie_Service movie_Service;

    @GetMapping("/screenings")
    public String showScreenings(Model model, HttpSession session) {
        String mobile = (String) session.getAttribute("mobile");
        Admin_Entity admin_Entity = movie_Service.findAdminByMobile(mobile);
        long adminid = admin_Entity.getId();

        List<Movies> movies = screening_Service.getActiveMoviesByAdminId(adminid);

        // list of all screenings
        List<Screening> screenings = screening_Service.getScreeningsByAdminId(adminid);

        // Get current date and time
        LocalDateTime now = LocalDateTime.now();
        // filter screenings
        List<Screening> upcomingScreenings = screenings.stream()
                .filter(screening -> {
                    // Check if screening time is in the future
                    return screening.getTime().isAfter(now);
                })
                .collect(Collectors.toList());

        // Add any model attributes needed for the screenings page
        model.addAttribute("theatre", admin_Entity);
        model.addAttribute("movies", movies);
        model.addAttribute("screenings", upcomingScreenings);
        return "screening"; // This should match your screening.html filename
    }

    @PostMapping("screenings/update/{screenId}")
    public String updateScreening(@PathVariable("screenId") long screenId,
            @RequestParam("movieId") long movieId,
            @RequestParam("theaterId") long theaterId,
            @RequestParam("screeningDate") LocalDate localDate,
            @RequestParam("startTime") LocalTime localTime,
            @RequestParam("status") String status,
            @RequestParam("price") double price) {

        // combine date and time
        LocalDateTime localDateTime = localDate.atTime(localTime);

        // get movie by id
        Movies movies = movie_Service.getMovieById(movieId);

        // find admin by id
        Optional<Admin_Entity> admOptional = screening_Service.findbyid(theaterId);
        Admin_Entity admin = admOptional.orElseThrow(() -> new RuntimeException("admin not found."));
        int numberofseats = admin.getNumberOfSeats();

        Screening screening = new Screening();
        screening.setAvailableSeats(numberofseats);
        screening.setTheater(admin);
        screening.setMovies(movies);
        screening.setTime(localDateTime);
        screening.setPrice(price);
        screening.setStatus(status);

        screening_Service.findByScreeningidOptional(screenId, screening);

        System.out.println("Movie:" + movieId + " Admin:" + theaterId + " Date:" + localDate + " Time:" + localTime
                + " status:" + status);
        System.out.println("Screening Id:" + screenId);
        return "redirect:/screenings";
    }

    @PostMapping("/screenings/save")
    public String savenewScreening(@RequestParam("movieId") long movieId,
            @RequestParam("theaterId") long theaterId,
            @RequestParam("screeningDate") LocalDate localDate,
            @RequestParam("startTime") LocalTime localTime,
            @RequestParam("status") String status,
            @RequestParam("price") double price) {
        // combine date and time
        LocalDateTime localDateTime = localDate.atTime(localTime);

        // get movie by id
        Movies movies = movie_Service.getMovieById(movieId);

        // find admin by id
        Optional<Admin_Entity> admOptional = screening_Service.findbyid(theaterId);
        Admin_Entity admin = admOptional.orElseThrow(() -> new RuntimeException("admin not found."));
        int capacity = admin.getNumberOfSeats();
        System.out.println();
        System.out.println();
        System.out.println("Capacity :" + capacity);
        System.out.println();
        System.out.println("Theatre Id:" + theaterId);

        Screening screening = new Screening();
        screening.setAvailableSeats(capacity);
        screening.setTheater(admin);
        screening.setMovies(movies);
        screening.setTime(localDateTime);
        screening.setPrice(price);
        screening.setStatus(status);

        screening_Service.savenewScreening(screening);
        return "redirect:/screenings";
    }

    @GetMapping("/filter")
    public String filterScreening(@RequestParam(value = "movie", required = false) String movieId,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "status", required = false) String status,
            HttpSession session,
            Model model) {

        String mobile = (String) session.getAttribute("mobile");
        Admin_Entity admin = movie_Service.findAdminByMobile(mobile);
        long adminId = admin.getId();

        List<Screening> filteredScreenings;

        // Get all movies for the dropdown
        List<Movies> movies = screening_Service.getActiveMoviesByAdminId(adminId);
        model.addAttribute("movies", movies);
        model.addAttribute("theatre", admin);

        // Apply filters
        if ("ALL".equals(movieId) && "ALL".equals(status) && date == null) {
            // No filters selected - return all screenings
            filteredScreenings = screening_Service.getScreeningsByAdminId(adminId);
        } else {
            // Apply filters based on selections
            filteredScreenings = screening_Service.filterScreenings(
                    adminId,
                    "ALL".equals(movieId) ? null : Long.parseLong(movieId),
                    date,
                    "ALL".equals(status) ? null : status);
        }

        model.addAttribute("screenings", filteredScreenings);

        // Return the same view but with filtered results
        return "screening";
    }

    @PostMapping("/screenings/delete/{id}")
    public String deleteScreening(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        Optional<Screening> screeningOpt = screening_Service.findById(id);
        String status = "CANCELLED";

        if (screeningOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Screening not found with ID: " + id);
            return "redirect:/screenings";
        }

        Screening screening = screeningOpt.get();

        // Check if there are any bookings for this screening
        List<Booking> bookings = booking_Repo.findByScreening(screening);

        if (!bookings.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Cannot delete screening - there are existing bookings for this screening");
            return "redirect:/screenings";
        }

        // If no bookings, update status to CANCELLED
        
        screening_Service.save(id,status);

        redirectAttributes.addFlashAttribute("success",
                "Screening has been cancelled successfully");
        return "redirect:/screenings";
    }
}