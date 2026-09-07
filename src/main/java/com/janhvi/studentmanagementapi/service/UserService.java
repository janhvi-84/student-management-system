package com.janhvi.studentmanagementapi.service;

import com.janhvi.studentmanagementapi.audit.AuditLogService;
import com.janhvi.studentmanagementapi.entity.Student;
import com.janhvi.studentmanagementapi.entity.User;
import com.janhvi.studentmanagementapi.repository.StudentRepository;
import com.janhvi.studentmanagementapi.repository.UserRepository;
import com.janhvi.studentmanagementapi.dto.ChangePasswordRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final AuditLogService auditLogService;

    private final StudentRepository studentRepository;


    // ========================================
    // CONSTRUCTOR
    // ========================================

    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            AuditLogService auditLogService,
            StudentRepository studentRepository) {

        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;

        this.auditLogService = auditLogService;

        this.studentRepository = studentRepository;
    }


    // ========================================
    // REGISTER USER
    // ========================================

    public User registerUser(User user) {

        if (userRepository
                .findByEmailAndDeletedFalse(user.getEmail())
                .isPresent()) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }


        if (user.getPassword() == null ||
                user.getPassword().isBlank()) {

            throw new RuntimeException(
                    "Password cannot be empty"
            );
        }


        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );


        // Normal registration = STUDENT

        user.setRole("STUDENT");


        return userRepository.save(user);
    }


    // ========================================
    // FIND USER BY ID
    // ========================================

    public User findById(Long id) {

        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }


    // ========================================
    // FIND USER BY EMAIL
    // ========================================

    public User findByEmail(String email) {

        return userRepository
                .findByEmail(email)
                .orElse(null);
    }


    // ========================================
    // CHECK PASSWORD
    // ========================================

    public boolean checkPassword(
            String rawPassword,
            String encodedPassword) {

        return passwordEncoder.matches(
                rawPassword,
                encodedPassword
        );
    }


    // ========================================
    // GET ALL ACTIVE USERS
    // ========================================

    public List<User> getAllUsers() {

        return userRepository
                .findByDeletedFalse();
    }


    // ========================================
    // GET RECENT USERS
    // ========================================

    public List<User> getRecentUsers() {

        return userRepository
                .findByDeletedFalseOrderByIdDesc();
    }


    // ========================================
    // GET USERS BY ROLE
    // ========================================

    public List<User> getUsersByRole(String role) {

        return userRepository
                .findByRoleAndDeletedFalse(role);
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


        Long userId = null;


        // ----------------------------------------
        // PRINCIPAL = LONG
        // ----------------------------------------

        if (principal instanceof Long) {

            userId = (Long) principal;
        }


        // ----------------------------------------
        // PRINCIPAL = INTEGER
        // ----------------------------------------

        else if (principal instanceof Integer) {

            userId =
                    ((Integer) principal).longValue();
        }


        // ----------------------------------------
        // PRINCIPAL = STRING
        // ----------------------------------------

        else if (principal instanceof String) {

            try {

                userId =
                        Long.parseLong(
                                (String) principal
                        );

            } catch (NumberFormatException e) {

                // String may be email
            }
        }


        // ----------------------------------------
        // FIND BY USER ID
        // ----------------------------------------

        if (userId != null) {

            return userRepository
                    .findById(userId)
                    .orElse(null);
        }


        // ----------------------------------------
        // FALLBACK - EMAIL
        // ----------------------------------------

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
    // UPDATE USER PROFILE
    // ========================================

    public User updateUser(
            Long id,
            User updatedUser) {


        // ========================================
        // FIND EXISTING USER
        // ========================================

        User existingUser =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        // ========================================
        // GET LOGGED-IN USER
        // ========================================

        User loggedInUser =
                getLoggedInUser();


        if (loggedInUser == null) {

            throw new RuntimeException(
                    "Logged-in user not found"
            );
        }


        // ========================================
        // SECURITY CHECK
        // ========================================

        /*
         * Student/Admin apna profile update
         * kar sakta hai.
         *
         * Admin kisi user ko update kare to
         * bhi allowed rahega.
         *
         * Existing project flow preserve.
         */

        if ("STUDENT".equalsIgnoreCase(
                loggedInUser.getRole())) {

            if (!loggedInUser.getId()
                    .equals(existingUser.getId())) {

                throw new RuntimeException(
                        "Student can update only own profile"
                );
            }
        }


        // ========================================
        // EMAIL
        // ========================================

        if (updatedUser.getEmail() != null &&
                !updatedUser.getEmail().isBlank()) {


            User emailUser =
                    userRepository
                            .findByEmail(
                                    updatedUser.getEmail()
                            )
                            .orElse(null);


            if (emailUser != null &&
                    !emailUser.getId()
                            .equals(existingUser.getId())) {

                throw new RuntimeException(
                        "Email already exists"
                );
            }


            existingUser.setEmail(
                    updatedUser.getEmail()
            );
        }


        // ========================================
        // PROFILE DETAILS
        // ========================================

        if (updatedUser.getUsername() != null) {

            existingUser.setUsername(
                    updatedUser.getUsername()
            );
        }


        if (updatedUser.getName() != null) {

            existingUser.setName(
                    updatedUser.getName()
            );
        }


        if (updatedUser.getAge() != null) {

            existingUser.setAge(
                    updatedUser.getAge()
            );
        }


        if (updatedUser.getCourse() != null) {

            existingUser.setCourse(
                    updatedUser.getCourse()
            );
        }


        if (updatedUser.getDepartment() != null) {

            existingUser.setDepartment(
                    updatedUser.getDepartment()
            );
        }


        if (updatedUser.getCity() != null) {

            existingUser.setCity(
                    updatedUser.getCity()
            );
        }


        if (updatedUser.getPhoneNumber() != null) {

            existingUser.setPhoneNumber(
                    updatedUser.getPhoneNumber()
            );
        }


        if (updatedUser.getAddress() != null) {

            existingUser.setAddress(
                    updatedUser.getAddress()
            );
        }


        // ========================================
        // ROLE
        // ========================================

        /*
         * Role intentionally update nahi kar rahe.
         *
         * Student STUDENT hi rahega.
         * Admin ADMIN hi rahega.
         */


        // ========================================
        // PASSWORD
        // ========================================

        /*
         * IMPORTANT:
         *
         * Profile update ke through password
         * change nahi karenge.
         *
         * Password ke liye separate
         * changePassword() method hai.
         */


        // ========================================
        // DELETED
        // ========================================

        existingUser.setDeleted(false);


        // ========================================
        // SAVE USER
        // ========================================

        User savedUser =
                userRepository.save(existingUser);


        // ========================================
        // TARGET EMAIL
        // ========================================

        String targetEmail =
                existingUser.getEmail();


        // ========================================
        // STUDENT TARGET ID
        // ========================================

        Integer targetStudentId = null;


        if ("STUDENT".equalsIgnoreCase(
                existingUser.getRole())) {


            /*
             * User ID aur Student ID alag hain.
             *
             * Example:
             *
             * User ID    = 62
             * Student ID = 152
             *
             * Audit mein Student ID = 152
             * jaana chahiye.
             */

            Student targetStudent =
                    studentRepository
                            .findByUserIdAndDeletedFalse(
                                    existingUser.getId()
                            );


            if (targetStudent != null) {

                targetStudentId =
                        targetStudent.getId();
            }
        }


        // ========================================
        // STUDENT UPDATE AUDIT
        // ========================================

        if ("STUDENT".equalsIgnoreCase(
                existingUser.getRole())) {


            auditLogService.log(

                    loggedInUser.getId(),

                    loggedInUser.getEmail(),

                    loggedInUser.getRole(),

                    "UPDATE",

                    "STUDENT",

                    "Student profile updated successfully",

                    targetStudentId,

                    targetEmail
            );
        }


        // ========================================
        // ADMIN UPDATE AUDIT
        // ========================================

        else if ("ADMIN".equalsIgnoreCase(
                existingUser.getRole())) {


            auditLogService.logAdminAction(

                    loggedInUser.getId(),

                    loggedInUser.getEmail(),

                    loggedInUser.getRole(),

                    "UPDATE",

                    "ADMIN",

                    "Admin profile updated successfully",

                    existingUser.getId(),

                    targetEmail
            );
        }


        // ========================================
        // RETURN
        // ========================================

        return savedUser;
    }


    // ========================================
    // CHANGE PASSWORD
    // STUDENT + ADMIN
    // ========================================

    public void changePassword(
            ChangePasswordRequest request) {


        // ========================================
        // GET LOGGED-IN USER
        // ========================================

        User loggedInUser =
                getLoggedInUser();


        if (loggedInUser == null) {

            throw new RuntimeException(
                    "Logged-in user not found"
            );
        }


        // ========================================
        // USER ID CHECK
        // ========================================

        if (request.getUserId() == null) {

            throw new RuntimeException(
                    "User ID is required"
            );
        }


        /*
         * User apna hi password change karega.
         */

        if (!loggedInUser.getId()
                .equals(request.getUserId())) {

            throw new RuntimeException(
                    "You can change only your own password"
            );
        }


        // ========================================
        // FIND USER
        // ========================================

        User user =
                userRepository
                        .findById(request.getUserId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        // ========================================
        // CURRENT PASSWORD
        // ========================================

        if (request.getCurrentPassword() == null ||
                request.getCurrentPassword().isBlank()) {

            throw new RuntimeException(
                    "Current password is required"
            );
        }


        // ========================================
        // NEW PASSWORD
        // ========================================

        if (request.getNewPassword() == null ||
                request.getNewPassword().isBlank()) {

            throw new RuntimeException(
                    "New password is required"
            );
        }


        // ========================================
        // CHECK CURRENT PASSWORD
        // ========================================

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }


        // ========================================
        // CHECK SAME PASSWORD
        // ========================================

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "New password must be different from current password"
            );
        }


        // ========================================
        // ENCODE NEW PASSWORD
        // ========================================

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );


        // ========================================
        // SAVE
        // ========================================

        userRepository.save(user);


        // ========================================
        // AUDIT
        // ========================================

        if ("STUDENT".equalsIgnoreCase(
                user.getRole())) {


            Student targetStudent =
                    studentRepository
                            .findByUserIdAndDeletedFalse(
                                    user.getId()
                            );


            Integer targetStudentId = null;


            if (targetStudent != null) {

                targetStudentId =
                        targetStudent.getId();
            }


            auditLogService.log(

                    loggedInUser.getId(),

                    loggedInUser.getEmail(),

                    loggedInUser.getRole(),

                    "PASSWORD_CHANGE",

                    "STUDENT",

                    "Student password changed successfully",

                    targetStudentId,

                    user.getEmail()
            );
        }


        else if ("ADMIN".equalsIgnoreCase(
                user.getRole())) {


            auditLogService.logAdminAction(

                    loggedInUser.getId(),

                    loggedInUser.getEmail(),

                    loggedInUser.getRole(),

                    "PASSWORD_CHANGE",

                    "ADMIN",

                    "Admin password changed successfully",

                    user.getId(),

                    user.getEmail()
            );
        }
    }


    // ========================================
    // DELETE USER - SOFT DELETE
    // ========================================

    public void deleteUser(Long id) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        user.setDeleted(true);


        userRepository.save(user);
    }
}