package com.example.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Entity.Movies;
import com.example.Entity.Payment;
import com.example.Entity.Screening;
import com.example.Entity.User;
import com.example.Repository.Admin_Repo;
import com.example.Repository.Booking_Repo;
import com.example.Repository.Movies_Repo;
import com.example.Repository.Payment_Repo;
import com.example.Repository.Screening_Repo;
import com.example.Repository.User_Repo;
import com.example.Entity.Admin_Entity;
import com.example.Entity.Booking;

@Service
public class Movie_Service {
    @Autowired
    private User_Repo user_Repo;

    @Autowired
    private Booking_Repo booking_Repo;

    @Autowired
    private Payment_Repo payment_Repo;

    @Autowired
    private Screening_Repo screening_Repo;

    @Autowired
    private Movies_Repo movies_Repo;

    @Autowired
    private Admin_Repo admin_Repo;

    // Save movies into database
    public Movies saveMovie(Movies movie) {
        return movies_Repo.save(movie);
    }

    // find all movies list
    public List<Movies> moviesList() {
        return movies_Repo.findAll();
    }

    // New method to get movie by ID
    public Movies getMovieById(Long id) {
        Optional<Movies> movie = movies_Repo.findById(id);
        if (movie.isPresent()) {
            return movie.get();
        } else {
            throw new RuntimeException("Movie not found with id: " + id);
        }
    }

    // change status
    public void changeStatus(long id) {
        Movies movies = movies_Repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not Found"));
        if (movies.getStatus().equals("ACTIVE")) {
            movies.setStatus("Passive");
        }
        if (movies.getStatus().equals("PASSIVE")) {
            movies.setStatus("ACTIVE");
        }
        movies_Repo.save(movies);
    }

    public List<Admin_Entity> getByStatus() {
        return admin_Repo.findByStatus("Active");
    }

    // find all theatre
    public List<Admin_Entity> allTheatre() {
        return admin_Repo.findAll();
    }

    // find by id
    public Optional<Screening> movieOptional(long id) {
        return screening_Repo.findById(id);
    }

    // Implement this method to find admin by mobile
    public Admin_Entity findAdminByMobile(String mobile) {

        // This is just an example - adjust based on your actual repository
        return admin_Repo.findByMobile(mobile);
    }

    public Booking saveBooking(Booking booking) {
        return booking_Repo.save(booking);
    }

    public Payment savePayment(Payment payment) {
        return payment_Repo.save(payment);
    }

    public Booking getBookingById(Long id) {
        return booking_Repo.findById(id).orElse(null);
    }

    public Payment getPaymentByBookingId(Long bookingId) {
        return payment_Repo.findByBookingId(bookingId);
    }

    public Optional<User> findUserByMobile(String mobile) {
        return user_Repo.findByMobile(mobile);
    }

    // count number of movies
    public long TotalMovies(long adminId) {
        return movies_Repo.countMoviesByAdminId(adminId);
    }

    public List<Movies> searchMovies(long adminId, String searchQuery) {
        // Implement your search logic here
        // For example, search by title, genre, director, etc.
        return movies_Repo.findByAdminIdAndTitleContainingIgnoreCase(adminId, searchQuery);
    }

    public void updateMovieStatus(long id, String newStatus) {
        movies_Repo.updateStatusById(id, newStatus);

    }

}
