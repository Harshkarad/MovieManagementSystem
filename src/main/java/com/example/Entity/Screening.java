package com.example.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "screenings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movies movies;

    @ManyToOne
    @JoinColumn(name = "theater_id", nullable = false)
    private Admin_Entity theater;

    private LocalDateTime time;
    private String screen;
    private int availableSeats;
    private double price;
    @Column(columnDefinition = "varchar(20) default 'ACTIVE'")
    private String status = "ACTIVE";

    @PrePersist
    @PreUpdate
    private void updateStatus() {
        LocalDateTime now = LocalDateTime.now();

        if (time.isBefore(now)) {
            status = "CANCELLED";
        } else {
            // If screening is today or in the future
            if (time.toLocalDate().isAfter(now.toLocalDate())) {
                // Screening is on a future date
                status = "UPCOMING";
            } else {
                // Screening is today - check the time
                if (time.toLocalTime().isAfter(now.toLocalTime())) {
                    status = "UPCOMING";
                } else {
                    status = "CANCELLED";
                }
            }
        }
    }

    // Custom toString() that excludes problematic fields
    @Override
    public String toString() {
        return "Screening{" +
                "id=" + id +
                ", movieId=" + (movies != null ? movies.getId() : "null") +
                ", movieTitle=" + (movies != null ? "'" + movies.getTitle() + "'" : "null") +
                ", theaterId=" + (theater != null ? theater.getId() : "null") +
                ", theaterName=" + (theater != null ? "'" + theater.getTheatreName() + "'" : "null") +
                ", time=" + time +
                ", screen='" + screen + '\'' +
                ", availableSeats=" + availableSeats +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}