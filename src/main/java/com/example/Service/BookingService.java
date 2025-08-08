package com.example.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

//import com.example.Controller.Screening;
import com.example.Entity.Admin_Entity;
import com.example.Entity.Booking;
import com.example.Entity.Movies;
import com.example.Entity.Screening;
import com.example.Entity.User;
import com.example.Repository.Admin_Repo;
import com.example.Repository.Booking_Repo;
import com.example.Repository.Movies_Repo;
import com.example.Repository.Payment_Repo;
import com.example.Repository.Screening_Repo;
import com.example.Repository.User_Repo;

import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
    @Autowired
    private Admin_Repo adminRepository;

    @Autowired
    private Movies_Repo moviesRepository;

    @Autowired
    private Booking_Repo bookingRepository;

    @Autowired
    private Screening_Repo screeningRepository;

    @Autowired
    private User_Repo userRepository;

    @Autowired
    private Payment_Repo paymentRepository;

    @Autowired
    private User_Repo user_Repo;

    // find by id
    public Optional<User> getById(long id) {
        return user_Repo.findById(id);
    }

    // Save booking
    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    // get admin by theatre name
    public Admin_Entity getAdmin_Entity(String theatrename) {
        return adminRepository.findByTheatreName(theatrename);
    }

    // get screening
    public Optional<Screening> getScreening(long movieId, long theatreId, String time, String screen) {
        return screeningRepository.findByMovieAndTheaterAndTimeAndScreen(movieId, theatreId, time, screen);
    }

    // find movie by name
    public Movies findbyName(String title) {
        return moviesRepository.findByTitle(title);
    }

    // get unique transaction
    public String TransactionId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        sb.append("TXN");

        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        sb.append(date);

        for (int i = 0; i < 6; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserId(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        // Debug logging
        bookings.forEach(booking -> {
            System.out.println("Booking ID: " + booking.getBookingReference());
            if (booking.getScreening() != null && booking.getScreening().getMovies() != null) {
                System.out.println("Movie: " + booking.getScreening().getMovies().getTitle());
            }
            if (booking.getScreening() != null && booking.getScreening().getTheater() != null) {
                System.out.println("Theater: " + booking.getScreening().getTheater().getTheatreName());
            }
        });

        return bookings;
    }

    public Booking findByBookingReference(String bookingReference) {
        return bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new RuntimeException("Booking not found with reference: " + bookingReference));
    }

    public Double getTotalAmountForTheater(Long theaterId) {
        return bookingRepository.findTotalAmountByTheaterId(theaterId);
    }

    public Double getTodaysRevenueByTheater(Long theaterId) {
        return bookingRepository.findDailyRevenueByTheater(theaterId, LocalDate.now());
    }

    // booking of today
    public int getTodayBookingsCountByAdmin(Long adminId) {
        return bookingRepository.findTotalTicketsTodayByTheaterId(adminId);
    }

    // find all booking by admin id
    public List<Booking> findByAdmin(long adminId) {
        return bookingRepository.findByAdminId(adminId);
    }

    public List<String> getAllBookedSeatsForScreening(Long screeningId) {
        List<List<String>> nestedSeats = bookingRepository.findBookedSeatsByScreeningId(screeningId);
        return nestedSeats.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Map<Long, List<String>> getBookedSeatsForMovie(Long movieId) {
        List<Screening> screenings = screeningRepository.findByMoviesId(movieId);
        Map<Long, List<String>> bookedSeatsMap = new HashMap<>();

        for (Screening screening : screenings) {
            List<String> bookedSeats = getAllBookedSeatsForScreening(screening.getId());
            bookedSeatsMap.put(screening.getId(), bookedSeats);
        }

        return bookedSeatsMap;
    }

    public List<Booking> findByAdminAndDateRange(long adminId, LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByTheaterIdAndBookingTimeBetween(adminId, start, end);
    }

    
}