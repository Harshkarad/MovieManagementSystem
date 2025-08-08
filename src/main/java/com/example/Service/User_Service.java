package com.example.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Entity.Movies;
import com.example.Entity.User;
import com.example.Repository.Movies_Repo;
import com.example.Repository.User_Repo;

@Service
public class User_Service {
    @Autowired
    private Movies_Repo movies_Repo;

    @Autowired
    private User_Repo user_Repo;

    // check if user already registered
    public boolean UserExist(String mobile, String email) {
        if (user_Repo.existsByMobile(mobile)) {
            return true;
        }
        if (user_Repo.existsByEmail(email)) {
            return true;
        }
        return false;
    }

    public boolean emailExists(String email) {
        if (user_Repo.existsByEmail(email)) {
            return true;
        }
        return false;
    }

    // save user
    public User saveUser(User user) {
        return user_Repo.save(user);
    }

    // verify login credentials
    public boolean verifylogin(String mobile, String password) {
        Optional<User> userInfo = user_Repo.findByMobileAndPassword(mobile, password);
        if (userInfo.isPresent()) {
            return true;
        } else {
            return false;
        }

    }

    // Find user by mobile number
    public Optional<User> findByMobile(String mobile) {
        return user_Repo.findByMobile(mobile);
    }

    public Optional<User> getUserById(Long id) {
        return user_Repo.findById(id);
    }

    public User updateUser(User user) {
        // Add your business logic/validation here
        return user_Repo.save(user);
    }

    // find all user
    public List<User> AllUser() {
        return user_Repo.findAll();
    }

    public User updateUser(Long id, User userDetails) {
        User user = user_Repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstname(userDetails.getFirstname());
        user.setLastname(userDetails.getLastname());
        user.setEmail(userDetails.getEmail());
        user.setMobile(userDetails.getMobile());
        user.setLocation(userDetails.getLocation());
        user.setStatus(userDetails.getStatus());

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword()); // In real app, hash the password
        }

        return user_Repo.save(user);
    }

    public User updateUserStatus(Long id, String status) {
        User user = user_Repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(status);
        return user_Repo.save(user);
    }

    // Method using date range parameters
    public List<User> getUsersCreatedThisMonthAlternative() {
        LocalDateTime startOfMonth = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now()
                .with(TemporalAdjusters.lastDayOfMonth())
                .atTime(23, 59, 59);

        return user_Repo.findUsersCreatedBetweenDates(startOfMonth, endOfMonth);
    }

    public Optional<User> findByEmail(String email) {
        return user_Repo.findByEmail(email);
    }

    public void updatePassword(User user, String newPassword) {
        // Add password hashing here
        user.setPassword(newPassword);
        user_Repo.save(user);
    }

    // find maintainence mode
    public List<Movies> maintenanceModeMovies(){
        return movies_Repo.findAllMoviesFromActiveAdmins();
    }
}
