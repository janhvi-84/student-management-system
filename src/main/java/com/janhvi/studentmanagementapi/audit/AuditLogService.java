package com.janhvi.studentmanagementapi.audit;

import com.janhvi.studentmanagementapi.entity.AuditLog;
import com.janhvi.studentmanagementapi.entity.User;
import com.janhvi.studentmanagementapi.repository.AuditLogRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    // ========================================
    // CONSTRUCTOR
    // ========================================

    public AuditLogService(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository = auditLogRepository;
    }


    // ========================================
    // BASIC AUDIT LOG
    //
    // LOGIN / LOGOUT / REGISTRATION
    //
    // Target student nahi hota
    // ========================================

    public void log(
            Long userId,
            String userEmail,
            String role,
            String action,
            String module,
            String description) {

        log(
                userId,
                userEmail,
                role,
                action,
                module,
                description,
                null,
                null
        );
    }


    // ========================================
    // TARGET STUDENT AUDIT LOG
    //
    // CREATE / UPDATE / DELETE / RESTORE
    // ========================================

    public void log(
            Long userId,
            String userEmail,
            String role,
            String action,
            String module,
            String description,
            Integer targetStudentId,
            String targetStudentEmail) {

        try {

            AuditLog auditLog = new AuditLog();

            // ========================================
            // PERFORMED BY
            // ========================================

            auditLog.setUserId(userId);

            auditLog.setUserEmail(userEmail);

            /*
             * IMPORTANT:
             * Yahan actual logged-in user ka role
             * save hoga.
             *
             * ADMIN ne student create kiya:
             * role = ADMIN
             *
             * ADMIN ne student update kiya:
             * role = ADMIN
             *
             * STUDENT ne apna profile update kiya:
             * role = STUDENT
             */
            auditLog.setRole(role);


            // ========================================
            // TARGET STUDENT
            // ========================================

            auditLog.setTargetStudentId(
                    targetStudentId
            );

            auditLog.setTargetStudentEmail(
                    targetStudentEmail
            );


            // ========================================
            // ACTION
            // ========================================

            auditLog.setAction(action);


            // ========================================
            // MODULE
            // ========================================

            auditLog.setModule(module);


            // ========================================
            // DESCRIPTION
            // ========================================

            auditLog.setDescription(
                    description
            );


            // ========================================
            // CREATED TIME
            // ========================================

            auditLog.setCreatedAt(
                    LocalDateTime.now()
            );


            // ========================================
            // SAVE
            // ========================================

            auditLogRepository.save(auditLog);


            // ========================================
            // DEBUG
            // ========================================

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "AUDIT LOG SAVED"
            );

            System.out.println(
                    "PERFORMED BY ID     : " + userId
            );

            System.out.println(
                    "PERFORMED EMAIL     : " + userEmail
            );

            System.out.println(
                    "PERFORMED ROLE      : " + role
            );

            System.out.println(
                    "TARGET STUDENT ID   : "
                            + targetStudentId
            );

            System.out.println(
                    "TARGET STUDENT EMAIL: "
                            + targetStudentEmail
            );

            System.out.println(
                    "ACTION              : " + action
            );

            System.out.println(
                    "MODULE              : " + module
            );

            System.out.println(
                    "DESCRIPTION         : " + description
            );

            System.out.println(
                    "========================================"
            );

        } catch (Exception e) {

            /*
             * Audit log ki wajah se
             * main application operation fail
             * nahi hona chahiye.
             */

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "AUDIT LOG ERROR"
            );

            e.printStackTrace();

            System.out.println(
                    "========================================"
            );
        }
    }


// ========================================
// ADMIN TARGET AUDIT LOG
// CREATE / UPDATE / DELETE ADMIN
// ========================================

public void logAdminAction(
        Long userId,
        String userEmail,
        String role,
        String action,
        String module,
        String description,
        Long targetAdminId,
        String targetAdminEmail) {

    try {

        AuditLog auditLog =
                new AuditLog();

        // ========================================
        // PERFORMED BY
        // ========================================

        auditLog.setUserId(userId);

        auditLog.setUserEmail(userEmail);

        auditLog.setRole(role);


        // ========================================
        // TARGET ADMIN
        // ========================================

        auditLog.setTargetAdminId(
                targetAdminId
        );

        auditLog.setTargetAdminEmail(
                targetAdminEmail
        );


        // ========================================
        // ACTION
        // ========================================

        auditLog.setAction(action);


        // ========================================
        // MODULE
        // ========================================

        auditLog.setModule(module);


        // ========================================
        // DESCRIPTION
        // ========================================

        auditLog.setDescription(
                description
        );


        // ========================================
        // CREATED TIME
        // ========================================

        auditLog.setCreatedAt(
                LocalDateTime.now()
        );


        // ========================================
        // SAVE
        // ========================================

        auditLogRepository.save(auditLog);


        // ========================================
        // DEBUG
        // ========================================

        System.out.println(
                "========================================"
        );

        System.out.println(
                "ADMIN AUDIT LOG SAVED"
        );

        System.out.println(
                "PERFORMED BY ID    : " + userId
        );

        System.out.println(
                "PERFORMED EMAIL     : " + userEmail
        );

        System.out.println(
                "PERFORMED ROLE      : " + role
        );

        System.out.println(
                "TARGET ADMIN ID     : " + targetAdminId
        );

        System.out.println(
                "TARGET ADMIN EMAIL  : " + targetAdminEmail
        );

        System.out.println(
                "ACTION              : " + action
        );

        System.out.println(
                "MODULE              : " + module
        );

        System.out.println(
                "DESCRIPTION         : " + description
        );

        System.out.println(
                "========================================"
        );

    } catch (Exception e) {

        System.out.println(
                "========================================"
        );

        System.out.println(
                "ADMIN AUDIT LOG ERROR"
        );

        e.printStackTrace();

        System.out.println(
                "========================================"
        );
    }
}


    // ========================================
    // LOG USING USER OBJECT
    //
    // LOGIN / LOGOUT / ETC.
    // ========================================

    public void log(
            User user,
            String action,
            String module,
            String description) {

        if (user == null) {

            log(
                    null,
                    null,
                    null,
                    action,
                    module,
                    description
            );

            return;
        }

        log(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                action,
                module,
                description
        );
    }


    // ========================================
    // LOG USING USER OBJECT
    // + TARGET STUDENT
    // ========================================

    public void log(
            User user,
            String action,
            String module,
            String description,
            Integer targetStudentId,
            String targetStudentEmail) {

        if (user == null) {

            log(
                    null,
                    null,
                    null,
                    action,
                    module,
                    description,
                    targetStudentId,
                    targetStudentEmail
            );

            return;
        }

        /*
         * IMPORTANT:
         * user.getRole() se actual performer ka
         * role automatically jayega.
         *
         * ADMIN -> ADMIN
         * STUDENT -> STUDENT
         */

        log(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                action,
                module,
                description,
                targetStudentId,
                targetStudentEmail
        );
    }
}