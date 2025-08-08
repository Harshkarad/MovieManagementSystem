package com.example.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Entity.Admin_Entity;
import com.example.Entity.Movies;
import com.example.Entity.Screening;
import com.example.Repository.Admin_Repo;
import com.example.Repository.Movies_Repo;
import com.example.Repository.Screening_Repo;

@Service
public class Admin_Service {
    @Autowired
    private Screening_Repo screening_Repo;

    @Autowired
    private Movies_Repo movies_Repo;

    @Autowired
    private Admin_Repo admin_Repo;

    // log in
    public Optional<Admin_Entity> AdminExist(String mobile, String password) {
        return admin_Repo.findByMobileAndPassword(mobile, password);
    }

    // find by mobile
    public Admin_Entity getUserByMobile(String mobile) {
        return admin_Repo.findByMobile(mobile);
    }

    // new
    public List<Movies> getByStatus(long adminId) {
        return movies_Repo.findActiveMoviesByAdminId(adminId);
    }

    public List<Movies> getByInActiveStatus(long adminId) {
        return movies_Repo.findActiveMoviesByAdminId(adminId);
    }

    // new
    public List<Screening> getNext7Screenings(long adminId) {
        LocalDateTime now = LocalDateTime.now();
        return screening_Repo.findNext7ScreeningsByAdmin(adminId, now);
    }
}
