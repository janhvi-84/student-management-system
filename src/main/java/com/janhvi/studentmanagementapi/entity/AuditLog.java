package com.janhvi.studentmanagementapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    // ========================================
    // AUDIT LOG ID
    // ========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // ========================================
    // PERFORMED BY USER ID
    // Kisne action perform kiya
    // ========================================

    @Column(name = "user_id")
    private Long userId;


    // ========================================
    // PERFORMED BY USER EMAIL
    // ========================================

    @Column(name = "user_email")
    private String userEmail;


    // ========================================
    // PERFORMED BY USER ROLE
    // ADMIN / STUDENT
    // ========================================

    @Column(name = "role")
    private String role;


    // ========================================
    // TARGET STUDENT ID
    // Kis student par action hua
    // ========================================

    @Column(name = "target_student_id")
    private Integer targetStudentId;


    // ========================================
    // TARGET STUDENT EMAIL
    // ========================================

    @Column(name = "target_student_email")
    private String targetStudentEmail;

// ========================================
// TARGET ADMIN ID
// ========================================

@Column(name = "target_admin_id")
private Long targetAdminId;


// ========================================
// TARGET ADMIN EMAIL
// ========================================

@Column(name = "target_admin_email")
private String targetAdminEmail;


    // ========================================
    // ACTION
    // LOGIN / LOGOUT / CREATE / UPDATE /
    // DELETE / REGISTRATION / PASSWORD_CHANGE
    // ========================================

    @Column(name = "action", nullable = false)
    private String action;


    // ========================================
    // MODULE
    // AUTH / STUDENT / ADMIN / USER
    // ========================================

    @Column(name = "module")
    private String module;


    // ========================================
    // DESCRIPTION
    // ========================================

    @Column(name = "description")
    private String description;


    // ========================================
    // CREATED TIME
    // ========================================

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    // ========================================
    // DEFAULT CONSTRUCTOR
    // ========================================

    public AuditLog() {
    }


    // ========================================
    // GET ID
    // ========================================

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    // ========================================
    // GET USER ID
    // ========================================

    public Long getUserId() {
        return userId;
    }


    public void setUserId(Long userId) {
        this.userId = userId;
    }


    // ========================================
    // GET USER EMAIL
    // ========================================

    public String getUserEmail() {
        return userEmail;
    }


    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    // ========================================
    // GET ROLE
    // ========================================

    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    // ========================================
    // GET TARGET STUDENT ID
    // ========================================

    public Integer getTargetStudentId() {
        return targetStudentId;
    }


    public void setTargetStudentId(Integer targetStudentId) {
        this.targetStudentId = targetStudentId;
    }


    // ========================================
    // GET TARGET STUDENT EMAIL
    // ========================================

    public String getTargetStudentEmail() {
        return targetStudentEmail;
    }


    public void setTargetStudentEmail(
            String targetStudentEmail) {

        this.targetStudentEmail = targetStudentEmail;
    }


    // ========================================
    // GET ACTION
    // ========================================

    public String getAction() {
        return action;
    }


    public void setAction(String action) {
        this.action = action;
    }


    // ========================================
    // GET MODULE
    // ========================================

    public String getModule() {
        return module;
    }


    public void setModule(String module) {
        this.module = module;
    }


    // ========================================
    // GET DESCRIPTION
    // ========================================

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    // ========================================
    // GET CREATED AT
    // ========================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getTargetAdminId() {
    return targetAdminId;
}

public void setTargetAdminId(Long targetAdminId) {
    this.targetAdminId = targetAdminId;
}

public String getTargetAdminEmail() {
    return targetAdminEmail;
}

public void setTargetAdminEmail(String targetAdminEmail) {
    this.targetAdminEmail = targetAdminEmail;
}
}