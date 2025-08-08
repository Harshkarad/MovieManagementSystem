package com.example.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "settings_activities")
public class SettingsActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // E.g., "TOGGLE_MAINTENANCE_MODE", "CHANGE_SESSION_TIMEOUT"

    @Column(nullable = false)
    private String description; // Human-readable description

    @Column(name = "old_value")
    private String oldValue; // Previous setting value

    @Column(name = "new_value")
    private String newValue; // New setting value

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin_Entity admin; // Changed from admin_Entity to admin

    @Column(name = "ip_address")
    private String ipAddress; // For tracking origin

    @Column(name = "host_name")
    private String hostName;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Override
    public String toString() {
        return "SettingsActivity [id=" + id + ", action=" + action + ", description=" + description + ", oldValue="
                + oldValue + ", newValue=" + newValue + ", ipAddress=" + ipAddress
                + ", timestamp=" + timestamp + "]";
    }

}
