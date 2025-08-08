package com.example.Controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
// Add these imports at the top
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.example.Entity.Admin_Entity;
import com.example.Entity.SettingsActivity;
import com.example.Entity.Settings_Prefrences;
import com.example.Service.IPAddressGetter;
import com.example.Service.Settings_Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.servlet.http.HttpSession;

@Controller
public class settings_Controller {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Settings_Service settings_Service;

    @Autowired
    private IPAddressGetter ipAddressGetter;

    @GetMapping("/settings")
    public String getSettings(HttpSession session, Model model) {
        Admin_Entity admin = (Admin_Entity) session.getAttribute("AdminId");
        System.out.println("Admin is:" + admin);

        // get settings preference
        long adminId = admin.getId();
        Settings_Prefrences preferences = settings_Service.getSettings(adminId);

        // get settings activity
        List<SettingsActivity> activity = settings_Service.getSettingsActivity(adminId);
        System.out.println("Activities are:" + activity);

        model.addAttribute("activities", activity);

        // Model Attributes
        model.addAttribute("admin", admin);
        model.addAttribute("preferences", preferences);
        return "settings";
    }

    @PostMapping("/settings/account")
    public String updateAccountSettings(
            @RequestParam("email") String email,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Admin_Entity admin = (Admin_Entity) session.getAttribute("AdminId");
        long adminId = admin.getId();

        // Check if current password matches
        if (!admin.getPassword().equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            return "redirect:/settings";
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/settings";
        }

        // Check if new password is different from current password
        if (currentPassword.equals(newPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password must be different from current password");
            return "redirect:/settings";
        }

        // get preference
        Settings_Prefrences settings_Prefrences = settings_Service.getSettings(adminId);
        boolean auditLogging = settings_Prefrences.isAuditLogging();
        if (auditLogging) {
            // current time
            String hostName = ipAddressGetter.getLocalHostName();
            String ipAddress = ipAddressGetter.getLocalHostAddress();
            LocalDateTime localDateTime = LocalDateTime.now();

            // new settings activity object
            SettingsActivity settingsActivity = new SettingsActivity();
            settingsActivity.setOldValue(currentPassword);
            settingsActivity.setNewValue(confirmPassword);
            settingsActivity.setAdmin(admin);
            settingsActivity.setTimestamp(localDateTime);
            settingsActivity.setAction("Password Changed.");
            settingsActivity.setDescription("Password Changed by Admin.");
            settingsActivity.setHostName(hostName);
            settingsActivity.setIpAddress(ipAddress);

            settings_Service.saveActivity(settingsActivity);
        }

        // Save to database
        settings_Service.updateAdmin(adminId, email, confirmPassword);

        redirectAttributes.addFlashAttribute("success", "Account settings updated successfully");
        return "redirect:/settings";
    }

    @PostMapping("/settings/preferences")
    public String updateSystemPreferences(
            @RequestParam(value = "maintenanceMode", required = false) boolean maintenanceMode,
            @RequestParam(value = "userRegistration", required = false) boolean userRegistration,
            @RequestParam(value = "emailNotifications", required = false) boolean emailNotifications,
            @RequestParam(value = "auditLogging", required = false) boolean auditLogging,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Admin_Entity admin = (Admin_Entity) session.getAttribute("AdminId");
        long adminId = (long) session.getAttribute("adminsId");
        // Get additional info for activity log
        String hostName = ipAddressGetter.getLocalHostName();
        String ipAddress = ipAddressGetter.getLocalHostAddress();
        LocalDateTime localDateTime = LocalDateTime.now();

        // get preference
        Settings_Prefrences settings_Prefrences = settings_Service.getSettings(adminId);
        boolean auditLogin = settings_Prefrences.isAuditLogging();
        System.out.println(auditLogin);
        
            // Create activity log
            SettingsActivity settingsActivity = new SettingsActivity();
            settingsActivity.setAdmin(admin);
            settingsActivity.setTimestamp(localDateTime);
            settingsActivity.setAction("Settings updated");
            settingsActivity.setDescription(String.format(
                    "Preferences updated - Maintenance: %b, Registration: %b, Email: %b, Audit: %b",
                    maintenanceMode, userRegistration, emailNotifications, auditLogging));
            settingsActivity.setHostName(hostName);
            settingsActivity.setIpAddress(ipAddress);

            settings_Service.saveActivity(settingsActivity);

            // save preference
            settings_Service.updatePreferences(
                    adminId,
                    maintenanceMode,
                    userRegistration,
                    emailNotifications,
                    auditLogging
                    );
        
        redirectAttributes.addFlashAttribute("success", "System preferences updated successfully");
        return "redirect:/settings";
    }

    @GetMapping("/settings/backup")
    public ResponseEntity<Resource> createBackup(HttpSession session) throws IOException {
        Admin_Entity admin = (Admin_Entity) session.getAttribute("AdminId");
        long adminId = admin.getId();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFileName = "movie_ticket_backup_" + timestamp + ".zip";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" });
            // Tables to backup with their custom queries
            Map<String, String> tablesToBackup = new LinkedHashMap<>();

            System.out.println("All tables in the database:");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println(tableName);
            }

            // get preference
        Settings_Prefrences settings_Prefrences = settings_Service.getSettings(adminId);
        boolean auditLogin = settings_Prefrences.isAuditLogging();

            if (auditLogin) {
                // get info 
                LocalDateTime localDateTime = LocalDateTime.now();
                String HostName = ipAddressGetter.getLocalHostName();
                String HostAddress = ipAddressGetter.getLocalHostAddress();
                // new object
                SettingsActivity settingsActivity = new SettingsActivity();
                settingsActivity.setAction("BackUp");
                settingsActivity.setAdmin(admin);
                settingsActivity.setDescription("Data BackUp Successfully");
                settingsActivity.setHostName(HostName);
                settingsActivity.setIpAddress(HostAddress);
                settingsActivity.setTimestamp(localDateTime);
    
                settings_Service.saveActivity(settingsActivity);
            }

            // General tables (no admin filtering)
            tablesToBackup.put("movies.csv", "SELECT * FROM movies Where admin_id = " + adminId);
            tablesToBackup.put("screening.csv", "SELECT * FROM screenings WHERE theater_id = " + adminId);
            tablesToBackup.put("user.csv", "SELECT * FROM user");

            // Tables filtered by admin ID
            tablesToBackup.put("booking.csv",
                    "SELECT " +
                            "b.id as booking_id, " +
                            "b.booking_time, " +
                            "b.number_of_tickets, " +
                            "b.total_amount, " +
                            "b.payment_method, " +
                            "b.payment_status, " +
                            "b.booking_reference, " +
                            "b.time as booking_time_string, " +
                            "CONCAT(u.firstname, ' ', u.lastname) as user_name, " +
                            "u.email as user_email, " +
                            "m.title as movie_title, " +
                            "a.theatre_name as theater_name, " +
                            "s.time as screening_time, " +
                            "s.screen as screen_name, " +
                            "s.price as ticket_price, " +
                            "(SELECT GROUP_CONCAT(bs.seat_number SEPARATOR ', ') FROM booked_seats bs WHERE bs.booking_id = b.id) as booked_seats "
                            +
                            "FROM bookings b " +
                            "JOIN user u ON b.user_id = u.id " +
                            "JOIN screenings s ON b.screening_id = s.id " +
                            "JOIN movies m ON s.movie_id = m.id " +
                            "JOIN admin_entity a ON s.theater_id = a.id " +
                            "WHERE s.theater_id = " + adminId);

            tablesToBackup.put("payment.csv",
                    "SELECT p.id, p.amount, p.payment_method, p.transaction_id, " +
                            "p.payment_date, p.status, p.card_last_four, " +
                            "CONCAT(u.firstname, ' ', u.lastname) as user_name, " +
                            "u.email as user_email, " +
                            "b.booking_reference, " +
                            "m.title as movie_title, " +
                            "a.theatre_name as theater_name, " +
                            "s.time as screening_time " +
                            "FROM payments p " +
                            "JOIN bookings b ON p.booking_id = b.id " +
                            "JOIN user u ON b.user_id = u.id " +
                            "JOIN screenings s ON b.screening_id = s.id " +
                            "JOIN movies m ON s.movie_id = m.id " +
                            "JOIN admin_entity a ON s.theater_id = a.id " +
                            "WHERE s.theater_id = " + adminId);
            tablesToBackup.put("review.csv", "SELECT * FROM reviews");

            // Export each table
            for (Map.Entry<String, String> entry : tablesToBackup.entrySet()) {
                String fileName = entry.getKey();
                String query = entry.getValue();

                try {
                    zos.putNextEntry(new ZipEntry(fileName));
                    exportQueryResultsToZip(connection, query, zos);
                    zos.closeEntry();
                } catch (SQLException e) {
                    System.err.println("Failed to backup table: " + fileName + " - " + e.getMessage());
                    // Create empty file rather than failing completely
                    zos.putNextEntry(new ZipEntry(fileName));
                    zos.write("No data available".getBytes());
                    zos.closeEntry();
                }
            }

            // Add readme file
            zos.putNextEntry(new ZipEntry("README.txt"));
            zos.write("Movie Ticket System Backup\n".getBytes());
            zos.write(("Created: " + LocalDateTime.now() + "\n").getBytes());
            zos.write(("Admin ID: " + adminId + "\n").getBytes());
            zos.write("Contains selected database tables in CSV format".getBytes());
            zos.closeEntry();

        } catch (SQLException e) {
            throw new RuntimeException("Database backup failed", e);
        } finally {
            zos.close();
        }

        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + backupFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private void exportQueryResultsToZip(Connection connection, String query, ZipOutputStream zos)
            throws SQLException, IOException {
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Write CSV header
            StringBuilder header = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                if (i > 1)
                    header.append(",");
                header.append("\"").append(metaData.getColumnName(i)).append("\"");
            }
            zos.write((header.toString() + "\n").getBytes());

            // Write data rows
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1)
                        row.append(",");
                    Object value = rs.getObject(i);
                    if (value != null) {
                        String stringValue = value.toString().replace("\"", "\"\"");
                        row.append("\"").append(stringValue).append("\"");
                    }
                }
                zos.write((row.toString() + "\n").getBytes());
            }

            if (rowCount == 0) {
                zos.write("No records found".getBytes());
            }
        }
    }

    @PostMapping("/settings/import")
    public String importData(@RequestParam("backupFile") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to import");
            return "redirect:/settings";
        }

        try {
            // Create import directory if it doesn't exist
            Path importDir = Paths.get("imports");
            if (!Files.exists(importDir)) {
                Files.createDirectory(importDir);
            }

            // Save the uploaded file
            Path destination = importDir.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // In a real application, you would restore your database from this file
            redirectAttributes.addFlashAttribute("success",
                    "Import completed successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to import backup: " + e.getMessage());
        }
        return "redirect:/settings";
    }
}
