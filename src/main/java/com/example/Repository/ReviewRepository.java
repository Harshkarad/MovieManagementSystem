package com.example.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review , Long>{
    List<Review> findByMovieId(Long movieId);
}
