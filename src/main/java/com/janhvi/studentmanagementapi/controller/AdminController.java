package com.janhvi.studentmanagementapi.controller;

import com.janhvi.studentmanagementapi.audit.AuditLogService;
import com.janhvi.studentmanagementapi.dto.ChangePasswordRequest;
import com.janhvi.studentmanagementapi.entity.User;
import com.janhvi.studentmanagementapi.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final AuditLogService auditLogService;


    // ========================================
    // CONSTRUCTOR
    // ========================================

    public AdminController(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            AuditLogService auditLogService) {

        this.userRepository =
                userRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.auditLogService =
                auditLogService;
    }


    // ========================================
    // GET LOGGED-IN USER
    // ========================================

    private User getLoggedInUser() {

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


        // ========================================
        // PRINCIPAL = LONG USER ID
        // ========================================

        if (principal instanceof Long) {

            return userRepository
                    .findById((Long) principal)
                    .orElse(null);
        }


        // ========================================
        // PRINCIPAL = INTEGER USER ID
        // ========================================

        if (principal instanceof Integer) {

            Long userId =
                    ((Integer) principal)
                            .longValue();

            return userRepository
                    .findById(userId)
                    .orElse(null);
        }


        // ========================================
        // PRINCIPAL = STRING USER ID
        // ========================================

        if (principal instanceof String) {

            try {

                Long userId =
                        Long.parseLong(
                                (String) principal
                        );

                return userRepository
                        .findById(userId)
                        .orElse(null);

            } catch (NumberFormatException e) {

                // String may be email
            }
        }


        // ========================================
        // FALLBACK - EMAIL
        // ========================================

        String email =
                authentication.getName();


        if (email == null ||
                email.isBlank()) {

            return null;
        }


        return userRepository
                .findByEmail(email)
                .orElse(null);
    }


    // ========================================
    // CREATE FIRST ADMIN
    // ========================================

    @PostMapping("/create-first")
    public ResponseEntity<?> createFirstAdmin(
            @RequestBody User user) {

        try {

            if (!userRepository
                    .findByRoleAndDeletedFalse("ADMIN")
                    .isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Admin already exists");
            }


            if (userRepository
                    .findByEmail(user.getEmail())
                    .isPresent()) {

                return ResponseEntity
                        .badRequest()
                        .body("Email already exists");
            }


            if (user.getPassword() == null ||
                    user.getPassword().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body("Password is required");
            }


            user.setRole("ADMIN");


            user.setPassword(
                    passwordEncoder.encode(
                            user.getPassword()
                    )
            );


            User savedAdmin =
                    userRepository.saveAndFlush(user);


            // ========================================
            // AUDIT
            // ========================================

            auditLogService.logAdminAction(
                    savedAdmin.getId(),
                    savedAdmin.getEmail(),
                    "ADMIN",
                    "CREATE",
                    "ADMIN",
                    "First admin created successfully",
                    savedAdmin.getId(),
                    savedAdmin.getEmail()
            );


            return ResponseEntity.ok(
                    savedAdmin
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }


    // ========================================
    // CREATE NEW ADMIN
    // ========================================

    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(
            @RequestBody User user) {

        try {

            System.out.println(
                    "=========== CREATE ADMIN ==========="
            );

            System.out.println(
                    "Name     : " + user.getName()
            );

            System.out.println(
                    "Email    : " + user.getEmail()
            );

            System.out.println(
                    "Phone    : " + user.getPhoneNumber()
            );

            System.out.println(
                    "Address  : " + user.getAddress()
            );


            // ========================================
            // CHECK EMAIL
            // ========================================

            if (userRepository
                    .findByEmail(user.getEmail())
                    .isPresent()) {

                return ResponseEntity
                        .badRequest()
                        .body("Email already exists");
            }


            // ========================================
            // CHECK PASSWORD
            // ========================================

            if (user.getPassword() == null ||
                    user.getPassword().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body("Password is required");
            }


            // ========================================
            // GET LOGGED-IN ADMIN
            // ========================================

            User loggedInAdmin =
                    getLoggedInUser();


            if (loggedInAdmin == null) {

                return ResponseEntity
                        .status(401)
                        .body("Logged-in admin not found");
            }


            // ========================================
            // CREATE ADMIN
            // ========================================

            user.setRole("ADMIN");


            user.setPassword(
                    passwordEncoder.encode(
                            user.getPassword()
                    )
            );


            User savedAdmin =
                    userRepository.saveAndFlush(user);


            // ========================================
            // AUDIT
            // ========================================

            auditLogService.logAdminAction(
                    loggedInAdmin.getId(),
                    loggedInAdmin.getEmail(),
                    loggedInAdmin.getRole(),
                    "CREATE",
                    "ADMIN",
                    "Admin created successfully",
                    savedAdmin.getId(),
                    savedAdmin.getEmail()
            );


            return ResponseEntity.ok(
                    savedAdmin
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }


    // ========================================
    // GET ALL ADMINS
    // ========================================

    @GetMapping
    public ResponseEntity<?> getAllAdmins() {

        List<User> admins =
                userRepository
                        .findByRoleAndDeletedFalse(
                                "ADMIN"
                        );

        return ResponseEntity.ok(admins);
    }


    // ========================================
    // GET ADMIN BY ID
    // ========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(
            @PathVariable Long id) {

        User admin =
                userRepository
                        .findById(id)
                        .orElse(null);


        if (admin == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Admin not found");
        }


        if (!"ADMIN".equals(
                admin.getRole())) {

            return ResponseEntity
                    .badRequest()
                    .body("User is not admin");
        }


        return ResponseEntity.ok(
                admin
        );
    }


    // ========================================
    // UPDATE ADMIN PROFILE
    // ========================================

    @PutMapping("/update")
    public ResponseEntity<?> updateAdmin(
            @RequestBody User request) {

        try {

            User admin =
                    userRepository
                            .findById(
                                    request.getId()
                            )
                            .orElse(null);


            if (admin == null) {

                return ResponseEntity
                        .badRequest()
                        .body("Admin not found");
            }


            if (!"ADMIN".equals(
                    admin.getRole())) {

                return ResponseEntity
                        .badRequest()
                        .body("User is not admin");
            }


            // ========================================
            // GET LOGGED-IN ADMIN
            // ========================================

            User loggedInAdmin =
                    getLoggedInUser();


            if (loggedInAdmin == null) {

                return ResponseEntity
                        .status(401)
                        .body(
                                "Logged-in admin not found"
                        );
            }


            // ========================================
            // UPDATE PROFILE
            // ========================================

            admin.setName(
                    request.getName()
            );

            admin.setPhoneNumber(
                    request.getPhoneNumber()
            );

            admin.setAddress(
                    request.getAddress()
            );


            // ========================================
            // SAVE
            // ========================================

            User updatedAdmin =
                    userRepository.saveAndFlush(
                            admin
                    );


            // ========================================
            // AUDIT
            // ========================================

            auditLogService.logAdminAction(
                    loggedInAdmin.getId(),
                    loggedInAdmin.getEmail(),
                    loggedInAdmin.getRole(),
                    "UPDATE",
                    "ADMIN",
                    "Admin profile updated successfully",
                    updatedAdmin.getId(),
                    updatedAdmin.getEmail()
            );


            return ResponseEntity.ok(
                    updatedAdmin
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }


    // ========================================
    // CHANGE PASSWORD
    // ========================================

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request) {

        try {

            User admin =
                    userRepository
                            .findById(
                                    request.getUserId()
                            )
                            .orElse(null);


            if (admin == null) {

                return ResponseEntity
                        .badRequest()
                        .body("Admin not found");
            }


            if (!"ADMIN".equals(
                    admin.getRole())) {

                return ResponseEntity
                        .badRequest()
                        .body("User is not admin");
            }


            // ========================================
            // LOGGED-IN ADMIN
            // ========================================

            User loggedInAdmin =
                    getLoggedInUser();


            if (loggedInAdmin == null) {

                return ResponseEntity
                        .status(401)
                        .body(
                                "Logged-in admin not found"
                        );
            }


            // ========================================
            // CURRENT PASSWORD
            // ========================================

            if (!passwordEncoder.matches(
                    request.getCurrentPassword(),
                    admin.getPassword())) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Current password is incorrect"
                        );
            }


            // ========================================
            // NEW PASSWORD
            // ========================================

            admin.setPassword(
                    passwordEncoder.encode(
                            request.getNewPassword()
                    )
            );


            userRepository.saveAndFlush(
                    admin
            );


            // ========================================
            // AUDIT
            // ========================================

            auditLogService.logAdminAction(
                    loggedInAdmin.getId(),
                    loggedInAdmin.getEmail(),
                    loggedInAdmin.getRole(),
                    "PASSWORD_CHANGE",
                    "ADMIN",
                    "Admin password changed successfully",
                    admin.getId(),
                    admin.getEmail()
            );


            return ResponseEntity.ok(
                    "Password changed successfully"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }


    // ========================================
    // DELETE ADMIN
    // ========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(
            @PathVariable Long id) {

        try {

            User admin =
                    userRepository
                            .findById(id)
                            .orElse(null);


            if (admin == null) {

                return ResponseEntity
                        .badRequest()
                        .body("Admin not found");
            }


            if (!"ADMIN".equals(
                    admin.getRole())) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "This user is not an admin"
                        );
            }


            // ========================================
            // LOGGED-IN ADMIN
            // ========================================

            User loggedInAdmin =
                    getLoggedInUser();


            if (loggedInAdmin == null) {

                return ResponseEntity
                        .status(401)
                        .body(
                                "Logged-in admin not found"
                        );
            }


            // ========================================
            // TARGET DATA BEFORE DELETE
            // ========================================

            Long targetAdminId =
                    admin.getId();

            String targetAdminEmail =
                    admin.getEmail();


            // ========================================
            // SOFT DELETE
            // ========================================

            admin.setDeleted(true);


            userRepository.saveAndFlush(
                    admin
            );


            // ========================================
            // AUDIT
            // ========================================

            auditLogService.logAdminAction(
                    loggedInAdmin.getId(),
                    loggedInAdmin.getEmail(),
                    loggedInAdmin.getRole(),
                    "DELETE",
                    "ADMIN",
                    "Admin moved to Recycle Bin",
                    targetAdminId,
                    targetAdminEmail
            );


            return ResponseEntity.ok(
                    "Admin deleted successfully"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }
}