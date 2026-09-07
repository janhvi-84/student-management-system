package com.janhvi.studentmanagementapi.audit;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditListener {

    // ========================================
    // CREATE
    // ========================================

    public void onCreate(Object entity) {

        Long currentUserId = getCurrentUserId();

        setField(
                entity,
                "createdBy",
                currentUserId
        );

        setField(
                entity,
                "createdDate",
                LocalDateTime.now()
        );

        // CREATE ke time update fields nahi bharne
        setField(
                entity,
                "updatedBy",
                null
        );

        setField(
                entity,
                "updatedDate",
                null
        );
    }


    // ========================================
    // UPDATE
    // ========================================

    public void onUpdate(Object entity) {

        Long currentUserId = getCurrentUserId();

        setField(
                entity,
                "updatedBy",
                currentUserId
        );

        setField(
                entity,
                "updatedDate",
                LocalDateTime.now()
        );
    }


    // ========================================
    // CURRENT USER ID
    // ========================================

    private Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
            !authentication.isAuthenticated()) {

            return null;
        }

        Object principal =
                authentication.getPrincipal();

        if (principal instanceof Long) {

            return (Long) principal;
        }

        return null;
    }


    // ========================================
    // SET FIELD
    // ========================================

    private void setField(
            Object entity,
            String fieldName,
            Object value) {

        try {

            Field field =
                    findField(
                            entity.getClass(),
                            fieldName
                    );

            if (field == null) {
                return;
            }

            field.setAccessible(true);

            field.set(
                    entity,
                    value
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to set audit field: "
                            + fieldName,
                    e
            );
        }
    }


    // ========================================
    // FIND FIELD
    // ========================================

    private Field findField(
            Class<?> clazz,
            String fieldName) {

        Class<?> currentClass =
                clazz;

        while (
                currentClass != null
                &&
                currentClass != Object.class
        ) {

            try {

                return currentClass
                        .getDeclaredField(
                                fieldName
                        );

            } catch (NoSuchFieldException e) {

                currentClass =
                        currentClass.getSuperclass();
            }
        }

        return null;
    }
}