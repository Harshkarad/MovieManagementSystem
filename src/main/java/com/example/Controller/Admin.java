package com.example.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Entity.Admin_Entity;
import com.example.Entity.Booking;
import com.example.Entity.Movies;
import com.example.Entity.Payment;
import com.example.Entity.Review;
import com.example.Entity.Screening;
import com.example.Entity.User;
import com.example.Repository.Movies_Repo;
import com.example.Repository.Screening_Repo;
import com.example.Service.Admin_Service;
import com.example.Service.BookingService;
import com.example.Service.IPAddressGetter;
import com.example.Service.Movie_Service;
import com.example.Service.Review_Service;
import com.example.Service.Screening_Service;
import com.example.Service.Wishlist_Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class Admin {
    @Autowired
    private Admin_Service admin_service;

    @Autowired
    private Wishlist_Service wishlist_Service;

    @Autowired
    private Review_Service review_Service;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private Screening_Service screening_Service;

    @Autowired
    private Screening_Repo screening_Repo;

    @Autowired
    private Movies_Repo movies_Repo;

    @Autowired
    private IPAddressGetter ipaAddressGetter;

    @Autowired
    private Movie_Service movie_Service;

    @GetMapping("/admin-home")
    public String adminhome(HttpSession session,
            Model model,
            @RequestParam(required = false) String searchQuery) {
        String mobile = (String) session.getAttribute("mobile");

        System.out.println(mobile);
        if (mobile == null) {
            return "redirect:/";
        }
        // Find admin by mobile
        Admin_Entity admin = movie_Service.findAdminByMobile(mobile);
        long theatreId = admin.getId();
        session.setAttribute("IdAdmin", theatreId);
        session.setAttribute("AdminId", admin);

        // upcoming 7 screening
        List<Screening> screenings7available = admin_service.getNext7Screenings(theatreId);
        System.out.println("Screenings are:" + screenings7available);
        // sum of tickets
        double totalsumtickets = bookingService.getTotalAmountForTheater(theatreId);
        // System.out.println("Sum is:" + totalsumtickets);

        // todays booking
        double todaysRevenue = bookingService.getTodaysRevenueByTheater(theatreId);
        // System.out.println(todaysRevenue);

        // todays number of booking
        int totalbooking = bookingService.getTodayBookingsCountByAdmin(theatreId);
        // System.out.println(totalbooking);

        // count total movies
        long numberofmovies = movie_Service.TotalMovies(theatreId);

        List<Movies> moviesList = movie_Service.moviesList();

        // Add search functionality
        if (searchQuery != null && !searchQuery.isEmpty()) {
            moviesList = movie_Service.searchMovies(theatreId, searchQuery);
            model.addAttribute("isSearching", true); // Add flag to indicate search is active
        } else {
            moviesList = movie_Service.moviesList();
        }

        // models
        model.addAttribute("moviesList", moviesList);
        model.addAttribute("totalSum", totalsumtickets);
        model.addAttribute("todaysTotal", todaysRevenue);
        model.addAttribute("totalmovies", numberofmovies);
        model.addAttribute("totalbooking", totalbooking);
        return "home";
    }

    @PostMapping("/addmovie")
    public String add(@ModelAttribute Movies movies,
            HttpServletRequest request,
            HttpSession session) {

        String mobile = (String) session.getAttribute("mobile");

        System.out.println(mobile);
        if (mobile == null) {
            return "redirect:/";
        }
        // Find admin by mobile
        Admin_Entity admin = movie_Service.findAdminByMobile(mobile);

        System.out.println("Mobile from session: " + mobile);
        System.out.println("Found admin: " + admin);
        System.out.println("Movie admin before save: " + movies.getAdmin_Entity());

        // set admin
        movies.setAdmin_Entity(admin);

        if (movies.getCast() == null || movies.getCast().isEmpty()) {
            // Alternative approach if the above doesn't work
            String castParam = request.getParameter("cast");
            if (castParam != null && !castParam.isEmpty()) {
                movies.setCast(Arrays.asList(castParam.split(",")));
            }
        }
        // Handle screenings
        LocalDateTime localDateTime = LocalDateTime.now();
        String[] screeningDates = request.getParameterValues("screeningDates");
        String[] screeningTimes = request.getParameterValues("screeningTimes");
        String[] screens = request.getParameterValues("screens");
        String[] prices = request.getParameterValues("prices");
        String[] availableSeats = request.getParameterValues("availableSeats");
        if (screeningDates != null && screeningDates.length > 0) {
            for (int i = 0; i < screeningDates.length; i++) {
                Screening screening = new Screening();
                screening.setMovies(movies);
                screening.setTheater(admin); // Assuming theater is same as admin for simplicity
                screening.setTime(LocalDateTime.parse(
                        screeningDates[i] + "T" + screeningTimes[i]));
                screening.setScreen(screens[i]);
                screening.setPrice(Double.parseDouble(prices[i]));
                // screening.setAvailableSeats(Integer.parseInt(availableSeats[i]));
                movies.getScreenings().add(screening);
            }
        }

        movies.setCreatedAt(localDateTime);
        movie_Service.saveMovie(movies);
        return "redirect:/movies";
    }

    @GetMapping("/movies")
    public String moviespage(
            @RequestParam(required = false) String searchQuery,
            HttpSession session,
            Model model) {

        String mobile = (String) session.getAttribute("mobile");
        if (mobile == null) {
            return "redirect:/";
        }

        Admin_Entity admin = movie_Service.findAdminByMobile(mobile);
        long adminId = (long) admin.getId();

        List<Movies> moviesList;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            // Search movies by title or other criteria
            moviesList = movie_Service.searchMovies(adminId, searchQuery);
            System.out.println("Movies are:" + moviesList);
        } else {
            // Get all movies if no search query
            moviesList = movie_Service.moviesList();
        }

        List<Movies> ActiveMovies = admin_service.getByStatus(adminId);
        List<Movies> InActiveMovies = admin_service.getByInActiveStatus(adminId);

        model.addAttribute("ActiveMovies", ActiveMovies);
        model.addAttribute("InActiveMovies", InActiveMovies);
        model.addAttribute("movieList", moviesList);
        model.addAttribute("movies", new Movies());
        model.addAttribute("searchQuery", searchQuery); // Pass the search query back to the view

        return "movies";
    }

    @GetMapping("/movie-details")
    public String movieDetails(@RequestParam Long id, HttpSession session, Model model) {
        System.out.println(id);

        Long userId = (long) session.getAttribute("id");

        // list of theatres by their status
        List<Admin_Entity> theatre = movie_Service.getByStatus();
        System.out.println(theatre);

        // finding movie Id
        Movies movie = movie_Service.getMovieById(id);
        long movie_id = movie.getId();

        // finding wishlist
        boolean ifExist = wishlist_Service.checkWatchlist(movie_id, userId);

        // reviews of movie
        List<Review> reviews = review_Service.getReviewsByMovieId(movie_id);

        // Screening of a particular movies
        List<Screening> screenings = screening_Service.getScreening(movie_id);
        System.out.println("Screenings for movie " + id + ":");
        screenings.forEach(screening -> {
            System.out.println("ID: " + screening.getId() +
                    ", Time: " + screening.getTime() +
                    ", Screen: " + screening.getScreen());
        });

        List<Screening> list5 = screenings.stream().collect(Collectors.toList());
        System.out.println("List of theatres:" + list5);

        long theatre_id = movie.getAdmin_Entity().getId();

        // Get all screenings for this movie
        List<Screening> screenings1List = screening_Service.getScreening(movie_id);

        // Get booked seats for all screenings of this movie
        Map<Long, List<String>> bookedSeatsMap = bookingService.getBookedSeatsForMovie(movie_id);

        // Calculate available seats for each screening
        screenings1List.forEach(screening -> {
            int totalSeats = screening.getTheater().getNumberOfSeats();
            List<String> bookedSeats = bookedSeatsMap.getOrDefault(screening.getId(), new ArrayList<>());
            System.out.println("Booked seats size:" + bookedSeats.size());
            System.out.println("These are the:" + bookedSeats);
            int availableSeats = totalSeats - bookedSeats.size();
            screening.setAvailableSeats(availableSeats);
        });

        ObjectMapper objectMapper = new ObjectMapper();
        String bookedSeatsJson;
        try {
            bookedSeatsJson = objectMapper.writeValueAsString(bookedSeatsMap);
        } catch (JsonProcessingException e) {
            bookedSeatsJson = "{}";
        }
        int theaterCapacity = movie.getAdmin_Entity().getNumberOfSeats();

        theatre.forEach(theater -> {
            theater.getScreenings().size(); // This triggers the lazy loading
        });

        model.addAttribute("bookedSeatsJson", bookedSeatsJson);
        model.addAttribute("screeninglist", screenings1List);
        model.addAttribute("watchlist", ifExist);
        model.addAttribute("reviews", reviews);
        model.addAttribute("movie", movie);
        model.addAttribute("screenings", screenings);
        model.addAttribute("theaterCapacity", theaterCapacity);
        return "moviedetail";
    }

    @GetMapping("/edit-movie/{id}")
    public String showEditMovieForm(@PathVariable("id") long id, Model model) {
        // Get the movie from database
        Movies movie = movies_Repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));

        // Get all screenings for this movie
        List<Screening> screenings = screening_Repo.findByMoviesId(id);

        model.addAttribute("movie", movie);
        model.addAttribute("screenings", screenings);

        return "edit-movie"; // This will be the Thymeleaf template name
    }

    @PostMapping("/update-movie/{id}")
    public String updateMovie(@PathVariable("id") long id, @ModelAttribute("movie") Movies movie,
            @RequestParam("screeningIds") List<Long> screeningIds,
            @RequestParam("screeningDates") List<String> screeningDates,
            @RequestParam("screeningTimes") List<String> screeningTimes,
            @RequestParam("screens") List<String> screens,
            @RequestParam("prices") List<Double> prices,
            @RequestParam("availableSeats") List<Integer> availableSeats,
            RedirectAttributes redirectAttributes) {

        // Get existing movie
        Movies existingMovie = movies_Repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));

        // Update movie properties
        existingMovie.setTitle(movie.getTitle());
        existingMovie.setDirector(movie.getDirector());
        existingMovie.setReleaseYear(movie.getReleaseYear());
        existingMovie.setGenre(movie.getGenre());
        existingMovie.setRating(movie.getRating());
        existingMovie.setDescription(movie.getDescription());
        existingMovie.setPoster(movie.getPoster());
        existingMovie.setTrailer(movie.getTrailer());
        existingMovie.setCast(movie.getCast());
        existingMovie.setLanguage(movie.getLanguage());
        existingMovie.setDuration(movie.getDuration());
        existingMovie.setStatus(movie.getStatus());

        // Update screenings
        List<Screening> updatedScreenings = new ArrayList<>();
        for (int i = 0; i < screeningDates.size(); i++) {
            LocalDateTime dateTime = LocalDateTime.parse(
                    screeningDates.get(i) + "T" + screeningTimes.get(i));

            Screening screening;
            if (i < screeningIds.size() && screeningIds.get(i) != null) {
                // Update existing screening
                screening = screening_Repo.findById(screeningIds.get(i))
                        .orElse(new Screening());
            } else {
                // Create new screening
                screening = new Screening();
                screening.setMovies(existingMovie);
                screening.setTheater(existingMovie.getAdmin_Entity());
            }

            screening.setTime(dateTime);
            screening.setScreen(screens.get(i));
            screening.setPrice(prices.get(i));
            // screening.setAvailableSeats(availableSeats.get(i));

            updatedScreenings.add(screening);
        }

        // Remove screenings that were deleted
        List<Screening> existingScreenings = screening_Repo.findByMoviesId(id);
        for (Screening existing : existingScreenings) {
            if (!updatedScreenings.stream().anyMatch(s -> s.getId() != null && s.getId().equals(existing.getId()))) {
                screening_Repo.delete(existing);
            }
        }

        // Save everything
        movies_Repo.save(existingMovie);
        screening_Repo.saveAll(updatedScreenings);

        redirectAttributes.addFlashAttribute("successMessage", "Movie updated successfully!");
        return "redirect:/movies";
    }

    @GetMapping("/payment-success")
    public String paymentSuccess(
            @RequestParam String ref,
            @RequestParam String movie,
            @RequestParam String theater,
            @RequestParam String time,
            @RequestParam String screen,
            @RequestParam String seats,
            @RequestParam int tickets,
            @RequestParam double total,
            @RequestParam String payment,
            @RequestParam Long screeningId, // Add this parameter
            Model model,
            HttpSession session) {

        Long screenidsLong = (Long) session.getAttribute("scId");
        System.out.println("Updated screen id:" + screenidsLong);
        System.out.println("Screening Id:" + screeningId);
        System.out.println("Reference Id:" + ref);
        System.out.println("time is:" + time);
        // Debug logging
        System.out.println("Received screeningId: " + screeningId);
        System.out.println("All params - ref: " + ref + ", movie: " + movie +
                ", theater: " + theater + ", screeningId: " + screeningId);

        String TransId = bookingService.TransactionId();

        Optional<Screening> screening1 = screening_Service.findById(screeningId);
        Screening screening = screening1.orElseThrow(() -> new RuntimeException("Screening not found."));
        long userId = (long) session.getAttribute("id");
        Optional<User> user1 = bookingService.getById(userId);
        User user = user1.orElseThrow(() -> new RuntimeException("User not found"));

        int available = screening.getAvailableSeats();
        System.out.println("Available :" + available);
        int afterBook = available - tickets;
        System.out.println("Recent seats:" + afterBook);
        screening.setAvailableSeats(afterBook);

        Booking booking = new Booking();

        // Convert seats string back to list
        List<String> seatList = Arrays.asList(seats.split(","));
        booking.setScreening(screening);
        booking.setSeats(seatList);
        booking.setBookingReference(ref.replaceAll("^\"|\"$", ""));
        booking.setTotalAmount(total);
        booking.setPaymentMethod(payment.replaceAll("^\"|\"$", ""));
        booking.setNumberOfTickets(tickets);
        booking.setUser(user);
        booking.setBookingTime(LocalDateTime.now());
        booking.setPaymentStatus("COMPLETED");

        Payment paymentEntity = new Payment();
        paymentEntity.setTransactionId(TransId);
        paymentEntity.setBooking(booking);
        paymentEntity.setAmount(total);
        paymentEntity.setPaymentMethod(payment.replaceAll("^\"|\"$", ""));
        paymentEntity.setPaymentDate(LocalDateTime.now());
        paymentEntity.setStatus("COMPLETED");
        // Save the booking
        booking.setPayment(paymentEntity);

        bookingService.saveBooking(booking);

        // paymentRepository.save(paymentEntity);

        // Clean parameters for the view
        model.addAttribute("ref", ref.replaceAll("^\"|\"$", ""));
        model.addAttribute("movie", movie.replaceAll("^\"|\"$", ""));
        model.addAttribute("theater", theater.replaceAll("^\"|\"$", ""));
        model.addAttribute("time", time.replaceAll("^\"|\"$", ""));
        model.addAttribute("screen", screen.replaceAll("^\"|\"$", ""));
        model.addAttribute("seats", seatList); // Use the cleaned list
        model.addAttribute("tickets", tickets);
        model.addAttribute("total", total);
        model.addAttribute("payment", payment.replaceAll("^\"|\"$", ""));

        return "payment-success";
    }

    @PostMapping("/toggle-movie-status/{id}")
    public String toggleMovieStatus(@PathVariable long id) {
        // Get the movie from database
        Movies movie = movies_Repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));

        // Toggle the status
        String newStatus = movie.getStatus().equalsIgnoreCase("ACTIVE") ? "INACTIVE" : "ACTIVE";
        movie.setStatus(newStatus);

        // Save the updated movie
        movies_Repo.save(movie);

        return "redirect:/movies";
    }

    @PostMapping("/delete{id}")
    public String deleteMovie(@PathVariable long id) {
        movie_Service.changeStatus(id);
        return "redirect:/movies";
    }

    @PutMapping("/change-InActive/{id}")
    public String inactiveStatus(@PathVariable("id") long id) {
        movie_Service.updateMovieStatus(id, "INACTIVE");
        return "redirect:/admin-home";
    }

    @PutMapping("/change-Active/{id}")
    public String activeStatus(@PathVariable("id") long id) {
        movie_Service.updateMovieStatus(id, "ACTIVE");
        return "redirect:/admin-home";
    }
}
