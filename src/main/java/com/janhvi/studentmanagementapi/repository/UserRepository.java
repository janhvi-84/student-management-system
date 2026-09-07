package com.janhvi.studentmanagementapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.janhvi.studentmanagementapi.entity.User;

@Repository
public interface UserRepository
        extends JpaRepository<User, Long> {

    // ========================================
    // LOGIN
    // ========================================
      
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndDeletedFalse(String email);

    // ========================================
    // GET ALL ADMINS (NOT DELETED)
    // ========================================

    List<User> findByRoleAndDeletedFalse(String role);

    // ========================================
    // ALL USERS (NOT DELETED)
    // ========================================

    List<User> findByDeletedFalse();

    // ========================================
    // LATEST USERS (NOT DELETED)
    // ========================================

    List<User> findByDeletedFalseOrderByIdDesc();

}