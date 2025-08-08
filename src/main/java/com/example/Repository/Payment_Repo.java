package com.example.Repository;

import com.example.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Payment_Repo extends JpaRepository<Payment, Long> {
    Payment findByBookingId(Long bookingId);
}