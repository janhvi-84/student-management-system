package com.janhvi.studentmanagementapi.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.janhvi.studentmanagementapi.entity.PasswordResetOtp;
import com.janhvi.studentmanagementapi.entity.User;
import com.janhvi.studentmanagementapi.repository.PasswordResetOtpRepository;
import com.janhvi.studentmanagementapi.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ForgotPasswordService {

    @Autowired
    private PasswordResetOtpRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // SEND OTP
    public String sendOtp(String email) {

    try {

        System.out.println("EMAIL = " + email);

        Optional<User> user = userRepository.findByEmail(email);

        System.out.println("USER FOUND = " + user.isPresent());

        if (user.isEmpty()) {
            return "Email not registered.";
        }

        otpRepository.deleteByEmail(email);

        String otp = String.format("%06d", new Random().nextInt(999999));

        PasswordResetOtp resetOtp = new PasswordResetOtp();
        resetOtp.setEmail(email);
        resetOtp.setOtp(otp);
        resetOtp.setExpiryTime(LocalDateTime.now().plusMinutes(10));

        otpRepository.save(resetOtp);

        System.out.println("OTP SAVED");

        emailService.sendOtpEmail(email, otp);

        System.out.println("EMAIL SENT");

        return "OTP sent successfully.";

    } catch (Exception e) {

        e.printStackTrace();

        return "ERROR : " + e.getClass().getName() + " : " + e.getMessage();

    }
}
    // VERIFY OTP
    public String verifyOtp(String email, String otp) {

        Optional<PasswordResetOtp> data =
                otpRepository.findByEmailAndOtp(email, otp);

        if (data.isEmpty()) {
            return "Invalid OTP.";
        }

        if (data.get().getExpiryTime().isBefore(LocalDateTime.now())) {
            return "OTP Expired.";
        }

        return "OTP Verified";
    }

    // RESET PASSWORD
    public String resetPassword(
            String email,
            String otp,
            String newPassword
    ) {

        String verify = verifyOtp(email, otp);

        if (!verify.equals("OTP Verified")) {
            return verify;
        }

        User user = userRepository
                .findByEmail(email)
                .orElseThrow();

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        otpRepository.deleteByEmail(email);

        return "Password reset successful.";
    }

}