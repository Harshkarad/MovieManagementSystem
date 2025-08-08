package com.example.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Getter
@Setter
//@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstname;
    private String lastname;
    private String email;
    private String mobile;
    private String password;
    private String location;
    private String status = "Active";

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String profileUrl; // Can store URL or file path to profile image

    // Watchlist items for this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Add this to break the circular reference
    @ToString.Exclude // Avoid circular reference in toString()
    private List<WatchList> watchListItems = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore // Add this to break the circular reference
    private List<Booking> bookings;

    // Add these methods to your User class
    public int getTotalBookings() {
        return this.bookings != null ? this.bookings.size() : 0;
    }

    public double getTotalSpent() {
        if (this.bookings == null || this.bookings.isEmpty()) {
            return 0.0;
        }
        return this.bookings.stream()
                .mapToDouble(Booking::getTotalAmount)
                .sum();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", password='[PROTECTED]'" +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", profileUrl=" + (profileUrl != null ? "available" : "null") +
                ", watchListItemsCount=" + (watchListItems != null ? watchListItems.size() : 0) +
                ", bookingsCount=" + getTotalBookings() +
                ", totalSpent=" + getTotalSpent() +
                '}';
    }
}
