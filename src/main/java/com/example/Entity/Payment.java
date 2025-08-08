package com.example.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking; //

    private double amount; //
    private String paymentMethod; //
    private String transactionId;
    private LocalDateTime paymentDate; //
    private String status; // "PENDING", "COMPLETED", "FAILED" //
    private String cardLastFour; // Last 4 digits if credit card

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", bookingId=" + (booking != null ? booking.getId() : "null") +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", paymentDate=" + paymentDate +
                ", status='" + status + '\'' +
                ", cardLastFour='" + (cardLastFour != null ? "****" + cardLastFour : "null") + '\'' +
                '}';
    }
}