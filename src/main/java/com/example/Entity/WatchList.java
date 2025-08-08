package com.example.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
//@ToString
public class WatchList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime addedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movies movies;

    @Override
    public String toString() {
        return "WatchList{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", movieId=" + (movies != null ? movies.getId() : "null") +
                ", movieTitle=" + (movies != null ? "'" + movies.getTitle() + "'" : "null") +
                ", addedAt=" + addedAt +
                '}';
    }
}
