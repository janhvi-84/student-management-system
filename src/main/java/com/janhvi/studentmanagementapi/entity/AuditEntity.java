package com.janhvi.studentmanagementapi.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@MappedSuperclass
public abstract class AuditEntity {

    // ========================================
    // AUDIT FIELDS
    // ========================================

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;


    // ========================================
    // CREATE
    // ========================================

    @PrePersist
    protected void onCreate() {

        Long currentUserId = getCurrentUserId();

        System.out.println("========================================");
        System.out.println("AUDIT CREATE");
        System.out.println("ENTITY          : " + getClass().getSimpleName());
        System.out.println("CURRENT USER ID : " + currentUserId);
        System.out.println("========================================");

     if (createdBy == null) {
    createdBy = currentUserId;
}

if (createdDate == null) {
    createdDate = LocalDateTime.now();
}

updatedBy = null;
updatedDate = null;
        // CREATE ke time update fields null rahenge
        updatedBy = null;
        updatedDate = null;
    }


    // ========================================
    // UPDATE
    // ========================================

    @PreUpdate
    protected void onUpdate() {

        Long currentUserId = getCurrentUserId();

        System.out.println("========================================");
        System.out.println("AUDIT UPDATE");
        System.out.println("ENTITY          : " + getClass().getSimpleName());
        System.out.println("CURRENT USER ID : " + currentUserId);
        System.out.println("========================================");

        /*
         * IMPORTANT:
         * createdBy aur createdDate ko yahan
         * change nahi karna hai.
         *
         * Sirf update information change hogi.
         */

        updatedBy = currentUserId;
        updatedDate = LocalDateTime.now();
    }


    // ========================================
    // GET CURRENT LOGGED-IN USER ID
    // ========================================

    private Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        // ----------------------------------------
        // AUTHENTICATION NULL
        // ----------------------------------------

        if (authentication == null) {

            System.out.println(
                    "AUDIT: Authentication is NULL"
            );

            return null;
        }


        // ----------------------------------------
        // NOT AUTHENTICATED
        // ----------------------------------------

        if (!authentication.isAuthenticated()) {

            System.out.println(
                    "AUDIT: Authentication is NOT authenticated"
            );

            return null;
        }


        // ----------------------------------------
        // PRINCIPAL
        // ----------------------------------------

        Object principal =
                authentication.getPrincipal();


        System.out.println(
                "AUDIT PRINCIPAL : "
                        + principal
        );

        System.out.println(
                "AUDIT PRINCIPAL TYPE : "
                        + principal.getClass().getName()
        );


        // ========================================
        // JWT FILTER STORES Long USER ID
        // ========================================

        if (principal instanceof Long) {

            Long userId = (Long) principal;

            System.out.println(
                    "AUDIT USER ID : "
                            + userId
            );

            return userId;
        }


        // ========================================
        // SAFETY: INTEGER
        // ========================================

        if (principal instanceof Integer) {

            Long userId =
                    ((Integer) principal).longValue();

            System.out.println(
                    "AUDIT USER ID : "
                            + userId
            );

            return userId;
        }


        // ========================================
        // SAFETY: STRING
        // ========================================

        if (principal instanceof String) {

            try {

                Long userId =
                        Long.parseLong(
                                (String) principal
                        );

                System.out.println(
                        "AUDIT USER ID : "
                                + userId
                );

                return userId;

            } catch (NumberFormatException e) {

                System.out.println(
                        "AUDIT: Principal is String but not USER ID"
                );

                return null;
            }
        }


        // ========================================
        // UNKNOWN PRINCIPAL
        // ========================================

        System.out.println(
                "AUDIT: Unknown principal type"
        );

        return null;
    }


    // ========================================
    // GETTERS
    // ========================================

    public Long getCreatedBy() {

        return createdBy;
    }


    public LocalDateTime getCreatedDate() {

        return createdDate;
    }


    public Long getUpdatedBy() {

        return updatedBy;
    }


    public LocalDateTime getUpdatedDate() {

        return updatedDate;
    }


    // ========================================
    // SETTERS
    // ========================================

    public void setCreatedBy(Long createdBy) {

        this.createdBy = createdBy;
    }


    public void setCreatedDate(
            LocalDateTime createdDate) {

        this.createdDate = createdDate;
    }


    public void setUpdatedBy(Long updatedBy) {

        this.updatedBy = updatedBy;
    }


    public void setUpdatedDate(
            LocalDateTime updatedDate) {

        this.updatedDate = updatedDate;
    }
}