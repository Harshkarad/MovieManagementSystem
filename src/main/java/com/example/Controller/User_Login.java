package com.example.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.relation.RelationNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Entity.Admin_Entity;
import com.example.Entity.Movies;
import com.example.Entity.SettingsActivity;
import com.example.Entity.Settings_Prefrences;
import com.example.Entity.User;
import com.example.Service.Admin_Service;
import com.example.Service.IPAddressGetter;
import com.example.Service.Movie_Service;
import com.example.Service.OTPSender;
import com.example.Service.Settings_Service;
import com.example.Service.User_Service;

import jakarta.servlet.http.HttpSession;

@Controller

public class User_Login {
    @Autowired
    private OTPSender otpSender;

    @Autowired
    private Admin_Service admin_Service;

    @Autowired
    private User_Service user_Service;

    @Autowired
    private Movie_Service movie_Service;

    @Autowired
    private IPAddressGetter ipAddressGetter;

    @Autowired
    private Settings_Service settings_Service;

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user-home")
    public String userhome(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("id");

        if (userId != null) {
            Optional<User> userOptional = user_Service.getUserById(userId);
            if (userOptional.isPresent()) {
                model.addAttribute("user", userOptional.get());
            }
        }

        List<Movies> onlyNonMainatened = user_Service.maintenanceModeMovies();
        System.out.println("Active movies:" + onlyNonMainatened);

        List<Movies> ActiveMainatain = onlyNonMainatened.stream()
                .filter(movie -> "ACTIVE".equals(movie.getStatus()))
                .collect(Collectors.toList());

        List<Movies> moviesList = movie_Service.moviesList();
        List<Movies> activeMoviesList = moviesList.stream()
                .filter(movie -> "ACTIVE".equals(movie.getStatus()))
                .collect(Collectors.toList());

        // Get today's date (without time)
        LocalDate today = LocalDate.now();
        // Calculate the date 7 days before today
        LocalDate sevenDaysBeforeDate = today.minusDays(7);

        List<Movies> moviesCreated7DaysAgo = moviesList.stream()
                .filter(movie -> {
                    LocalDateTime createdAt = movie.getCreatedAt(); // Assuming getCreatedAt() returns LocalDateTime
                    if (createdAt == null)
                        return false;
                    // Compare only the date part (ignore time)
                    return createdAt.toLocalDate().isEqual(sevenDaysBeforeDate);
                })
                .collect(Collectors.toList());

        LocalDateTime fourDaysAgo = LocalDateTime.now().minusDays(2);

        List<Movies> recentMovies = moviesList.stream()
                .filter(movie -> {
                    LocalDateTime createdAt = movie.getCreatedAt();
                    return createdAt != null && !createdAt.isBefore(fourDaysAgo);
                })
                .collect(Collectors.toList());
        System.out.println(recentMovies);
        model.addAttribute("recentMovies", recentMovies);
        model.addAttribute("movies", ActiveMainatain);
        return "userhome";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String mobile,
            @RequestParam("password") String password,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Admin_Entity> getDataAdmin = admin_Service.AdminExist(mobile, password);

        if (getDataAdmin.isPresent()) {
            Admin_Entity newAdmin = getDataAdmin.get();

            String Adminmobile = getDataAdmin.get().getMobile();
            String Adminpassword = getDataAdmin.get().getPassword();
            if (mobile.equals(Adminmobile) && password.equals(Adminpassword)) {
                long adId = newAdmin.getId();
                session.setAttribute("adminsId", adId);

                // get settings prefrence
                Settings_Prefrences settings_Prefrences = settings_Service.getSettings(adId);
                boolean auditValue = settings_Prefrences.isAuditLogging();
                // check id audit is on or not
                if (auditValue == true) {
                    String hostName = ipAddressGetter.getLocalHostName();
                    String ipAddress = ipAddressGetter.getLocalHostAddress();
                    LocalDateTime localDateTime = LocalDateTime.now();

                    SettingsActivity settingsActivity = new SettingsActivity();
                    settingsActivity.setDescription("Admin Logged In.");
                    settingsActivity.setAction("Login");
                    settingsActivity.setIpAddress(ipAddress);
                    settingsActivity.setHostName(hostName);
                    settingsActivity.setAdmin(newAdmin);
                    settingsActivity.setTimestamp(localDateTime);
                    settings_Service.saveActivity(settingsActivity);

                }

                session.setAttribute("mobile", Adminmobile);
                return "redirect:/admin-home";
            }
        }

        boolean loginverify = user_Service.verifylogin(mobile, password);
        if (loginverify) {
            Optional<User> optional = user_Service.findByMobile(mobile);
            String status = optional.get().getStatus();
            System.out.println("Status is :" + status);
            if (status.equals("Banned")) {
                redirectAttributes.addFlashAttribute("status", "");
                return "redirect:/";
            }
            Long id = optional.get().getId();
            System.out.println("Id is :" + id);

            // session for id
            session.setAttribute("id", id);
            session.setAttribute("mobile", mobile); // Store user in session
            return "redirect:/user-home"; // Redirect to user home endpoint

        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> response = new HashMap<>();

        // Check if email is verified
        Boolean isEmailVerified = (Boolean) session.getAttribute("emailVerified");
        String verifiedEmail = (String) session.getAttribute("verifiedEmail");

        if (isEmailVerified == null || !isEmailVerified || !user.getEmail().equals(verifiedEmail)) {
            response.put("success", false);
            response.put("message", "Please verify your email first");
            return ResponseEntity.badRequest().body(response);
        }

        // Validate user input
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            response.put("success", false);
            response.put("message", "Validation errors");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        // Check if user already exists
        if (user_Service.UserExist(user.getMobile(), user.getEmail())) {
            response.put("success", false);
            response.put("message", "User already exists with this email or mobile number");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Hash password before saving
            user.setPassword(user.getPassword());
            user.setStatus("Active");
            user.setCreatedAt(LocalDateTime.now());

            User savedUser = user_Service.saveUser(user);
            // Clear verification flags after successful registration
            session.removeAttribute("emailVerified");
            session.removeAttribute("verifiedEmail");
            session.removeAttribute("emailOtp");
            session.removeAttribute("emailToVerify");

            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("userId", savedUser.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verify-otp")
    @ResponseBody
    public Map<String, Object> verifyOtp(@RequestParam String email,
            @RequestParam String otp,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Check if OTP expired
        Long expiryTime = (Long) session.getAttribute("otpExpiry");
        if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
            response.put("success", false);
            response.put("message", "OTP expired. Please request a new one.");
            return response;
        }

        String sessionOtp = (String) session.getAttribute("emailOtp");
        String sessionEmail = (String) session.getAttribute("emailToVerify");

        if (sessionOtp == null || sessionEmail == null) {
            response.put("success", false);
            response.put("message", "OTP verification failed. Please try again.");
            return response;
        }

        if (sessionOtp.equals(otp) && sessionEmail.equals(email)) {
            session.setAttribute("emailVerified", true);
            session.setAttribute("verifiedEmail", email);
            response.put("success", true);
            response.put("message", "Email verified successfully");
        } else {
            response.put("success", false);
            response.put("message", "Invalid OTP");
        }

        return response;
    }

    // View profile
    @GetMapping("/profile/{id}")
    public String showProfile(@PathVariable Long id, Model model) {
        Optional<User> getDataOptional = user_Service.getUserById(id);

        if (getDataOptional.isPresent()) {
            User user = getDataOptional.get();
            model.addAttribute("user", user);
            return "user-profile";
        } else {
            return "redirect:/";
        }

    }

    // New method to handle form submission
    @PostMapping("/save")
    public String saveProfile(@ModelAttribute("user") User updatedUser,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "user-profile";
        }

        try {
            // Get the existing user from database
            User existingUser = user_Service.getUserById(updatedUser.getId())
                    .orElseThrow(() -> new RelationNotFoundException("User not found"));

            // Update only the allowed fields
            existingUser.setFirstname(updatedUser.getFirstname());
            existingUser.setLastname(updatedUser.getLastname());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setMobile(updatedUser.getMobile());
            existingUser.setLocation(updatedUser.getLocation());
            existingUser.setProfileUrl(updatedUser.getProfileUrl());

            // Save the updated user (password remains unchanged)
            user_Service.updateUser(existingUser);

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/profile/" + existingUser.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            return "redirect:/profile/" + updatedUser.getId();
        }
    }

    @PostMapping("/send-otp")
    @ResponseBody
    public Map<String, Object> sendOtp(@RequestParam String email, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            response.put("success", false);
            response.put("message", "Invalid email format");
            return response;
        }

        try {
            // Check if email already registered
            if (user_Service.emailExists(email)) {
                response.put("success", false);
                response.put("message", "Email already registered");
                return response;
            }

            String otp = otpSender.sendOTP(email);
            if (otp != null) {
                // Store OTP in session with expiration (5 minutes)
                session.setAttribute("emailOtp", otp);
                session.setAttribute("emailToVerify", email);
                session.setAttribute("otpExpiry", System.currentTimeMillis() + 300000); // 5 minutes

                response.put("success", true);
                response.put("message", "OTP sent successfully");
                return response;
            } else {
                response.put("success", false);
                response.put("message", "Failed to send OTP");
                return response;
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error sending OTP: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage(Model model) {
        System.out.println("Forgot password page accessed");
        return "forgot-password";
    }

    @PostMapping("/send-password-reset-otp")
    public ResponseEntity<?> sendPasswordResetOtp(@RequestBody Map<String, String> request, HttpSession session) {
        String email = request.get("email");
        Map<String, Object> response = new HashMap<>();

        // Check if email exists in database
        if (!user_Service.emailExists(email)) {
            response.put("success", false);
            response.put("message", "No account found with this email address");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Generate and send OTP
            String otp = otpSender.sendOTP(email);

            // Store OTP in session with expiration (5 minutes)
            session.setAttribute("resetOtp", otp);
            session.setAttribute("resetEmail", email);
            session.setAttribute("otpExpiry", System.currentTimeMillis() + 300000); // 5 minutes

            response.put("success", true);
            response.put("message", "OTP sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send OTP. Please try again.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/verify-password-reset-otp")
    public ResponseEntity<?> verifyPasswordResetOtp(@RequestBody Map<String, String> request, HttpSession session) {
        String email = request.get("email");
        String otp = request.get("otp");
        Map<String, Object> response = new HashMap<>();

        // Check if OTP expired
        Long expiryTime = (Long) session.getAttribute("otpExpiry");
        if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
            response.put("success", false);
            response.put("message", "OTP expired. Please request a new one.");
            return ResponseEntity.badRequest().body(response);
        }

        String sessionOtp = (String) session.getAttribute("resetOtp");
        String sessionEmail = (String) session.getAttribute("resetEmail");

        if (sessionOtp == null || sessionEmail == null || !sessionEmail.equals(email)) {
            response.put("success", false);
            response.put("message", "OTP verification failed. Please try again.");
            return ResponseEntity.badRequest().body(response);
        }

        if (sessionOtp.equals(otp)) {
            // Mark OTP as verified for password reset
            session.setAttribute("otpVerified", true);
            response.put("success", true);
            response.put("message", "OTP verified successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid OTP");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/resend-password-reset-otp")
    public ResponseEntity<?> resendPasswordResetOtp(@RequestBody Map<String, String> request, HttpSession session) {
        String email = request.get("email");
        Map<String, Object> response = new HashMap<>();

        try {
            // Generate and send new OTP
            String otp = otpSender.sendOTP(email);

            // Store new OTP in session with expiration
            session.setAttribute("resetOtp", otp);
            session.setAttribute("resetEmail", email);
            session.setAttribute("otpExpiry", System.currentTimeMillis() + 300000); // 5 minutes

            response.put("success", true);
            response.put("message", "New OTP sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to resend OTP. Please try again.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request, HttpSession session) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        Map<String, Object> response = new HashMap<>();

        // Check if OTP was verified
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        if (otpVerified == null || !otpVerified) {
            response.put("success", false);
            response.put("message", "OTP verification required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Update user password
            User user = user_Service.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            user_Service.updatePassword(user, newPassword);

            // Clear session attributes
            session.removeAttribute("resetOtp");
            session.removeAttribute("resetEmail");
            session.removeAttribute("otpExpiry");
            session.removeAttribute("otpVerified");

            response.put("success", true);
            response.put("message", "Password reset successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to reset password. Please try again.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/logout-user")
    public String logout(HttpSession session) {
        // Invalidate the entire session
        session.invalidate();

        // Redirect to login page with logout parameter
        return "redirect:/?logout=true";
    }

    @GetMapping("/help")
    public String helpPage(HttpSession session,Model model){
        Long userId = (Long) session.getAttribute("id");

        if (userId != null) {
            Optional<User> userOptional = user_Service.getUserById(userId);
            if (userOptional.isPresent()) {
                model.addAttribute("user", userOptional.get());
            }
        }
        return "help";
    }

    @GetMapping("/terms")
    public String Terms(HttpSession session,Model model){
        Long userId = (Long) session.getAttribute("id");

        if (userId != null) {
            Optional<User> userOptional = user_Service.getUserById(userId);
            if (userOptional.isPresent()) {
                model.addAttribute("user", userOptional.get());
            }
        }
        return "terms";
    }

    @GetMapping("/privacy")
    public String Privacy(HttpSession session,Model model){
        Long userId = (Long) session.getAttribute("id");

        if (userId != null) {
            Optional<User> userOptional = user_Service.getUserById(userId);
            if (userOptional.isPresent()) {
                model.addAttribute("user", userOptional.get());
            }
        }
        return "privacy";
    }
}
