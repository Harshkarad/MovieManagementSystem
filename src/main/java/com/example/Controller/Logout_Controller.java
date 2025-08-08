package com.example.Controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Entity.Admin_Entity;
import com.example.Entity.SettingsActivity;
import com.example.Service.Admin_Service;
import com.example.Service.IPAddressGetter;
import com.example.Service.Settings_Service;

import jakarta.servlet.http.HttpSession;

@Controller
public class Logout_Controller {
    @Autowired
    private Admin_Service admin_Service;

    @Autowired
    private IPAddressGetter ipAddressGetter;

    @Autowired
    private Settings_Service Settings_Service;

    @GetMapping("/logout")
    public String logoutUser(HttpSession session, RedirectAttributes redirectAttributes) {
        String mobile = (String) session.getAttribute("mobile");
        Admin_Entity admin_Entity = admin_Service.getUserByMobile(mobile);
        LocalDateTime localDateTime = LocalDateTime.now();
        String hostName = ipAddressGetter.getLocalHostName();
        String hostAddress = ipAddressGetter.getLocalHostAddress();

        SettingsActivity settingsActivity = new SettingsActivity();
        settingsActivity.setAction("Logout");
        settingsActivity.setAdmin(admin_Entity);
        settingsActivity.setDescription("Admin Logged Out.");
        settingsActivity.setTimestamp(localDateTime);
        settingsActivity.setHostName(hostName);
        settingsActivity.setIpAddress(hostAddress);

        Settings_Service.saveActivity(settingsActivity);

        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("logout", "true");
        return "redirect:/?logout=true";
    }

    
}
