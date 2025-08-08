package com.example.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Entity.SettingsActivity;
import com.example.Entity.Settings_Prefrences;
import com.example.Repository.Admin_Repo;
import com.example.Repository.SettingsActivity_Repo;
import com.example.Repository.SettingsPrefrence_Repo;

import jakarta.transaction.Transactional;

@Service
public class Settings_Service {
    @Autowired
    private SettingsPrefrence_Repo settingsPrefrence_Repo;

    @Autowired
    private Admin_Repo admin_Repo;

    @Autowired
    private SettingsActivity_Repo settingsActivity_Repo;

    public void updateAdmin(long adminId,String email,String password){
        admin_Repo.updateAdminCredentials(adminId, email, password);
    }


    // save settings activity
    public SettingsActivity saveActivity(SettingsActivity settingsActivity){
        return settingsActivity_Repo.save(settingsActivity);
    }

    @Transactional
    public void updatePreferences(Long adminId, 
                                boolean maintenanceMode, 
                                boolean userRegistration,
                                boolean emailNotifications, 
                                boolean auditLogging) {
        settingsPrefrence_Repo.updateSettingsByAdminId(
            adminId,
            maintenanceMode,
            userRegistration,
            emailNotifications,
            auditLogging
        );
    }

    // find by admin id
    public Settings_Prefrences getSettings(long adminId){
        return settingsPrefrence_Repo.findSettingsByAdminId(adminId);
    }

    public List<SettingsActivity> getSettingsActivity(long adminId){
        return settingsActivity_Repo.findRecentActivitiesByAdmin(adminId);
    }
}
