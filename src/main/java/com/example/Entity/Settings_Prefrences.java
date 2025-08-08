package com.example.Entity;

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

@Entity
@Table(name = "Settings_Prefrences")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Settings_Prefrences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean maintenanceMode;
    private boolean userRegistration;
    private boolean emailNotifications;
    private boolean auditLogging;
    

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin_Entity admin_Entity; // Changed from admin_Entity to admin

    @Override
    public String toString() {
        return "Settings_Prefrences [maintenanceMode=" + maintenanceMode + ", userRegistration=" + userRegistration
                + ", emailNotifications=" + emailNotifications + ", auditLogging=" + auditLogging + "]";
    }
}
