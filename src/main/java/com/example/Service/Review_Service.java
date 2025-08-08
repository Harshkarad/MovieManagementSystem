package com.example.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Entity.Review;
import com.example.Entity.Screening;
import com.example.Repository.ReviewRepository;
import com.example.Repository.Screening_Repo;
@Service
public class Review_Service {
    @Autowired
    private Screening_Repo screening_Repo;

    @Autowired
    private ReviewRepository reviewRepository;

    // Save Review 
    public Review saveReview(Review review){
        return reviewRepository.save(review);
    }

    // find by movie id
    public List<Review> getReviewsByMovieId(Long movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    // public Optional<Screening> updateScreening(long screenId){
    //     return screening_Repo.findById(screenId);
    // }
}
