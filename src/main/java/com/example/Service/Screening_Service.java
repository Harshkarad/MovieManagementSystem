package com.example.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Entity.Admin_Entity;
import com.example.Entity.Movies;
import com.example.Entity.Screening;
import com.example.Repository.Admin_Repo;
import com.example.Repository.Booking_Repo;
import com.example.Repository.Movies_Repo;
import com.example.Repository.Screening_Repo;

@Service
public class Screening_Service {
    @Autowired
    private Booking_Repo booking_Repo;

    @Autowired
    private Admin_Repo admin_Repo;

    @Autowired
    private Movies_Repo movies_Repo;

    @Autowired
    private Screening_Repo screening_Repo;

    public List<Screening> getScreening(long movieId) {
        return screening_Repo.findByMoviesId(movieId);
    }

    public Optional<Screening> findById(Long id) {
        return screening_Repo.findById(id);
    }

    // find movie by admin id
    public List<Movies> getActiveMoviesByAdminId(long adminId) {
        return movies_Repo.findAllMoviesByAdminId(adminId);
    }

    // Method to get all screenings by admin ID
    public List<Screening> getScreeningsByAdminId(Long adminId) {
        // Using the custom query method
        return screening_Repo.findByAdminIdOrderByTimeDesc(adminId);
    }

    // find admin by id
    public Optional<Admin_Entity> findbyid(long adminId) {
        return admin_Repo.findById(adminId);
    }

    public Optional<Screening> findByScreeningidOptional(Long id, Screening screening) {
        // Optional<Screening> screeningOptional = screening_Repo.findById(id);
        // if (screeningOptional.isPresent()) {
        // screening_Repo.save(screening);
        // }
        return screening_Repo.findById(id).map(existing -> {
            // save values to db
            existing.setTheater(screening.getTheater());
            existing.setMovies(screening.getMovies());
            existing.setTime(screening.getTime());
            existing.setPrice(screening.getPrice());
            existing.setStatus(screening.getStatus());

            return screening_Repo.save(existing);
        });
    }

    // save new screening
    public Screening savenewScreening(Screening screening) {
        return screening_Repo.save(screening);
    }

    public List<Screening> filterScreenings(long adminId, Long movieId, LocalDate date, String status) {
        // Create a query based on the provided parameters
        // This is a simplified example - you'll need to adapt it to your actual query
        // method

        if (movieId == null && date == null && status == null) {
            return getScreeningsByAdminId(adminId);
        }

        // Example implementation using JPA repository
        return screening_Repo.findByFilters(
                adminId,
                movieId,
                date != null ? date.atStartOfDay() : null,
                date != null ? date.plusDays(1).atStartOfDay() : null,
                status);
    }


    public void save(long screenId, String status){
        screening_Repo.updateStatusById(screenId, status);
    }
}
