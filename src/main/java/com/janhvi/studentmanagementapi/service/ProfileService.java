package com.janhvi.studentmanagementapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.janhvi.studentmanagementapi.audit.AuditLogService;
import com.janhvi.studentmanagementapi.dto.ChangePasswordRequest;
import com.janhvi.studentmanagementapi.dto.ProfileUpdateRequest;
import com.janhvi.studentmanagementapi.entity.Student;
import com.janhvi.studentmanagementapi.entity.User;
import com.janhvi.studentmanagementapi.repository.StudentRepository;
import com.janhvi.studentmanagementapi.repository.UserRepository;

@Service
@Transactional
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogService auditLogService;


    // =========================================================
    // GET LOGGED-IN USER
    // =========================================================

    private User getLoggedInUser(Long fallbackUserId) {

        try {

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();


            // =================================================
            // AUTHENTICATION AVAILABLE
            // =================================================

            if (authentication != null &&
                    authentication.isAuthenticated()) {


                // =================================================
                // FIRST TRY EMAIL
                // =================================================

                String email =
                        authentication.getName();


                if (email != null &&
                        !email.isBlank() &&
                        !"anonymousUser".equalsIgnoreCase(email)) {


                    User authenticatedUser =
                            userRepository
                                    .findByEmail(email)
                                    .orElse(null);


                    if (authenticatedUser != null) {

                        return authenticatedUser;
                    }
                }


                // =================================================
                // PRINCIPAL FALLBACK
                // =================================================

                Object principal =
                        authentication.getPrincipal();


                Long principalId = null;


                // LONG
                if (principal instanceof Long) {

                    principalId =
                            (Long) principal;
                }


                // INTEGER
                else if (principal instanceof Integer) {

                    principalId =
                            ((Integer) principal).longValue();
                }


                // STRING
                else if (principal instanceof String) {

                    try {

                        principalId =
                                Long.parseLong(
                                        (String) principal
                                );

                    } catch (NumberFormatException e) {

                        // Principal may be email.
                    }
                }


                // =================================================
                // FIND BY PRINCIPAL USER ID
                // =================================================

                if (principalId != null) {

                    User authenticatedUser =
                            userRepository
                                    .findById(principalId)
                                    .orElse(null);


                    if (authenticatedUser != null) {

                        return authenticatedUser;
                    }
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Unable to get authenticated user: "
                            + e.getMessage()
            );
        }


        // =========================================================
        // FINAL FALLBACK
        // =========================================================

        if (fallbackUserId != null) {

            return userRepository
                    .findById(fallbackUserId)
                    .orElse(null);
        }


        return null;
    }


    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    public User updateProfile(
            ProfileUpdateRequest request) {


        // =========================================================
        // VALIDATE REQUEST
        // =========================================================

        if (request == null ||
                request.getUserId() == null) {

            throw new RuntimeException(
                    "User ID is required"
            );
        }


        // =========================================================
        // FIND USER
        // =========================================================

        User user =
                userRepository
                        .findById(request.getUserId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        // =========================================================
        // GET LOGGED-IN USER
        // =========================================================

        User loggedInUser =
                getLoggedInUser(
                        request.getUserId()
                );


        if (loggedInUser == null) {

            throw new RuntimeException(
                    "Logged-in user not found"
            );
        }


        // =========================================================
        // UPDATE USER PROFILE
        // =========================================================

        user.setName(
                request.getName()
        );

        user.setAge(
                request.getAge()
        );

        user.setCourse(
                request.getCourse()
        );

        user.setDepartment(
                request.getDepartment()
        );

        user.setCity(
                request.getCity()
        );

        user.setPhoneNumber(
                request.getPhoneNumber()
        );

        user.setAddress(
                request.getAddress()
        );


        userRepository.save(user);


        // =========================================================
        // FIND STUDENT
        // =========================================================

        Student student =
                studentRepository
                        .findByUserIdAndDeletedFalse(
                                request.getUserId()
                        );


        // =========================================================
        // UPDATE STUDENT PROFILE ALSO
        // =========================================================

        if (student != null) {

            student.setName(
                    request.getName()
            );

            student.setAge(
                    request.getAge()
            );

            student.setCourse(
                    request.getCourse()
            );

            student.setDepartment(
                    request.getDepartment()
            );

            student.setCity(
                    request.getCity()
            );

            student.setPhoneNumber(
                    request.getPhoneNumber()
            );

            student.setAddress(
                    request.getAddress()
            );


            studentRepository.save(student);
        }


        // =========================================================
        // AUDIT LOG
        // =========================================================

        try {


            // =====================================================
            // STUDENT PROFILE UPDATE
            // =====================================================

            if ("STUDENT".equalsIgnoreCase(
                    user.getRole())) {


                /*
                 * Student.id tumhare project me int hai.
                 *
                 * Isliye:
                 *
                 * student.getId() != null
                 *
                 * use nahi karna hai.
                 *
                 * Pehle student != null check karenge.
                 */

                Integer targetStudentId = null;


                if (student != null) {

                    targetStudentId =
                            student.getId();
                }


                auditLogService.log(

                        loggedInUser.getId(),

                        loggedInUser.getEmail(),

                        loggedInUser.getRole(),

                        "UPDATE",

                        "STUDENT",

                        "Student profile updated successfully",

                        targetStudentId,

                        user.getEmail()
                );


                System.out.println(
                        "========================================"
                );

                System.out.println(
                        "STUDENT PROFILE UPDATE AUDIT CREATED"
                );

                System.out.println(
                        "PERFORMER ID    : "
                                + loggedInUser.getId()
                );

                System.out.println(
                        "PERFORMER EMAIL : "
                                + loggedInUser.getEmail()
                );

                System.out.println(
                        "PERFORMER ROLE  : "
                                + loggedInUser.getRole()
                );

                System.out.println(
                        "TARGET STUDENT ID : "
                                + targetStudentId
                );

                System.out.println(
                        "TARGET EMAIL : "
                                + user.getEmail()
                );

                System.out.println(
                        "========================================"
                );
            }


            // =====================================================
            // ADMIN PROFILE UPDATE
            // =====================================================

            else if ("ADMIN".equalsIgnoreCase(
                    user.getRole())) {


                auditLogService.logAdminAction(

                        loggedInUser.getId(),

                        loggedInUser.getEmail(),

                        loggedInUser.getRole(),

                        "UPDATE",

                        "ADMIN",

                        "Admin profile updated successfully",

                        user.getId(),

                        user.getEmail()
                );


                System.out.println(
                        "========================================"
                );

                System.out.println(
                        "ADMIN PROFILE UPDATE AUDIT CREATED"
                );

                System.out.println(
                        "PERFORMER ID    : "
                                + loggedInUser.getId()
                );

                System.out.println(
                        "PERFORMER EMAIL : "
                                + loggedInUser.getEmail()
                );

                System.out.println(
                        "TARGET ADMIN ID : "
                                + user.getId()
                );

                System.out.println(
                        "TARGET ADMIN EMAIL : "
                                + user.getEmail()
                );

                System.out.println(
                        "========================================"
                );
            }


        } catch (Exception e) {

            /*
             * Audit failure ki wajah se
             * profile update fail nahi hoga.
             */

            System.out.println(
                    "PROFILE UPDATE AUDIT ERROR"
            );

            e.printStackTrace();
        }


        // =========================================================
        // RETURN UPDATED USER
        // =========================================================

        return user;
    }


    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    public String changePassword(
            ChangePasswordRequest request) {


        // =========================================================
        // VALIDATE REQUEST
        // =========================================================

        if (request == null ||
                request.getUserId() == null) {

            return "User ID is required.";
        }


        if (request.getCurrentPassword() == null ||
                request.getCurrentPassword().isBlank()) {

            return "Current password is required.";
        }


        if (request.getNewPassword() == null ||
                request.getNewPassword().isBlank()) {

            return "New password is required.";
        }


        // =========================================================
        // FIND USER
        // =========================================================

        User user =
                userRepository
                        .findById(request.getUserId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        // =========================================================
        // GET LOGGED-IN USER
        // =========================================================

        User loggedInUser =
                getLoggedInUser(
                        request.getUserId()
                );


        if (loggedInUser == null) {

            return "Logged-in user not found.";
        }


        // =========================================================
        // CHECK CURRENT PASSWORD
        // =========================================================

        if (user.getPassword() == null ||
                user.getPassword().isBlank()) {

            return "Current password is not available.";
        }


        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            return "Current password is incorrect.";
        }


        // =========================================================
        // NEW PASSWORD
        // =========================================================

        if (request.getNewPassword().length() < 8) {

            return "New password must be at least 8 characters.";
        }


        // =========================================================
        // SET NEW PASSWORD
        // =========================================================

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );


        userRepository.save(user);


        // =========================================================
        // PASSWORD CHANGE AUDIT
        // =========================================================

        try {


            // =====================================================
            // STUDENT PASSWORD CHANGE
            // =====================================================

            if ("STUDENT".equalsIgnoreCase(
                    user.getRole())) {


                Student student =
                        studentRepository
                                .findByUserIdAndDeletedFalse(
                                        user.getId()
                                );


                /*
                 * Student.id = int
                 *
                 * Isliye student.getId() ko
                 * null check nahi karna hai.
                 */

                Integer targetStudentId = null;


                if (student != null) {

                    targetStudentId =
                            student.getId();
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


                System.out.println(
                        "========================================"
                );

                System.out.println(
                        "STUDENT PASSWORD CHANGE AUDIT CREATED"
                );

                System.out.println(
                        "PERFORMER ID    : "
                                + loggedInUser.getId()
                );

                System.out.println(
                        "PERFORMER EMAIL : "
                                + loggedInUser.getEmail()
                );

                System.out.println(
                        "PERFORMER ROLE  : "
                                + loggedInUser.getRole()
                );

                System.out.println(
                        "TARGET STUDENT ID : "
                                + targetStudentId
                );

                System.out.println(
                        "TARGET STUDENT EMAIL : "
                                + user.getEmail()
                );

                System.out.println(
                        "========================================"
                );
            }


            // =====================================================
            // ADMIN PASSWORD CHANGE
            // =====================================================

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


                System.out.println(
                        "========================================"
                );

                System.out.println(
                        "ADMIN PASSWORD CHANGE AUDIT CREATED"
                );

                System.out.println(
                        "PERFORMER ID    : "
                                + loggedInUser.getId()
                );

                System.out.println(
                        "PERFORMER EMAIL : "
                                + loggedInUser.getEmail()
                );

                System.out.println(
                        "TARGET ADMIN ID : "
                                + user.getId()
                );

                System.out.println(
                        "TARGET ADMIN EMAIL : "
                                + user.getEmail()
                );

                System.out.println(
                        "========================================"
                );
            }


        } catch (Exception e) {

            /*
             * Audit failure ki wajah se
             * password change fail nahi hoga.
             */

            System.out.println(
                    "PASSWORD CHANGE AUDIT ERROR"
            );

            e.printStackTrace();
        }


        // =========================================================
        // SUCCESS
        // =========================================================

        return "Password changed successfully.";
    }
}