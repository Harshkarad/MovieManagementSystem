package com.example.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

public class Movies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String director;
    private String releaseYear;

    @ElementCollection
    @CollectionTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "genre")
    private List<String> genre = new ArrayList<>();

    private String rating; // PG-13, R, etc.
    @Column(name = "description", length = 1000)
    private String description;
    @Column(name = "poster", length = 1000)
    private String poster;
    private String trailer;

    @ElementCollection
    @CollectionTable(name = "movie_cast", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "cast_member")
    private List<String> cast = new ArrayList<>();

    private String language;
    private Integer duration;
    private String status;
    private String country;
    private double averageRating; // 8.5, 7.2, etc.

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin_Entity admin_Entity;

    // In Movies.java
    @OneToMany(mappedBy = "movies", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Screening> screenings = new ArrayList<>();

    // Users who added this movie to their watchlist
    @OneToMany(mappedBy = "movies", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<WatchList> watchListEntries = new ArrayList<>();

    // Custom toString() that excludes problematic fields
    @Override
    public String toString() {
        return "Movies{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", releaseYear='" + releaseYear + '\'' +
                ", genre=" + (genre != null ? genre.size() + " genres" : "null") +
                ", rating='" + rating + '\'' +
                ", descriptionLength=" + (description != null ? description.length() : 0) +
                ", poster=" + (poster != null ? "available" : "null") +
                ", trailer=" + (trailer != null ? "available" : "null") +
                ", castMembers=" + (cast != null ? cast.size() : 0) +
                ", language='" + language + '\'' +
                ", duration=" + duration +
                ", status='" + status + '\'' +
                ", country='" + country + '\'' +
                ", averageRating=" + averageRating +
                ", adminId=" + (admin_Entity != null ? admin_Entity.getId() : "null") +
                ", screeningsCount=" + (screenings != null ? screenings.size() : 0) +
                ", watchListEntriesCount=" + (watchListEntries != null ? watchListEntries.size() : 0) +
                '}';
    }
}
