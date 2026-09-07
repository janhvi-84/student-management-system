package com.janhvi.studentmanagementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Student Management System - Password Reset OTP");

        message.setText(
                "Hello,\n\n"
                        + "Your OTP for password reset is:\n\n"
                        + otp
                        + "\n\n"
                        + "This OTP is valid for 10 minutes.\n\n"
                        + "If you did not request this, please ignore this email.\n\n"
                        + "Regards,\n"
                        + "Student Management System"
        );

        mailSender.send(message);
    }
}