package com.janhvi.studentmanagementapi.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.janhvi.studentmanagementapi.service.ForgotPasswordService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    // =====================================
    // SEND OTP
    // =====================================
    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestBody Map<String, String> request
    ) {

        return forgotPasswordService.sendOtp(
                request.get("email")
        );

    }

    // =====================================
    // VERIFY OTP
    // =====================================
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestBody Map<String, String> request
    ) {

        return forgotPasswordService.verifyOtp(

                request.get("email"),
                request.get("otp")

        );

    }

    // =====================================
    // RESET PASSWORD
    // =====================================
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestBody Map<String, String> request
    ) {

        try {

            return forgotPasswordService.resetPassword(

                    request.get("email"),
                    request.get("otp"),
                    request.get("newPassword")

            );

        }

        catch (Exception e) {

            e.printStackTrace();

            return "ERROR : " + e.getMessage();

        }

    }

}