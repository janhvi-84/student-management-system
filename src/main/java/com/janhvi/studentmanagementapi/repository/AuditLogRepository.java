package com.janhvi.studentmanagementapi.repository;

import com.janhvi.studentmanagementapi.entity.AuditLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    // ========================================
    // ALL AUDIT LOGS
    // NEWEST FIRST
    // ========================================

    List<AuditLog> findAllByOrderByCreatedAtDesc();


    // ========================================
    // AUDIT LOGS BY USER
    // ========================================

    List<AuditLog> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );


    // ========================================
    // AUDIT LOGS BY ROLE
    // ========================================

    List<AuditLog> findByRoleOrderByCreatedAtDesc(
            String role
    );


    // ========================================
    // AUDIT LOGS BY ACTION
    // ========================================

    List<AuditLog> findByActionOrderByCreatedAtDesc(
            String action
    );


    // ========================================
    // AUDIT LOGS BY MODULE
    // ========================================

    List<AuditLog> findByModuleOrderByCreatedAtDesc(
            String module
    );
}