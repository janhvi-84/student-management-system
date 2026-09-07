package com.janhvi.studentmanagementapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.janhvi.studentmanagementapi.dto.ChangePasswordRequest;
import com.janhvi.studentmanagementapi.dto.ProfileUpdateRequest;
import com.janhvi.studentmanagementapi.entity.User;
import com.janhvi.studentmanagementapi.service.ProfileService;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private ProfileService profileService;


    // ==========================================
    // UPDATE PROFILE
    // ==========================================

    @PutMapping("/update")
    public User updateProfile(
            @RequestBody ProfileUpdateRequest request) {

        return profileService.updateProfile(request);
    }


    // ==========================================
    // CHANGE PASSWORD
    // ==========================================

    @PutMapping("/change-password")
    public String changePassword(
            @RequestBody ChangePasswordRequest request) {

        try {

            return profileService.changePassword(request);

        } catch (Exception e) {

            e.printStackTrace();

            return "ERROR : "
                    + e.getClass().getName()
                    + " : "
                    + e.getMessage();
        }
    }
}