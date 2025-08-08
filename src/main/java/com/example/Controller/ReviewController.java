package com.example.Controller;

import com.example.Entity.Review;
import com.example.Entity.Movies;
import com.example.Entity.User;
import com.example.Repository.Movies_Repo;
import com.example.Repository.ReviewRepository;
import com.example.Repository.User_Repo;
import com.example.Service.Movie_Service;
import com.example.Service.Review_Service;
import com.example.Service.User_Service;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ReviewController {
    @Autowired
    private Review_Service review_Service;

    @Autowired
    private Movie_Service movie_Service;

    @Autowired
    private User_Service user_Service;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private Movies_Repo movieRepository;

    @Autowired
    private User_Repo userRepository;

    @PostMapping("/submit-review")
    public String submitReview(
            @RequestParam("movieId") Long movieId,
            @RequestParam("rating") int rating,
            @RequestParam("comment") String comment,
            @RequestParam("userId") Long userId,
            HttpServletRequest request) {

        LocalDateTime localDateTime = LocalDateTime.now();

        Movies movies = movie_Service.getMovieById(movieId);
       
        
        Optional<User> userOptional = user_Service.getUserById(userId);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User not found with mobile: " + userId));
        

        Review review = new Review();
        review.setMovie(movies);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setReviewDate(localDateTime);
        review_Service.saveReview(review);

        return "redirect:/booking-history";
    }
}