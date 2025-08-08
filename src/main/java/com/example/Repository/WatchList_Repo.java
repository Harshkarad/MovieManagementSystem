package com.example.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Entity.WatchList;

import jakarta.transaction.Transactional;

public interface WatchList_Repo extends JpaRepository<WatchList , Long>{
    boolean existsByMoviesIdAndUserId(Long movieId, Long userId);

    // Method 1: Using derived query method
    @Transactional
    void deleteByUserIdAndMoviesId(Long userId, Long movieId);

    // find all list of users watchlist
    List<WatchList> findByUserId(long userId);
}
