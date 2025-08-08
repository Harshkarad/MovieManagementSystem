package com.example.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString(exclude = "payment") // Exclude payment to prevent circular reference
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // or LAZY with proper initialization
    @JoinColumn(name = "screening_id", nullable = false)

    private Screening screening;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)

    private User user; //

    private LocalDateTime bookingTime;
    private int numberOfTickets; // choosesn by user to book

    @ElementCollection
    @CollectionTable(name = "booked_seats", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "seat_number")
    private List<String> seats; // chosses ticket as A1,A2, etc

    // In Booking.java
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment; //

    private double totalAmount; //
    private String paymentMethod; //
    private String paymentStatus; //
    private String bookingReference; // booking id eg.BKN1000000
    private String time; //

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", screeningId=" + (screening != null ? screening.getId() : "null") +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", bookingTime=" + bookingTime +
                ", numberOfTickets=" + numberOfTickets +
                ", seats=" + (seats != null ? seats.size() + " seats" : "null") +
                ", totalAmount=" + totalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", bookingReference='" + bookingReference + '\'' +
                ", time='" + time + '\'' +
                ", paymentId=" + (payment != null ? payment.getId() : "null") +
                '}';
    }
}