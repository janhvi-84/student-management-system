package com.janhvi.studentmanagementapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.janhvi.studentmanagementapi.entity.PasswordResetOtp;

@Repository
public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findByEmail(String email);

    Optional<PasswordResetOtp> findByEmailAndOtp(
            String email,
            String otp
    );

    @Modifying
    @Transactional
    void deleteByEmail(String email);

}