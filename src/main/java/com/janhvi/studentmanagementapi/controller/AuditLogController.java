package com.janhvi.studentmanagementapi.controller;

import com.janhvi.studentmanagementapi.entity.AuditLog;
import com.janhvi.studentmanagementapi.repository.AuditLogRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;


    // ========================================
    // CONSTRUCTOR
    // ========================================

    public AuditLogController(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository =
                auditLogRepository;
    }


    // ========================================
    // GET ALL AUDIT LOGS
    // ========================================

    @GetMapping
    public ResponseEntity<?> getAllAuditLogs() {

        try {

            List<AuditLog> logs =
                    auditLogRepository
                            .findAllByOrderByCreatedAtDesc();

            return ResponseEntity.ok(logs);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body("Unable to load audit logs");
        }
    }


    // ========================================
    // GET BY ACTION
    // ========================================

    @GetMapping("/action/{action}")
    public ResponseEntity<?> getByAction(
            @PathVariable String action) {

        try {

            return ResponseEntity.ok(
                    auditLogRepository
                            .findByActionOrderByCreatedAtDesc(
                                    action
                            )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body("Unable to load audit logs");
        }
    }


    // ========================================
    // GET BY MODULE
    // ========================================

    @GetMapping("/module/{module}")
    public ResponseEntity<?> getByModule(
            @PathVariable String module) {

        try {

            return ResponseEntity.ok(
                    auditLogRepository
                            .findByModuleOrderByCreatedAtDesc(
                                    module
                            )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body("Unable to load audit logs");
        }
    }


    // ========================================
    // GET BY ROLE
    // ========================================

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getByRole(
            @PathVariable String role) {

        try {

            return ResponseEntity.ok(
                    auditLogRepository
                            .findByRoleOrderByCreatedAtDesc(
                                    role
                            )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body("Unable to load audit logs");
        }
    }
}