package com.example.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString
public class Admin_Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String theatreName;
    private int numberOfSeats;
    private String mobile;
    private String password;
    private String location;
    private String status;
    private String screen;
    private String qrscanner;
    private String auditLog = "Active";

    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude // Prevent circular reference in toString()
    private List<Screening> screenings = new ArrayList<>();

    @OneToMany(mappedBy = "admin_Entity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Movies> movies = new ArrayList<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<SettingsActivity> settingsActivities = new ArrayList<>();

    @OneToMany(mappedBy = "admin_Entity" , cascade = CascadeType.ALL , orphanRemoval = true , fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Settings_Prefrences> settings_Prefrences = new ArrayList<>();


    // Helper method to get screening times
    public List<LocalDateTime> getScreeningTimes() {
        return screenings.stream()
                .map(Screening::getTime)
                .collect(Collectors.toList());
    }

    // Helper method to get screening times for a specific movie
    public List<LocalDateTime> getScreeningTimesForMovie(Movies movie) {
        return screenings.stream()
                .filter(s -> s.getMovies().equals(movie))
                .map(Screening::getTime)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Admin_Entity{" +
                "id=" + id +
                
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", theatreName='" + theatreName + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                ", mobile='" + mobile + '\'' +
                ", password='[PROTECTED]'" + // Don't expose password in logs
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", screen='" + screen + '\'' +
                ", qrscanner='" + qrscanner + '\'' +
                ", screeningsCount=" + (screenings != null ? screenings.size() : 0) +
                ", moviesCount=" + (movies != null ? movies.size() : 0) +
                '}';
    }
}
