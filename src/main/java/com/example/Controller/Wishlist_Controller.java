package com.example.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.Entity.Movies;
import com.example.Entity.User;
import com.example.Entity.WatchList;
import com.example.Service.Movie_Service;
import com.example.Service.User_Service;
import com.example.Service.Wishlist_Service;

import jakarta.servlet.http.HttpSession;

@Controller
public class Wishlist_Controller {
    @Autowired
    private Wishlist_Service wishlist_Service;

    @Autowired
    private Movie_Service movie_Service;

    @Autowired
    private User_Service user_Service;

    @GetMapping("/wishlist/{movieid}")
    public String addtoWishlist(@PathVariable("movieid") long movieId, HttpSession session) {
        long userId = (long) session.getAttribute("id");
        Optional<User> userOptional = user_Service.getUserById(userId);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User not found"));

        Movies movies = movie_Service.getMovieById(movieId);

        LocalDateTime localDateTime = LocalDateTime.now();

        WatchList watchList = new WatchList();
        watchList.setMovies(movies);
        watchList.setUser(user);
        watchList.setAddedAt(localDateTime);

        // saving watchlist
        wishlist_Service.saveWatchlist(watchList);
        return "redirect:/user-home";
    }

    @GetMapping("/wishlist/remove/{movieId}")
    public String removeFromWishlist(@PathVariable Long movieId, HttpSession session) {
        Long userId = (Long) session.getAttribute("id");
        wishlist_Service.deleteByUserIdAndMovieId(userId, movieId);
        return "redirect:/user-home";
    }

    @GetMapping("/watchlist")
    public String allWatchlist(HttpSession session,Model model){
        Long userId = (Long) session.getAttribute("id");
        List<WatchList> watchList = wishlist_Service.listOfWatch(userId);
        model.addAttribute("watchlist", watchList);
        return "watchlist";
    }
}
