package com.example.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movies movie;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int rating; // 1-5 or 1-10
    private String comment;
    private LocalDateTime reviewDate;

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", movieId=" + (movie != null ? movie.getId() : "null") +
                ", movieTitle=" + (movie != null ? "'" + movie.getTitle() + "'" : "null") +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", rating=" + rating +
                ", commentLength=" + (comment != null ? comment.length() : 0) +
                ", reviewDate=" + reviewDate +
                '}';
    }
}