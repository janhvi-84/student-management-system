package com.janhvi.studentmanagementapi.controller;

import com.janhvi.studentmanagementapi.entity.User;
import com.janhvi.studentmanagementapi.service.UserService;
import com.janhvi.studentmanagementapi.dto.ChangePasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ========================================
    // CREATE USER
    // ========================================

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody User user) {

        try {

            User savedUser =
                    userService.registerUser(user);

            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ========================================
    // GET ALL USERS
    // ========================================

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    // ========================================
    // GET USER BY ID
    // ========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id) {

        try {

            User user =
                    userService.findById(id);

            return ResponseEntity.ok(user);

        } catch (Exception e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    // ========================================
    // GET USERS BY ROLE
    // ========================================

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(
            @PathVariable String role) {

        return ResponseEntity.ok(
                userService.getUsersByRole(role)
        );
    }

    // ========================================
    // UPDATE USER / STUDENT PROFILE
    // ========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        try {

            User updatedUser =
                    userService.updateUser(
                            id,
                            user
                    );

            return ResponseEntity.ok(
                    updatedUser
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ========================================
    // DELETE USER
    // ========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id) {

        try {

            userService.deleteUser(id);

            return ResponseEntity.ok(
                    "User deleted successfully"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // ========================================
// CHANGE PASSWORD
// STUDENT + ADMIN
// ========================================

@PostMapping("/change-password")
public ResponseEntity<?> changePassword(
        @RequestBody ChangePasswordRequest request) {

    try {

        userService.changePassword(request);

        return ResponseEntity.ok(
                "Password changed successfully"
        );

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}
}