package com.example.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Entity.WatchList;
import com.example.Repository.WatchList_Repo;

@Service
public class Wishlist_Service {
    @Autowired
    private WatchList_Repo watchList_Repo;

    // save WatchList
    public WatchList saveWatchlist(WatchList watchList){
        return watchList_Repo.save(watchList);
    }

    // check if movie is added to watchlist
    public boolean checkWatchlist(long movieId , long userId){
        return watchList_Repo.existsByMoviesIdAndUserId(movieId, userId);
    }

    // delete the wishlist
    public void deleteByUserIdAndMovieId(long userId , long movieId){
        watchList_Repo.deleteByUserIdAndMoviesId(userId, movieId);
    }

    // list of watchlist
    public List<WatchList> listOfWatch(long userId){
        return watchList_Repo.findByUserId(userId);
    }
}
